LICENSE = "CLOSED"

SRC_URI += " \
    file://rugix-system \ 
"

DEPENDS = "tedge-inventory"
RDEPENDS:${PN} += " tedge-inventory rugix-ctrl jq"

do_install () {
    install -d ${D}${datadir}/tedge-inventory/scripts.d
    install -m 0755 ${UNPACKDIR}/rugix-system ${D}${datadir}/tedge-inventory/scripts.d/85_rugix_System
}

FILES:${PN} += " \
    ${datadir}/tedge-inventory/scripts.d/85_rugix_System \
"
