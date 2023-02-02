do_install:append () {
    install -d ${D}${sysconfdir}/tedge/sm-plugins/ 
    install -m 0755 ${WORKDIR}/build/target/${TARGET_ARCH}-poky-${TARGET_OS}/release/tedge_dummy_plugin ${D}${sysconfdir}/tedge/sm-plugins/dummy
    rm -f ${D}${bindir}/tedge_dummy_plugin
}
