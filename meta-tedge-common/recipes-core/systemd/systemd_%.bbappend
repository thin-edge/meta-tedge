
# Prevent NetworkManager from overriding systemd's (systemd-resolved) /etc/resolv.conf symlink
# https://community.toradex.com/t/override-yocto-alternative-priority-with-bbappend/15596/2
# increase priority from 50 to 150
ALTERNATIVE_PRIORITY[resolv-conf] = "150"

do_install:append () {
    if ${@bb.utils.contains('IMAGE_FEATURES', 'read-only-rootfs', 'true', 'false', d)}; then
        # Set PrivateTmp=no as this systemd feature is incompatible with a read-only rootfs
        for i in "${D}${systemd_system_unitdir}/"*.service; do
            sed -i -e 's|PrivateTmp=true|PrivateTmp=no|g' -e 's|PrivateTmp=yes|PrivateTmp=no|g' "$i"
        done
    fi
}
