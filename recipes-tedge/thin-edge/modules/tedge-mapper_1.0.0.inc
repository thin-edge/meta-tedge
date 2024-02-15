RDEPENDS:tedge-mapper = "collectd"

do_install:append () {
    install -d "${D}${sysconfdir}/tedge/contrib/collectd"
    install -m 0644 "${S}/configuration/contrib/collectd/collectd.conf" "${D}${sysconfdir}/tedge/contrib/collectd"

    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-az.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-c8y.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-collectd.service" "${D}${systemd_system_unitdir}"
}

FILES:tedge-mapper += " \
    ${systemd_system_unitdir}/tedge-mapper-collectd.service \
    ${systemd_system_unitdir}/tedge-mapper-az.service \
    ${systemd_system_unitdir}/tedge-mapper-c8y.service \
"
