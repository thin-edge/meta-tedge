# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.0.0"
SRC_URI[aarch64.md5sum] = "daa8ad686a78e99458dc7e1dcafa21ad"
SRC_URI[armv6.md5sum] = "bc7d9da45afe4d3bf4368f4a7874d208"
SRC_URI[armv7.md5sum] = "15e8527ee551f48cb54bec3e988d9438"
SRC_URI[x86_64.md5sum] = "f35f3374650cd084c326fd271e7f478e"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.3.0"
SRC_URI[openrc.md5sum] = "b47d51788d5cf0d83a7f138fb2cf037c"
SRC_URI[systemd.md5sum] = "c815f7666216ef5cb3d78be50eddc073"
SRC_URI[sysvinit.md5sum] = "7286a80f0d6b42cf5a0681f951f0e318"

require tedge.inc
