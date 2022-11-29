require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL:append = " \
        tedge  \
        tedge-mapper  \
        tedge-agent \
        c8y-configuration-plugin \
        c8y-log-plugin \
        tedge-apama-plugin \
        tedge-apt-plugin \
        tedge-dummy-plugin \
        tedge-watchdog \
"

# Additional packages

IMAGE_INSTALL:append = " \
        mosquitto-clients \
"