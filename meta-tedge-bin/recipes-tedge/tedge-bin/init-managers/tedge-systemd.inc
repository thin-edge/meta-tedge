SRC_URI += "https://dl.cloudsmith.io/public/thinedge/${INIT_REPO_CHANNEL}/raw/names/tedge-systemd/versions/${INIT_VERSION}/tedge-systemd.tar.gz;name=systemd;subdir=${BP}"

FILES:${PN} += "\
    ${systemd_system_unitdir}/* \
    ${sysconfdir}/init.d/* \
    ${sysconfdir}/tedge/system.toml \
    ${sysconfdir}/tedgectl/env \
    ${bindir}/tedgectl \
"
