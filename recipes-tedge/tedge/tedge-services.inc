SRC_URI += "git://git@github.com/thin-edge/tedge-services.git;protocol=https;branch=main;name=tedge-services;destsuffix=tedge-services"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'update-rc.d', '', d)}

# Fix issue with 'tedge-services' being treated as rust crate
python fix_cargo_common_do_patch_paths() {
    cargo_config = os.path.join(d.getVar("CARGO_HOME"), "config")
    if not os.path.exists(cargo_config):
        return

    with open(cargo_config, "r") as config:
        lines = config.readlines()
    with open(cargo_config, "w") as config:
        for line in lines:
            if not "tedge-services" in line.strip("\n"):
                config.write(line)
}

do_configure[postfuncs] += "fix_cargo_common_do_patch_paths"

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
        install -d ${D}${sysconfdir}/tedge
        install -m 0644 ${WORKDIR}/tedge-services/services/system.toml ${D}${sysconfdir}/tedge

        install -d ${D}${sysconfdir}/tedgectl
        install -m 0644 ${WORKDIR}/tedge-services/services/tedgectl_env ${D}${sysconfdir}/tedgectl/env

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/tedge-services/services/tedgectl ${D}${bindir}
    fi
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/* \
    ${sysconfdir}/init.d/* \
    ${sysconfdir}/tedge/system.toml \
    ${sysconfdir}/tedgectl/env \
    ${bindir}/tedgectl \
"
