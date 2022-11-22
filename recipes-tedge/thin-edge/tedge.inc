inherit systemd

SRC_URI += " \
    file://postinst-tedge.service \
"

do_install:append() {
    install -d ${D}${sbindir}/tedge
    install -m 0755 ${S}/configuration/debian/tedge/preinst ${D}${sbindir}/tedge
    install -m 0755 ${S}/configuration/debian/tedge/postinst ${D}${sbindir}/tedge

    if [ ! -d "${D}${systemd_system_unitdir}" ]; then
        install -d ${D}${systemd_system_unitdir}
    fi
    install -m 0644 "${WORKDIR}/postinst-tedge.service" "${D}${systemd_system_unitdir}"
}

pkg_postinst_ontarget:tedge() {
    chmod -R 1777 /run/lock
}

FILES:tedge += " ${systemd_system_unitdir}/postinst-tedge.service"

SYSTEMD_SERVICE:tedge = "postinst-tedge.service"

RDEPENDS:tedge = "mosquitto ca-certificates glibc-utils sudo"
