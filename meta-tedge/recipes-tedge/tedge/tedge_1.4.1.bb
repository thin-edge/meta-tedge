SRCREV_tedge = "b48c19c148d9f71cda434b0f8b76c0a115fd5fb2"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
