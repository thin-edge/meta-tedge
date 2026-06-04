SRC_URI += "https://dl.cloudsmith.io/public/thinedge/${INIT_REPO_CHANNEL}/raw/names/tedge-openrc/versions/${INIT_VERSION}/tedge-openrc.tar.gz;name=openrc;subdir=${BP}"
do_install:append () {
    # Install service/init files from the init-manager tarball
    if [ -d "${UNPACKDIR}/${BP}" ]; then
        tar -C "${UNPACKDIR}/${BP}" --no-same-owner -cpf - . | tar --no-same-owner -xpf - -C "${D}${base_prefix}"
    fi
}
