inherit cargo
inherit systemd

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

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

# Install package scripts based on a type of package for all packages assigned to `PACKAGES` variable.
# Because not every tool (e.g. getent) is available during do_rootfs step, all preinst scripts are delayed to first boot.
# Same goes with postinst scripts as they require to be run after initial boot. 
# Use debian script for ipk packages as we currently don't support ipk scripts
python do_package:prepend () {
    for pkg in d.getVar('PACKAGES').split():
        pkg_type = d.getVar('PACKAGE_CLASSES').split()[0].replace("package_ipk", "deb") if "ipk" in d.getVar('PACKAGE_CLASSES') else d.getVar('PACKAGE_CLASSES').split()[0].replace("package_", "") 
        scripts_dir = "%s/git/configuration/package_scripts/_generated/%s/%s" %(d.getVar('WORKDIR'), pkg, pkg_type)
        
        if os.path.exists(scripts_dir):
            for script_name in os.listdir(scripts_dir):
                with open(os.path.join(scripts_dir, script_name), 'r') as script_file:
                    if script_name == "preinst":
                        d.prependVar('pkg_postinst_ontarget:%s' % pkg, script_file.read())
                    if script_name == "postinst":
                        d.appendVar('pkg_postinst_ontarget:%s' % pkg, script_file.read())
                    else:
                        d.appendVar('pkg_%s:%s' % (script_name, pkg), script_file.read())
}   
