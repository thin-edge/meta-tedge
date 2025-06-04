# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.5.1"
SRC_URI[aarch64.md5sum] = "3f11f16601a10f880967ba00aa23e511"
SRC_URI[armv6.md5sum] = "260679a0c88d2d577c9d50fc055ba79c"
SRC_URI[armv7.md5sum] = "2e07da81c0a512fd7494011296c29c6c"
SRC_URI[x86_64.md5sum] = "b46f1fb1a3252c4ff534e02f02b1c32f"
SRC_URI[riscv64.md5sum] = "7bff0395f7c527be5c06f1f31ab38778"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

require tedge.inc
