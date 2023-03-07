require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL:append = " \
    thin-edge \
    tedge \
    tedge-mapper \
    tedge-agent \
    c8y-configuration-plugin \
    c8y-log-plugin \
    tedge-apt-plugin \
    tedge-dummy-plugin \
    tedge-watchdog \
"
