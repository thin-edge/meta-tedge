SRCREV_tedge = "${AUTOREV}"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"
PV = "1.8+git${SRCPV}"
DEFAULT_PREFERENCE = "-1"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
require tedge-config.inc
require tedge-flows.inc
