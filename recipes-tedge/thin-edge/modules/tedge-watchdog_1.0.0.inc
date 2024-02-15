do_install:append () {
    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-watchdog.service" "${D}${systemd_system_unitdir}"
}

FILES:tedge-watchdog += "${systemd_system_unitdir}/tedge-watchdog.service"
