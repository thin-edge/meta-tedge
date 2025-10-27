DOCKER_DATA_DIR ?= "/data/docker"
DOCKER_DRIVER ?= "journald"

do_install:append:tedge-docker () {
    # Store docker files on persistent volume
    echo "{
    \"data-root\": \"${DOCKER_DATA_DIR}\",
    \"log-driver\": \"${DOCKER_DRIVER}\"
}" > ${D}${sysconfdir}/docker/daemon.json

}

FILES:${PN} += " \
    ${sysconfdir}/docker/daemon.json \
"
