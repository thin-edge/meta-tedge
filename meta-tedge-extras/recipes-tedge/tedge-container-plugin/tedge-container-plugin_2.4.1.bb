SUMMARY = "thin-edge.io container plugin"

UPSTREAM_CHECK_COMMITS = "1"

GO_IMPORT = "github.com/thin-edge/tedge-container-plugin"
GO_INSTALL = "${GO_IMPORT}"

HOMEPAGE = "https://${GO_IMPORT}"
SRC_URI = "git://${GO_IMPORT};branch=main;protocol=https"
SRCREV = "1231ed3f601d2859cb0fa7de1585931735845ed7"
PV = "2.4.1"

RDEPENDS:${PN}-dev += " bash"

# ignore files with s6-overlay shebang, e.g. /command/execlineb
INSANE_SKIP:${PN}-dev += " file-rdeps"

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
    file://src/${GO_IMPORT}/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v3/LICENSE;md5=3c91c17266710e16afdbb2b6d15c761c \
    file://src/${GO_IMPORT}/vendor/gopkg.in/tomb.v2/LICENSE;md5=95d4102f39f26da9b66fee5d05ac597b \
    file://src/${GO_IMPORT}/vendor/gopkg.in/ini.v1/LICENSE;md5=4e2a8d8f9935d6a766a5879a77ddc24d \
    file://src/${GO_IMPORT}/vendor/go.opentelemetry.io/otel/trace/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://src/${GO_IMPORT}/vendor/go.opentelemetry.io/otel/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://src/${GO_IMPORT}/vendor/go.opentelemetry.io/otel/metric/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://src/${GO_IMPORT}/vendor/go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://src/${GO_IMPORT}/vendor/go.uber.org/zap/LICENSE;md5=5e8153e456a82529ea845e0d511abb69 \
    file://src/${GO_IMPORT}/vendor/go.uber.org/multierr/LICENSE.txt;md5=721ac51ede11efb667ff53a866be23c4 \
    file://src/${GO_IMPORT}/vendor/golang.org/x/net/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
    file://src/${GO_IMPORT}/vendor/golang.org/x/sys/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
    file://src/${GO_IMPORT}/vendor/golang.org/x/text/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
    file://src/${GO_IMPORT}/vendor/golang.org/x/sync/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
    file://src/${GO_IMPORT}/vendor/golang.org/x/exp/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
    file://src/${GO_IMPORT}/vendor/github.com/gorilla/websocket/LICENSE;md5=c007b54a1743d596f46b2748d9f8c044 \
    file://src/${GO_IMPORT}/vendor/github.com/mitchellh/mapstructure/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd \
    file://src/${GO_IMPORT}/vendor/github.com/pelletier/go-toml/v2/LICENSE;md5=390892c6562af0d807e527b06d635f94 \
    file://src/${GO_IMPORT}/vendor/github.com/xeipuuv/gojsonschema/LICENSE-APACHE-2.0.txt;md5=f7a6312e0b85b17786de3ae15ab42bed \
    file://src/${GO_IMPORT}/vendor/github.com/xeipuuv/gojsonreference/LICENSE-APACHE-2.0.txt;md5=f7a6312e0b85b17786de3ae15ab42bed \
    file://src/${GO_IMPORT}/vendor/github.com/xeipuuv/gojsonpointer/LICENSE-APACHE-2.0.txt;md5=f7a6312e0b85b17786de3ae15ab42bed \
    file://src/${GO_IMPORT}/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56 \
    file://src/${GO_IMPORT}/vendor/github.com/reubenmiller/go-c8y/LICENSE;md5=5ad50ede22fd4ba20c16a11b227eccac \
    file://src/${GO_IMPORT}/vendor/github.com/felixge/httpsnoop/LICENSE.txt;md5=684da2bf3eed8fc8860e75ad84638225 \
    file://src/${GO_IMPORT}/vendor/github.com/golang-jwt/jwt/v5/LICENSE;md5=a21b708d8b320c68979c44ac9dba9b0d \
    file://src/${GO_IMPORT}/vendor/github.com/klauspost/compress/LICENSE;md5=d0fd9ebda39468b51ff4539c9fbb13a8 \
    file://src/${GO_IMPORT}/vendor/github.com/klauspost/compress/internal/snapref/LICENSE;md5=b8b79c7d4cda128290b98c6a21f9aac6 \
    file://src/${GO_IMPORT}/vendor/github.com/klauspost/compress/zstd/internal/xxhash/LICENSE.txt;md5=802da049c92a99b4387d3f3d91b00fa9 \
    file://src/${GO_IMPORT}/vendor/github.com/docker/docker/LICENSE;md5=4859e97a9c7780e77972d989f0823f28 \
    file://src/${GO_IMPORT}/vendor/github.com/docker/go-connections/LICENSE;md5=04424bc6f5a5be60691b9824d65c2ad8 \
    file://src/${GO_IMPORT}/vendor/github.com/docker/go-units/LICENSE;md5=04424bc6f5a5be60691b9824d65c2ad8 \
    file://src/${GO_IMPORT}/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e \
    file://src/${GO_IMPORT}/vendor/github.com/eclipse/paho.mqtt.golang/LICENSE;md5=dcdb33474b60c38efd27356d8f2edec7 \
    file://src/${GO_IMPORT}/vendor/github.com/fsnotify/fsnotify/LICENSE;md5=8bae8b116e2cfd723492b02d9a212fe2 \
    file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-version/LICENSE;md5=81eb103d8076ec72ebe52e3ee40176db \
    file://src/${GO_IMPORT}/vendor/github.com/hashicorp/hcl/LICENSE;md5=b278a92d2c1509760384428817710378 \
    file://src/${GO_IMPORT}/vendor/github.com/h2non/filetype/LICENSE;md5=d4004d1b897e7c064b57b0f7127643d6 \
    file://src/${GO_IMPORT}/vendor/github.com/stretchr/testify/LICENSE;md5=188f01994659f3c0d310612333d2a26f \
    file://src/${GO_IMPORT}/vendor/github.com/go-viper/mapstructure/v2/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd \
    file://src/${GO_IMPORT}/vendor/github.com/op/go-logging/LICENSE;md5=d01d49c2816e0112c83b3a9f77029b8a \
    file://src/${GO_IMPORT}/vendor/github.com/google/go-querystring/LICENSE;md5=29f156828ca5f2df0d1c12543a75f12a \
    file://src/${GO_IMPORT}/vendor/github.com/go-logr/stdr/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://src/${GO_IMPORT}/vendor/github.com/go-logr/logr/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
    file://src/${GO_IMPORT}/vendor/github.com/Microsoft/go-winio/LICENSE;md5=69205ff73858f2c22b2ca135b557e8ef \
    file://src/${GO_IMPORT}/vendor/github.com/distribution/reference/LICENSE;md5=d2794c0df5b907fdace235a619d80314 \
    file://src/${GO_IMPORT}/vendor/github.com/compose-spec/compose-go/v2/dotenv/LICENSE;md5=b5d436f39fbc753d5ce95d08b2f1c90b \
    file://src/${GO_IMPORT}/vendor/github.com/compose-spec/compose-go/v2/LICENSE;md5=9740d093a080530b5c5c6573df9af45a \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/gjson/LICENSE;md5=f1ccda76a282fada49760e27335f1c28 \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/match/LICENSE;md5=f1ccda76a282fada49760e27335f1c28 \
    file://src/${GO_IMPORT}/vendor/github.com/tidwall/pretty/LICENSE;md5=898bc94f87439245e8cf1c5797098e90 \
    file://src/${GO_IMPORT}/vendor/github.com/juju/errors/LICENSE;md5=2d1c30374313ae40df7772dc92ef9fd5 \
    file://src/${GO_IMPORT}/vendor/github.com/magiconair/properties/LICENSE.md;md5=714beb7325ffa89d5a68d936a3bb04e5 \
    file://src/${GO_IMPORT}/vendor/github.com/sourcegraph/conc/LICENSE;md5=c001385e94f81477d77ffaf4321c647d \
    file://src/${GO_IMPORT}/vendor/github.com/sirupsen/logrus/LICENSE;md5=8dadfef729c08ec4e631c4f6fc5d43a0 \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/afero/LICENSE.txt;md5=920d76114a32b0fb75b3f2718c5a91be \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/cast/LICENSE;md5=67fac7567cbf6ba946e5576d590b1ed4 \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/viper/LICENSE;md5=67fac7567cbf6ba946e5576d590b1ed4 \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/pflag/LICENSE;md5=1e8b7dc8b906737639131047a590f21d \
    file://src/${GO_IMPORT}/vendor/github.com/spf13/cobra/LICENSE.txt;md5=920d76114a32b0fb75b3f2718c5a91be \
    file://src/${GO_IMPORT}/vendor/github.com/moby/docker-image-spec/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/image-spec/LICENSE;md5=27ef03aa2da6e424307f102e8b42621d \
    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/go-digest/LICENSE;md5=2d6fc0e85c3f118af64c85a78d56d3cf \
    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/go-digest/LICENSE.docs;md5=943c0794b61de2c8ca0d32947015508b \
    file://src/${GO_IMPORT}/vendor/github.com/sagikazarmark/locafero/LICENSE;md5=6e63b3f726f4acaf0ce766e25bcdaf04 \
    file://src/${GO_IMPORT}/vendor/github.com/sagikazarmark/slog-shim/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
    file://src/${GO_IMPORT}/vendor/github.com/codeclysm/extract/v4/LICENSE;md5=6787329c9e2058e5f5d1844b9f948256 \
    file://src/${GO_IMPORT}/vendor/github.com/ulikunitz/xz/LICENSE;md5=3c82255323cf3d48815acdbf9068b715 \
    file://src/${GO_IMPORT}/vendor/github.com/subosito/gotenv/LICENSE;md5=0873257f40b8747d351ccc4288d06a40 \
    file://src/${GO_IMPORT}/vendor/github.com/cihub/seelog/LICENSE.txt;md5=981ed23d3733a4b1505510215fc9d77f \
    file://src/${GO_IMPORT}/vendor/github.com/obeattie/ohmyglob/LICENSE;md5=e8fe0b978e1aaf903b70cc20638221c0 \
    file://src/${GO_IMPORT}/vendor/github.com/inconshreveable/mousetrap/LICENSE;md5=7ea8c6c3cf90c1ca8494325e32c35579 \
    file://src/${GO_IMPORT}/vendor/github.com/gogo/protobuf/LICENSE;md5=38be95f95200434dc208e2ee3dab5081 \
    file://src/${GO_IMPORT}/vendor/github.com/mattn/go-shellwords/LICENSE;md5=e5116fc268f5118168ff06a271b50ef9 \
    file://src/${GO_IMPORT}/vendor/github.com/pkg/errors/LICENSE;md5=6fe682a02df52c6653f33bd0f7126b5a \
"