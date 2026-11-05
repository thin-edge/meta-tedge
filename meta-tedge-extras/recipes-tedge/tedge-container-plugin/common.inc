inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'update-rc.d', '', d)}

SYSTEMD_AUTO_ENABLE ?= "enable"
SYSTEMD_SERVICE:${PN} = "tedge-container-plugin.service"

do_install:append () {
    # Service files
    SED_PATTERN=$(printf -- 's|--config .*|--config "%s"|g' "${TEDGE_RUNTIME_CONFIG_DIR}/plugins/tedge-container-plugin.toml")
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/src/${GO_IMPORT}/packaging/services/systemd/tedge-container-plugin.service ${D}${systemd_system_unitdir}
        sed -i "$SED_PATTERN" ${D}${systemd_system_unitdir}/tedge-container-plugin.service

	elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${S}/src/${GO_IMPORT}/packaging/services/init.d/service.init -T ${D}${sysconfdir}/init.d/tedge-container-plugin
        sed -i "$SED_PATTERN" ${D}${sysconfdir}/init.d/tedge-container-plugin
    fi

    # configuration and change default directory
    install -d ${D}${TEDGE_CONFIG_DIR}/plugins
    install -m 0644 ${S}/src/${GO_IMPORT}/packaging/config.toml -T ${D}${TEDGE_CONFIG_DIR}/plugins/tedge-container-plugin.toml
    sed -i 's|^data_dir =.*|data_dir = ["/data/tedge-container-plugin", "/var/tedge-container-plugin"]|g' ${D}${TEDGE_CONFIG_DIR}/plugins/tedge-container-plugin.toml

    # Rename the binary
    mv ${D}${bindir}/tedge-container-plugin ${D}${bindir}/tedge-container

    # Create symlinks to enable tedge sm-plugin interface
    install -d ${D}${TEDGE_CONFIG_DIR}/sm-plugins
    ln --relative --symbolic ${D}${bindir}/tedge-container ${D}${TEDGE_CONFIG_DIR}/sm-plugins/container
    ln --relative --symbolic ${D}${bindir}/tedge-container ${D}${TEDGE_CONFIG_DIR}/sm-plugins/container-group
    ln --relative --symbolic ${D}${bindir}/tedge-container ${D}${TEDGE_CONFIG_DIR}/sm-plugins/container-image

    # tedge log plugins
    install -d ${D}${datadir}/tedge/log-plugins
    install -m 0755 ${S}/src/${GO_IMPORT}/packaging/log-plugins/container ${D}${datadir}/tedge/log-plugins/container
    install -m 0755 ${S}/src/${GO_IMPORT}/packaging/log-plugins/container-group ${D}${datadir}/tedge/log-plugins/container-group

    # tedge service plugins
    install -d ${D}${datadir}/tedge/service-plugins
    install -m 0755 ${S}/src/${GO_IMPORT}/packaging/service-plugins/container ${D}${datadir}/tedge/service-plugins/container
    install -m 0755 ${S}/src/${GO_IMPORT}/packaging/service-plugins/container-group ${D}${datadir}/tedge/service-plugins/container-group

    # Allow sudo access
    install -d -m 0750 "${D}/etc/sudoers.d"
    echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/tedge-container" > "${D}${sysconfdir}/sudoers.d/tedge-container-plugin"
    chmod 0644 "${D}${sysconfdir}/sudoers.d/tedge-container-plugin"
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/* \
    ${sysconfdir}/init.d/* \
    ${sysconfdir}/init.d/* \
    ${TEDGE_CONFIG_DIR}/plugins/config.toml \
    ${TEDGE_CONFIG_DIR}/sm-plugins/container \
    ${TEDGE_CONFIG_DIR}/sm-plugins/container-group \
    ${TEDGE_CONFIG_DIR}/sm-plugins/container-image \
    ${datadir}/tedge/log-plugins/* \
    ${datadir}/tedge/service-plugins/* \
    ${sysconfdir}/sudoers.d/tedge-container-plugin \
"
