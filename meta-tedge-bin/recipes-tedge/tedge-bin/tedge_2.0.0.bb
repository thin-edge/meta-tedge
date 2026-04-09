# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "2.0.0"
SRC_URI[aarch64.md5sum] = "2737e1e7bf8c71afd15da227df332805"
SRC_URI[armv6.md5sum] = "3b5b8629f8d3d71c24c91d76dde153e4"
SRC_URI[armv7.md5sum] = "2bba637ed78c139c08fb31a4362e3b87"
SRC_URI[x86_64.md5sum] = "d1d826bc424fff218b56d9ef37dff3be"
SRC_URI[riscv64.md5sum] = "c830f64ac8489798b43645386c34e53b"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

# checkout source
SRCREV_tedge = "1dc8a3fdfb51532005dfee8fcca7a117a8a1cb85"
SRCREV_FORMAT = "tedge"
SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
require tedge-config.inc
require tedge-flows.inc
