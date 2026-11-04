# Install the keyring configured via RAUC_KEYRING_FILE (see
# tedge-rauc-signing.bbclass) instead of the example CA from
# meta-rauc-raspberrypi, and point system.conf at it.
#
# Since wrynose, meta-rauc installs system.conf and the keyring to
# ${nonarch_libdir}/rauc (/usr/lib/rauc) instead of ${sysconfdir}/rauc.
inherit tedge-rauc-signing

do_install:append() {
    rauc_conf="${D}${nonarch_libdir}/rauc/system.conf"
    keyring_name="$(basename "${RAUC_KEYRING_FILE}")"
    sed -i "/^\[keyring\]/,/^\[/ s|^path=.*|path=${nonarch_libdir}/rauc/${keyring_name}|" "$rauc_conf"

    if ! grep -q "^path=${nonarch_libdir}/rauc/${keyring_name}$" "$rauc_conf"; then
        bbfatal "Could not set the [keyring] path in system.conf to ${nonarch_libdir}/rauc/${keyring_name}"
    fi

    # Check the certificate validity at the bundle's signing time instead of
    # the device's clock (see tedge-rauc-signing.bbclass)
    sed -i "/^\[keyring\]/,/^\[/ { /^use-bundle-signing-time=/d }" "$rauc_conf"
    sed -i "/^\[keyring\]/a use-bundle-signing-time=${TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME}" "$rauc_conf"
}
