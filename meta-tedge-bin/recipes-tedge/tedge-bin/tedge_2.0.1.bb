# Architecture variables
ARCH_REPO_CHANNEL = "release"
ARCH_VERSION = "2.0.1"
SRC_URI[aarch64.md5sum] = "5662cda07eedf8648542c4062ee0e4f3"
SRC_URI[armv6.md5sum] = "dd9fc7d71aea2827a096861279c2a051"
SRC_URI[armv7.md5sum] = "02a9763226bf91ccfd36f618e04888b7"
SRC_URI[x86_64.md5sum] = "914c36c63793d6a34adbe321b0db6715"
SRC_URI[riscv64.md5sum] = "3f7b22d326d141664d9e7fb73e5a5de9"

# Init manager variables
INIT_REPO_CHANNEL = "community"
INIT_VERSION = "0.7.5"
SRC_URI[openrc.md5sum] = "e06035d45ce87b67457b1d3052bf4b46"
SRC_URI[systemd.md5sum] = "83e4d1a5503f3f75a4084f5212fcb722"
SRC_URI[sysvinit.md5sum] = "a9d2d58bdfec752800182609f420a677"

# checkout source
SRCREV_tedge = "19662f12d8df2ad0bab60d749e4133b4dbd7844e"
SRCREV_FORMAT = "tedge"
SRC_URI += "git://git@github.com/thin-edge/thin-edge.io.git;protocol=https;branch=main;name=tedge;destsuffix=tedge"
S = "${UNPACKDIR}/tedge"

require tedge.inc
require tedge-diag.inc
require tedge-log.inc
require tedge-config.inc
require tedge-flows.inc
