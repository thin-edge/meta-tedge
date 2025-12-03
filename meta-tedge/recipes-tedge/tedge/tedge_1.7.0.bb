SRCREV_tedge = "404a0c54d811ae107c43b574a0829cc8fa5153e1"
SRCREV_tedge-services = "f495b82f0002be81a7778483003139a223a508a1"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc

# build tedge without tedge-flows due to an issue with rquickjs and bindgen
CARGO_BUILD_FLAGS:append = " --bin tedge --features aws,azure,c8y --no-default-features "
