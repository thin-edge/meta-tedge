require recipes-core/images/core-image-tedge.bb

IMAGE_INSTALL:append = " \
    tedge-state-scripts \
    tedge-firmware \
"

# Add Network Manager
IMAGE_INSTALL:append = " \ 
    networkmanager \
    networkmanager-nmtui \
"
