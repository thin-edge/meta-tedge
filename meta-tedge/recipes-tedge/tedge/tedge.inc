SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

inherit cargo-tedge useradd

RDEPENDS:${PN} = " mosquitto ca-certificates libgcc glibc-utils libxcrypt sudo collectd"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system --gid 950 tedge"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge"

do_install:append () {
    # install collectd for tedge-mapper
    install -d ${D}${sysconfdir}/tedge/contrib/collectd
    install -m 0644 ${S}/configuration/contrib/collectd/collectd.conf ${D}${sysconfdir}/tedge/contrib/collectd

    # Create symlink for tedge-apt-plugin in `/etc/tedge/sm-plugins`
    install -d ${D}${sysconfdir}/tedge/sm-plugins
    ln -s -r  ${D}${bindir}/tedge-apt-plugin ${D}${sysconfdir}/tedge/sm-plugins/apt

    # Remove packages indicated in `TEDGE_EXCLUDE` (NOTE: they cannot be part of tedge-binary)
    for package in ${@d.getVar('TEDGE_EXCLUDE')}; do 
        rm -f ${D}${bindir}/${package}
    done
}
 
pkg_postinst_ontarget:${PN} () {
    ### Create file in /etc/sudoers.d directory. With this configuration, the tedge user have the right to call the tedge command with sudo rights, which is required for system-wide configuration in "/etc/tedge"
    if [ -d /etc/sudoers.d ]; then
        echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/tedge, /etc/tedge/sm-plugins/[a-zA-Z0-9]*, /bin/sync, /sbin/init, /usr/bin/tedge-write /etc/*, /usr/bin/tedge-write /data/*" >/etc/sudoers.d/tedge

        if [ -f "/etc/sudoers.d/010_pi-nopasswd" ]; then
            echo "tedge   ALL = (ALL) NOPASSWD: /usr/bin/tedge, /etc/tedge/sm-plugins/[a-zA-Z0-9]*, /bin/sync, /sbin/init" >/etc/sudoers.d/tedge-nopasswd
        fi
    fi

    ### Add include to mosquitto.conf so tedge specific conf will be loaded
    if [ -f /etc/mosquitto/mosquitto.conf ]; then
        if ! grep -q "include_dir /etc/tedge/mosquitto-conf" "/etc/mosquitto/mosquitto.conf"; then
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
            if grep -qE '^#?include_dir' /etc/mosquitto/mosquitto.conf; then
                # insert tedge include_dir before the first `included_dir` (but only the first!)
                mosquitto_conf=$(awk '!found && /^#?include_dir/ \
                { print "include_dir /etc/tedge/mosquitto-conf"; found=1 }1' \
                /etc/mosquitto/mosquitto.conf)
                echo "$mosquitto_conf" > /etc/mosquitto/mosquitto.conf
            else
                # config does not contain any include_dir directive, so we can safely append it
                echo "include_dir /etc/tedge/mosquitto-conf" >> /etc/mosquitto/mosquitto.conf
            fi
        fi
    fi

    # Initialize the tedge
    tedge init

    if command -v c8y-remote-access-plugin >/dev/null 2>&1; then
        c8y-remote-access-plugin --init
    fi

    # reduce number of packages shown (supported only from >= 1.1.0)
    tedge config set software.plugin.exclude "^(glibc|lib|kernel-|iptables-module).*" || true
}

FILES:${PN} = "\ 
    ${bindir}/* \ 
    ${sysconfdir}/tedge/contrib/collectd \
    ${sysconfdir}/tedge/sm-plugins/apt \
"

require tedge-services.inc
