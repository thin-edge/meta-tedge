inherit useradd

# Used fix uid/gid to avoid permission problems on /data
GROUPADD_PARAM:${PN} = "--system --gid 960 mosquitto"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /bin/false --uid 961 --gid 960 mosquitto"

do_install:append () {
    # Add include to mosquitto.conf so tedge specific conf will be loaded
    echo "include_dir /etc/tedge/mosquitto-conf" >> "${D}${sysconfdir}/mosquitto/mosquitto.conf"
}
