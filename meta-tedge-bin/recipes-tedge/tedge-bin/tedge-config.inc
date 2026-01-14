do_install:append () {
    install -d ${D}${datadir}/tedge/config-plugins
    install -m 0755 ${WORKDIR}/git/configuration/contrib/config-plugins/file ${D}${datadir}/tedge/config-plugins/
}

FILES:${PN} += "\
    ${datadir}/tedge/config-plugins/file \
"
