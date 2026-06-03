SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

SRC_URI += " \
    file://operations/c8y/c8y_RemoteAccessConnect \ 
    file://tedge.toml \
    file://mosquitto-conf/tedge-mosquitto.conf \
"

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

inherit cargo-tedge tedge-user

# Build only tedge, as tedge-p11-server is handled by a separate recipe
CARGO_BUILD_FLAGS:append = " --bin tedge"

RDEPENDS:${PN} = " \
    ${PN}-agent \
    ${PN}-mapper-c8y \
    ${PN}-mapper-aws \
    ${PN}-mapper-az \
    ${PN}-mapper-collectd \
    ${@'${PN}-mapper-local ' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') >= 0 else ''} \
    mosquitto \
    ca-certificates \
    libgcc \
    glibc-utils \
    libxcrypt \
    sudo \
"
TEDGE_CONFIG_DIR ?= "/etc/tedge"
TEDGE_CONFIG_SYMLINK ?= ""
TEDGE_CONFIG_SYMLINK_SRC ?= ""

do_install:append () {
    bbnote "tedge recipe config: TEDGE_CONFIG_DIR=${TEDGE_CONFIG_DIR}, TEDGE_CONFIG_SYMLINK=${TEDGE_CONFIG_SYMLINK}"

    # Create all folders
    install -d ${D}${TEDGE_CONFIG_DIR}
    install -d ${D}${TEDGE_CONFIG_DIR}/device
    install -d ${D}${TEDGE_CONFIG_DIR}/device-certs
    install -d ${D}${TEDGE_CONFIG_DIR}/mosquitto-conf
    install -d ${D}${TEDGE_CONFIG_DIR}/operations/c8y
    install -d ${D}${TEDGE_CONFIG_DIR}/plugins
    install -d ${D}${TEDGE_CONFIG_DIR}/sm-plugins

    # install collectd for tedge-mapper
    install -d ${D}${datadir}/contrib/collectd
    install -m 0644 ${S}/configuration/contrib/collectd/collectd.conf ${D}${datadir}/contrib/collectd/collectd.conf

    # Create symlink for tedge-apt-plugin in `/etc/tedge/sm-plugins`
    install -d ${D}${TEDGE_CONFIG_DIR}/sm-plugins
    ln -s -r  ${D}${bindir}/tedge-apt-plugin ${D}${TEDGE_CONFIG_DIR}/sm-plugins/apt

    # Create symlink for tedge-flows-plugin in `/etc/tedge/sm-plugins` (version >= 1.8.0)
    if ${@'true' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') >= 0 else 'false'}; then
        install -d ${D}${TEDGE_CONFIG_DIR}/sm-plugins
        ln -s -r  ${D}${bindir}/tedge-flows-plugin ${D}${TEDGE_CONFIG_DIR}/sm-plugins/flow
    fi

    # Remove packages indicated in `TEDGE_EXCLUDE` (NOTE: they cannot be part of tedge-binary)
    for package in ${@d.getVar('TEDGE_EXCLUDE')}; do 
        rm -f ${D}${bindir}/${package}
    done

    # Initialize the tedge
    install -m 0644 "${UNPACKDIR}/operations/c8y/"* ${D}${TEDGE_CONFIG_DIR}/operations/c8y/
    install -m 0644 "${UNPACKDIR}/tedge.toml" ${D}${TEDGE_CONFIG_DIR}/
    install -m 0644 "${UNPACKDIR}/mosquitto-conf/tedge-mosquitto.conf" ${D}${TEDGE_CONFIG_DIR}/mosquitto-conf/

    # Create symlinks
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-agent
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-mapper
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-apt-plugin
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/c8y-remote-access-plugin
    ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-watchdog
    if ${@'true' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') >= 0 else 'false'}; then
        ln --relative --symbolic ${D}${bindir}/tedge ${D}${bindir}/tedge-flows-plugin
    fi

    chown -R tedge:tedge "${D}${TEDGE_CONFIG_DIR}"

    #
    # If using a pure read-only rootfs without the overlayfs-etc feature, then
    # make a copy of the /etc/tedge data included in the image (so it can be copied later)
    # and create a symlink from /etc/tedge to /data/tedge
    #
    if [ -n "${TEDGE_CONFIG_SYMLINK}" ] && [ -n "${TEDGE_CONFIG_SYMLINK_SRC}" ]; then
        ln --relative --symbolic ${D}${TEDGE_CONFIG_SYMLINK_SRC} ${D}${TEDGE_CONFIG_SYMLINK}
    fi
}

FILES:${PN} = "\ 
    ${bindir}/* \
    ${datadir}/contrib/collectd/collectd.conf \
    ${TEDGE_CONFIG_SYMLINK} \
    ${TEDGE_CONFIG_DIR} \
    ${TEDGE_CONFIG_DIR}/tedge.toml \
    ${TEDGE_CONFIG_DIR}/operations/c8y/c8y_RemoteAccessConnect \
    ${TEDGE_CONFIG_DIR}/mosquitto-conf/tedge-mosquitto.conf \
    ${TEDGE_CONFIG_DIR}/contrib/collectd \
    ${TEDGE_CONFIG_DIR}/sm-plugins/apt \
    ${@'${TEDGE_CONFIG_DIR}/sm-plugins/flow' if bb.utils.vercmp_string(d.getVar('PV'), '1.8') >= 0 else ''} \
"

require tedge-services.inc
