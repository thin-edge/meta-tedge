SUMMARY = "thin-edge.io software management plugin for rpm packages"
DESCRIPTION = "Installs and removes rpm packages through dnf, microdnf or zypper \
via the thin-edge.io software management plugin API."
HOMEPAGE = "https://github.com/thin-edge/tedge-rpm-plugin"

SRC_URI += "git://github.com/thin-edge/tedge-rpm-plugin.git;protocol=https;branch=main"
SRCREV = "0823cfbecfa9d4284072db93cde54010d73beee3"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=e3391664dbe36f415af096559cc3bc71 \
"

inherit allarch

S = "${WORKDIR}/git"

# The plugin uses whichever of dnf, microdnf or zypper it finds on the device.
TEDGE_RPM_PLUGIN_PACKAGE_MANAGER ?= "dnf"

DEPENDS += " tedge"
RDEPENDS:${PN} += " tedge rpm ${TEDGE_RPM_PLUGIN_PACKAGE_MANAGER}"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install () {
    install -d "${D}${bindir}"
    install -m 0755 "${S}/src/sm-plugin/tedge-rpm-plugin" "${D}${bindir}/"

    # Create symlink to enable the tedge sm-plugin interface
    install -d "${D}${TEDGE_CONFIG_DIR}/sm-plugins"
    ln --relative --symbolic "${D}${bindir}/tedge-rpm-plugin" "${D}${TEDGE_CONFIG_DIR}/sm-plugins/rpm"
}

FILES:${PN} += " \
    ${bindir}/tedge-rpm-plugin \
    ${TEDGE_CONFIG_DIR}/sm-plugins/rpm \
"
