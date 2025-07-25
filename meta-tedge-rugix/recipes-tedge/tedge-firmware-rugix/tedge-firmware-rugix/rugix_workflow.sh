#!/bin/sh
set -e
FIRMWARE_NAME=
FIRMWARE_VERSION=
FIRMWARE_URL=
MANUAL_DOWNLOAD=0
STREAM_DOWNLOAD=auto
NETRC_FILE="${NETRC_FILE:-"/etc/tedge/.netrc"}"
CHECK_META_INFO=0

# Enable indexes whilst using dynamic or static delta updates as the first delta update
DELTA_UPDATE_METHOD=${DELTA_UPDATE_METHOD:-casync}

# Exit codes
OK=0
FAILED=1
REQUEST_RESTART=4

# Use temp directory so that the file can't accidentally persist across partitions
# thus always booting into the spare partition
REBOOT_SPARE_REQUEST=/tmp/.reboot_spare

# Detect if sudo should be used or not. It will be used if it is found
SUDO=""
if command -V sudo >/dev/null 2>&1; then
    SUDO="sudo"
fi

_WORKDIR=$(pwd)

# Change to a directory which is readable otherwise rugix-ctrl can have problems reading the mounts
cd /tmp || cd /

BOOT_ACTIVE=$($SUDO rugix-ctrl system info --json | jq -r '.boot.activeGroup' | tr '[:lower:]' '[:upper:]')
BOOT_DEFAULT=$($SUDO rugix-ctrl system info --json | jq -r '.boot.defaultGroup' | tr '[:lower:]' '[:upper:]')

if [ "$BOOT_ACTIVE" = "A" ]; then
    BOOT_SPARE=B
else
    BOOT_SPARE=A
fi

ACTION="$1"
shift

log() {
    msg="$(date +%Y-%m-%dT%H:%M:%S) [current=$ACTION] $*"
    echo "$msg" >&2

    # publish to pub for better resolution
    tedge mqtt pub -q 2 te/device/main///e/firmware_update "{\"text\":\"Firmware Workflow: [$ACTION] $*\",\"state\":\"$ACTION\",\"partition\":\"$BOOT_ACTIVE\"}"
    sleep 1
}

local_log() {
    # Only log locally and don't push to the cloud
    msg="$(date +%Y-%m-%dT%H:%M:%S) [current=$ACTION] $*"
    echo "$msg" >&2
}

update_state() {
    echo ":::begin-tedge:::"
    echo "$1"
    echo ":::end-tedge:::"
    sleep 1
}

set_reason() {
    reason="$1"
    message=$(printf '{"reason":"%s"}' "$reason")
    update_state "$message"
}

#
# main
#
while [ $# -gt 0 ]; do
    case "$1" in
        --firmware-name)
            FIRMWARE_NAME="$2"
            shift
            ;;
        --firmware-version)
            FIRMWARE_VERSION="$2"
            shift
            ;;
        --url)
            FIRMWARE_URL="$2"
            shift
            ;;
    esac
    shift
done

wait_for_network() {
    #
    # Wait for network to be ready but don't block if still not available as the commit
    # might be used to restore network connectivity.
    #
    attempt=0
    max_attempts=10
    # Network ready: 0 = no, 1 = yes
    ready=0
    local_log "Waiting for network to be ready, and time to be synced"

    while [ "$attempt" -lt "$max_attempts" ]; do
        # TIME_SYNC_ACTIVE=$(timedatectl | grep NTP | awk '{print $NF}')
        TIME_IN_SYNC=$(timedatectl | awk '/System clock synchronized/{print $NF}')
        case "${TIME_IN_SYNC}" in
            yes)
                ready=1
                break
                ;;
        esac
        attempt=$((attempt + 1))
        local_log "Network not ready yet (attempt: $attempt from $max_attempts)"
        sleep 30
    done

    # Duration can only be based on uptime since the device's clock might not be synced yet, so 'date' will not be monotonic
    duration=$(awk '{print $1}' /proc/uptime)

    local_log "Network: ready=$ready (after ${duration}s)"
    if [ "$ready" = "1" ]; then
        log "Network is ready after ${duration}s (from startup)"
        return 0
    fi

    # Don't send cloud message if it is not ready
    return 1
}

executing() {
    if [ "$BOOT_ACTIVE" != "$BOOT_DEFAULT" ]; then
        set_reason "Refusing to install update as the current (active) partition is not the default partition. This indicates that you may be in the middle of an update. Please reboot to switch to the default partition"
        exit "$FAILED"
    fi
    log "Starting firmware update. Current partition is $BOOT_ACTIVE, so update will be applied to $BOOT_SPARE"
}

set_streaming_setting() {
    if [ "$STREAM_DOWNLOAD" != "auto" ]; then
        return
    fi

    url="$1"
    case "$url" in
        */inventory/binaries/*)
            # disable streaming as rugix can use the c8y local proxy service
            STREAM_DOWNLOAD=0
            ;;
        *)
            STREAM_DOWNLOAD=1
            ;;
    esac
}

download() {
    url="$1"

    #
    # Change url to a local url using the c8y proxy
    #
    url_hosted_in_c8y=0
    case "$url" in
        https://*/inventory/binaries/*)
            # Cumulocity URL, use the c8y auth proxy service
            partial_path=$(echo "$url" | sed 's|https://[^/]*/||g')
            c8y_proxy_host=$(tedge config get c8y.proxy.client.host)
            c8y_proxy_port=$(tedge config get c8y.proxy.client.port)
            tedge_url="http://${c8y_proxy_host}:${c8y_proxy_port}/c8y/$partial_path"
            url_hosted_in_c8y=1
            ;;
        http://*|https://*)
            # External URL, pass it untouched
            # NOTE: If a service required authorization, then this would be the place to add it
            # For example some blob stores support signed URLS
            partial_path=$(echo "$url" | sed 's|https://[^/]*/||g')
            tedge_url="$url"
            ;;
        *)
            # Assume url is actually a file and just go to the next state
            update_state "$(printf '{"url":"%s"}\n' "$url")"
            return "$OK"
            ;;
    esac

    if [ "$MANUAL_DOWNLOAD" = 1 ]; then
        TEDGE_DATA=$(tedge config get data.path)

        # Removing any older files to ensure space for next file to download
        # Note: busy box does not support the -delete flag
        find "$TEDGE_DATA" -name "*.firmware" -exec rm {} \;

        last_part=$(echo "$partial_path" | rev | cut -d/ -f1 | rev)
        local_file="$TEDGE_DATA/${last_part}.firmware"
        log "Manually downloading artifact from $tedge_url and saving to $local_file"

        download_file "$tedge_url" "$local_file"
        log "Downloaded file from: $tedge_url"
        update_state "$(printf '{"url":"%s"}\n' "$local_file")"
    else
        log "Replacing url with a tedge url: $tedge_url"
        update_state "$(printf '{"url":"%s"}\n' "$tedge_url")"
    fi
}

download_file() {
    url="$1"

    # Output file. Defaults to stdout, "-"
    output_file="-"
    if [ $# -gt 1 ]; then
        output_file="$2"
    fi

    # Use curl so that netrc files can be supported
    case "$url" in
        *zip)
            # decompress using bsdtar as it supports unzipping via streaming
            # so that we don't have to download the file, unzip, then pass it on
            if [ "$output_file" != "-" ]; then
                curl -sfL \
                    --connect-timeout 30 \
                    --retry 5 \
                    --retry-delay 0 \
                    --netrc-file "$NETRC_FILE" \
                    --netrc-optional \
                    "$url" \
                | bsdtar -x -O > "$output_file"
            else
                # stream to stdout
                curl -sfL \
                    --connect-timeout 30 \
                    --retry 5 \
                    --retry-delay 0 \
                    --netrc-file "$NETRC_FILE" \
                    --netrc-optional \
                    "$url" \
                | bsdtar -x -O
            fi
            ;;
        *)
            curl -sfL \
                --connect-timeout 30 \
                --retry 5 \
                --retry-delay 0 \
                --netrc-file "$NETRC_FILE" \
                --netrc-optional \
                -o "$output_file" \
                -C - \
                "$url"
            ;;
    esac
}

install() {
    url="$1"

    set_streaming_setting "$url"

    # TODO: Is this required, or can the update provide additional information about what
    # type of update it is and if the indexes are required or not
    case "$DELTA_UPDATE_METHOD" in
        casync)
            # Note: dynamic updates aren't supported when streaming downloads as rugix needs
            # do send the HTTP request to download the relevant portions of the binary
            if [ "$STREAM_DOWNLOAD" = 0 ]; then
                # Note: It is possible that the need for this may be removed in future rugix versions
                local_log "Preparing index for dynamic delta updates"
                if [ "$BOOT_ACTIVE" = "a" ]; then
                    $SUDO rugix-ctrl slots create-index boot-a casync-64 sha512-256
                    $SUDO rugix-ctrl slots create-index system-a casync-64 sha512-256
                else
                    $SUDO rugix-ctrl slots create-index boot-b casync-64 sha512-256
                    $SUDO rugix-ctrl slots create-index system-b casync-64 sha512-256
                fi
            fi
            ;;
        xdelta)
            # TODO: How to check if the slot can be used or not for delta updates
            # See https://oss.silitics.com/rugix/docs/next/ctrl/delta-updates/#static-delta-updates
            ;;
    esac

    set +e
    case "$url" in
        http://*|https://*)
            if [ "$STREAM_DOWNLOAD" = 1 ]; then
                log "Downloading and streaming image to rugix"
                download_file "$url" | $SUDO rugix-ctrl update install --reboot no -
            else
                log "Downloading image using rugix"
                $SUDO rugix-ctrl update install --reboot no "$url"
            fi
            ;;
        *)
            # It is a file
            log "Installing local image to rugix"
            $SUDO rugix-ctrl update install --reboot no "$url"
            ;;
    esac
    EXIT_CODE=$?
    set -e

    case "$EXIT_CODE" in
        0)
            log "OK, RESTART required"
            ;;
        *)
            log "ERROR. Unexpected return code. code=$EXIT_CODE"
            ;;
    esac

    # Create mark file which is used by the restart state to reboot into the spare partition
    touch "$REBOOT_SPARE_REQUEST" ||:
    exit "$EXIT_CODE"
}

restart() {
    # NOTE: This function should not be called in the script directly but rather via the system.toml
    if [ -f "$REBOOT_SPARE_REQUEST" ]; then
        rm -f "$REBOOT_SPARE_REQUEST" ||:

        message=$(printf '{"text":"Rebooting into spare partition (%s -> %s)","partition":"%s"}' "$BOOT_ACTIVE" "$BOOT_SPARE" "$BOOT_ACTIVE")
        tedge mqtt pub -q 1 "te/device/main///e/reboot_spare" "$message" ||:
        sleep 5
        $SUDO rugix-ctrl system reboot --spare
    else
        message=$(printf '{"text":"Rebooting into default partition (%s -> %s)","partition":"%s"}' "$BOOT_ACTIVE" "$BOOT_DEFAULT" "$BOOT_ACTIVE")
        tedge mqtt pub -q 1 "te/device/main///e/reboot_default" "$message" ||:
        sleep 5
        $SUDO rugix-ctrl system reboot
    fi
    exit "$OK"
}

verify() {
    log "Checking device health"

    # Rollback just in case if the partitions could not be read, so we can't confirm which partition we are on
    if [ -z "$BOOT_ACTIVE" ] || [ -z "$BOOT_DEFAULT" ]; then
        set_reason "Could not read partition information so rolling back to be safe. ACTIVE=$BOOT_ACTIVE, DEFAULT=$BOOT_DEFAULT"
        exit "$REQUEST_RESTART"
    fi

    if [ "$BOOT_ACTIVE" = "$BOOT_DEFAULT" ]; then
        # Don't both to reboot if no partition swap occurred because we are already in the ok partition
        set_reason "Partition swap did not occur. Reasons could be, corrupt/non-bootable image, someone did a manual rollback or the machine was restarted manually before the health check was run"
        exit "$FAILED"
    fi

    # Check that the versions match the expected otherwise people can get
    # into bad habits of not ensuring the meta information in the operation
    # does not match the actual image
    # TODO: Use rugix-ctrl instead of reading the file directly (once the cli supports showing the image info)
    if [ "$CHECK_META_INFO" = 1 ]; then
        ARTIFACT_FILE=/etc/rugix/system-build-info.json
        if [ -f /etc/rugix/system-build-info.json ]; then
            ACTUAL_NAME=$(jq -r '.name' "$ARTIFACT_FILE")
            ACTUAL_VERSION=$(jq -r '.release.version // "0.0"' "$ARTIFACT_FILE")

            if [ "$ACTUAL_NAME" != "$FIRMWARE_NAME" ] || [ "$ACTUAL_VERSION" != "$FIRMWARE_VERSION" ]; then
                DETAILS=$(printf '\n    actual: name=%s, version=%s\n  expected: name=%s, version=%s)' "$$ACTUAL_NAME" "$ACTUAL_VERSION" "$$FIRMWARE_NAME" "$FIRMWARE_VERSION")
                set_reason "New image name does not match the values from the operation. Please check that the firmware name/version matches the actual image.${DETAILS}"
                exit "$REQUEST_RESTART"
            fi
        fi
    fi
}

commit() {
    log "Executing: rugix-ctrl system commit"
    set +e
    $SUDO rugix-ctrl system commit
    EXIT_CODE=$?
    set -e

    case "$EXIT_CODE" in
        0)
            # Check what the updated default partition is
            BOOT_DEFAULT=$($SUDO rugix-ctrl system info --json | jq -r '.boot.defaultGroup' | tr '[:lower:]' '[:upper:]')

            log "Commit successful. New default partition is $BOOT_DEFAULT"
            ;;
        *)
            log "rugix-ctrl returned code: $EXIT_CODE. Rolling back to previous partition"
            ;;
    esac

    # update inventory scripts after the commit has been changed
    $SUDO systemctl start tedge-inventory.service || local_log "Failed to run tedge-inventory.service"

    exit "$EXIT_CODE"
}

rollback_successful() {
    # TODO: Support cloud profiles and look for all enabled mappers, not just the standard names
    MAPPERS="c8y az aws"
    for CLOUD_MAPPER in $MAPPERS; do
        if [ -n "$(tedge config get "${CLOUD_MAPPER}.url" 2>/dev/null)" ]; then
            # Use a reconnect as it will also recreate the bridge config
            log "Reconnecting $CLOUD_MAPPER mapper"
            if ! $SUDO tedge reconnect "$CLOUD_MAPPER"; then
                log "WARNING: Failed to reconnect the mapper"
            fi
        fi
    done

    # update inventory scripts after the commit has been changed
    $SUDO systemctl start tedge-inventory.service || local_log "Failed to run tedge-inventory.service"

    log "Firmware update failed, but the rollback was successful. partition=$BOOT_ACTIVE, default=$BOOT_DEFAULT"
}

case "$ACTION" in
    executing) executing; ;;
    download) download "$FIRMWARE_URL"; ;;
    install) install "$FIRMWARE_URL"; ;;
    verify) verify; ;;
    commit) commit; ;;
    restart) restart; ;;
    restarted)
	    wait_for_network ||:
        log "Device has been restarted...continuing workflow. partition=$BOOT_ACTIVE, default=$BOOT_DEFAULT"
        ;;
    rollback_successful)
        rollback_successful
        ;;
    failed_restart) ;;
    *)
        log "Unknown command. This script only accepts: download, install, commit, rollback, rollback_successful, failed_restart"
        exit "$FAILED"
        ;;
esac

# switch back to original directory
cd "$_WORKDIR" ||:

exit "$OK"
