# Automatically choose tedge package based on target architecture
def get_tedge_pkg(d):
	TA = d.getVar('TARGET_ARCH', True)
	if TA == "arm":
		FPU = d.getVar('TARGET_FPU', True)
		if FPU == "soft":
			tedgePkg = "tedge-armv6"
		else:
			tedgePkg = "tedge-armv7"
    elif TA == "aarch64":
        tedgePkg = "tedge-aarch64"
	elif TA == "i586":
		tedgePkg = "tedge-i586"
	elif TA == "x86_64":
		tedgePkg = "tedge-x86-64"
	else:
		raise bb.parse.SkipPackage("Target architecture '%s' is not supported by the meta-tedge layer" %TA)

	return tedgePkg

TEDGE_PKG = "${@get_tedge_pkg(d)}"

require ${TEDGE_PKG}.inc
