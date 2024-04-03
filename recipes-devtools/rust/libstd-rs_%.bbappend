# Fix issue with `proc_macro` crate not being included in stdlib
S = "${RUSTSRC}/library/sysroot"

# With the following commit: https://github.com/yoctoproject/poky/commit/d4c14304c92d76a2bdb612ffa3ca3477cd06cb6e
# --frozen flag was introduced to supersed --offline flag and guarantee that Cargo.lock file will not be modified during the build.
# In consequence it prevents network access that we need to load source for x509-parser library.
CARGO_BUILD_FLAGS:remove = "--frozen"