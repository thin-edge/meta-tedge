LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

RDEPENDS:${PN} += " avahi-daemon"

inherit systemd allarch features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI += " \
    file://tedge-identity \ 
    file://tedge-bootstrap \
    file://tedge-bootstrap.service \
    file://tedge-avahi.service \
    file://init-pkcs11.sh \
"

do_install () {
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/tedge-identity" "${D}${bindir}"

    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/tedge-bootstrap" "${D}${bindir}"

    install -d "${D}${systemd_system_unitdir}"
    install -m 0644 "${WORKDIR}/tedge-bootstrap.service" "${D}${systemd_system_unitdir}"

    # Create bootstrap hook directory
    install -d "${D}${datadir}/tedge-bootstrap/scripts.d"

    # Enable service discovery
    install -d "${D}${sysconfdir}/avahi/services"
    install -m 0644 "${WORKDIR}/tedge-avahi.service" "${D}${sysconfdir}/avahi/services/"

    # pkcs11 helper script to init a module and on-board to Cumulocity
    install -d "${D}${bindir}"
    install -m 0755 "${WORKDIR}/init-pkcs11.sh" "${D}${bindir}"
}

FILES:${PN} += " \
    ${bindir}/tedge-identity \
    ${bindir}/tedge-bootstrap \
    ${systemd_system_unitdir}/tedge-bootstrap.service \
    ${sysconfdir}/avahi/services/tedge-avahi.service \
    ${datadir}/tedge-bootstrap/scripts.d \
"

SYSTEMD_SERVICE:${PN} = "tedge-bootstrap.service"
