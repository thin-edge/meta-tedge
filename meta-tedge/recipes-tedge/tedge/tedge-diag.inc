do_install:append () {
    install -d ${D}${datadir}/tedge
    install -d ${D}${datadir}/tedge/diag-plugins
    install -m 0755 ${S}/configuration/contrib/diag-plugins/* ${D}${datadir}/tedge/diag-plugins/
}

FILES:${PN} += "\ 
    ${datadir}/tedge \
    ${datadir}/tedge/diag-plugins/* \
"
