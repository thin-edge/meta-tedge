# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.4.2"
SRC_URI[aarch64.md5sum] = "dbc8526dcc00870075f42e98231e8978"
SRC_URI[armv6.md5sum] = "c37b10652eaba3b7e965cc0195262080"
SRC_URI[armv7.md5sum] = "b1369a0a49bc6e34a897eb7e8a2d7b68"
SRC_URI[x86_64.md5sum] = "59508a3a684f69b53245e33a013c64df"
SRC_URI[riscv64.md5sum] = "e4a1aff5cb923beb1c0eccf95911c3b5"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.2"
SRC_URI[openrc.md5sum] = "1908ef396b448987ada3e8ed4f6cda61"
SRC_URI[systemd.md5sum] = "b6b90194363d1e08288b99f9d0e3948e"
SRC_URI[sysvinit.md5sum] = "fe8334fc012ca52579022cb6403c64ac"

require tedge.inc
