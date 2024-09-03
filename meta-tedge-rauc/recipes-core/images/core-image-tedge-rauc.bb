require recipes-core/images/core-image-tedge.bb

IMAGE_INSTALL:append = " \
    tedge-firmware-rauc \
    ${@bb.utils.contains('INIT_MANAGER','systemd','tedge-bootstrap','',d)} \
"

# Add Network Manager
IMAGE_INSTALL:append = " \ 
    networkmanager \
    monit \
"

# Optimizations for RAUC adaptive method 'block-hash-index'
# rootfs image size must to be 4K-aligned
# reference: https://github.com/rauc/meta-rauc-community/blob/master/meta-rauc-qemux86/recipes-core/images/core-image-minimal.bbappend
IMAGE_ROOTFS_ALIGNMENT = "4"
# ext4 block size should be set to 4K and use a fixed directory hash seed to
# reduce the image delta size (keep oe-core's 4K bytes-per-inode)
EXTRA_IMAGECMD:ext4 = "-i 4096 -b 4096 -E hash_seed=86ca73ff-7379-40bd-a098-fcb03a6e719d"
