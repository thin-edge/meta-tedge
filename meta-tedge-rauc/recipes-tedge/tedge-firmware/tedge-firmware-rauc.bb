LICENSE = "CLOSED"

SRC_URI += " \
    file://rauc_workflow.sh \ 
    file://firmware_update.rauc.toml \
    file://tedge-firmware-rauc \
    file://persist.conf \
"

do_install () {
    # Add firmware worfklow and script
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/rauc_workflow.sh" "${D}${bindir}"

    install -d "${D}${datadir}/tedge-workflows"
    install -d "${D}${sysconfdir}/tedge/operations"
    install -m 0644 "${WORKDIR}/firmware_update.rauc.toml" "${D}${datadir}/tedge-workflows/"

    # Use a symlink to allow updating the workflow across updates
    ln --relative -s "${D}${datadir}/tedge-workflows/firmware_update.rauc.toml" "${D}${sysconfdir}/tedge/operations/firmware_update.toml"

    # FIXME: This cases a conflict with the existing sudoers.d folder
    # Allow sudo access
    install -d -m 0750 "${D}/etc/sudoers.d"
    install -m 0644 "${WORKDIR}/tedge-firmware-rauc" "${D}${sysconfdir}/sudoers.d/"

    # mosquitto setup
    install -d "${D}/var/lib/mosquitto"
    install -d "${D}${sysconfdir}/tedge/mosquitto-conf/"
    install -m 0644 "${WORKDIR}/persist.conf" "${D}${sysconfdir}/tedge/mosquitto-conf/"

    # Enable firmware management
    tedge config set c8y.enable.firmware_update true
}

FILES:${PN} += " \
    ${bindir}/rauc_workflow.sh \
    ${sysconfdir}/tedge/operations/firmware_update.toml \
    ${datadir}/tedge-workflows/firmware_update.rauc.toml \
    ${sysconfdir}/sudoers.d/tedge-firmware-rauc \
    ${sysconfdir}/image_version \
"
