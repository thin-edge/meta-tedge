inherit systemd

SRC_URI += " \
    file://postinst-tedge-mapper.service \
"

do_install:append() {
    install -d "${D}${sbindir}/tedge-mapper"
    install -m 0755 "${S}/configuration/debian/tedge_mapper/postinst" "${D}${sbindir}/tedge-mapper"

    install -d "${D}${sysconfdir}/tedge/contrib/collectd"
    install -m 0644 "${S}/configuration/contrib/collectd/collectd.conf" "${D}${sysconfdir}/tedge/contrib/collectd"
    
    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d "${D}${systemd_system_unitdir}"
    fi
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-az.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-c8y.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-collectd.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/postinst-tedge-mapper.service" "${D}${systemd_system_unitdir}"
}

FILES:tedge-mapper += " ${systemd_system_unitdir}/tedge-mapper-collectd.service ${systemd_system_unitdir}/tedge-mapper-az.service ${systemd_system_unitdir}/tedge-mapper-c8y.service ${systemd_system_unitdir}/postinst-tedge-mapper.service"

SYSTEMD_SERVICE:tedge-mapper = "postinst-tedge-mapper.service"
