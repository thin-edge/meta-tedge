inherit cargo
inherit systemd

LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=175792518e4ac015ab6696d16c4f607e \
"

SUMMARY = "tedge is the cli tool for thin-edge.io"
HOMEPAGE = "https://thin-edge.io"
LICENSE = "Apache-2.0"

# This prevents disabling crates.io registry in cargo_do_configure task and
# allows cargo to fetch dependencies during the do_compile step.
#
# It's still not perfect, because ideally we'd want to download all the source
# code in the do_fetch step, but it's challenging because we'd have to either
# duplicate do_configure step just for fetching, or swap the order and run
# do_configure before do_fetch, which might be confusing for the users.
#
# Still, it makes the update-layer.sh script entirely obsolete by significantly
# improving the maintenance of the layer, simplifying updating to 2 steps:
#
# 1. Update recipe version and point to the new revision
# 2. Handle package/systemd configuration changes, if any.
#
# We'll be looking into how to do it in do_fetch step, but as long as we don't
# have it figured out, or somebody tells us we've broken their build, we're
# going for this approach.
do_compile[network] = "1"
CARGO_DISABLE_BITBAKE_VENDORING = "1"
