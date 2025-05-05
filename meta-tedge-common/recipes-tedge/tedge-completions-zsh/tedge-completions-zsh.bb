SUMMARY = "zsh-completions for tedge"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10" 

inherit allarch

RDEPENDS:${PN} += "zsh"

SRC_URI += " \
    file://tedge-completions.zsh \
    file://zshrc \
"

do_install () {
    install -d ${D}${datadir}/zsh/site-functions
    install -m 0644 ${WORKDIR}/tedge-completions.zsh ${D}${datadir}/zsh/site-functions/_tedge

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/zshrc ${D}${sysconfdir}
}

FILES:${PN} += " \
    ${datadir}/zsh/site-functions/_tedge \
    ${sysconfdir}/zshrc \
"
