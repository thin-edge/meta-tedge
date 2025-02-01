require recipes-core/images/core-image-base.bb

IMAGE_INSTALL:append = " \
    tedge \
    tedge-command-plugin \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-bootstrap', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-sethostname', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-inventory', '', d)} \
"
