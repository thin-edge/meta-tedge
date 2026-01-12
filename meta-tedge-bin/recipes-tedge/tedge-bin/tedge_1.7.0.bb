# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.7.0"
SRC_URI[aarch64.md5sum] = "59e6e849dfe61ce4ac4eaa0a117a07e4"
SRC_URI[armv6.md5sum] = "55b2fbbd109e2f1ac4955d1fb88ff4ad"
SRC_URI[armv7.md5sum] = "35af1a4f137dee257a645f05dc6949aa"
SRC_URI[x86_64.md5sum] = "492b11d6e3b40a8070e7eacc145d6539"
SRC_URI[riscv64.md5sum] = "05e2ae808a740497400c738c384444b1"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

# required for other files from the tedge source repo
SRCREV_tedge = "404a0c54d811ae107c43b574a0829cc8fa5153e1"
SRCREV_FORMAT = "tedge"
SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
