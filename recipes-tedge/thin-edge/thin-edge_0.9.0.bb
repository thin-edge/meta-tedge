SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV}"
SRCREV = "ad9828864484f902fbae502f3856bb94a6c21d8a"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0001-Cargo.toml-do-not-abort-on-panic.patch \
file://0002-Cargo.toml-remove-workspace-inheritance.patch \
"

PACKAGES =+ "c8y-configuration-plugin c8y-log-plugin tedge tedge-agent tedge-apt-plugin tedge-mapper tedge-watchdog"

FILES:c8y-configuration-plugin = "${bindir}/c8y-configuration-plugin"
FILES:c8y-log-plugin           = "${bindir}/c8y-log-plugin"
FILES:tedge                    = "${bindir}/tedge"
FILES:tedge-agent              = "${bindir}/tedge-agent"
FILES:tedge-apt-plugin         = "${sysconfdir}/tedge/sm-plugins/apt"
FILES:tedge-mapper             = "${bindir}/tedge-mapper"
FILES:tedge-watchdog           = "${bindir}/tedge-watchdog"

require modules/c8y-configuration-plugin.inc
require modules/c8y-log-plugin.inc
require modules/tedge-agent.inc
require modules/tedge-apt-plugin.inc
require modules/tedge.inc
require modules/tedge-mapper.inc
require modules/tedge-watchdog.inc

require thin-edge.inc
