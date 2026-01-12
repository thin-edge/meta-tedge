# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.6.1"
SRC_URI[aarch64.md5sum] = "6b0a25ae273cb74ba66bc2f0ef25a4dd"
SRC_URI[armv6.md5sum] = "d93a80ce5d0e7921b0a357d40cae36e2"
SRC_URI[armv7.md5sum] = "0ce61130da55d1e2b5f650423565ba8c"
SRC_URI[x86_64.md5sum] = "6294b4eadf8f5a522b6a7cf7bfb648bb"
SRC_URI[riscv64.md5sum] = "3a48d8fc95bf872e45284381a7645127"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

# required for other files from the tedge source repo
SRCREV_tedge = "e7c50099ce6b418c411e1ae049b3c6d997c18a1f"
SRCREV_FORMAT = "tedge"
SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

require tedge.inc
require tedge-diag.inc
