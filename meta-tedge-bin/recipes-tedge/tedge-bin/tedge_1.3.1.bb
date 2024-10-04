# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.3.1"
SRC_URI[aarch64.md5sum] = "0006c47d48ae214c236a07f863bb533b"
SRC_URI[armv6.md5sum] = "1892147ece0872665e323d00d3fd79e1"
SRC_URI[armv7.md5sum] = "8208b9e107805d4459a9b99ca195a766"
SRC_URI[x86_64.md5sum] = "c8b256a1cdef070b7d6bd1a2102f7460"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.5.0"
SRC_URI[openrc.md5sum] = "ca866b351b70befdf11882f0cb278e8d"
SRC_URI[systemd.md5sum] = "b0bbcbefdd85ae31e09e6ff22ab8035f"
SRC_URI[sysvinit.md5sum] = "28738a13f950a03aa5b388f57ff4a3af"

require tedge.inc
