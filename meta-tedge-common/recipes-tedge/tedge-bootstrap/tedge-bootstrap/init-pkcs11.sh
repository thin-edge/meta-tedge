#!/bin/sh
set -e

DEVICE_ID="${DEVICE_ID:-}"
C8Y_URL="${C8Y_URL:-}"
DEVICE_ONE_TIME_PASSWORD="${DEVICE_ONE_TIME_PASSWORD:-}"
TOKEN_URL="${TOKEN_URL:-}"

export GNUTLS_PIN="${GNUTLS_PIN:-123456}"
export GNUTLS_SO_PIN="${GNUTLS_SO_PIN:-123456}"
export TOKEN_LABEL="${TOKEN_LABEL:-tedge}"
export TEDGE_CONFIG_DIR="${TEDGE_CONFIG_DIR:-/etc/tedge}"

# Only used for TPM 2.0
export TPM2_PKCS11_STORE="${TPM2_PKCS11_STORE:-/etc/tedge/hsm}"

PKCS11_MODULE="${PKCS11_MODULE:-}"
KEY="${KEY:-}"
IS_SELF_SIGNED=0

ACTION="create"

HSM_TYPE="${HSM_TYPE:-}"

# For most pkcs11 compatible HSM's, certtool can get the public key automatically, but for Yubikey
# you need to manually export the key using 'ykman piv keys export 9a "<path>"'
PUBLIC_KEY="${PUBLIC_KEY:-$TEDGE_CONFIG_DIR/device-certs/tedge.pub}"

usage() {
    cat <<EOT
$0 [OPTIONS]

ARGUMENTS
  --c8y-url <url>           Cumulocity URL
  --type <string>           Type of HSM (using the PKCS#11 interface) to use. Available values: [softhsm2, yubikey, nitrokey, tpm2]
  --token-url <url>         Token PKCS#11 URL which is to be used for initialization.
  --key <url>               Key's PKCS#11 URL. If left blank then it will be auto detected
  --self-signed             Generate a self-signed certificate
  --pin <string>            Pin used to access the HSM
  --so-pin <string>         Special pin
  --device-id <string>      Device ID to use during initialization. Defaults to first non-zero value from: DEVICE_ID env, tedge-identity, hostname
  --module <path>           Path to the PKCS#11 module to use
  -p, --one-time-password <string>      one-time-password use to request the certificate from the Cumulocity CA
  --debug                   Enable debugging
  -h, --help                Show this help

EXAMPLES

## Nitrokey

sudo $0 --type nitrokey --c8y-url example.c8y.io --token-url 'pkcs11:model=PKCS%2315%20emulated;manufacturer=www.CardContact.de;serial=DENK0400089;token=SmartCard-HSM%20%28UserPIN%29'
# Initialize private key using nitrokey, where you have to specify the slot where the nitrokey is accessible from


## SoftHSM2

sudo $0 --type softhsm2 --c8y-url example.c8y.io
# Initialize private key using softhsm2, and use the Cumulocity CA to request a certificate


## TPM2

sudo $0 --type tpm2 --c8y-url example.c8y.io --token-url 'pkcs11:model=SLB9672%00%00%00%00%00%00%00%00%00;manufacturer=Infineon;serial=0000000000000000;token='
# Initialize private key using a tpm 2.0 module, and use the Cumulocity CA to request a certificate

EOT
}

#
# Parse arguments
#
while [ $# -gt 0 ]; do
    case "$1" in
        --self-signed)
            IS_SELF_SIGNED=1
            ;;
        --label)
            TOKEN_LABEL="$2"
            shift
            ;;
        --token-url)
            TOKEN_URL="$2"
            shift
            ;;
        --key)
            if [ -n "$2" ]; then
                KEY="$2"
            fi
            shift
            ;;
        --pin)
            GNUTLS_PIN="$2"
            shift
            ;;
        --so-pin)
            GNUTLS_SO_PIN="$2"
            shift
            ;;
        --device-id)
            DEVICE_ID="$2"
            shift
            ;;
        --module)
            PKCS11_MODULE="$2"
            shift
            ;;
        --type)
            HSM_TYPE="$2"
            shift
            ;;
        --c8y-url)
            C8Y_URL="$2"
            shift
            ;;
        # Cumulocity Enrollment token
        --one-time-password|-p)
            DEVICE_ONE_TIME_PASSWORD="$2"
            shift
            ;;
        --debug)
            set -x
            ;;
        --help|-h)
            usage
            exit 0
            ;;
    esac
    shift
done

if [ "$(id -u)" -ne 0 ]; then
    echo "This script must be run as root. Please use sudo or run as root." >&2
    exit 1
fi

if [ -z "${DEVICE_ID:-}" ]; then
    DEVICE_ID=$(tedge config get device.id 2>/dev/null || tedge-identity 2>/dev/null || hostname)
fi

# Set module defaults
case "$HSM_TYPE" in
    yubikey)
        if [ -z "$PKCS11_MODULE" ]; then
            PKCS11_MODULE=$(find /usr/lib -name libykcs11.so | head -n1)
        fi
        ;;
    softhsm2)
        if [ -z "$PKCS11_MODULE" ]; then
            PKCS11_MODULE=$(find /usr/lib -name libsofthsm2.so | head -n1)
        fi
        ;;
    nitrokey)
        if [ -z "$PKCS11_MODULE" ]; then
            PKCS11_MODULE=$(find /usr/lib -name opensc-pkcs11.so | head -n1)
        fi
        ;;
    tpm2)
        if [ -z "$PKCS11_MODULE" ]; then
            PKCS11_MODULE=$(find /usr/lib -name libtpm2_pkcs11.so | head -n1)
        fi
        ;;
    *)
        # Don't use an explicit pkcs11 module, let the tooling choose the default
        ;;
esac

#
# Enable usage with thin-edge.io
#

if [ -n "$C8Y_URL" ]; then
    C8Y_URL=$(echo "$C8Y_URL" | sed 's|https?://||g')
    tedge config set c8y.url "$C8Y_URL"
fi

tedge config set mqtt.bridge.built_in true
tedge config set device.cryptoki.mode socket
if [ -f "$PKCS11_MODULE" ]; then
    tedge config set device.cryptoki.module_path "$PKCS11_MODULE"
    tedge config set device.cryptoki.pin "$GNUTLS_PIN"

    if ! grep -q '^TPM2_PKCS11_STORE=.\+' "$TEDGE_CONFIG_DIR/plugins/tedge-p11-server.conf"; then
        cat <<EOT > "$TEDGE_CONFIG_DIR/plugins/tedge-p11-server.conf"
# TPM specific settings
TPM2_PKCS11_STORE="$TPM2_PKCS11_STORE"
EOT
    fi
elif [ -n "$PKCS11_MODULE" ]; then
    echo "Could not find PKCS11 Module. path=$PKCS11_MODULE" >&2
fi

# set common arguments to ensure p11tool finds the correct module if there are multiple
P11_TOOL_ARGS=
if [ -n "$PKCS11_MODULE" ]; then
    P11_TOOL_ARGS="--provider=$PKCS11_MODULE"
fi

get_token() {
    p11tool $P11_TOOL_ARGS --list-tokens 2>/dev/null | grep "token=$TOKEN_LABEL" | awk '{ print $2 }' | head -n1
}

get_key() {
    p11tool $P11_TOOL_ARGS --login --list-all "$TOKEN_URL" 2>/dev/null | grep type=private | awk '{ print $2 }' | head -n1
}

init_private_key() {
    case "$1" in
        yubikey)
            ykman piv keys generate --algorithm ECCP256 9a "$PUBLIC_KEY"
            ;;
        nitrokey)
            p11tool $P11_TOOL_ARGS --initialize-pin "$TOKEN_URL"
            p11tool $P11_TOOL_ARGS --login --generate-privkey ECDSA --curve=secp256r1 --label "$TOKEN_LABEL" --outfile "$PUBLIC_KEY" "$TOKEN_URL"
            ;;
        tpm2)
            # TODO: Should the store be removed if the user wants to re-initialize it?
            # rm -rf "$TPM2_PKCS11_STORE"

            mkdir -p "$TPM2_PKCS11_STORE"
            chown -R tedge:tedge "$TPM2_PKCS11_STORE"

            p11tool $P11_TOOL_ARGS --initialize --label "$TOKEN_LABEL" --set-so-pin "$GNUTLS_SO_PIN" "$TOKEN_URL"

            # refresh as there should be a new token created
            TOKEN_URL=$(p11tool $P11_TOOL_ARGS --list-token-urls | grep "token=$TOKEN_LABEL" | head -n 1)

            p11tool $P11_TOOL_ARGS --initialize-pin "$TOKEN_URL"
            p11tool $P11_TOOL_ARGS --login --generate-privkey ECDSA --curve=secp256r1 --label "$TOKEN_LABEL" --outfile "$PUBLIC_KEY" "$TOKEN_URL"
            ;;
        softhsm2)
            softhsm2-util --init-token --free --label "$TOKEN_LABEL" --pin "$GNUTLS_PIN" --so-pin "$GNUTLS_SO_PIN"

            # TODO: How to limit changing ownership to the token which was created, as each
            # token is stored in a subfolder, so we should only change the one that was just created
            chown -R tedge:softhsm /var/lib/softhsm/tokens/*
            ;;
        *)
            echo "Warning: Unknown HSM type (name=$1). Trying to initialize using standard p11tool commands" >&2
            p11tool $P11_TOOL_ARGS --initialize-pin "$TOKEN_URL"
            p11tool $P11_TOOL_ARGS --login --generate-privkey ECDSA --curve=secp256r1 --label "$TOKEN_LABEL" --outfile "$PUBLIC_KEY" "$TOKEN_URL"
            ;;
    esac
}

get_random_code() {
    awk '
function rand_string(n,         s,i) {
    for ( i=1; i<=n; i++ ) {
        s = s chars[int(1+rand()*numChars)]
    }
    return s
}
BEGIN{
    srand()
    for (i=48; i<=122; i++) {
        char = sprintf("%c", i)
        if ( char ~ /[[:alnum:]]/ ) {
            chars[++numChars] = char
        }
    }

    for (i=1; i<=1; i++) {print rand_string(30)}
}'
}

#
# Get/Init slot
#
if [ -z "$TOKEN_URL" ]; then
    # Select first URL
    TOKEN_URL=$(p11tool $P11_TOOL_ARGS --list-token-urls | head -n 1)
fi

# check if a key can be found or not (to auto detect whether an initialization is needed)
if [ -z "$KEY" ]; then
    KEY=$(get_key)
fi

# Create the key if required
if [ -z "$KEY" ]; then
    case "$ACTION" in
        create)
            init_private_key "$HSM_TYPE"
            ;;
    esac
    TOKEN_URL=$(get_token)
fi
echo "Using Token URL: $TOKEN_URL" >&2

#
# Get key
#
if [ -z "$KEY" ]; then
    KEY=$(get_key)
fi


#
# Get/Create CSR template
#
CSR_TEMPLATE="$TEDGE_CONFIG_DIR/device-certs/cert.template"

# If it is self-signed, then Cumulocity requires the ca property
# to be added, otherwise certificate will be rejected by Cumulocity
# when trying to upload it
IS_CA=""
if [ "$IS_SELF_SIGNED" ]; then
    IS_CA="ca"
fi

cat <<EOT > "$CSR_TEMPLATE"
organization = "Thin Edge"
unit = "Test Device"
#state = "QLD"
#country = AU
cn = "$DEVICE_ID"
expiration_days = 365
$IS_CA
EOT

#
# Create CSR (to be signed externally) or create a self-signed certificate
#
# on macOS there is BSD certtool instead of gnu-certtool which have different interfaces
CERT_TOOL="certtool"
if command -V gnutls-certtool >/dev/null 2>&1; then
    CERT_TOOL="gnutls-certtool"
fi

GSED="sed"
if command -V gsed >/dev/null 2>&1; then
    GSED="gsed"
fi

if [ ! -f "$PUBLIC_KEY" ]; then
    case "$HSM_TYPE" in
        yubikey)
            ykman piv keys export 9a "$PUBLIC_KEY"
            ;;
        nitrokey)
            ;;
        tpm2)
            ;;
        softhsm2)
            ;;
        *)
            echo "Warning: Unknown HSM type (name=$HSM_TYPE). You need to init this on your own" >&2
            ;;
    esac
fi

if [ "$IS_SELF_SIGNED" = 0 ]; then
    #
    # Create CSR
    #
    CSR_PATH=$(tedge config get device.csr_path)
    [ -f "$CSR_PATH" ] && chmod 644 "$CSR_PATH"
    
    "$CERT_TOOL" \
        $P11_TOOL_ARGS \
        --generate-request \
        --template "$CSR_TEMPLATE" \
        --load-privkey "$KEY" \
        --load-pubkey "$PUBLIC_KEY" \
        --outfile "$CSR_PATH"

    echo "Created csr: $CSR_PATH" >&2
else
    # Optional: Self sign the Certificate
    echo "Creating self-signed certificate" >&2
    CERT_PATH=$(tedge config get device.cert_path)
    [ -f "$CERT_PATH" ] && chmod 644 "$CERT_PATH"

    "$CERT_TOOL" \
        $P11_TOOL_ARGS \
        --generate-self-signed \
        --template "$CSR_TEMPLATE" \
        --load-privkey "$KEY" \
        --load-pubkey "$PUBLIC_KEY" \
        --outfile "$CERT_PATH"
    chmod 444 "$CERT_PATH" ||:
fi

case "$ACTION" in
    create)
        # Restart the existing tedge-p11-server instance so it can reload the new key (used later on)
        if command -V systemctl >/dev/null 2>&1; then
            systemctl restart tedge-p11-server.socket ||:
        fi

        if [ "$IS_SELF_SIGNED" = 1 ]; then
            echo "Uploading self-signed certificate" >&2
            tedge cert upload c8y
            tedge reconnect c8y
            exit 0
        fi

        if [ -z "$DEVICE_ONE_TIME_PASSWORD" ]; then
            # Generate a code
            DEVICE_ONE_TIME_PASSWORD=$(get_random_code)
        fi

        if [ -n "$C8Y_URL" ]; then
            echo "" >&2
            echo "Register in Cumulocity using:" >&2
            echo "" >&2
            echo "  https://$C8Y_URL/apps/devicemanagement/index.html#/deviceregistration?externalId=$DEVICE_ID&one-time-password=$DEVICE_ONE_TIME_PASSWORD" >&2
            echo "" >&2
        fi

        if ! tedge cert download c8y --device-id "$DEVICE_ID" --csr-path "$CSR_PATH" --one-time-password "$DEVICE_ONE_TIME_PASSWORD" --retry-every 5s 2>/dev/null; then
            echo "Failed to download certificate from Cumulocity" >&2
            exit 1
        fi
        echo "Successfully downloaded certificate. Trying to connect with the cloud..." >&2
        
        if ! tedge reconnect c8y; then
            echo "Failed to connect to Cumulocity. Please look through the console messages for details, or try running with --debug" >&2
            exit 1
        fi

        printf '\nSuccessfully connected the device to the cloud!\n\n' >&2
        ;;
    *)
        echo "No action given by the user" >&2
        ;;
esac
