do_install:append () {

    patch_service() {
        if [ -f "${D}${systemd_system_unitdir}/$1" ]; then
            sed -i -e 's|PrivateTmp=yes|PrivateTmp=no|g' "${D}${systemd_system_unitdir}/$1"
        fi    
    }

    # TODO: Only run if being used in readonly mode
    patch_service systemd-timedated.service
    patch_service systemd-timesyncd.service
    patch_service systemd-networkd.service
    patch_service systemd-resolved.service
    patch_service systemd-hostnamed.service
    patch_service systemd-logind.service
}
