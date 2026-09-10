SUMMARY = "thin-edge.io nodered software management plugin"

UPSTREAM_CHECK_COMMITS = "1"

GO_IMPORT = "github.com/thin-edge/tedge-nodered-plugin"
GO_INSTALL = "${GO_IMPORT}"

HOMEPAGE = "https://${GO_IMPORT}"
SRC_URI = "git://${GO_IMPORT};branch=main;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX}"
SRCREV = "a91031d87b1a2627532b0f1da79cba89a4d1752d"
PV = "1.0.0"

RDEPENDS:${PN}-dev += " bash"

DEPENDS += " tedge"
RDEPENDS:${PN} += " tedge"

TEDGE_CONFIG_DIR ?= "/etc/tedge"

def tedge_config_dir(d):
    return d.getVar('TEDGE_CONFIG_SYMLINK', True) or d.getVar('TEDGE_CONFIG_DIR', True)

TEDGE_RUNTIME_CONFIG_DIR = "${@tedge_config_dir(d)}"

do_compile[network] = "1"

# build executable instead of shared object
GO_LINKSHARED = ""
GOBUILDFLAGS:remove = "-buildmode=pie"
GO_EXTRA_LDFLAGS:append = "-X ${GO_IMPORT}/cmd.buildVersion=${PV} -X ${GO_IMPORT}/cmd.buildBranch=main"

inherit go-mod
require common.inc

################################################################################
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = " \
    file://src/${GO_IMPORT}/LICENSE;md5=7894111ce68eebdd7ae63357a988636e \
    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v3/LICENSE;md5=3c91c17266710e16afdbb2b6d15c761c \
    file://src/${GO_IMPORT}/vendor/gopkg.in/ini.v1/LICENSE;md5=4e2a8d8f9935d6a766a5879a77ddc24d \
    file://src/${GO_IMPORT}/vendor/go.uber.org/multierr/LICENSE.txt;md5=721ac51ede11efb667ff53a866be23c4 \
    file://src/${GO_IMPORT}/vendor/golang.org/x/net/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
    file://src/${GO_IMPORT}/vendor/golang.org/x/sys/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
    file://src/${GO_IMPORT}/vendor/golang.org/x/text/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
    file://src/${GO_IMPORT}/vendor/golang.org/x/exp/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
    file://src/${GO_IMPORT}/vendor/github.com/go-resty/resty/v2/LICENSE;md5=1fd20eb7ff613b50e8e31a0006518a76 \
    file://src/${GO_IMPORT}/vendor/github.com/mitchellh/mapstructure/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd \
    file://src/${GO_IMPORT}/vendor/github.com/pelletier/go-toml/v2/LICENSE;md5=390892c6562af0d807e527b06d635f94 \
    file://src/${GO_IMPORT}/vendor/github.com/fsnotify/fsnotify/LICENSE;md5=8bae8b116e2cfd723492b02d9a212fe2 \
    file://src/${GO_IMPORT}/vendor/github.com/hashicorp/hcl/LICENSE;md5=b278a92d2c1509760384428817710378 \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/sjson/LICENSE;md5=4394a63dad791b76d012a64b063ad872 \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/gjson/LICENSE;md5=f1ccda76a282fada49760e27335f1c28 \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/match/LICENSE;md5=f1ccda76a282fada49760e27335f1c28 \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/pretty/LICENSE;md5=898bc94f87439245e8cf1c5797098e90 \
    file://src/${GO_IMPORT}/vendor/github.com/magiconair/properties/LICENSE.md;md5=714beb7325ffa89d5a68d936a3bb04e5 \
    file://src/${GO_IMPORT}/vendor/github.com/sourcegraph/conc/LICENSE;md5=c001385e94f81477d77ffaf4321c647d \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/afero/LICENSE.txt;md5=920d76114a32b0fb75b3f2718c5a91be \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/cast/LICENSE;md5=67fac7567cbf6ba946e5576d590b1ed4 \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/viper/LICENSE;md5=67fac7567cbf6ba946e5576d590b1ed4 \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/pflag/LICENSE;md5=1e8b7dc8b906737639131047a590f21d \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/cobra/LICENSE.txt;md5=920d76114a32b0fb75b3f2718c5a91be \
    file://src/${GO_IMPORT}/vendor/github.com/sagikazarmark/locafero/LICENSE;md5=6e63b3f726f4acaf0ce766e25bcdaf04 \
    file://src/${GO_IMPORT}/vendor/github.com/sagikazarmark/slog-shim/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
    file://src/${GO_IMPORT}/vendor/github.com/subosito/gotenv/LICENSE;md5=0873257f40b8747d351ccc4288d06a40 \
    file://src/${GO_IMPORT}/vendor/github.com/inconshreveable/mousetrap/LICENSE;md5=7ea8c6c3cf90c1ca8494325e32c35579 \
"