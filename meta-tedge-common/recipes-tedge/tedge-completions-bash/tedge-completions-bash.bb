SUMMARY = "bash-completions for tedge"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

inherit allarch

RDEPENDS:${PN} += "bash-completion"

SRC_URI += " \
    file://tedge-completions.bash \
"

do_install () {
    install -d "${D}${datadir}/bash-completion/completions"
    install -m 0644 "${UNPACKDIR}/tedge-completions.bash" "${D}${datadir}/bash-completion/completions/tedge"
}

FILES:${PN} += " \
    ${datadir}/bash-completion/completions/tedge \
"
