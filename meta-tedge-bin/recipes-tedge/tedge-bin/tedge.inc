SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "CLOSED"
RDEPENDS:${PN} += " mosquitto ca-certificates libgcc glibc-utils libxcrypt sudo"

inherit logging
inherit bin_package useradd

INSANE_SKIP:${PN} = "already-stripped"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system --gid 950 tedge"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge"

do_install:append () {
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
    elif target == "riscv64" or target == "riscv64gc":
        tedgePkg = "tedge-riscv64"
    else:
        bb.warn("Target architecture '%s' is not supported by the meta-tedge-bin layer" % target)
        raise bb.parse.SkipPackage("Target architecture '%s' is not supported by the meta-tedge-bin layer" % target)
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
        bb.warn("Init manager '%s' is not supported by the meta-tedge-bin layer" % initManager)
        raise bb.parse.SkipPackage("Init manager '%s' is not supported by the meta-tedge-bin layer" % initManager)
    return tedgeService

TEDGE_PKG = "${@get_tedge_pkg(d)}"
TEDGE_SERVICE = "${@get_tedge_service(d)}"

require architectures/${TEDGE_PKG}.inc
require init-managers/${TEDGE_SERVICE}.inc
