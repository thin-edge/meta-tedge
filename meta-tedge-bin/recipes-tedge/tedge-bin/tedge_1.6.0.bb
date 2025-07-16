# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.6.0"
SRC_URI[aarch64.md5sum] = "00bf610ef8e6be6dc4c524c411347d76"
SRC_URI[armv6.md5sum] = "029db8ba6e45116966e566db2d3512a9"
SRC_URI[armv7.md5sum] = "13856e166ba08f64eb637da406f8a4db"
SRC_URI[x86_64.md5sum] = "5cc482caad1183ccbb2266aeda964803"
SRC_URI[riscv64.md5sum] = "e9872ebc993238f82cecd60be9e4cb8a"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

require tedge.inc
