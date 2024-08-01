# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.2.0"
SRC_URI[aarch64.md5sum] = "334cd74429ca70c9fa0ee67db6eb5f1f"
SRC_URI[armv6.md5sum] = "7f3027bb743a2e8663219d7b8912bb5a"
SRC_URI[armv7.md5sum] = "e3005e98131783f11729ef3f08dec141"
SRC_URI[x86_64.md5sum] = "dcf8c5d463a900f445079c12a0df3f0e"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.4.1"
SRC_URI[openrc.md5sum] = "1852d7742cb8ffcf0eb765b43895912d"
SRC_URI[systemd.md5sum] = "67e02f3c03145eada06525f8df39813f"
SRC_URI[sysvinit.md5sum] = "bed2e55a38a560b3ac4c0baa796b33ee"

require tedge.inc
