RDEPENDS:tedge = "mosquitto ca-certificates glibc-utils sudo"

SRC_URI += " \
    file://run-tedge-postinsts.service \
    file://run-tedge-postinsts \
"

do_install:append () {
    install -d "${D}${sbindir}"
    install -m 0755 "${WORKDIR}/run-tedge-postinsts" "${D}${sbindir}"

    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/run-tedge-postinsts.service" "${D}${systemd_system_unitdir}/"

    install -d "${D}${sysconfdir}/tedge-postinsts"
    install -m 0755 "${S}/configuration/debian/tedge/preinst" "${D}${sysconfdir}/tedge-postinsts/"
    install -m 0755 "${S}/configuration/debian/tedge/postinst" "${D}${sysconfdir}/tedge-postinsts/"
    mv "${D}${sysconfdir}/tedge-postinsts/preinst" "${D}${sysconfdir}/tedge-postinsts/1-tedge"
    mv "${D}${sysconfdir}/tedge-postinsts/postinst" "${D}${sysconfdir}/tedge-postinsts/2-tedge"
}

do_package:prepend () {
    work_dir = "%s"%(d.getVar('WORKDIR', True))
    package_name = "%s"%(d.getVar('PN', True))

    postrm_path = "%s/git/configuration/debian/tedge/postrm"%(work_dir)

    with open(postrm_path, 'r') as postrm:
        d.appendVar("pkg_postrm:%s"%(package_name), postrm.read())
}

pkg_postinst_ontarget:tedge () {
    chmod -R 1777 /run/lock 
    sed -i 's/d \/run\/lock 0755 root root -/d \/run\/lock 1777 root root -/' /usr/lib/tmpfiles.d/legacy.conf
}

FILES:tedge += " \
    ${systemd_system_unitdir}/run-tedge-postinsts.service \
    ${sbindir}/run-tedge-postinsts \
"

SYSTEMD_SERVICE:tedge = "run-tedge-postinsts.service"
SYSTEMD_PACKAGES += "tedge"
