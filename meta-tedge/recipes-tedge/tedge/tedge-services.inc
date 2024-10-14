SRC_URI += "git://git@github.com/thin-edge/tedge-services.git;protocol=https;branch=main;name=tedge-services;destsuffix=tedge-services"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'update-rc.d', '', d)}

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
        for service in ${WORKDIR}/tedge-services/services/systemd/system/*.service; do
            filename=$(basename "$service" ".service")
            if ${@bb.utils.contains('TEDGE_EXCLUDE', '$filename', 'false', 'true', d)}; then
                install -m 0755 $service ${D}${systemd_system_unitdir}
            fi
        done 
	elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        for service in ${WORKDIR}/tedge-services/services/sysvinit-yocto/init.d/*; do
            filename=$(basename -- "$service")
            if ${@bb.utils.contains('TEDGE_EXCLUDE', '$filename', 'false', 'true', d)}; then
                install -m 0755 $service ${D}${sysconfdir}/init.d
            fi
        done 
    fi

    # Install for every init manager but systemd as it is natively supported by thin-edge
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}; then
        install -m 0644 ${WORKDIR}/tedge-services/services/system.toml ${D}${TEDGE_CONFIG_DIR}

        install -d ${D}${sysconfdir}/tedgectl
        install -m 0644 ${WORKDIR}/tedge-services/services/tedgectl_env ${D}${sysconfdir}/tedgectl/env

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/tedge-services/services/tedgectl ${D}${bindir}
    fi
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/* \
    ${sysconfdir}/init.d/* \
    ${TEDGE_CONFIG_DIR}/system.toml \
    ${sysconfdir}/tedgectl/env \
    ${bindir}/tedgectl \
"
