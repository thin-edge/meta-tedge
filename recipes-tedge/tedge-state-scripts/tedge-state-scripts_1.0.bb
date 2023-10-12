LICENSE = "CLOSED"

SRC_URI += " \
    file://tedge-install-leave-script;subdir=${BPN}-${PV} \
    file://tedge-commit-leave-script;subdir=${BPN}-${PV} \
"

do_compile() {
    cp tedge-install-leave-script ${MENDER_STATE_SCRIPTS_DIR}/ArtifactInstall_Leave_00
    cp tedge-commit-leave-script ${MENDER_STATE_SCRIPTS_DIR}/ArtifactCommit_Leave_00
}

ALLOW_EMPTY:${PN} = "1"
