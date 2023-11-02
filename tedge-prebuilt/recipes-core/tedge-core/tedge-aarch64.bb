CHANNEL = "main"
VERSION = "0.12.1-rc374+g5d57ed1"

require tedge.inc

SRC_URI = "https://dl.cloudsmith.io/public/thinedge/tedge-${CHANNEL}/raw/names/tedge-arm64/versions/${VERSION}/tedge.tar.gz"

SRC_URI[md5sum] = "8ccf8b73541a1f8d232a5af8bd8f2759"
SRC_URI[sha256sum] = "4dc9902a4f8ce8ca57d420c20cbb1fa39a796516d38846c48c3a6f7d7d9b4c1b"

COMPATIBLE_HOST = "(aarch64.*-linux)"