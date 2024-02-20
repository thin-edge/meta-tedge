SRC_URI += "git://git@github.com/thin-edge/tedge-services.git;protocol=https;branch=main;name=init-manager;destsuffix=init-manager"
SRCREV_init-manager = "${AUTOREV}"

inherit ${@bb.utils.contains('INIT_MANAGER', 'systemd', 'systemd', '', d)}
inherit ${@bb.utils.contains_any('INIT_MANAGER', ['sysvinit', 'none'], 'update-rc.d', '', d)}

do_install:append () {
    if ${@bb.utils.contains('INIT_MANAGER', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
        for service in ${WORKDIR}/init-manager/services/systemd/system/*.service; do
            filename=$(basename "$service" ".service")
            if ${@bb.utils.contains('TEDGE_EXCLUDE', '$filename', 'false', 'true', d)}; then
                install -m 0755 $service ${D}${systemd_system_unitdir}
            fi
        done 
	elif ${@bb.utils.contains_any('INIT_MANAGER', ['sysvinit', 'none'], 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        for service in ${WORKDIR}/init-manager/services/sysvinit-yocto/init.d/*; do
            filename=$(basename -- "$service")
            if ${@bb.utils.contains('TEDGE_EXCLUDE', '$filename', 'false', 'true', d)}; then
                install -m 0755 $service ${D}${sysconfdir}/init.d
            fi
        done 
    fi

    # Install for every init manager but systemd as it is natively supported by thin-edge
    if ${@bb.utils.contains('INIT_MANAGER', 'systemd', 'false', 'true', d)}; then
        install -d ${D}${sysconfdir}/tedge
        install -m 0644 ${WORKDIR}/init-manager/services/system.toml ${D}${sysconfdir}/tedge

        install -d ${D}${sysconfdir}/tedgectl
        install -m 0644 ${WORKDIR}/init-manager/services/tedgectl_env ${D}${sysconfdir}/tedgectl/env

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/init-manager/services/tedgectl ${D}${bindir}
    fi
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/* \
    ${sysconfdir}/init.d/* \
    ${sysconfdir}/tedge/system.toml \
    ${sysconfdir}/tedgectl/env \
    ${bindir}/tedgectl \
"