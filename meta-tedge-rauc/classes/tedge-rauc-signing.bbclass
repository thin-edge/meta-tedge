# Shared RAUC bundle signing configuration
#
# Used by both the bundle recipe (signing) and rauc-conf (the keyring installed
# on the device for verification), so that both sides always use the same
# certificate authority.
#
# Configure your own signer by setting the following variables, either via the
# environment (they are passed through by kas/config/rauc-signing.yaml) or in
# local.conf/site.conf. All values must be absolute paths.
#
#   RAUC_KEY_FILE       Private key of the signer (a PKCS#11 URI is also accepted)
#   RAUC_CERT_FILE      Certificate of the signer
#   RAUC_KEYRING_FILE   CA certificate(s) which the device trusts. This file is
#                       installed into the image and used to verify bundles
#                       during an update. It can contain multiple PEM
#                       certificates, which is useful when rotating CAs.
#   RAUC_INTERMEDIATE_FILE
#                       Optional intermediate CA certificate(s) which are
#                       embedded in the bundle, so the device can build the
#                       chain from the signer to the root CA in the keyring.
#
# The device checks the certificate validity at the bundle's signing time
# instead of the current time (RAUC's use-bundle-signing-time), so that
# short-lived signers don't make existing bundles invalid when they expire, and
# devices without a real-time clock can verify bundles before NTP has synced.
# Set TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME = "false" to use the device's clock
# instead.
#
# If none of the variables are set, the public development PKI shipped in this
# layer is used. Development keys are public, so anyone can sign a bundle
# which a device with the development keyring will accept. Never use them for
# production devices.

TEDGE_RAUC_DEV_PKI_DIR ??= "${TEDGE_RAUC_LAYERDIR}/files/pki/development"
TEDGE_RAUC_DEV_KEY_FILE = "${TEDGE_RAUC_DEV_PKI_DIR}/development-1.key.pem"
TEDGE_RAUC_DEV_CERT_FILE = "${TEDGE_RAUC_DEV_PKI_DIR}/development-1.cert.pem"
TEDGE_RAUC_DEV_KEYRING_FILE = "${TEDGE_RAUC_DEV_PKI_DIR}/ca.cert.pem"

RAUC_KEY_FILE ?= "${TEDGE_RAUC_DEV_KEY_FILE}"
RAUC_CERT_FILE ?= "${TEDGE_RAUC_DEV_CERT_FILE}"
RAUC_KEYRING_FILE ?= "${TEDGE_RAUC_DEV_KEYRING_FILE}"
RAUC_INTERMEDIATE_FILE ??= ""

TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME ??= "true"

# meta-rauc-raspberrypi sets RAUC_KEY_FILE, RAUC_CERT_FILE and
# RAUC_KEYRING_FILE in its layer.conf to its own copy of the same development
# keys, so compare the file contents and not only the paths.
def tedge_rauc_is_dev_file(d, var):
    import os
    value = d.getVar(var) or ""
    dev_value = d.getVar("TEDGE_RAUC_DEV_" + var[len("RAUC_"):]) or ""
    if os.path.realpath(value) == os.path.realpath(dev_value):
        return True
    try:
        with open(value, "rb") as a, open(dev_value, "rb") as b:
            return a.read().strip() == b.read().strip()
    except OSError:
        return False

def tedge_rauc_using_dev_signer(d):
    return "1" if tedge_rauc_is_dev_file(d, "RAUC_KEY_FILE") else "0"

# Arguments to embed the intermediate CA certificate(s) in the bundle
def tedge_rauc_intermediate_args(d):
    value = d.getVar("RAUC_INTERMEDIATE_FILE") or ""
    return " ".join("--intermediate=%s" % f for f in value.split())

python __anonymous() {
    import os

    variables = ("RAUC_KEY_FILE", "RAUC_CERT_FILE", "RAUC_KEYRING_FILE")

    # An empty value (e.g. an unset CI secret exported as "") falls back to
    # the development PKI instead of failing later with an obscure error
    for var in variables:
        if not (d.getVar(var) or "").strip():
            d.setVar(var, d.getVar("TEDGE_RAUC_DEV_" + var[len("RAUC_"):]))

    for var in variables:
        value = d.getVar(var)
        if var == "RAUC_KEY_FILE" and value.startswith("pkcs11:"):
            continue
        if not os.path.isabs(value):
            bb.fatal("%s must be an absolute path, got: %s" % (var, value))
        # Normalize, as rauc-conf unpacks the keyring to ${UNPACKDIR}/${RAUC_KEYRING_FILE}
        d.setVar(var, os.path.normpath(value))

    # Optional, and may contain multiple (space separated) files
    intermediates = (d.getVar("RAUC_INTERMEDIATE_FILE") or "").split()
    for value in intermediates:
        if not value.startswith("pkcs11:") and not os.path.isabs(value):
            bb.fatal("RAUC_INTERMEDIATE_FILE must be an absolute path, got: %s" % value)
    d.setVar("RAUC_INTERMEDIATE_FILE", " ".join(
        v if v.startswith("pkcs11:") else os.path.normpath(v) for v in intermediates))

    if d.getVar("TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME") not in ("true", "false"):
        bb.fatal("TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME must be 'true' or 'false', got: %s"
                 % d.getVar("TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME"))

    dev = {var: tedge_rauc_is_dev_file(d, var) for var in variables}
    if dev["RAUC_KEY_FILE"] != dev["RAUC_CERT_FILE"]:
        bb.fatal("RAUC_KEY_FILE and RAUC_CERT_FILE must be set together. "
                 "Only one of them points to a custom signer.")
    if not dev["RAUC_KEY_FILE"] and dev["RAUC_KEYRING_FILE"]:
        bb.fatal("A custom RAUC signer is configured (RAUC_KEY_FILE/RAUC_CERT_FILE) "
                 "but RAUC_KEYRING_FILE still points to the development CA. "
                 "Set RAUC_KEYRING_FILE to the CA certificate which issued your signer certificate.")
}
