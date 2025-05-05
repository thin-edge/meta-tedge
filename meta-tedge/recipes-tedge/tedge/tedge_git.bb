SRCREV_tedge = "${AUTOREV}"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"
PV = "1.5+git${SRCPV}"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
