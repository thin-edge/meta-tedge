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
INIT_VERSION = "0.6.1"
SRC_URI[openrc.md5sum] = "c59ead922897c5e277398db1bd3f2539"
SRC_URI[systemd.md5sum] = "d489b415ac05abb8572111ef9e1efbd9"
SRC_URI[sysvinit.md5sum] = "3929e983d643027dfa65309631c420d4"

require tedge.inc
