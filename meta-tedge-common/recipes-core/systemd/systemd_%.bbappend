do_install:append () {
    # TODO: Only run if being used in readonly mode
    if [ -f ${D}${systemd_system_unitdir}/systemd-timedated.service ]; then
        sed -i -e 's|PrivateTmp=yes|PrivateTmp=no|g' ${D}${systemd_system_unitdir}/systemd-timedated.service
    fi
    if [ -f ${D}${systemd_system_unitdir}/systemd-timesyncd.service ]; then
        sed -i -e 's|PrivateTmp=yes|PrivateTmp=no|g' ${D}${systemd_system_unitdir}/systemd-timesyncd.service
    fi
}
