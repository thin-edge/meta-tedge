# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.4.0"
SRC_URI[aarch64.md5sum] = "8ff3ad40d6ca32f93ab4e38bf414b50a"
SRC_URI[armv6.md5sum] = "ae3564517489a4f6f2dd325bf199a5d8"
SRC_URI[armv7.md5sum] = "e502d230b72240ac0732586ff049b2e3"
SRC_URI[x86_64.md5sum] = "5cb24872ab3ab7c29045d13bf9d34ec8"
SRC_URI[riscv64.md5sum] = "26bfdd59d1760578b69b7288ab71dbd9"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.6.0"
SRC_URI[openrc.md5sum] = "515bfcb3ff01cb6929dda6c6922eb7a7"
SRC_URI[systemd.md5sum] = "579cf149b89256fb83c7eb44aa45ccb1"
SRC_URI[sysvinit.md5sum] = "493f1a2e66fa3e154c558b138e5da6bf"

require tedge.inc
