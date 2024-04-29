# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.0.1"
SRC_URI[aarch64.md5sum] = "15c07399d966e2358898acbda64eb2b2"
SRC_URI[armv6.md5sum] = "a1ef90a58c36064e92a398db70ecd3b8"
SRC_URI[armv7.md5sum] = "cd63debbc9bfb8e3e48fbcb53e4b8060"
SRC_URI[x86_64.md5sum] = "96826d16bcddf0e483f9650a72ffa0a3"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.3.0"
SRC_URI[openrc.md5sum] = "b47d51788d5cf0d83a7f138fb2cf037c"
SRC_URI[systemd.md5sum] = "c815f7666216ef5cb3d78be50eddc073"
SRC_URI[sysvinit.md5sum] = "7286a80f0d6b42cf5a0681f951f0e318"

require tedge.inc
