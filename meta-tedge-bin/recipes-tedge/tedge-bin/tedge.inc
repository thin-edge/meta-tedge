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

python __anonymous () {
    install_dir = "${sysconfdir}/tedge"
    tedge_config_symlink = ""

    if bb.utils.contains('IMAGE_FEATURES', 'read-only-rootfs', True, False, d):
        if not bb.utils.contains('IMAGE_FEATURES', 'overlayfs-etc', True, False, d):
            tedge_config_symlink = install_dir
            install_dir = "${sysconfdir}/tedge-default"

    d.setVar("TEDGE_CONFIG_DIR", d.expand(install_dir))
    d.setVar("TEDGE_CONFIG_SYMLINK", d.expand(tedge_config_symlink))
}

do_install:append () {
    # Create all folders
    install -d ${D}${TEDGE_CONFIG_DIR}
    install -d ${D}${TEDGE_CONFIG_DIR}/device
    install -d ${D}${TEDGE_CONFIG_DIR}/device-certs
    install -d ${D}${TEDGE_CONFIG_DIR}/mosquitto-conf
    install -d ${D}${TEDGE_CONFIG_DIR}/operations/c8y
    install -d ${D}${TEDGE_CONFIG_DIR}/plugins
    install -d ${D}${TEDGE_CONFIG_DIR}/sm-plugins

    # Create symlink for tedge-apt-plugin in `/etc/tedge/sm-plugins`
    install -d ${D}${TEDGE_CONFIG_DIR}/sm-plugins
    ln -s -r  ${D}${bindir}/tedge-apt-plugin ${D}${TEDGE_CONFIG_DIR}/sm-plugins/apt

    # Remove packages indicated in `TEDGE_EXCLUDE` (NOTE: they cannot be part of tedge-binary)
    for package in ${@d.getVar('TEDGE_EXCLUDE')}; do 
        rm -f ${D}${bindir}/${package}
    done

    # Initialize the tedge
    install -m 0644 "${WORKDIR}/operations/c8y/"* ${D}${TEDGE_CONFIG_DIR}/operations/c8y/
    install -m 0644 "${WORKDIR}/tedge.toml" ${D}${TEDGE_CONFIG_DIR}/

    # Create symlinks
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-agent
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-mapper
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-apt-plugin
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/c8y-remote-access-plugin
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-watchdog

    chown -R tedge:tedge "${D}${TEDGE_CONFIG_DIR}"

    #
    # If using a pure read-only rootfs without the overlayfs-etc feature, then
    # make a copy of the /etc/tedge data included in the image (so it can be copied later)
    # and create a symlink from /etc/tedge to /data/tedge
    #
    if [ -n "${TEDGE_CONFIG_SYMLINK}" ]; then
        ln --relative --symbolic ${D}/data/tedge ${D}${TEDGE_CONFIG_SYMLINK}
    fi
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
    ${TEDGE_CONFIG_SYMLINK} \
    ${TEDGE_CONFIG_DIR} \
    ${TEDGE_CONFIG_DIR}/tedge.toml \
    ${TEDGE_CONFIG_DIR}/operations/c8y/c8y_RemoteAccessConnect \
    ${TEDGE_CONFIG_DIR}/contrib/collectd \
    ${TEDGE_CONFIG_DIR}/sm-plugins/apt \
"

require architectures/${TEDGE_PKG}.inc
require init-managers/${TEDGE_SERVICE}.inc
