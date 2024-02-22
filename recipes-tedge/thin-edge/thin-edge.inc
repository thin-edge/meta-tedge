inherit cargo

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

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

# Add all package scrpits located in thin-edge repo.
# Skip package if assigned to `TEDGE_EXCLUDE` variable.
# Because not every tool (e.g. getent) is available during do_rootfs step, all preinst scripts are delayed to first boot.
# Same goes with postinst scripts as they need to be executed after initial boot. 
# Use debian script for ipk packages as we currently don't support ipk scripts.
python do_package:prepend () {
    package_type = d.getVar('PACKAGE_CLASSES').split()[0].replace("package_ipk", "deb") if "ipk" in d.getVar('PACKAGE_CLASSES') else d.getVar('PACKAGE_CLASSES').split()[0].replace("package_", "") 
    package_dir = "%s/git/configuration/package_scripts/_generated" %(d.getVar('WORKDIR'))
        
    for package in os.listdir(package_dir):
        if package in d.getVar('TEDGE_EXCLUDE').split():
            continue
        
        scripts_dir = os.path.join(package_dir, package, package_type)

        for script_name in os.listdir(scripts_dir):
            with open(os.path.join(scripts_dir, script_name), 'r') as script_file:
                if script_name == "preinst":
                    d.prependVar('pkg_postinst_ontarget:%s' % d.getVar('PN'), script_file.read())
                elif script_name == "postinst":
                    d.appendVar('pkg_postinst_ontarget:%s' % d.getVar('PN'), script_file.read())
                else:
                    d.appendVar('pkg_%s:%s' % (script_name, d.getVar('PN')), script_file.read())
}   

FILES:${PN} = "\ 
    ${bindir}/* \ 
    ${sysconfdir}/tedge/contrib/collectd \
    ${sysconfdir}/tedge/sm-plugins/apt \
"

require init-manager.inc