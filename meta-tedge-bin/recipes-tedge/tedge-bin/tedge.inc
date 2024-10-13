SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "CLOSED"

SRC_URI += " \
    file://operations/c8y/c8y_RemoteAccessConnect \ 
    file://tedge.toml \
"

RDEPENDS:${PN} += " mosquitto ca-certificates libgcc glibc-utils libxcrypt sudo collectd"

inherit bin_package useradd

INSANE_SKIP:${PN} = "already-stripped"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system --gid 950 tedge"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge"

do_install:append () {
    # Create all folders
    install -d ${D}${sysconfdir}/tedge
    install -d ${D}${sysconfdir}/tedge/device-certs
    install -d ${D}${sysconfdir}/tedge/mosquitto-conf
    install -d ${D}${sysconfdir}/tedge/operations/c8y
    install -d ${D}${sysconfdir}/tedge/plugins
    install -d ${D}${sysconfdir}/tedge/sm-plugins

    # Create symlink for tedge-apt-plugin in `/etc/tedge/sm-plugins`
    install -d ${D}${sysconfdir}/tedge/sm-plugins
    ln -s -r  ${D}${bindir}/tedge-apt-plugin ${D}${sysconfdir}/tedge/sm-plugins/apt

    # Remove packages indicated in `TEDGE_EXCLUDE` (NOTE: they cannot be part of tedge-binary)
    for package in ${@d.getVar('TEDGE_EXCLUDE')}; do 
        rm -f ${D}${bindir}/${package}
    done

    # Initialize the tedge
    install -m 0644 "${WORKDIR}/operations/c8y/"* ${D}${sysconfdir}/tedge/operations/c8y/
    install -m 0644 "${WORKDIR}/tedge.toml" ${D}${sysconfdir}/tedge/
}

# Automatically choose tedge package based on target architecture
def get_tedge_pkg(d):
    target = d.getVar('TARGET_ARCH', True)
    if target == "arm":
        fpu = d.getVar('TARGET_FPU', True)
        if fpu == "soft":
            tedgePkg = "tedge-armv6"
        else:
            tedgePkg = "tedge-armv7"
    elif target == "aarch64":
        tedgePkg = "tedge-aarch64"
    elif target == "x86_64":
        tedgePkg = "tedge-x86-64"
    else:
        raise bb.parse.SkipPackage("Target architecture '%s' is not supported by the meta-tedge-bin layer" %target)
    return tedgePkg

# Automatically setup service system based on init manager
def get_tedge_service(d):
    initManager = d.getVar('INIT_MANAGER', True)
    if "sysvinit" in initManager or "none" in initManager: 
        tedgeService = "tedge-sysvinit"
    elif "openrc" in initManager:
        tedgeService = "tedge-openrc"
    elif "systemd" in initManager:
        tedgeService = "tedge-systemd"
    else:
        raise bb.parse.SkipPackage("Init manager '%s' is not supported by the meta-tedge-bin layer" %initManager)
    return tedgeService

TEDGE_PKG = "${@get_tedge_pkg(d)}"
TEDGE_SERVICE = "${@get_tedge_service(d)}"

FILES:${PN} += "\ 
    ${bindir}/* \ 
    ${sysconfdir}/tedge \
    ${sysconfdir}/tedge/tedge.toml \
    ${sysconfdir}/tedge/operations/c8y/c8y_RemoteAccessConnect \
    ${sysconfdir}/tedge/contrib/collectd \
    ${sysconfdir}/tedge/sm-plugins/apt \
"

require architectures/${TEDGE_PKG}.inc
require init-managers/${TEDGE_SERVICE}.inc
