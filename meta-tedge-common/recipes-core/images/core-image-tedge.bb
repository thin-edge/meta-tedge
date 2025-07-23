require recipes-core/images/core-image-base.bb

IMAGE_INSTALL:append = " \
    tedge \
    tedge-command-plugin \
    opensc \
    gnutls-bin \
    vnstat \
    less \
    zsh \
    tedge-completions-zsh \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-bootstrap', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-sethostname', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-inventory', '', d)} \
"

# Set default shell
ROOTFS_POSTPROCESS_COMMAND += "change_default_shell;"
change_default_shell() {
    if [ -f ${IMAGE_ROOTFS}/bin/zsh ]; then
        chsh -R ${IMAGE_ROOTFS} -s /bin/zsh
    fi
}

# Create default bashrc file
ROOTFS_POSTPROCESS_COMMAND += "modify_default_bashrc;"
modify_default_bashrc() {
    if [ -f ${IMAGE_ROOTFS}/etc/skel/.bashrc ]; then
        echo '[ -f /etc/bash_completion ] && . /etc/bash_completion' >> ${IMAGE_ROOTFS}/etc/skel/.bashrc
        cp ${IMAGE_ROOTFS}${sysconfdir}/skel/.bashrc ${IMAGE_ROOTFS}${ROOT_HOME}/.bashrc
    fi
}
