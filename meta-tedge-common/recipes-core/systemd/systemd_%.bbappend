remove_privatetmp_usage() {
    # Set PrivateTmp=no as this systemd feature is incompatible with a read-only rootfs
    for i in "${IMAGE_ROOTFS}/lib/systemd/system/"*.service; do
        sed -i -e 's|PrivateTmp=true|PrivateTmp=no|g' -e 's|PrivateTmp=yes|PrivateTmp=no|g' "$i"
    done
}

# Run after all packages have been installed
ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('IMAGE_FEATURES', 'read-only-rootfs', 'remove_privatetmp_usage', '', d)}"
