require core-image-tedge.bb

# Add mender related recipes
IMAGE_INSTALL:append = "\
    tedge-state-scripts \
    tedge-bootstrap \
    tedge-firmware \ 
    tedge-inventory \
"
# Add Network Manager
IMAGE_INSTALL:append = "\
    networkmanager \
    networkmanager-bash-completion \
    networkmanager-nmtui \
"

# Used fix uid/gid to avoid permission problems on /data
inherit extrausers

EXTRA_USERS_PARAMS += "\
    groupadd --system --gid 950 tedge; \
    useradd --system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge; \
    groupmod -g 960 mosquitto; \
    usermod -u 961 mosquitto; \
"