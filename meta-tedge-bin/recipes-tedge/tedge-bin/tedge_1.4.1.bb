# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.4.1"
SRC_URI[aarch64.md5sum] = "ad98f480aaaf7a1c047b28dadf62db60"
SRC_URI[armv6.md5sum] = "0d4d2be12e8d1c8890d28f7f7908a612"
SRC_URI[armv7.md5sum] = "d377623dd47ba6462e3d3234ec5b5b37"
SRC_URI[x86_64.md5sum] = "7e38ce53e12cae228928025098101e36"
SRC_URI[riscv64.md5sum] = "0c22cea1350ca78c6a4aaaa13fb5a105"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.6.0"
SRC_URI[openrc.md5sum] = "515bfcb3ff01cb6929dda6c6922eb7a7"
SRC_URI[systemd.md5sum] = "579cf149b89256fb83c7eb44aa45ccb1"
SRC_URI[sysvinit.md5sum] = "493f1a2e66fa3e154c558b138e5da6bf"

require tedge.inc
