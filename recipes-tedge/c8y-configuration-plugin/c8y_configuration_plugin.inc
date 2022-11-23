inherit systemd

SRC_URI += " \
    file://0001-Cargo.toml-do-not-abort-on-panic.patch \
    file://postinst-c8y-configuration-plugin.service \
"

do_install:append(){
    install -d ${D}${sbindir}/c8y-configuration-plugin
    install -m 0755 ${S}/configuration/debian/c8y_configuration_plugin/postinst ${D}${sbindir}/c8y-configuration-plugin

    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${S}/configuration/init/systemd/c8y-configuration-plugin.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/postinst-c8y-configuration-plugin.service" "${D}${systemd_system_unitdir}"
}

FILES:${PN} += " ${systemd_system_unitdir}/c8y-configuration-plugin.service ${systemd_system_unitdir}/postinst-c8y-configuration-plugin.service"

SYSTEMD_SERVICE:${PN} = "postinst-c8y-configuration-plugin.service"