CHANNEL = "main"
VERSION = "0.12.1-rc374+g5d57ed1"

require tedge.inc

SRC_URI = "https://dl.cloudsmith.io/public/thinedge/tedge-${CHANNEL}/raw/names/tedge-amd64/versions/${VERSION}/tedge.tar.gz"

SRC_URI[md5sum] = "819fda04f4104f2e536fffff20062bc6"
SRC_URI[sha256sum] = "8688de3decc51ce803056b8dcd8bc8b82ad93ba229bb6ec544ca86cf48bff1c0"

COMPATIBLE_HOST = "(x86_64.*-linux)"