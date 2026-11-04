# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "2.1.0"
SRC_URI[aarch64.md5sum] = "0d0bb0158d3a8a6425098066d4214ffb"
SRC_URI[armv6.md5sum] = "e79576d9acbf1d48faffdd6c88f7891d"
SRC_URI[armv7.md5sum] = "ffdcf5eb264d445e5d01417293042b44"
SRC_URI[x86_64.md5sum] = "5142a1beaf0d7c7c438d537f09627949"
SRC_URI[riscv64.md5sum] = "40ac8884e74169c38a3a66871d8e9949"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.7"
SRC_URI[openrc.md5sum] = "6ba95e9dcd20606f145828edf989db76"
SRC_URI[systemd.md5sum] = "a76a06d926b5ba85fa707fe76b6fef7a"
SRC_URI[sysvinit.md5sum] = "cb569a58d967dcf8f09b4260d2d55a0a"

# checkout source
SRCREV_tedge = "35bd52846edde1bce7ac3648da875c0f962d9cf3"
SRCREV_FORMAT = "tedge"
SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
require tedge-config.inc
require tedge-flows.inc
