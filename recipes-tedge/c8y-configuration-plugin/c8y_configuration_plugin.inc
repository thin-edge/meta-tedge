inherit systemd

SRC_URI += " \
    file://0001-Cargo.toml-do-not-abort-on-panic.patch \
"

do_install:append () {
    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${S}/configuration/init/systemd/c8y-configuration-plugin.service" "${D}${systemd_system_unitdir}"

    install -d "${D}${sysconfdir}/tedge-postinsts"
    install -m 0755 "${S}/configuration/debian/c8y_configuration_plugin/postinst" "${D}${sysconfdir}/tedge-postinsts/"
    mv "${D}${sysconfdir}/tedge-postinsts/postinst" "${D}${sysconfdir}/tedge-postinsts/7-c8y_configuration_plugin"
}

do_package:prepend () {
    work_dir = "%s"%(d.getVar('WORKDIR', True))
    package_name = "%s"%(d.getVar('PN', True))

    prerm_path = "%s/git/configuration/debian/c8y_configuration_plugin/prerm"%(work_dir)

    with open(prerm_path, "r") as prerm:
        d.appendVar("pkg_prerm:%s"%(package_name), prerm.read())
}

FILES:${PN} += "${systemd_system_unitdir}/c8y-configuration-plugin.service"
