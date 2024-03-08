inherit cargo

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

RDEPENDS:${PN} = " mosquitto ca-certificates libgcc glibc-utils libxcrypt sudo collectd"

# This prevents disabling crates.io registry in cargo_do_configure task and
# allows cargo to fetch dependencies during the do_compile step.
#
# It's still not perfect, because ideally we'd want to download all the source
# code in the do_fetch step, but it's challenging because we'd have to either
# duplicate do_configure step just for fetching, or swap the order and run
# do_configure before do_fetch, which might be confusing for the users.
#
# Still, it makes the update-layer.sh script entirely obsolete by significantly
# improving the maintenance of the layer, simplifying updating to 2 steps:
#
# 1. Update recipe version and point to the new revision
# 2. Handle package/systemd configuration changes, if any.
#
# We'll be looking into how to do it in do_fetch step, but as long as we don't
# have it figured out, or somebody tells us we've broken their build, we're
# going for this approach.
do_compile[network] = "1"
CARGO_DISABLE_BITBAKE_VENDORING = "1"


do_install:append () {
    # install collectd for tedge-mapper
    install -d ${D}${sysconfdir}/tedge/contrib/collectd
    install -m 0644 ${S}/configuration/contrib/collectd/collectd.conf ${D}${sysconfdir}/tedge/contrib/collectd

    # Create symlink for tedge-apt-plugin in `/etc/tedge/sm-plugins`
    install -d ${D}${sysconfdir}/tedge/sm-plugins
    ln -s -r  ${D}${bindir}/tedge-apt-plugin ${D}${sysconfdir}/tedge/sm-plugins/apt

    # Remove packages indicated in `TEDGE_EXCLUDE` (NOTE: they cannot be part of tedge-binary)
    for package in ${@d.getVar('TEDGE_EXCLUDE')}; do 
        rm -f ${D}${bindir}/${package}
    done
}
 
pkg_postinst_ontarget:${PN} () {
    command_exists() {
	    command -v "$@" > /dev/null 2>&1
    }

    group_exists() {
        name="$1"
        if command_exists getent; then
            getent group "$name" >/dev/null
        else
            # Fallback to plain grep, as busybox does not have getent
            grep -q "^${name}:" /etc/group
        fi
    }

    user_exists() {
        name="$1"
        if command_exists getent; then
            getent passwd "$name" >/dev/null
        else
            # Fallback to plain grep, as busybox does not have getent
            grep -q "^${name}:" /etc/passwd
        fi
    }

    ### Create groups
    if ! group_exists tedge; then
        if command_exists groupadd; then
            groupadd --system tedge
        elif command_exists addgroup; then
            addgroup -S tedge
        else
            echo "WARNING: Could not create group: tedge" >&2
        fi
    fi

    ### Create users
    # Create user tedge with no home(--no-create-home), no login(--shell) and in group tedge(--gid)
    if ! user_exists tedge; then
        if command_exists useradd; then
            useradd --system --no-create-home --shell /sbin/nologin --gid tedge tedge
        elif command_exists adduser; then
            adduser -g "" -H -D tedge -G tedge
        else
            echo "WARNING: Could not create user: tedge" >&2
        fi
    fi

    ### Create file in /etc/sudoers.d directory. With this configuration, the tedge user have the right to call the tedge command with sudo rights, which is required for system-wide configuration in "/etc/tedge"
    if [ -d /etc/sudoers.d ]; then
        echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/tedge, /etc/tedge/sm-plugins/[a-zA-Z0-9]*, /bin/sync, /sbin/init" >/etc/sudoers.d/tedge

        if [ -f "/etc/sudoers.d/010_pi-nopasswd" ]; then
            echo "tedge   ALL = (ALL) NOPASSWD: /usr/bin/tedge, /etc/tedge/sm-plugins/[a-zA-Z0-9]*, /bin/sync, /sbin/init" >/etc/sudoers.d/tedge-nopasswd
        fi
    fi

    ### Add include to mosquitto.conf so tedge specific conf will be loaded
    if [ -f /etc/mosquitto/mosquitto.conf ]; then
        if ! grep -q "include_dir /etc/tedge/mosquitto-conf" "/etc/mosquitto/mosquitto.conf"; then
            # Insert `include_dir /etc/tedge/mosquitto-conf` before any `include_dir`
            # directive so that all other partial conf files inherit the
            # `per_listener_settings` defined in /etc/tedge/mosquitto-conf.
            # `per_listener_settings` has to be defined once, before other listener
            # settings or else it causes the following error:
            #
            # Error: per_listener_settings must be set before any other security
            # settings.
            # Match any included_dir directive as different distributions have different default settings:
            #  On Fedora: `#include_dir`. mosquitto does not use a /etc/mosquitto/conf.d folder
            #  On Debian: `include_dir /etc/mosquitto/conf.d`. Uses a conf.d folder, so the tedge setting must be before this

            # Check if `include_dir` or `#include_dir` (as the latter could be a future problem if the user uncomments it)
            if grep -qE '^#?include_dir' /etc/mosquitto/mosquitto.conf; then
                # insert tedge include_dir before the first `included_dir` (but only the first!)
                mosquitto_conf=$(awk '!found && /^#?include_dir/ \
                { print "include_dir /etc/tedge/mosquitto-conf"; found=1 }1' \
                /etc/mosquitto/mosquitto.conf)
                echo "$mosquitto_conf" > /etc/mosquitto/mosquitto.conf
            else
                # config does not contain any include_dir directive, so we can safely append it
                echo "include_dir /etc/tedge/mosquitto-conf" >> /etc/mosquitto/mosquitto.conf
            fi
        fi
    fi

    # FIXME: Add to recipe at build time rather than on first run
    if [ -d /etc/sudoers.d/ ]; then
        echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/mender, /usr/bin/tedgectl" > /etc/sudoers.d/tedge-firmware
    fi

    # Ensure persistence of operation state across partition swaps by storing
    # information on a persisted mount
    mkdir -p /data/tedge/agent
    chown -R tedge:tedge /data/tedge/agent

    # FIXME: Check if there is a better place to do this
    if [ -d /var/lib/mosquitto ]; then
        chown -R mosquitto:mosquitto /var/lib/mosquitto
    fi

    # FIXME: Currently some workflow state is reliant on the mosquitto db
    # and there might be a race condition on startup after a partition swap
    # where the existing mosquitto state is sometimes processed before the state
    # stored under the /data/tedge/agent folder
    # https://github.com/thin-edge/thin-edge.io/issues/2495
    mkdir -p /data/mosquitto
    chown mosquitto:mosquitto /data/mosquitto

    # Change log dir
    mkdir -p "/data/tedge/log"
    chown -R tedge:tedge "/data/tedge/log"
    tedge config set logs.path "/data/tedge/log"

    # Enable firmware management
    tedge config set c8y.enable.firmware_update true

    # Initialize the tedge
    tedge init 

    if command_exists c8y-remote-access-plugin; then
        c8y-remote-access-plugin --init
    fi
}

FILES:${PN} = "\ 
    ${bindir}/* \ 
    ${sysconfdir}/tedge/contrib/collectd \
    ${sysconfdir}/tedge/sm-plugins/apt \
"

require init-manager.inc