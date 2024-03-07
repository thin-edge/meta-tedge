require core-image-tedge.bb

IMAGE_INSTALL:append = " \
    tedge-state-scripts \
    tedge-firmware \
    ${@bb.utils.contains('INIT_MANAGER','systemd','tedge-bootstrap','',d)} \
"

# Add Network Manager
IMAGE_INSTALL += "networkmanager networkmanager-bash-completion networkmanager-nmtui"