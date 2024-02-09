do_install:append () {
    install -d "${D}${sysconfdir}/tedge-postinsts"
    install -m 0755 "${S}/configuration/package_scripts/_generated/c8y-remote-access-plugin/deb/postinst" "${D}${sysconfdir}/tedge-postinsts/"
    mv "${D}${sysconfdir}/tedge-postinsts/postinst" "${D}${sysconfdir}/tedge-postinsts/8-c8y-remote-access-plugin"
}

do_package:prepend () {
    work_dir = "%s"%(d.getVar('WORKDIR', True))
    package_name = "%s"%(d.getVar('PN', True))

    prerm_path = "%s/git/configuration/package_scripts/_generated/c8y-remote-access-plugin/deb/prerm"%(work_dir)

    with open(prerm_path, "r") as prerm:
        d.appendVar("pkg_prerm:%s"%(package_name), prerm.read())
}
