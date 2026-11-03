
DESCRIPTION = "RAUC bundle generator"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

inherit bundle tedge-rauc-signing

RAUC_BUNDLE_COMPATIBLE ?= "${MACHINE}"
RAUC_BUNDLE_VERSION ?= "v20200703"
RAUC_BUNDLE_DESCRIPTION ?= "RAUC thin-edge.io Bundle"

RAUC_BUNDLE_FORMAT = "verity"

RAUC_BUNDLE_SLOTS = "rootfs" 
RAUC_SLOT_rootfs ?= "core-image-tedge-rauc"
RAUC_SLOT_rootfs[fstype] = "ext4"
RAUC_SLOT_rootfs[adaptive] ?= "block-hash-index"

# Hooks
# see: https://github.com/rauc/meta-rauc/issues/185
SRC_URI += " file://hook.sh"
RAUC_BUNDLE_HOOKS[file] = "hook.sh"
RAUC_SLOT_rootfs[hooks] = "post-install"

# Signing: RAUC_KEY_FILE, RAUC_CERT_FILE and RAUC_KEYRING_FILE are set by
# tedge-rauc-signing.bbclass (development PKI by default).
#
# Verify the bundle against the same keyring which is installed on the device
# (see rauc-conf.bbappend). This fails the build if the signer certificate
# does not chain to RAUC_KEYRING_FILE, instead of producing an image which
# rejects all future updates.
BUNDLE_ARGS += "--signing-keyring=${RAUC_KEYRING_FILE} ${@tedge_rauc_intermediate_args(d)}"
do_bundle[file-checksums] += "${RAUC_KEYRING_FILE}:False ${@' '.join(f + ':False' for f in (d.getVar('RAUC_INTERMEDIATE_FILE') or '').split() if not f.startswith('pkcs11:'))}"

TEDGE_RAUC_USING_DEV_SIGNER = "${@tedge_rauc_using_dev_signer(d)}"

do_bundle:prepend() {
    if [ "${TEDGE_RAUC_USING_DEV_SIGNER}" = "1" ]; then
        bbwarn "The RAUC bundle is signed with the public development key (${RAUC_KEY_FILE}). Anyone can sign bundles which devices with the development keyring accept, so do not use this image in production. Set RAUC_KEY_FILE, RAUC_CERT_FILE and RAUC_KEYRING_FILE to use your own CA (see kas/README.md)."
    fi
}
