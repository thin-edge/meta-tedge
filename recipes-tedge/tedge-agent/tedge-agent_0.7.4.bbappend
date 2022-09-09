inherit systemd

SRC_URI += " \
    file://0001-Cargo.toml-do-not-abort-on-panic.patch \
    file://postinst-tedge-agent.service \
"

do_install:append(){
    install -d ${D}/${sbindir}/tedge-agent
    install -m 0755 ${S}/configuration/debian/tedge_agent/postinst ${D}/${sbindir}/tedge-agent

    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${S}/configuration/init/systemd/tedge-agent.service" "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/postinst-tedge-agent.service" "${D}${systemd_system_unitdir}"
}

FILES:${PN} += " ${systemd_system_unitdir}/tedge-agent.service ${systemd_system_unitdir}/postinst-tedge-agent.service"

NATIVE_SYSTEMD_SUPPORT = "1"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "postinst-tedge-agent.service"
