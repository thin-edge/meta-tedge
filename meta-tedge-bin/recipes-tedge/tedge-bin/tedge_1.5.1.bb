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
INIT_VERSION = "0.7.3"
SRC_URI[openrc.md5sum] = "3d6ab19a12e1d3d2b963b2bb709e3bf2"
SRC_URI[systemd.md5sum] = "73d4070f339e96369b9e7b4f3c3c3b72"
SRC_URI[sysvinit.md5sum] = "fd35bc27226c235ca5c1d8081480f357"

require tedge.inc
