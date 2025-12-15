do_install:append () {
    install -d ${D}${datadir}/tedge/config-plugins
    install -m 0755 ${S}/configuration/contrib/config-plugins/file ${D}${datadir}/tedge/config-plugins/
}

FILES:${PN} += "\
    ${datadir}/tedge/config-plugins/file \
"
