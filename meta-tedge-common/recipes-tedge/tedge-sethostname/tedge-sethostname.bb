LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} += " tedge-bootstrap"

inherit allarch

SRC_URI += " \
    file://tedge-sethostname \
"

do_install () {
    install -d "${D}${datadir}/tedge-bootstrap/scripts.d"
    install -m 0755 "${UNPACKDIR}/tedge-sethostname" -T "${D}${datadir}/tedge-bootstrap/scripts.d/70_tedge-sethostname"
}

FILES:${PN} += " \
    ${datadir}/tedge-bootstrap/scripts.d/70_tedge-sethostname \
"
