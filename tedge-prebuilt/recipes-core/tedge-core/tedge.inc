LICENSE_FLAGS = "tedge"

FETCHCMD_wget_append = " --no-check-certificate "

RDEPENDS_${PN} += " mosquitto"
S = "${WORKDIR}"

do_install () {
    install -d -m 0755 ${D}
}

# All the files are provided in a binary package, and keeping all the
# files in a single package causes packaging QA errors and warnings.
# Avoid these packaging failure by skipping all the QA checks
INSANE_SKIP_${PN} = "${ERROR_QA} ${WARN_QA}"

# Inhibit warnings about files being stripped, we can't do anything about it.
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PACKAGES = "${PN}-sources ${PN}"

RPROVIDES_${PN} = "tedge"
PROVIDES += "tedge"

inherit update-alternatives

pkg_postinst_${PN} () {
    ${bindir}/tedge init
}
