SUMMARY = "Native build of tedge for use during target recipe do_install"
DESCRIPTION = "Builds the tedge binary for the host machine so that target recipes can invoke it at build time (e.g. to initialise folder structures)"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

SRCREV_tedge = "${AUTOREV}"
SRCREV_FORMAT = "tedge"
PV = "0.0+git${SRCPV}"

inherit cargo-tedge native

# Build only the tedge binary for the host
CARGO_BUILD_FLAGS:append = " --bin tedge"
