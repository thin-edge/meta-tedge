do_install:append () {
    install -d ${D}${sysconfdir}/tedge/sm-plugins/ 
    install -m 0755 ${WORKDIR}/image/usr/bin/tedge_apama_plugin ${D}${sysconfdir}/tedge/sm-plugins/apama
}
