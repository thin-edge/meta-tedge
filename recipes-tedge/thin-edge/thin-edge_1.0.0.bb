SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV}"
SRCREV = "9cf69044581fc4046d52009f909b41af40124a95"
S = "${WORKDIR}/git"

SRC_URI += "\
file://0003-Cargo.toml-do-not-strip.patch \
"

# 

PACKAGES =+ "c8y-firmware-plugin c8y-remote-access-plugin tedge tedge-agent tedge-apt-plugin tedge-mapper tedge-watchdog"

# Configuration
#
# - support all package formats (deb first)
# - init configuration
# - systemd configuration


FILES:c8y-firmware-plugin      = "${bindir}/c8y-firmware-plugin"
FILES:c8y-remote-access-plugin = "${bindir}/c8y-remote-access-plugin"
FILES:tedge                    = "${bindir}/tedge"
FILES:tedge-agent              = "${bindir}/tedge-agent"
FILES:tedge-apt-plugin         = "${sysconfdir}/tedge/sm-plugins/apt"
FILES:tedge-mapper             = "${bindir}/tedge-mapper"
FILES:tedge-watchdog           = "${bindir}/tedge-watchdog"

require modules/c8y-firmware-plugin_1.0.0.inc
require modules/c8y-remote-access-plugin_1.0.0.inc
require modules/tedge-agent_1.0.0.inc
require modules/tedge-apt-plugin.inc
require modules/tedge_1.0.0.inc
require modules/tedge-mapper_1.0.0.inc
require modules/tedge-watchdog_1.0.0.inc

require thin-edge.inc
