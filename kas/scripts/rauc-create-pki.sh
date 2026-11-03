#!/usr/bin/env bash
#
# Create a PKI for signing firmware (RAUC bundles):
#
#   root CA  ->  intermediate CA  ->  signer
#
# * The root CA certificate is installed on the device (keyring). The root CA
#   private key is only needed to issue intermediate CAs, so keep it offline.
# * The intermediate CA issues signers. Its certificate is embedded in each
#   bundle, so the device can build the chain to the root CA.
# * The signer is used at build time to sign the bundles.
#
# Files created in the output directory:
#
#   ca.cert.pem                     Root CA certificate (public, installed on the device)
#   private/ca.key.pem              Root CA private key (keep offline)
#   intermediate.cert.pem           Intermediate CA certificate (public, embedded in bundles)
#   private/intermediate.key.pem    Intermediate CA private key (only needed to issue signers)
#   <signer>.cert.pem               Signer certificate (public)
#   private/<signer>.key.pem        Signer private key (secret, used to sign bundles)
#
# Running the script again with an existing output directory reuses the
# existing CAs and only creates what is missing (e.g. a new signer).
#
set -euo pipefail

usage() {
    cat <<EOF
Create a root CA, an intermediate CA and a signer for firmware (RAUC bundle) signing

Usage:
  $0 [options]

Options:
  --dir <path>                Output directory (default: ./pki)
  --org <name>                Organization name used in the certificates (default: "Example Org",
                              or the organization of the existing CA in --dir)
  --ca-name <name>            Common name (CN) of the root CA (default: "<org> Firmware Signing CA").
                              Must match the existing root CA in --dir, if there is one.
  --intermediate-name <name>  Common name (CN) of the intermediate CA
                              (default: "<org> Firmware Signing Intermediate CA")
  --new-intermediate          Create a new intermediate CA, even if one exists. The existing
                              intermediate CA is kept with a date suffix. Requires the root CA key.
  --signer <name>             Name of the signer (default: signer-1)
  --ca-days <days>            Validity of the root CA (default: 7300, 20 years)
  --intermediate-days <days>  Validity of the intermediate CA (default: 1825, 5 years)
  --signer-days <days>        Validity of the signer (default: 730, 2 years)
  --key-type <type>           Key type: rsa (RSA 4096) or ec (ECDSA P-256) (default: rsa)
  --write-env <file>          Append the RAUC_* settings to the given .env file (e.g. kas/.env)
  -h, --help                  Show this help

Examples:
  $0 --dir ./pki --org "Acme Inc." --write-env .env
  $0 --dir ./pki --org "Acme Inc." --ca-name "Acme Device Firmware CA" --write-env .env
  $0 --dir ./pki --signer signer-2      # issue a new signer with the existing intermediate CA
  $0 --dir ./pki --new-intermediate     # replace the intermediate CA (needs the root CA key)
EOF
}

OUT_DIR="./pki"
ORG=""
DEFAULT_ORG="Example Org"
CA_NAME=""
INT_NAME=""
NEW_INT=0
SIGNER="signer-1"
CA_DAYS=7300
INT_DAYS=1825
SIGNER_DAYS=730
KEY_TYPE="rsa"
ENV_FILE=""

while [ $# -gt 0 ]; do
    case "$1" in
        --dir) OUT_DIR="$2"; shift ;;
        --org) ORG="$2"; shift ;;
        --ca-name) CA_NAME="$2"; shift ;;
        --intermediate-name) INT_NAME="$2"; shift ;;
        --new-intermediate) NEW_INT=1 ;;
        --signer) SIGNER="$2"; shift ;;
        --ca-days) CA_DAYS="$2"; shift ;;
        --intermediate-days) INT_DAYS="$2"; shift ;;
        --signer-days) SIGNER_DAYS="$2"; shift ;;
        --key-type) KEY_TYPE="$2"; shift ;;
        --write-env) ENV_FILE="$2"; shift ;;
        -h|--help) usage; exit 0 ;;
        *) echo "Unknown argument: $1" >&2; usage >&2; exit 1 ;;
    esac
    shift
done

fail() {
    echo "$*" >&2
    exit 1
}

for value in "$CA_DAYS" "$INT_DAYS" "$SIGNER_DAYS"; do
    case "$value" in
        ''|*[!0-9]*|0) fail "Invalid number of days: '$value'" ;;
    esac
done

case "$KEY_TYPE" in
    rsa) KEY_ARGS=(-algorithm RSA -pkeyopt rsa_keygen_bits:4096) ;;
    ec) KEY_ARGS=(-algorithm EC -pkeyopt ec_paramgen_curve:P-256) ;;
    *) fail "Invalid --key-type: $KEY_TYPE (expected rsa or ec)" ;;
esac

command -v openssl >/dev/null 2>&1 || fail "openssl is required but was not found"

mkdir -p "$OUT_DIR"
OUT_DIR="$(cd "$OUT_DIR" && pwd)"
umask 077
mkdir -p "$OUT_DIR/private"

ROOT_CERT="$OUT_DIR/ca.cert.pem"
ROOT_KEY="$OUT_DIR/private/ca.key.pem"
INT_CERT="$OUT_DIR/intermediate.cert.pem"
INT_KEY="$OUT_DIR/private/intermediate.key.pem"
SIGNER_CERT="$OUT_DIR/$SIGNER.cert.pem"
SIGNER_KEY="$OUT_DIR/private/$SIGNER.key.pem"

# Print a field (e.g. organizationName, commonName) of a certificate's subject
cert_subject_field() {
    openssl x509 -in "$1" -noout -subject -nameopt multiline | sed -n "s/^ *$2 *= *//p"
}

cert_enddate() {
    openssl x509 -in "$1" -noout -enddate | sed 's/^notAfter=//'
}

# Fail if a certificate issued for <days> would be valid for longer than its issuer
check_within_issuer() {
    local issuer="$1" days="$2" what="$3" option="$4"
    if ! openssl x509 -in "$issuer" -noout -checkend "$((days * 86400))" >/dev/null; then
        fail "The $what would be valid for longer than its issuer, which expires on $(cert_enddate "$issuer"). Use a shorter $option, or replace the issuer first."
    fi
}

init_db() {
    mkdir -p "$1/certs"
    touch "$1/index.txt"
    [ -f "$1/serial" ] || openssl rand -hex 16 > "$1/serial"
}

# An existing root CA is reused, so the organization and name must match it.
# Otherwise new certificates would silently be issued by a different CA.
if [ -f "$ROOT_CERT" ]; then
    CA_ORG="$(cert_subject_field "$ROOT_CERT" organizationName)"
    CA_CN="$(cert_subject_field "$ROOT_CERT" commonName)"
    if [ -z "$ORG" ]; then
        ORG="$CA_ORG"
    elif [ "$ORG" != "$CA_ORG" ]; then
        fail "The existing CA in $OUT_DIR belongs to organization \"$CA_ORG\", not \"$ORG\".
Use another --dir to create a new CA for \"$ORG\", or remove $OUT_DIR to replace the existing CA."
    fi
    if [ -n "$CA_NAME" ] && [ "$CA_NAME" != "$CA_CN" ]; then
        fail "The existing CA in $OUT_DIR is named \"$CA_CN\", not \"$CA_NAME\".
Use another --dir to create a new CA, or remove $OUT_DIR to replace the existing CA."
    fi
fi
ORG="${ORG:-$DEFAULT_ORG}"
CA_NAME="${CA_NAME:-$ORG Firmware Signing CA}"
INT_NAME="${INT_NAME:-$ORG Firmware Signing Intermediate CA}"

if [ -f "$SIGNER_CERT" ]; then
    fail "Signer already exists: $SIGNER_CERT (use --signer to choose another name)"
fi

# Check the lifetimes of new certificates before creating anything, so a
# mistake doesn't leave a partially created PKI behind
if [ ! -f "$ROOT_CERT" ] && [ "$INT_DAYS" -gt "$CA_DAYS" ]; then
    fail "--intermediate-days ($INT_DAYS) must not be longer than --ca-days ($CA_DAYS)"
fi
if { [ ! -f "$INT_CERT" ] || [ "$NEW_INT" = 1 ]; } && [ "$SIGNER_DAYS" -gt "$INT_DAYS" ]; then
    fail "--signer-days ($SIGNER_DAYS) must not be longer than --intermediate-days ($INT_DAYS)"
fi

init_db "$OUT_DIR/db/root"
init_db "$OUT_DIR/db/intermediate"

cat > "$OUT_DIR/openssl.cnf" <<EOF
[ CA_root ]
dir             = $OUT_DIR
database        = \$dir/db/root/index.txt
new_certs_dir   = \$dir/db/root/certs
serial          = \$dir/db/root/serial
certificate     = \$dir/ca.cert.pem
private_key     = \$dir/private/ca.key.pem
default_md      = sha256
policy          = policy_any
email_in_dn     = no
copy_extensions = none
unique_subject  = no

[ CA_intermediate ]
dir             = $OUT_DIR
database        = \$dir/db/intermediate/index.txt
new_certs_dir   = \$dir/db/intermediate/certs
serial          = \$dir/db/intermediate/serial
certificate     = \$dir/intermediate.cert.pem
private_key     = \$dir/private/intermediate.key.pem
default_md      = sha256
policy          = policy_any
email_in_dn     = no
copy_extensions = none
unique_subject  = no

[ policy_any ]
organizationName = supplied
commonName       = supplied

[ req ]
distinguished_name = req_dn
prompt             = no

[ req_dn ]
CN = unused

[ v3_root ]
subjectKeyIdentifier   = hash
authorityKeyIdentifier = keyid:always
basicConstraints       = critical, CA:TRUE
keyUsage               = critical, keyCertSign, cRLSign

[ v3_intermediate ]
subjectKeyIdentifier   = hash
authorityKeyIdentifier = keyid:always
basicConstraints       = critical, CA:TRUE, pathlen:0
keyUsage               = critical, keyCertSign, cRLSign

[ v3_signer ]
subjectKeyIdentifier   = hash
authorityKeyIdentifier = keyid:always
basicConstraints       = critical, CA:FALSE
keyUsage               = critical, digitalSignature
EOF
export OPENSSL_CONF="$OUT_DIR/openssl.cnf"

# Create a key and a certificate signing request: new_csr <key> <csr> <subject>
new_csr() {
    openssl genpkey "${KEY_ARGS[@]}" -out "$1"
    openssl req -new -key "$1" -subj "$3" -out "$2"
}

#
# Root CA
#
if [ ! -f "$ROOT_CERT" ]; then
    echo "Creating root CA: $CA_NAME (valid for $CA_DAYS days)"
    new_csr "$ROOT_KEY" "$OUT_DIR/db/root/ca.csr.pem" "/O=$ORG/CN=$CA_NAME"
    openssl ca -batch -notext -name CA_root -selfsign -extensions v3_root -days "$CA_DAYS" \
        -keyfile "$ROOT_KEY" -in "$OUT_DIR/db/root/ca.csr.pem" -out "$ROOT_CERT" 2>/dev/null
else
    echo "Using existing root CA: $(cert_subject_field "$ROOT_CERT" commonName) (expires $(cert_enddate "$ROOT_CERT"))"
fi

#
# Intermediate CA
#
if [ ! -f "$INT_CERT" ] || [ "$NEW_INT" = 1 ]; then
    if [ ! -f "$ROOT_KEY" ]; then
        fail "The root CA private key ($ROOT_KEY) is needed to create an intermediate CA.
Restore it temporarily from your offline backup, and delete it again afterwards."
    fi
    check_within_issuer "$ROOT_CERT" "$INT_DAYS" "intermediate CA" "--intermediate-days"

    if [ -f "$INT_CERT" ]; then
        SUFFIX="$(date +%Y%m%d%H%M%S)"
        mv "$INT_CERT" "$OUT_DIR/intermediate.$SUFFIX.cert.pem"
        [ -f "$INT_KEY" ] && mv "$INT_KEY" "$OUT_DIR/private/intermediate.$SUFFIX.key.pem"
        echo "Kept the previous intermediate CA as intermediate.$SUFFIX.cert.pem"
    fi

    echo "Creating intermediate CA: $INT_NAME (valid for $INT_DAYS days)"
    new_csr "$INT_KEY" "$OUT_DIR/db/root/intermediate.csr.pem" "/O=$ORG/CN=$INT_NAME"
    openssl ca -batch -notext -name CA_root -extensions v3_intermediate -days "$INT_DAYS" \
        -in "$OUT_DIR/db/root/intermediate.csr.pem" -out "$INT_CERT" 2>/dev/null
else
    echo "Using existing intermediate CA: $(cert_subject_field "$INT_CERT" commonName) (expires $(cert_enddate "$INT_CERT"))"
fi

#
# Signer
#
if [ ! -f "$INT_KEY" ]; then
    fail "The intermediate CA private key ($INT_KEY) is needed to issue a signer."
fi
check_within_issuer "$INT_CERT" "$SIGNER_DAYS" "signer" "--signer-days"

echo "Creating signer: $ORG $SIGNER (valid for $SIGNER_DAYS days)"
new_csr "$SIGNER_KEY" "$OUT_DIR/db/intermediate/$SIGNER.csr.pem" "/O=$ORG/CN=$ORG $SIGNER"
openssl ca -batch -notext -name CA_intermediate -extensions v3_signer -days "$SIGNER_DAYS" \
    -in "$OUT_DIR/db/intermediate/$SIGNER.csr.pem" -out "$SIGNER_CERT" 2>/dev/null

chmod 644 "$OUT_DIR"/*.cert.pem

# Sanity check: the signer must chain to the root CA via the intermediate CA
openssl verify -CAfile "$ROOT_CERT" -untrusted "$INT_CERT" "$SIGNER_CERT" >/dev/null

ENV_SETTINGS="RAUC_KEY_FILE=$SIGNER_KEY
RAUC_CERT_FILE=$SIGNER_CERT
RAUC_INTERMEDIATE_FILE=$INT_CERT
RAUC_KEYRING_FILE=$ROOT_CERT"

if [ -n "$ENV_FILE" ]; then
    if [ -f "$ENV_FILE" ] && grep -qE '^RAUC_(KEY|CERT|INTERMEDIATE|KEYRING)_FILE=' "$ENV_FILE"; then
        echo "Not modifying $ENV_FILE as it already contains RAUC_* settings. Update them manually:" >&2
        echo "$ENV_SETTINGS" >&2
    else
        (umask 022; printf '\n# RAUC bundle signing\n%s\n' "$ENV_SETTINGS" >> "$ENV_FILE")
        echo "Added the RAUC_* settings to $ENV_FILE"
    fi
fi

cat <<EOF

Created the firmware signing PKI in $OUT_DIR

  Root CA:          $(cert_subject_field "$ROOT_CERT" commonName) (expires $(cert_enddate "$ROOT_CERT"))
  Intermediate CA:  $(cert_subject_field "$INT_CERT" commonName) (expires $(cert_enddate "$INT_CERT"))
  Signer:           $(cert_subject_field "$SIGNER_CERT" commonName) (expires $(cert_enddate "$SIGNER_CERT"))

Build with this signer by setting the following variables (for example in kas/.env):

$ENV_SETTINGS

Next steps:
  * Move private/ca.key.pem to offline storage. It is only needed to create a new intermediate CA.
  * Keep private/intermediate.key.pem in a secure place. It is only needed to issue new signers.
  * Store private/$SIGNER.key.pem securely (e.g. as a CI secret).
  * Devices only accept bundles signed by this PKI once they run an image built with
    RAUC_KEYRING_FILE=$ROOT_CERT
EOF
