SRCREV_tedge = "${AUTOREV}"
SRCREV_tedge-services = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
S = "${WORKDIR}/git"
PV = "1.7+git${SRCPV}"
DEFAULT_PREFERENCE = "-1"

TEDGE_EXCLUDE = "c8y-firmware-plugin"

require tedge.inc
require tedge-diag.inc

# build tedge without tedge-flows due to an issue with rquickjs and bindgen
CARGO_BUILD_FLAGS:append = " --bin tedge "
DEPENDS += "clang-native"

# Remove unsupported GCC flag causing cc-rs crates to fail
CFLAGS:remove = "-fcanon-prefix-map"
BUILD_CFLAGS:remove = "-fcanon-prefix-map"
TARGET_CFLAGS:remove = "-fcanon-prefix-map"
CARGO_BUILD_TARGET_CFLAGS:remove = "-fcanon-prefix-map"

export BINDGEN_EXTRA_CLANG_ARGS
target = "${@d.getVar('TARGET_SYS').replace('-', ' ')}"
BINDGEN_EXTRA_CLANG_ARGS = "${@bb.utils.contains('target', 'arm', \
                              '--target=${RUST_TARGET_SYS} --sysroot=${WORKDIR}/recipe-sysroot -I${WORKDIR}/recipe-sysroot/usr/include -mfloat-abi=hard', \
                              '--target=${RUST_TARGET_SYS} --sysroot=${WORKDIR}/recipe-sysroot -I${WORKDIR}/recipe-sysroot/usr/include', \
                              d)}"

# RUST_HOST_SYS and RUST_TARGET_SYS
