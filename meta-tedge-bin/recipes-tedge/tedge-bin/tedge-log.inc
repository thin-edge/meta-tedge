do_install:append () {
    install -d ${D}${datadir}/tedge/log-plugins

    install -m 0755 ${WORKDIR}/git/configuration/contrib/log-plugins/dmesg ${D}${datadir}/tedge/log-plugins/
    install -m 0755 ${WORKDIR}/git/configuration/contrib/log-plugins/file ${D}${datadir}/tedge/log-plugins/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0755 ${WORKDIR}/git/configuration/contrib/log-plugins/journald ${D}${datadir}/tedge/log-plugins/
    fi
}

FILES:${PN} += "\
    ${datadir}/tedge/log-plugins/dmesg \
    ${datadir}/tedge/log-plugins/file \
"
FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${datadir}/tedge/log-plugins/journald', '', d)}"
