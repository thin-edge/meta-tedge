remove_privatetmp_usage() {
    # Set PrivateTmp=no as this systemd feature is incompatible with a read-only rootfs
    for i in "${D}${systemd_system_unitdir}/"*.service; do
        sed -i -e 's|PrivateTmp=true|PrivateTmp=no|g' -e 's|PrivateTmp=yes|PrivateTmp=no|g' "$i"
    done
}

# Run after all packages have been installed
IMAGE_POSTPROCESS_COMMAND += " ${@bb.utils.contains('IMAGE_FEATURES', 'read-only-rootfs', 'remove_privatetmp_usage;', '', d)}"
