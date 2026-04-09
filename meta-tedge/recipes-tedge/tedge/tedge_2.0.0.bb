SRCREV_tedge = "1dc8a3fdfb51532005dfee8fcca7a117a8a1cb85"
SRCREV_tedge-services = "056c9bdbaf77435c9bb3a613a5e2e67100432283"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
require tedge-config.inc
require tedge-flows.inc
