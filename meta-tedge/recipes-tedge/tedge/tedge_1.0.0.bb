SRCREV_tedge = "9cf69044581fc4046d52009f909b41af40124a95"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

TEDGE_EXCLUDE = "c8y-log-plugin c8y-configuration-plugin tedge-log-plugin tedge-configuration-plugin sawtooth-publisher"

require tedge.inc
