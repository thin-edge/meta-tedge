EXTRA_OECONF:append:tedge-p11-kit = " --with-default-trust-store-dir=/etc/ssl/certs"

PACKAGECONFIG:append:tedge-p11-kit = " p11-kit"
