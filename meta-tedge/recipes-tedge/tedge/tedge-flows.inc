TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install:append () {
    install -d ${D}${TEDGE_CONFIG_DIR}/mappers/local/flows
    chown -R tedge:tedge "${D}${TEDGE_CONFIG_DIR}"/mappers
}

FILES:${PN} += "\ 
    ${TEDGE_CONFIG_DIR}/mappers/local/flows \
"

# Required configurations to enable rquickjs bindgen
DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"
inherit pkgconfig

DEPENDS += " \
    clang-native \
"

# Path to find libclang.so for the target system
export LIBCLANG_PATH = "${STAGING_LIBDIR_NATIVE}"

# Note: TARGET_SYS is not equals to RUST_TARGET_SYS. For example, they can be like below.
#   TARGET_SYS="arm-poky-linux-gnueabi"
#   RUST_TARGET_SYS="armv7-poky-linux-gnueabihf"
# It seems rquickjs build.rs attempts bindgen for both host and target.
# Use the target-specific variable to prevent sysroot paths from breaking the host-side compilation.
python() {
    # A target-specific BINDGEN_EXTRA_CLANG_ARGS requires underscore instead of hyphen as the system name
    var_name = "BINDGEN_EXTRA_CLANG_ARGS_" + d.getVar('RUST_TARGET_SYS').replace('-', '_')
    var_value = "-I${RECIPE_SYSROOT}/usr/include --target=${RUST_TARGET_SYS}"
    d.setVar(var_name, var_value)
    d.setVarFlag(var_name, 'export', '1')
}

# Force the bindgen feature enabled
CARGO_BUILD_FLAGS:append = " --features rquickjs/bindgen"
