inherit systemd

do_install:append () {
    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/tedge-watchdog.service" "${D}${systemd_system_unitdir}"

    install -d "${D}${sysconfdir}/tedge-postinsts"
    install -m 0755 "${S}/configuration/debian/tedge_watchdog/postinst" "${D}${sysconfdir}/tedge-postinsts"
    mv "${D}${sysconfdir}/tedge-postinsts/postinst" "${D}${sysconfdir}/tedge-postinsts/5-tedge_watchdog"
}

do_package:prepend () {
    work_dir = "%s"%(d.getVar('WORKDIR', True))
    package_name = "%s"%(d.getVar('PN', True))

    prerm_path = "%s/git/configuration/debian/tedge_watchdog/prerm"%(work_dir)

    with open(prerm_path, "r") as prerm:
        d.appendVar("pkg_prerm:%s"%(package_name), prerm.read())
}

FILES:tedge-watchdog += "${systemd_system_unitdir}/tedge-watchdog.service"
