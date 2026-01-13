# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "1.7.1"
SRC_URI[aarch64.md5sum] = "532a09054fc7884fbe77c24178dcf25d"
SRC_URI[armv6.md5sum] = "070955426cbaf211ef665d6877fb5174"
SRC_URI[armv7.md5sum] = "aeffad6b8f377db36d9a40422f382182"
SRC_URI[x86_64.md5sum] = "bbf66de4472aea8c61c86079f115e089"
SRC_URI[riscv64.md5sum] = "adfcba3ec50263f3704356ad3a065da0"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.4"
SRC_URI[openrc.md5sum] = "eda14cc61d3c1be5d7fd8ff9543fad07"
SRC_URI[systemd.md5sum] = "2220a0eecd01da450176e60db2fef895"
SRC_URI[sysvinit.md5sum] = "fc7a3913b556afd503717e4d457e65a8"

require tedge.inc
