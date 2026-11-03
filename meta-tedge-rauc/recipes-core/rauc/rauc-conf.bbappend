# Install the keyring configured via RAUC_KEYRING_FILE (see
# tedge-rauc-signing.bbclass) instead of the example CA from
# meta-rauc-raspberrypi, and point system.conf at it.
inherit tedge-rauc-signing

do_install:append() {
    keyring_name="$(basename "${RAUC_KEYRING_FILE}")"
    sed -i "/^\[keyring\]/,/^\[/ s|^path=.*|path=${sysconfdir}/rauc/${keyring_name}|" \
        "${D}${sysconfdir}/rauc/system.conf"

    if ! grep -q "^path=${sysconfdir}/rauc/${keyring_name}$" "${D}${sysconfdir}/rauc/system.conf"; then
        bbfatal "Could not set the [keyring] path in system.conf to ${sysconfdir}/rauc/${keyring_name}"
    fi

    # Check the certificate validity at the bundle's signing time instead of
    # the device's clock (see tedge-rauc-signing.bbclass)
    sed -i "/^\[keyring\]/,/^\[/ { /^use-bundle-signing-time=/d }" "${D}${sysconfdir}/rauc/system.conf"
    sed -i "/^\[keyring\]/a use-bundle-signing-time=${TEDGE_RAUC_USE_BUNDLE_SIGNING_TIME}" "${D}${sysconfdir}/rauc/system.conf"
}
