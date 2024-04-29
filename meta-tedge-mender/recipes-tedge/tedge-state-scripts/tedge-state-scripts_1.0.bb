LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

inherit mender-state-scripts allarch

SRC_URI += " \
    file://transfer;subdir=${BPN}-${PV} \
    file://restore;subdir=${BPN}-${PV} \
    file://verify-tedge;subdir=${BPN}-${PV} \
"

do_compile() {
    cp transfer ${MENDER_STATE_SCRIPTS_DIR}/ArtifactInstall_Leave_00_transfer
    cp restore ${MENDER_STATE_SCRIPTS_DIR}/ArtifactCommit_Enter_00_restore
    cp verify-tedge ${MENDER_STATE_SCRIPTS_DIR}/ArtifactCommit_Enter_50_verify-tedge
}

ALLOW_EMPTY:${PN} = "1"