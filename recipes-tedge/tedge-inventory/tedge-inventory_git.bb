SRC_URI += "git://git@github.com/thin-edge/tedge-inventory-plugin.git;protocol=https;branch=main"
SRCREV = "${AUTOREV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

inherit systemd allarch

PV = "0.1.0+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI += " \
    file://firmware-version \
"

do_install () {
    install -d "${D}${datadir}/tedge-inventory"
    install -m 0755 "${S}/src/runner.sh" "${D}${datadir}/tedge-inventory"

    install -d "${D}${datadir}/tedge-inventory/scripts.d"
    for file in ${S}/src/scripts.d/*; do
        install -m 0755 "$file" "${D}${datadir}/tedge-inventory/scripts.d"
    done

    install -m 0755 "${WORKDIR}/firmware-version" "${D}${datadir}/tedge-inventory/scripts.d/80_c8y_Firmware"

    install -d "${D}${systemd_system_unitdir}"
    for file in ${S}/src/services/systemd/tedge-inventory*; do
        install -m 0644 "$file" "${D}${systemd_system_unitdir}"
    done

}

SYSTEMD_SERVICE:${PN} = "tedge-inventory.timer"

FILES:${PN} += " \
    ${systemd_system_unitdir}/tedge-inventory* \
    ${datadir}/tedge-inventory/* \
"

FILES:${PN} += " \
    ${datadir}/tedge-inventory/scripts.d/firmware-version \
"
