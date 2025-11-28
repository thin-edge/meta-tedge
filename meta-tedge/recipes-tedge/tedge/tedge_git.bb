SRCREV_tedge = "${AUTOREV}"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"
PV = "1.7+git${SRCPV}"
DEFAULT_PREFERENCE = "-1"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
require tedge-diag.inc

CARGO_BUILD_FLAGS:append = " --bin tedge"
DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

SRC_URI += "\
file://0001-Cargo.toml-change-rev-of-rquickjs.patch \
"
