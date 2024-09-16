SRCREV_tedge = "61fdb06b3407c4d744cd4a8cec94df2b7eab97de"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
