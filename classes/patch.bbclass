# patch out `panic = "abort"`, which fails
SRC_URI += " \
    file://patches/0001-Cargo.toml-do-not-abort-on-panic.patch \
"