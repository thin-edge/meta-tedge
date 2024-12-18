LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

SRC_URI += " \
    file://mender_workflow.sh \ 
    file://firmware_update.mender.toml \
    file://tedge-firmware \
    file://persist.conf \
"

inherit allarch

DEPENDS = "tedge mosquitto"
RDEPENDS:${PN} += " tedge"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install () {
    bbnote "tedge recipe config: TEDGE_CONFIG_DIR=${TEDGE_CONFIG_DIR}"

    # Add firmware worfklow and script
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/mender_workflow.sh" "${D}${bindir}"

    install -d "${D}${datadir}/tedge-workflows"
    install -d "${D}${TEDGE_CONFIG_DIR}/operations"
    install -m 0644 "${WORKDIR}/firmware_update.mender.toml" "${D}${datadir}/tedge-workflows/"

    # Use a symlink to allow updating the workflow across updates
    ln --relative -s "${D}${datadir}/tedge-workflows/firmware_update.mender.toml" "${D}${TEDGE_CONFIG_DIR}/operations/firmware_update.toml"

    # mosquitto setup
    install -d "${D}/var/lib/mosquitto"
    install -d "${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/"
    install -m 0644 "${WORKDIR}/persist.conf" "${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/"

    # Allow sudo access
    install -d -m 0750 "${D}/etc/sudoers.d"
    install -m 0644 "${WORKDIR}/tedge-firmware" "${D}${sysconfdir}/sudoers.d/"

    # FIXME: Check if there is a better place to do this
    if [ -d "${D}/var/lib/mosquitto" ]; then
        chown -R mosquitto:mosquitto "${D}/var/lib/mosquitto"
    fi

}

FILES:${PN} += " \
    ${bindir}/mender_workflow.sh \
    ${TEDGE_CONFIG_DIR}/operations/firmware_update.toml \
    ${datadir}/tedge-workflows/firmware_update.mender.toml \
    ${sysconfdir}/sudoers.d/tedge-firmware \
    ${TEDGE_CONFIG_DIR}/mosquitto-conf/persist.conf \
"
