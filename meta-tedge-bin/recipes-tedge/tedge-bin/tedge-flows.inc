TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install:append () {
    install -d ${D}${TEDGE_CONFIG_DIR}/mappers/local/flows
    chown -R tedge:tedge "${D}${TEDGE_CONFIG_DIR}"/mappers
}

FILES:${PN} += "\ 
    ${TEDGE_CONFIG_DIR}/mappers/local/flows \
"
