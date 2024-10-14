do_install:append () {
    # TODO: Only run if being used in readonly mode
    # Set PrivateTmp=no as this systemd feature is incompatible with a read-only rootfs
    for i in "${D}${systemd_system_unitdir}/"*.service; do
        sed -i -e 's|PrivateTmp=true|PrivateTmp=no|g' -e 's|PrivateTmp=yes|PrivateTmp=no|g' "$i"
    done
}
