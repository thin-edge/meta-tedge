SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV};name=tedge"

SRC_URI += " \
    file://operations/c8y/c8y_RemoteAccessConnect \ 
    file://tedge.toml \
"

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

inherit cargo useradd

DEPENDS = "mosquitto"
RDEPENDS:${PN} = " mosquitto ca-certificates libgcc glibc-utils libxcrypt sudo collectd"

# This prevents disabling crates.io registry in cargo_do_configure task and
# allows cargo to fetch dependencies during the do_compile step.
#
# It's still not perfect, because ideally we'd want to download all the source
# code in the do_fetch step, but it's challenging because we'd have to either
# duplicate do_configure step just for fetching, or swap the order and run
# do_configure before do_fetch, which might be confusing for the users.
#
# Still, it makes the update-layer.sh script entirely obsolete by significantly
# improving the maintenance of the layer, simplifying updating to 2 steps:
#
# 1. Update recipe version and point to the new revision
# 2. Handle package/systemd configuration changes, if any.
#
# We'll be looking into how to do it in do_fetch step, but as long as we don't
# have it figured out, or somebody tells us we've broken their build, we're
# going for this approach.
do_compile[network] = "1"
CARGO_DISABLE_BITBAKE_VENDORING = "1"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system --gid 950 tedge"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge"

do_install:append () {
    # install collectd for tedge-mapper
    install -d ${D}${sysconfdir}/tedge/contrib/collectd
    install -m 0644 ${S}/configuration/contrib/collectd/collectd.conf ${D}${sysconfdir}/tedge/contrib/collectd

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

    # Add include to mosquitto.conf so tedge specific conf will be loaded
    if ! grep -q "include_dir /etc/tedge/mosquitto-conf" "${D}${sysconfdir}/mosquitto/mosquitto.conf"; then
        # Insert `include_dir /etc/tedge/mosquitto-conf` before any `include_dir`
        # directive so that all other partial conf files inherit the
        # `per_listener_settings` defined in /etc/tedge/mosquitto-conf.
        # `per_listener_settings` has to be defined once, before other listener
        # settings or else it causes the following error:
        #
        # Error: per_listener_settings must be set before any other security
        # settings.
        # Match any included_dir directive as different distributions have different default settings:
        #  On Fedora: `#include_dir`. mosquitto does not use a /etc/mosquitto/conf.d folder
        #  On Debian: `include_dir /etc/mosquitto/conf.d`. Uses a conf.d folder, so the tedge setting must be before this

        # Check if `include_dir` or `#include_dir` (as the latter could be a future problem if the user uncomments it)
        if grep -qE '^#?include_dir' ${D}${sysconfdir}/mosquitto/mosquitto.conf; then
            # insert tedge include_dir before the first `included_dir` (but only the first!)
            mosquitto_conf=$(awk '!found && /^#?include_dir/ \
            { print "include_dir /etc/tedge/mosquitto-conf"; found=1 }1' \
            "${D}${sysconfdir}/mosquitto/mosquitto.conf")
            echo "$mosquitto_conf" > "${D}${sysconfdir}/mosquitto/mosquitto.conf"
        else
            # config does not contain any include_dir directive, so we can safely append it
            echo "include_dir /etc/tedge/mosquitto-conf" >> "${D}${sysconfdir}/mosquitto/mosquitto.conf"
        fi
    fi
}

FILES:${PN} = "\ 
    ${bindir}/* \ 
    ${sysconfdir}/tedge \
    ${sysconfdir}/tedge/tedge.toml \
    ${sysconfdir}/tedge/operations/c8y/c8y_RemoteAccessConnect \
    ${sysconfdir}/tedge/contrib/collectd \
    ${sysconfdir}/tedge/sm-plugins/apt \
    ${sysconfdir}/tedge/sm-plugins/apt \
"

require tedge-services.inc
