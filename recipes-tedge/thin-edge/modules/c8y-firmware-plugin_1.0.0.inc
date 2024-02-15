do_install:append () {
    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/c8y-firmware-plugin.service" "${D}${systemd_system_unitdir}"
}

FILES:c8y-firmware-plugin += "${systemd_system_unitdir}/c8y-firmware-plugin.service"
