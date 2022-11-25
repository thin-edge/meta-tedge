require recipes-core/images/core-image-minimal.bb

# Minimal image to run thin-edge

IMAGE_INSTALL:append = " \
        tedge \
        tedge-mapper \
        tedge-agent \
        c8y-configuration-plugin \
        c8y-log-plugin \
        tedge-apama-plugin \
        tedge-apt-plugin \
        tedge-dummy-plugin \
        tedge-watchdog \
"

# Additional features for developers

IMAGE_INSTALL:append = " \
        mosquitto-clients \
"
