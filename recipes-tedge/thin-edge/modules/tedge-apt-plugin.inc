do_install:append () {
    install -d ${D}${sysconfdir}/tedge/sm-plugins/
    install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/tedge-apt-plugin ${D}${sysconfdir}/tedge/sm-plugins/apt
    rm -f ${D}${bindir}/tedge-apt-plugin
}
