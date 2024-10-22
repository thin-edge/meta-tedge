do_install:append () {
    if ${@bb.utils.contains('IMAGE_FEATURES', 'read-only-rootfs', 'true', 'false', d)}; then
        # Set PrivateTmp=no as this systemd feature is incompatible with a read-only rootfs
        for i in "${D}${systemd_system_unitdir}/"*.service; do
            sed -i -e 's|PrivateTmp=true|PrivateTmp=no|g' -e 's|PrivateTmp=yes|PrivateTmp=no|g' "$i"
        done
    fi
}
