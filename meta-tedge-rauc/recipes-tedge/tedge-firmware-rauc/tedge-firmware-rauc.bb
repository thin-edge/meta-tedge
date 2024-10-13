LICENSE = "CLOSED"

SRC_URI += " \
    file://rauc_workflow.sh \ 
    file://firmware_update.rauc.toml \
    file://tedge-firmware-rauc \
    file://persist.conf \
"

DEPENDS = "tedge mosquitto"
RDEPENDS:${PN} += " tedge"

do_install () {
    # Add firmware worfklow and script
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/rauc_workflow.sh" "${D}${bindir}"

    install -d "${D}${datadir}/tedge-workflows"
    install -d "${D}${sysconfdir}/tedge/operations"
    install -m 0644 "${WORKDIR}/firmware_update.rauc.toml" "${D}${datadir}/tedge-workflows/"

    # Use a symlink to allow updating the workflow across updates
    ln --relative -s "${D}${datadir}/tedge-workflows/firmware_update.rauc.toml" "${D}${sysconfdir}/tedge/operations/firmware_update.toml"

    # Allow sudo access
    install -d -m 0750 "${D}/etc/sudoers.d"
    install -m 0644 "${WORKDIR}/tedge-firmware-rauc" "${D}${sysconfdir}/sudoers.d/"

    # mosquitto setup
    install -d "${D}/var/lib/mosquitto"
    install -d "${D}${sysconfdir}/tedge/mosquitto-conf/"
    install -m 0644 "${WORKDIR}/persist.conf" "${D}${sysconfdir}/tedge/mosquitto-conf/"


    # Ensure persistence of operation state across partition swaps by storing
    # information on a persisted mount
    install -d "${D}/data/tedge/agent"
    chown -R tedge:tedge "${D}/data/tedge/agent"

    # FIXME: Check if there is a better place to do this
    if [ -d "${D}/var/lib/mosquitto" ]; then
        chown -R mosquitto:mosquitto "${D}/var/lib/mosquitto"
    fi

    # FIXME: Currently some workflow state is reliant on the mosquitto db
    # and there might be a race condition on startup after a partition swap
    # where the existing mosquitto state is sometimes processed before the state
    # stored under the /data/tedge/agent folder
    # https://github.com/thin-edge/thin-edge.io/issues/2495
    mkdir -p "${D}/data/mosquitto"
    chown mosquitto:mosquitto "${D}/data/mosquitto"

    # Change log dir
    mkdir -p "${D}/data/tedge/log"
    chown -R tedge:tedge "${D}/data/tedge/log"
}

FILES:${PN} += " \
    ${bindir}/rauc_workflow.sh \
    ${sysconfdir}/tedge/operations/firmware_update.toml \
    ${datadir}/tedge-workflows/firmware_update.rauc.toml \
    ${sysconfdir}/sudoers.d/tedge-firmware-rauc \
    ${sysconfdir}/image_version \
    /data \
    /data/tedge \
    /data/tedge/log \
    /data/tedge/agent \
    /data/mosquitto \
"
