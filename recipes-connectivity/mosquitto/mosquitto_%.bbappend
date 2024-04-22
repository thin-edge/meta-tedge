inherit useradd

# Used fix uid/gid to avoid permission problems on /data
GROUPADD_PARAM:${PN} = "--system --gid 960 mosquitto"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /bin/false --uid 961 --gid 960 mosquitto"