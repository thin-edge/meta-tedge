SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV}"
SRCREV = "4f23dd24e17e9c6359147c4c46c4b70a9f0e869f"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0001-Cargo.toml-do-not-abort-on-panic.patch \
"

PACKAGES =+ "c8y-configuration-plugin c8y-firmware-plugin c8y-log-plugin c8y-remote-access-plugin tedge tedge-agent tedge-apt-plugin tedge-mapper tedge-watchdog"

FILES:c8y-configuration-plugin = "${bindir}/c8y-configuration-plugin"
FILES:c8y-firmware-plugin      = "${bindir}/c8y-firmware-plugin"
FILES:c8y-log-plugin           = "${bindir}/c8y-log-plugin"
FILES:c8y-remote-access-plugin = "${bindir}/c8y-remote-access-plugin"
FILES:tedge                    = "${bindir}/tedge"
FILES:tedge-agent              = "${bindir}/tedge-agent"
FILES:tedge-apt-plugin         = "${sysconfdir}/tedge/sm-plugins/apt"
FILES:tedge-mapper             = "${bindir}/tedge-mapper"
FILES:tedge-watchdog           = "${bindir}/tedge-watchdog"

require modules/c8y-configuration-plugin.inc
require modules/c8y-firmware-plugin.inc
require modules/c8y-log-plugin.inc
require modules/c8y-remote-access-plugin.inc
require modules/tedge-agent.inc
require modules/tedge-apt-plugin.inc
require modules/tedge.inc
require modules/tedge-mapper.inc
require modules/tedge-watchdog.inc

require thin-edge.inc
