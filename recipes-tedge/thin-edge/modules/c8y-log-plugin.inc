do_install:append () {
    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/c8y-log-plugin.service" "${D}${systemd_system_unitdir}"
    
    install -d "${D}${sysconfdir}/tedge-postinsts"
    install -m 0755 "${S}/configuration/debian/c8y-log-plugin/postinst" "${D}${sysconfdir}/tedge-postinsts/"
    mv "${D}${sysconfdir}/tedge-postinsts/postinst" "${D}${sysconfdir}/tedge-postinsts/6-c8y-log-plugin"
}

do_package:prepend () {
    work_dir = "%s"%(d.getVar('WORKDIR', True))
    package_name = "%s"%(d.getVar('PN', True))

    prerm_path = "%s/git/configuration/debian/c8y-log-plugin/prerm"%(work_dir)

    with open(prerm_path, "r") as prerm:
        d.appendVar("pkg_prerm:%s"%(package_name), prerm.read())
}

FILES:c8y-log-plugin += "${systemd_system_unitdir}/c8y-log-plugin.service"