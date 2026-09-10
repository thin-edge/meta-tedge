SUMMARY = "A thin-edge server that allows thin-edge service possibly working in a container to access host's cryptographic tokens."
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

inherit cargo-tedge tedge-user
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}

SRC_URI += "\
    git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge \
    git://git@github.com/thin-edge/tedge-services.git;protocol=https;branch=main;name=tedge-services;destsuffix=tedge-services \
    "

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

CARGO_BUILD_FLAGS:append = " --bin tedge-p11-server"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/tedge-services/services/systemd/system/tedge-p11-server.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/tedge-services/services/systemd/system/tedge-p11-server.socket ${D}${systemd_system_unitdir}
    fi

    install -d ${D}${TEDGE_CONFIG_DIR}
    chown -R tedge:tedge "${D}${TEDGE_CONFIG_DIR}"
}

FILES:${PN} += "\
    ${bindir}/tedge-p11-server \
    ${systemd_system_unitdir}/tedge-p11-server.service \
    ${systemd_system_unitdir}/tedge-p11-server.socket \
"

SYSTEMD_SERVICE:${PN} += "tedge-p11-server.socket"
