# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.1.1"
SRC_URI[aarch64.md5sum] = "166c14c7e101b7ffd0d6d9e0332239f5"
SRC_URI[armv6.md5sum] = "fa368894f6c0c18affc932eb7bfe4460"
SRC_URI[armv7.md5sum] = "ccf738b4a27505903c179263c8d77458"
SRC_URI[x86_64.md5sum] = "64f5e3a865ce0cdb6aea4c2083049205"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.4.1"
SRC_URI[openrc.md5sum] = "1852d7742cb8ffcf0eb765b43895912d"
SRC_URI[systemd.md5sum] = "67e02f3c03145eada06525f8df39813f"
SRC_URI[sysvinit.md5sum] = "bed2e55a38a560b3ac4c0baa796b33ee"

require tedge.inc
