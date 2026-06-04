SRC_URI += "https://dl.cloudsmith.io/public/thinedge/${INIT_REPO_CHANNEL}/raw/names/tedge-sysvinit-yocto/versions/${INIT_VERSION}/tedge-sysvinit-yocto.tar.gz;name=sysvinit;subdir=${BP}"
do_install:append () {
    # Install service/init files from the init-manager tarball
    if [ -d "${UNPACKDIR}/${BP}" ]; then
        tar -C "${UNPACKDIR}/${BP}" --no-same-owner -cpf - . | tar --no-same-owner -xpf - -C "${D}${base_prefix}"
    fi
}
