require recipes-core/images/core-image-base.bb

IMAGE_INSTALL:append = " \
    tedge \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-bootstrap', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'tedge-inventory', '', d)} \
"
