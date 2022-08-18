require recipes-extended/images/core-image-full-cmdline.bb

IMAGE_INSTALL += " tedge tedge-mapper tedge-agent c8y-configuration-plugin c8y-log-plugin tedge-apama-plugin tedge-apt-plugin tedge-dummy-plugin tedge-watchdog mosquitto ca-certificates util-linux"


# On Ubuntu, which is primary target of thin-edge.io, directory `/run/lock`
# has `1777` permissions, which enables any user to freely create lockfiles
# there. Thin-edge.io relies on write permissions to the lock directory to
# create lockfiles for mappers. Thus we need to change the default permissions
# in Yocto rootfs.

ROOTFS_POSTPROCESS_COMMAND += "chmod_rootfs;"

chmod_rootfs() {
    sed -i 's/d \/run\/lock 0755 root root -/d \/run\/lock 1777 root root -/' ${IMAGE_ROOTFS}/usr/lib/tmpfiles.d/legacy.conf
}
