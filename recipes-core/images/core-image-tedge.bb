require recipes-core/images/core-image-base.bb

IMAGE_INSTALL:append = " \
    thin-edge \
    ${@bb.utils.contains('INIT_MANAGER','systemd','tedge-bootstrap','',d)} \
    ${@bb.utils.contains('INIT_MANAGER','systemd','tedge-inventory','',d)} \
"

# Used fix uid/gid to avoid permission problems on /data
inherit extrausers
EXTRA_USERS_PARAMS += "\
    groupadd --system --gid 950 tedge; \
    useradd --system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge; \
    groupmod -g 960 mosquitto; \
    usermod -u 961 mosquitto; \
"