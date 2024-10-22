LICENSE = "CLOSED"

SRC_URI += " \
    file://rauc_workflow.sh \ 
    file://firmware_update.rauc.toml \
    file://tedge-firmware-rauc \
    file://persist.conf \
"

DEPENDS = "tedge mosquitto"
RDEPENDS:${PN} += " tedge"

# TODO: Move logic to a single class and inherit it
python __anonymous () {
    install_dir = "${sysconfdir}/tedge"
    tedge_config_symlink = ""

    if bb.utils.contains('IMAGE_FEATURES', 'read-only-rootfs', True, False, d):
        if not bb.utils.contains('IMAGE_FEATURES', 'overlayfs-etc', True, False, d):
            tedge_config_symlink = install_dir
            install_dir = "${sysconfdir}/tedge-default"

    d.setVar("TEDGE_CONFIG_DIR", d.expand(install_dir))
    d.setVar("TEDGE_CONFIG_SYMLINK", d.expand(tedge_config_symlink))
}

do_install () {
    # Add firmware worfklow and script
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/rauc_workflow.sh" "${D}${bindir}"

    install -d "${D}${datadir}/tedge-workflows"
    install -d "${D}${TEDGE_CONFIG_DIR}/operations"
    install -m 0644 "${WORKDIR}/firmware_update.rauc.toml" "${D}${datadir}/tedge-workflows/"

    # Use a symlink to allow updating the workflow across updates
    ln --relative -s "${D}${datadir}/tedge-workflows/firmware_update.rauc.toml" "${D}${TEDGE_CONFIG_DIR}/operations/firmware_update.toml"

    # Allow sudo access
    install -d -m 0750 "${D}/etc/sudoers.d"
    install -m 0644 "${WORKDIR}/tedge-firmware-rauc" "${D}${sysconfdir}/sudoers.d/"

    # mosquitto setup
    install -d "${D}/var/lib/mosquitto"
    install -d "${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/"
    install -m 0644 "${WORKDIR}/persist.conf" "${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/"

    # FIXME: Check if there is a better place to do this
    if [ -d "${D}/var/lib/mosquitto" ]; then
        chown -R mosquitto:mosquitto "${D}/var/lib/mosquitto"
    fi
}

FILES:${PN} += " \
    ${bindir}/rauc_workflow.sh \
    ${TEDGE_CONFIG_DIR}/operations/firmware_update.toml \
    ${datadir}/tedge-workflows/firmware_update.rauc.toml \
    ${sysconfdir}/sudoers.d/tedge-firmware-rauc \
    ${sysconfdir}/image_version \
"
