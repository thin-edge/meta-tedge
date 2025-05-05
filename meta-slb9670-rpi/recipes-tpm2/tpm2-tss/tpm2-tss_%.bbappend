inherit useradd

USERADD_PACKAGES = "${PN}"
# use fixed uid/gid to avoid problems across system updates
GROUPADD_PARAM:${PN} = "--system --gid 114 tss"
USERADD_PARAM:${PN} = "--system -M -d /var/lib/tpm -s /bin/false --uid 114 --gid 114 tss"
