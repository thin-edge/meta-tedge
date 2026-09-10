LICENSE = "CLOSED"

SRC_URI += " \
    file://firmware-auto-rollback \
    file://firmware-auto-rollback.timer \
    file://firmware-auto-rollback.service \
"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}

RDEPENDS:${PN} += " rugix-ctrl jq"

do_install () {
    # auto rollback service incase if new agent is corrupt (only rely on tooling which is definitely there)
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -D -m 0755 ${UNPACKDIR}/firmware-auto-rollback ${D}${bindir}/firmware-auto-rollback
        install -D -m 0644 ${UNPACKDIR}/firmware-auto-rollback.service -t ${D}${systemd_system_unitdir}
        install -D -m 0644 ${UNPACKDIR}/firmware-auto-rollback.timer -t ${D}${systemd_system_unitdir}
    fi
}

SYSTEMD_SERVICE:${PN} = "firmware-auto-rollback.timer"

FILES:${PN} += " \
    ${bindir}/firmware-auto-rollback \
    ${systemd_system_unitdir}/firmware-auto-rollback.service \
    ${systemd_system_unitdir}/firmware-auto-rollback.timer \
"
