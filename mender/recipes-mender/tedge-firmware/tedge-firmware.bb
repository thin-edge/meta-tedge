LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

SRC_URI += " \
    file://mender_workflow.sh \ 
    file://firmware_update.mender.toml \
    file://tedge-firmware \
    file://persist.conf \
"

do_install () {
    # Add firmware worfklow and script
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/mender_workflow.sh" "${D}${bindir}"

    install -d "${D}${datadir}/tedge-workflows"
    install -d "${D}${sysconfdir}/tedge/operations"
    install -m 0644 "${WORKDIR}/firmware_update.mender.toml" "${D}${datadir}/tedge-workflows/"

    # Use a symlink to allow updating the workflow across updates
    ln --relative -s "${D}${datadir}/tedge-workflows/firmware_update.mender.toml" "${D}${sysconfdir}/tedge/operations/firmware_update.toml"

    # FIXME: This cases a conflict with the existing sudoers.d folder
    # Allow sudo access
    #install -d -m 0700 "${D}/etc/sudoers.d"
    #install -m 0644 "${WORKDIR}/tedge-firmware" "${D}${sysconfdir}/sudoers.d/"

    # mosquitto setup
    install -d "${D}/var/lib/mosquitto"
    install -d "${D}${sysconfdir}/tedge/mosquitto-conf/"
    install -m 0644 "${WORKDIR}/persist.conf" "${D}${sysconfdir}/tedge/mosquitto-conf/"
}

FILES:${PN} += " \
    ${bindir}/mender_workflow.sh \
    ${sysconfdir}/tedge/operations/firmware_update.toml \
    ${datadir}/tedge-workflows/firmware_update.mender.toml \
    ${sysconfdir}/sudoers.d/tedge-firmware \
    ${sysconfdir}/tedge/mosquitto-conf/persist.conf \
"
