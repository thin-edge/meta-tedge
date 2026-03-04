SRC_URI += "git://git@github.com/thin-edge/tedge-services.git;protocol=https;branch=main;name=tedge-services;destsuffix=tedge-services"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'update-rc.d', '', d)}

# tedge-p11-server is defined in a seprated recipe
TEDGE_EXCLUDE += "tedge-p11-server"
# Exclude tedge-mapper-local for versions before 1.8.0
TEDGE_EXCLUDE += "${@'tedge-mapper-local' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') < 0 else ''}"

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_system_unitdir}
        
        for service in ${WORKDIR}/tedge-services/services/systemd/system/*; do
            unit_name=$(basename "$service" | cut -d. -f1)
            if ! echo "${@d.getVar('TEDGE_EXCLUDE')}" | grep -qw "$unit_name"; then
                install -m 0644 $service ${D}${systemd_system_unitdir}
            fi
        done 
	elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        for service in ${WORKDIR}/tedge-services/services/sysvinit-yocto/init.d/*; do
            filename=$(basename -- "$service")
            if ! echo "${@d.getVar('TEDGE_EXCLUDE')}" | grep -iq $filename; then
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

PACKAGES += "${PN}-agent ${PN}-mapper-c8y ${PN}-mapper-aws ${PN}-mapper-az ${PN}-mapper-collectd"
# Add tedge-mapper-local package only for versions >= 1.8.0
PACKAGES += "${@'${PN}-mapper-local' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') >= 0 else ''}"

FILES:${PN} += "\
    ${sysconfdir}/init.d/* \
    ${TEDGE_CONFIG_DIR}/system.toml \
    ${sysconfdir}/tedgectl/env \
    ${bindir}/tedgectl \
"

FILES:${PN}-agent += "\
    ${systemd_system_unitdir}/tedge-agent.service \
"
FILES:${PN}-mapper-c8y += "\
    ${systemd_system_unitdir}/tedge-mapper-c8y.service \
    ${systemd_system_unitdir}/c8y-remote-access-plugin@.service \
    ${systemd_system_unitdir}/c8y-remote-access-plugin.socket \
    ${systemd_system_unitdir}/tedge-cert-renewer@.service \
    ${systemd_system_unitdir}/tedge-cert-renewer@.timer \
    ${systemd_system_unitdir}/tedge-cert-renewer.target \
"
FILES:${PN}-mapper-aws += "\
    ${systemd_system_unitdir}/tedge-mapper-aws.service \
"
FILES:${PN}-mapper-az += "\
    ${systemd_system_unitdir}/tedge-mapper-az.service \
"
FILES:${PN}-mapper-collectd += "\
    ${systemd_system_unitdir}/tedge-mapper-collectd.service \
"
FILES:${PN}-mapper-local += "\
    ${systemd_system_unitdir}/tedge-mapper-local.service \
"

SYSTEMD_PACKAGES += "${PN}-agent ${PN}-mapper-c8y ${PN}-mapper-aws ${PN}-mapper-az ${PN}-mapper-collectd"
SYSTEMD_PACKAGES += "${@'${PN}-mapper-local' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') >= 0 else ''}"

SYSTEMD_SERVICE:${PN}-agent = "tedge-agent.service"
SYSTEMD_SERVICE:${PN}-mapper-c8y = "tedge-mapper-c8y.service c8y-remote-access-plugin@.service c8y-remote-access-plugin.socket"
SYSTEMD_SERVICE:${PN}-mapper-aws = "tedge-mapper-aws.service"
SYSTEMD_SERVICE:${PN}-mapper-az = "tedge-mapper-az.service"
SYSTEMD_SERVICE:${PN}-mapper-collectd = "tedge-mapper-collectd.service"
SYSTEMD_SERVICE:${PN}-mapper-local = "tedge-mapper-local.service"
