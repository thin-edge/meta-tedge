
DESCRIPTION = "RAUC bundle generator"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

inherit bundle

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


RAUC_KEY_FILE ?= "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE ?= "${THISDIR}/files/development-1.cert.pem"
