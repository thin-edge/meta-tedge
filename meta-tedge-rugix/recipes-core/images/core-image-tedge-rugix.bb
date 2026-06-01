require recipes-core/images/core-image-tedge.bb

IMAGE_INSTALL:append = " \
    tedge-firmware-rugix \
    firmware-auto-rollback \
    tedge-inventory-rugix \
"
