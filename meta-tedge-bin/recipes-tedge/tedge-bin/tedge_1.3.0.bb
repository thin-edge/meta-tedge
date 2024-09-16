# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.3.0"
SRC_URI[aarch64.md5sum] = "8816428869260661b2d704fcd64bc3c5"
SRC_URI[armv6.md5sum] = "b32502f0eb5610cff1b4b0909ece160c"
SRC_URI[armv7.md5sum] = "5d6cd3ac04ef1c16ecb6322d119ba03b"
SRC_URI[x86_64.md5sum] = "a2ac0ce96b11af2faa1276e3a1945f4e"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.4.1"
SRC_URI[openrc.md5sum] = "1852d7742cb8ffcf0eb765b43895912d"
SRC_URI[systemd.md5sum] = "67e02f3c03145eada06525f8df39813f"
SRC_URI[sysvinit.md5sum] = "bed2e55a38a560b3ac4c0baa796b33ee"

require tedge.inc
