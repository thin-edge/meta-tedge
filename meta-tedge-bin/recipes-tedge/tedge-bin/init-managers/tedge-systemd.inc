SRC_URI += "https://dl.cloudsmith.io/public/thinedge/${INIT_REPO_CHANNEL}/raw/names/tedge-systemd/versions/${INIT_VERSION}/tedge-systemd.tar.gz;name=systemd;subdir=${BP}"

FILES:${PN} += "\
    ${systemd_system_unitdir}/* \
    ${sysconfdir}/init.d/* \
    ${TEDGE_CONFIG_DIR}/system.toml \
    ${sysconfdir}/tedgectl/env \
    ${bindir}/tedgectl \
"

do_install:append () {
    # Install service/init files from the init-manager tarball
    if [ -d "${UNPACKDIR}/${BP}" ]; then
        tar -C "${UNPACKDIR}/${BP}" --no-same-owner -cpf - . | tar --no-same-owner -xpf - -C "${D}${base_prefix}"
    fi
}
