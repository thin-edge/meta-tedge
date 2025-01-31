SRC_URI += "git://git@github.com/thin-edge/c8y-command-plugin.git;protocol=https;branch=main"
SRCREV = "23af5d1f686ef059f928c694bc2b5c914de18b92"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=db2da9b0c3565e9eb835818160c34022 \
"

inherit allarch

PV = "1.0.0~rc2+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS += " tedge"
RDEPENDS:${PN} += " tedge jq"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install () {
    install -d "${D}${bindir}"
    install -d "${D}${TEDGE_CONFIG_DIR}/operations/c8y"
    install -d "${D}${sysconfdir}/tedge-command-plugin"

    install -m 0644 "${S}/src/tedge-command-plugin/c8y_Command.template" "${D}${TEDGE_CONFIG_DIR}/operations/c8y/"
    install -m 0644 "${S}/src/tedge-command-plugin/shell_execute.toml" "${D}${TEDGE_CONFIG_DIR}/operations/"
    install -m 0755 "${S}/src/tedge-command-plugin/shell_execute.sh" "${D}${bindir}/"
    install -m 0644 "${S}/src/env" "${D}${sysconfdir}/tedge-command-plugin/"
}

FILES:${PN} += " \
    ${TEDGE_CONFIG_DIR}/operations/c8y/c8y_Command.template \
    ${TEDGE_CONFIG_DIR}/operations/shell_execute.toml \
    ${bindir}/shell_execute.sh \
    ${sysconfdir}/tedge-command-plugin/env \
"
