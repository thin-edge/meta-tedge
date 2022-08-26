inherit systemd

SRC_URI += " \
    file://0001-Cargo.toml-do-not-abort-on-panic.patch \
    file://postinst-tedge-mapper.service \
"

do_install:append(){
    install -d ${D}/${sbindir}/tedge-mapper
    install -m 0755 ${S}/configuration/debian/tedge_mapper/postinst ${D}/${sbindir}/tedge-mapper

    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-az.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-c8y.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-mapper-collectd.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/postinst-tedge-mapper.service" "${D}${systemd_system_unitdir}"
}

FILES:${PN} += " ${systemd_system_unitdir}/tedge-mapper-collectd.service ${systemd_system_unitdir}/tedge-mapper-az.service ${systemd_system_unitdir}/tedge-mapper-c8y.service ${systemd_system_unitdir}/postinst-tedge-mapper.service"

NATIVE_SYSTEMD_SUPPORT = "1"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "postinst-tedge-mapper.service"
