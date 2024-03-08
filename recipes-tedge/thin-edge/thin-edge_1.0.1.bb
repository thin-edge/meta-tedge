SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV};name=thin-edge"
SRCREV_thin-edge = "64def4e4c5ed8e600d3ab090725ad3af57f1579f"
SRCREV_FORMAT = "thin-edge_init-manager"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

TEDGE_EXCLUDE = "c8y-log-plugin c8y-configuration-plugin tedge-log-plugin tedge-configuration-plugin sawtooth-publisher"

require thin-edge.inc
