DOCKER_DATA_DIR ?= "/data/docker"

do_install:append () {
    # Store docker files on persistent volume

    # Use 'use deprecated-key-path' setting so that older docker versions For docker < 19.x,
    # will not try to create the key under the /etc/docker folder, as this fails when the root-fs is read only
    # resulting in the docker service failing to start.
    # There is only a hint of this setting in the PR https://github.com/moby/moby/pull/32587
    #
    # On newer docker versions, the key file has been moved, see https://github.com/moby/moby/issues/41443
    # so once the docker version is upgraded the 'deprecated-key-path' setting can be removed (if it breaks the config)
    echo '{"data-root": "${DOCKER_DATA_DIR}","deprecated-key-path":"${DOCKER_DATA_DIR}/key.json"}' > "${D}${sysconfdir}/docker/daemon.json"
}

FILES:${PN} += " \
    ${sysconfdir}/docker/daemon.json \
"
