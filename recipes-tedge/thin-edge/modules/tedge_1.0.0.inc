RDEPENDS:tedge = " mosquitto ca-certificates libgcc glibc-utils libxcrypt sudo"

pkg_postinst:tedge () {
    if ${@bb.utils.contains('DISTRO_CODENAME','honister','true','false',d)}; then
        sed -i 's/d \/run\/lock 0755 root root -/d \/run\/lock 1777 root root -/' $D/usr/lib/tmpfiles.d/legacy.conf
    fi
}
