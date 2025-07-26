# Create tedge user and group
inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system --gid 950 tedge"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /sbin/nologin --uid 951 --gid 950 tedge"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

do_install:append () {
    install -d ${D}${TEDGE_CONFIG_DIR}
    chown -R tedge:tedge "${D}${TEDGE_CONFIG_DIR}"
}
