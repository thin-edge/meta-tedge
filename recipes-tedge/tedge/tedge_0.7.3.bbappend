inherit systemd

SRC_URI += " \
    file://0001-Cargo.toml-do-not-abort-on-panic.patch \
    file://postinst-tedge.service \
"

do_install:append(){
    install -d ${D}/${sbindir}/tedge
    install -m 0755 ${S}/configuration/debian/tedge/preinst ${D}/${sbindir}/tedge
    install -m 0755 ${S}/configuration/debian/tedge/postinst ${D}/${sbindir}/tedge

    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${WORKDIR}/postinst-tedge.service" "${D}${systemd_system_unitdir}"
}

FILES:${PN} += " ${systemd_system_unitdir}/postinst-tedge.service"

NATIVE_SYSTEMD_SUPPORT = "1"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "postinst-tedge.service"