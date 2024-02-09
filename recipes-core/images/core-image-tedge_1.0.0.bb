require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL:append = " \
    thin-edge \
    tedge \
    tedge-mapper \
    tedge-agent \
    c8y-firmware-plugin \
    c8y-remote-access-plugin \
    tedge-apt-plugin \
    tedge-watchdog \
"

