# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "2.0.1"
SRC_URI[aarch64.md5sum] = "5662cda07eedf8648542c4062ee0e4f3"
SRC_URI[armv6.md5sum] = "dd9fc7d71aea2827a096861279c2a051"
SRC_URI[armv7.md5sum] = "02a9763226bf91ccfd36f618e04888b7"
SRC_URI[x86_64.md5sum] = "914c36c63793d6a34adbe321b0db6715"
SRC_URI[riscv64.md5sum] = "3f7b22d326d141664d9e7fb73e5a5de9"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

# checkout source
SRCREV_tedge = "19662f12d8df2ad0bab60d749e4133b4dbd7844e"
SRCREV_FORMAT = "tedge"
SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
require tedge-config.inc
require tedge-flows.inc
