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
INIT_VERSION = "0.6.1"
SRC_URI[openrc.md5sum] = "c59ead922897c5e277398db1bd3f2539"
SRC_URI[systemd.md5sum] = "d489b415ac05abb8572111ef9e1efbd9"
SRC_URI[sysvinit.md5sum] = "3929e983d643027dfa65309631c420d4"

require tedge.inc
