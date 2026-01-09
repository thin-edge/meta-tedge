TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install:append () {
    install -d ${D}${TEDGE_CONFIG_DIR}/flows
    install -d ${D}${datadir}/tedge/flows
}

FILES:${PN} += "\ 
    ${TEDGE_CONFIG_DIR}/flows \
    ${datadir}/tedge/flows \
"
