inherit cargo 
# This prevents disabling crates.io registry in cargo_do_configure task and
# allows cargo to fetch dependencies during the do_compile step.
#
# It's still not perfect, because ideally we'd want to download all the source
# code in the do_fetch step, but it's challenging because we'd have to either
# duplicate do_configure step just for fetching, or swap the order and run
# do_configure before do_fetch, which might be confusing for the users.
do_compile[network] = "1"
CARGO_DISABLE_BITBAKE_VENDORING = "1"

# The function cargo_common_do_patch_paths adds extra Git repositories with `name` and `destsuffix` from `SRC_URI`  
# to the Cargo configuration as a patch so that they will be built during the do_compile step with Cargo.  
#  
# However, we add a repository with services that meet these conditions but have nothing  
# to do with Rust code, so we skip this postfunction to prevent errors.  
#  
# Note: If we plan to compile two Rust repositories in a single recipe in the future, this change  
# must be reverted, and cargo_common_do_patch_paths must be redesigned. However, for now,  
# it is simpler to just skip it.  
python cargo_common_do_patch_paths:prepend () {
    return
} 

# With the following commit: https://github.com/yoctoproject/poky/commit/d4c14304c92d76a2bdb612ffa3ca3477cd06cb6e
# --frozen flag was introduced to supersed --offline flag and guarantee that Cargo.lock file will not be modified during the build.
# In consequence it prevents network access that we need to load source for x509-parser library.
CARGO_BUILD_FLAGS:remove = "--frozen"

# Yocto has its own stripping mechanism, so it raises an error if binaries produced by `cargo build` are already stripped.
# To prevent this, override `profile.release.strip` in Cargo.toml to "none".
# References:
# https://doc.rust-lang.org/cargo/reference/profiles.html#strip
# https://doc.rust-lang.org/cargo/reference/config.html#command-line-overrides
CARGO_BUILD_FLAGS:append = " --config 'profile.release.strip="none"'"
