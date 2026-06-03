FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://9670.cfg \
            file://letstrust-tpm-overlay.dts;subdir=${BP}/arch/${ARCH}/boot/dts/overlays \
            "

KERNEL_DEVICETREE:append = " overlays/letstrust-tpm.dtbo"
