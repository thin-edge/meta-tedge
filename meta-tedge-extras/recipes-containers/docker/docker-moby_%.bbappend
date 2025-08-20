DOCKER_DATA_DIR ?= "/data/docker"

do_install:append:tedge-docker () {
    # Store docker files on persistent volume
    echo '{"data-root": "${DOCKER_DATA_DIR}"}' > "${D}${sysconfdir}/docker/daemon.json"
}

FILES:${PN} += " \
    ${sysconfdir}/docker/daemon.json \
"
