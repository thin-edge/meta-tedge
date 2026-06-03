LICENSE = "CLOSED"

SRC_URI += " \
    file://rugix_workflow.sh \
    file://system.toml \
    file://firmware_update.rugix.toml \
    file://tedge-firmware-rugix \
    file://persist.conf \
    file://state.toml \
    file://state-persistence.toml \
    file://state-persistence-systemd.toml \
    file://hooks/pre-commit/00-transfer-state \
    file://hooks/pre-commit/01-time-sync \
    file://hooks/pre-commit/10-tedge-health \
"

DEPENDS = "tedge mosquitto"
RDEPENDS:${PN} += " tedge rugix-ctrl xdelta3 curl"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install () {
    bbnote "tedge recipe config: TEDGE_CONFIG_DIR=${TEDGE_CONFIG_DIR}"

    # Add firmware worfklow and script
    install -d "${D}${bindir}"
    install -m 0755 "${UNPACKDIR}/rugix_workflow.sh" "${D}${bindir}"

    install -d "${D}${datadir}/tedge-workflows"
    install -d "${D}${TEDGE_CONFIG_DIR}/operations"
    install -m 0644 "${UNPACKDIR}/firmware_update.rugix.toml" "${D}${datadir}/tedge-workflows/"

    # Use a symlink to allow updating the workflow across updates
    ln --relative -s "${D}${datadir}/tedge-workflows/firmware_update.rugix.toml" "${D}${TEDGE_CONFIG_DIR}/operations/firmware_update.toml"

    # Allow sudo access
    install -d -m 0750 "${D}/etc/sudoers.d"
    install -m 0644 "${UNPACKDIR}/tedge-firmware-rugix" "${D}${sysconfdir}/sudoers.d/"

    install -d ${D}${TEDGE_CONFIG_DIR}
    install -m 0644 ${UNPACKDIR}/system.toml ${D}${TEDGE_CONFIG_DIR}/

    # mosquitto setup
    install -d "${D}/var/lib/mosquitto"
    install -d "${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/"
    install -m 0644 "${UNPACKDIR}/persist.conf" "${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/"

    # FIXME: Check if there is a better place to do this
    if [ -d "${D}/var/lib/mosquitto" ]; then
        chown -R mosquitto:mosquitto "${D}/var/lib/mosquitto"
    fi

    # Configure state persistence options
    install -m 0755 -d ${D}${sysconfdir}/rugix
    install -D -m 0755 ${UNPACKDIR}/state.toml ${D}${sysconfdir}/rugix/

    # Store files/directories which should be persisted in the overlay
    install -m 0755 -d ${D}${sysconfdir}/rugix/state
    install -D -m 0644 ${UNPACKDIR}/state-persistence.toml ${D}${sysconfdir}/rugix/state/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0644 ${UNPACKDIR}/state-persistence-systemd.toml ${D}${sysconfdir}/rugix/state/
    fi

    # Add system-hooks
    # https://oss.silitics.com/rugix/docs/ctrl/hooks#system-update-hooks
    install -m 0755 -d ${D}${sysconfdir}/rugix/hooks/system-commit/pre-commit
    install -D -m 0755 "${UNPACKDIR}/hooks/pre-commit/"* ${D}${sysconfdir}/rugix/hooks/system-commit/pre-commit/
}

SYSTEMD_SERVICE:${PN} = "firmware-auto-rollback.timer"

FILES:${PN} += " \
    ${bindir}/rugix_workflow.sh \
    ${TEDGE_CONFIG_DIR}/operations/system.toml \
    ${TEDGE_CONFIG_DIR}/operations/firmware_update.toml \
    ${datadir}/tedge-workflows/firmware_update.rugix.toml \
    ${sysconfdir}/sudoers.d/tedge-firmware-rugix \
    ${sysconfdir}/image_version \
    ${sysconfdir}/rugix/state.toml \
    ${sysconfdir}/rugix/state/state-persistence.toml \
    ${sysconfdir}/rugix/hooks/system-commit/pre-commit \
"
