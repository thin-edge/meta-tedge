# meta-slb9670-rpi

Yocto layer for Letstrust TPM2.0 Module and RaspberryPi.

It allows you to support the SLB9670 TPM2.0 with your RaspberryPi (Tested with RaspberryPi4-64).

It also gives an Image for quick start tests.

## Quick links

* Article related to this layer <https://ubs_csse.gitlab.io/secu_os/tutorials/tpm_rpi.html>
* Letstrust website <https://letstrust.de/>
* Meta Security <https://git.yoctoproject.org/cgit/cgit.cgi/meta-security>
* Meta RaspberryPi <https://github.com/agherzan/meta-raspberrypi>
* Patch for U-Boot and Device Tree <https://github.com/joholl/rpi4-uboot-tpm>

## Dependencies

  URI: <https://github.com/agherzan/meta-raspberrypi.git>
  branch: **scarthgap**

  URI: <git://git.yoctoproject.org/meta-security>
  branch: **scarthgap**

## Quick Start

* source poky/oe-init-build-env rpi-build
* Add this layer to bblayers.conf and the dependencies above
* Set MACHINE in local.conf to one of the supported RaspberryPi Boards
* bitbake core-image-slb9670-rpi
* dd to a SD card the generated sdimg file (use xzcat if rpi-sdimg.xz is used)
* Plug the TPM Header on the right pins
* Boot your RPI.

## U-Boot test

```bash
U-Boot> tpm2 init
U-Boot> tpm2 startup TPM2_SU_CLEAR
U-Boot> tpm2 get_capability 0x6 0x106 0x200 2
Capabilities read from TPM:
Property 0x00000106: 0x534c4239
Property 0x00000107: 0x36373000
```

```python
python3 -c "print(bytes.fromhex('534c423936373000'))"
b'SLB9670\x00'
```

## Tips

Edit your local conf in order to enable U-Boot and support fitImage.

```
MACHINE ??= "raspberrypi4-64"
ENABLE_UART = "1"
RPI_USE_U_BOOT = "1"
ENABLE_SPI_BUS = "1"
RPI_EXTRA_CONFIG = "dtoverlay=letstrust-tpm"
KERNEL_CLASSES = "kernel-fitimage"
KERNEL_IMAGETYPE = "fitImage"
KERNEL_FITCONFIG = "conf-bcm2711-rpi-4-b.dtb"
KERNEL_BOOTCMD = "bootm"
UBOOT_SIGN_ENABLE = "1"
MACHINE_FEATURES:append = "tpm2"
DISTRO_FEATURES:append = " systemd tpm2 usrmerge"
VIRTUAL-RUNTIME_init_manager = "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"
VIRTUAL-RUNTIME_initscripts = ""
```

## Maintainer

Original Maintainer: Pierre Fontaine <pierre.ftn@pfontaine.fr>

`scarthgap` Branch editor: ejaaskel <esa.jaaskela@suomi24.fi>
