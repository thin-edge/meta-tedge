do_install:append () {
    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-agent.service" "${D}${systemd_system_unitdir}"
}

FILES:tedge-agent += "${systemd_system_unitdir}/tedge-agent.service"

