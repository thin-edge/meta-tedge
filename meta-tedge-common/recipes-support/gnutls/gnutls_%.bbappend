EXTRA_OECONF += " \
    --with-default-trust-store-dir=/etc/ssl/certs  \
"

PACKAGECONFIG:append = " p11-kit"
