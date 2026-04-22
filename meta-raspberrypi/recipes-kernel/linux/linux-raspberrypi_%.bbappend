FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'file://usb-wifi-rt5370.cfg', '', d)}"
