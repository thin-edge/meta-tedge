inherit systemd

do_install:append(){
    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${S}/configuration/init/systemd/tedge-watchdog.service" "${D}${systemd_system_unitdir}"
}

FILES:tedge-watchdog += " ${systemd_system_unitdir}/tedge-watchdog.service"
