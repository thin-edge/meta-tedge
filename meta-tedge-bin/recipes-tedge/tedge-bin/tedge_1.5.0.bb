# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.5.0"
SRC_URI[aarch64.md5sum] = "50ccdac0bde6dc032b7010c0ed62a4ab"
SRC_URI[armv6.md5sum] = "64c83d9f4059693bcd792aa3626d6049"
SRC_URI[armv7.md5sum] = "e1e40ca4d3e3e8dd21d772b5a0177de4"
SRC_URI[x86_64.md5sum] = "3ec623e0ecc12cbe650e5bf69501364b"
SRC_URI[riscv64.md5sum] = "d3a025149017d3848976e4d1c578e640"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.3"
SRC_URI[openrc.md5sum] = "3d6ab19a12e1d3d2b963b2bb709e3bf2"
SRC_URI[systemd.md5sum] = "73d4070f339e96369b9e7b4f3c3c3b72"
SRC_URI[sysvinit.md5sum] = "fd35bc27226c235ca5c1d8081480f357"

require tedge.inc
