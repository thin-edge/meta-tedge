inherit useradd
RDEPENDS:${PN} += "tpm2-tss"
GROUPMEMS_PARAM:${PN} += " -a tedge -g tss;"
