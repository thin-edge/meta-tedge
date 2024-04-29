SRCREV_tedge = "64def4e4c5ed8e600d3ab090725ad3af57f1579f"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
