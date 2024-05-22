# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.1.0"
SRC_URI[aarch64.md5sum] = "b8b9941cdf026cfde9a650c06cd12a9e"
SRC_URI[armv6.md5sum] = "e673ec9de42b59cfe2cdba2aa7492012"
SRC_URI[armv7.md5sum] = "d67d2e98da8f627bfa321655022cd769"
SRC_URI[x86_64.md5sum] = "fc86e95c1d75a9a715d768ceb649e070"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.4.1"
SRC_URI[openrc.md5sum] = "1852d7742cb8ffcf0eb765b43895912d"
SRC_URI[systemd.md5sum] = "67e02f3c03145eada06525f8df39813f"
SRC_URI[sysvinit.md5sum] = "bed2e55a38a560b3ac4c0baa796b33ee"

require tedge.inc
