SRCREV_tedge = "0866b9b9d7571f1479571d0600a42923a9b2980f"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
