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
INIT_VERSION = "0.7.2"
SRC_URI[openrc.md5sum] = "1908ef396b448987ada3e8ed4f6cda61"
SRC_URI[systemd.md5sum] = "b6b90194363d1e08288b99f9d0e3948e"
SRC_URI[sysvinit.md5sum] = "fe8334fc012ca52579022cb6403c64ac"

require tedge.inc
