# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.3.0"
SRC_URI[aarch64.md5sum] = "8816428869260661b2d704fcd64bc3c5"
SRC_URI[armv6.md5sum] = "b32502f0eb5610cff1b4b0909ece160c"
SRC_URI[armv7.md5sum] = "5d6cd3ac04ef1c16ecb6322d119ba03b"
SRC_URI[x86_64.md5sum] = "a2ac0ce96b11af2faa1276e3a1945f4e"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.5.0"
SRC_URI[openrc.md5sum] = "ca866b351b70befdf11882f0cb278e8d"
SRC_URI[systemd.md5sum] = "b0bbcbefdd85ae31e09e6ff22ab8035f"
SRC_URI[sysvinit.md5sum] = "28738a13f950a03aa5b388f57ff4a3af"

require tedge.inc
