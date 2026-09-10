SUMMARY = "Default systemd journald configuration"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

SRC_URI = "file://00-storage-limits.conf"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/systemd/journald.conf.d
    install -m 0644 ${UNPACKDIR}/00-storage-limits.conf ${D}${sysconfdir}/systemd/journald.conf.d/00-storage-limits.conf
}

FILES:${PN} = "${sysconfdir}/systemd/journald.conf.d/00-storage-limits.conf"
