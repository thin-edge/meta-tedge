inherit cargo
inherit systemd

SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV}"
SRCREV = "ad9828864484f902fbae502f3856bb94a6c21d8a"
S = "${WORKDIR}/git"

include dependencies.inc

SRC_URI += "file://0001-Cargo.toml-do-not-abort-on-panic.patch"

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

PACKAGES =+ "c8y-configuration-plugin c8y-log-plugin tedge tedge-agent tedge-apt-plugin tedge-dummy-plugin tedge-mapper tedge-watchdog"

FILES:c8y-configuration-plugin = "${bindir}/c8y_configuration_plugin"
FILES:c8y-log-plugin           = "${bindir}/c8y_log_plugin"
FILES:tedge                    = "${bindir}/tedge"
FILES:tedge-agent              = "${bindir}/tedge_agent"
FILES:tedge-apt-plugin         = "${sysconfdir}/tedge/sm-plugins/apt"
FILES:tedge-dummy-plugin       = "${sysconfdir}/tedge/sm-plugins/dummy"
FILES:tedge-mapper             = "${bindir}/tedge_mapper"
FILES:tedge-watchdog           = "${bindir}/tedge_watchdog"

include c8y_configuration_plugin.inc
include c8y_log_plugin.inc
include tedge_agent.inc
include tedge_apt_plugin.inc
include tedge_dummy_plugin.inc
include tedge.inc
include tedge_mapper.inc
include tedge_watchdog.inc
