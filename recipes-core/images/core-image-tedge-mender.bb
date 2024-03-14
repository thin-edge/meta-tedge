require core-image-tedge.bb

IMAGE_INSTALL:append = " \
    tedge-state-scripts \
    tedge-firmware \
"

# Add Network Manager
IMAGE_INSTALL:append = " \ 
    networkmanager \
    networkmanager-bash-completion \
    networkmanager-nmtui \
"

inherit extrausers
# Used fix uid/gid to avoid permission problems on /data
EXTRA_USERS_PARAMS = "\
    groupmod -g 960 mosquitto; \
    usermod -u 961 mosquitto; \
"