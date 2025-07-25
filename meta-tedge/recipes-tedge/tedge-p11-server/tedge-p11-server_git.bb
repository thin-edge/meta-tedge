SRCREV_tedge = "${AUTOREV}"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"
PV = "1.7+git${SRCPV}"
DEFAULT_PREFERENCE = "-1"

require tedge-p11-server.inc
