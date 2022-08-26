inherit systemd

SRC_URI += " \
    file://0001-Cargo.toml-do-not-abort-on-panic.patch \
    file://postinst-c8y-log-plugin.service \
"

do_install:append(){
    install -d ${D}/${sbindir}/c8y-log-plugin
    install -m 0755 ${S}/configuration/debian/c8y_log_plugin/postinst ${D}/${sbindir}/c8y-log-plugin

    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${S}/configuration/init/systemd/c8y-log-plugin.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/postinst-c8y-log-plugin.service" "${D}${systemd_system_unitdir}"
}

FILES:${PN} += " ${systemd_system_unitdir}/c8y-log-plugin.service ${systemd_system_unitdir}/postinst-c8y-log-plugin.service"

NATIVE_SYSTEMD_SUPPORT = "1"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "postinst-c8y-log-plugin.service"