# How to use meta-tedge with Mender

You can swap the image partition with the mender artifact generated during the Yocto build with [meta-mender](https://github.com/mendersoftware/meta-mender/tree/master) layer.

## Installation

If you are not familiar with building Yocto distribution or you have not built `meta-tedge` yet, we strongly recommend our[Installation Guide](build-yocto.md). You can also check [demo build](https://docs.mender.io/operating-system-updates-yocto-project/build-for-demo) prepared by Mender team.

> Note: Following guide is based on raspberry pi chapter from [Installation Guide](build-yocto.md).
#### I. Clone Mender repository

Clone Mender repository. We will use `kirkstone` release. 

```bash
git clone --branch=kirkstone https://github.com/mendersoftware/meta-mender
```
#### II. Initialize the build environment

To use Yocto tools, you need to initialize the environment first using the `oe-init-build-env` script located in the poky directory:

```bash
cd poky
source oe-init-build-env
```

#### III. Add layers to the build

Add the Mender layers to your project. The first one is `meta-mender-core`:
 
```bash
bitbake-layers add-layer ../meta-mender/meta-mender-core
``` 

Next, you need to add a layer that supports your device. We will use `meta-mender-raspberrypi`. It also needs `meta-raspberry` layer to function correctly:

```bash 
bitbake-layers add-layer ../meta-raspberrypi
bitbake-layers add-layer ../meta-mender/meta-mender-raspberrypi
```

You can also build a Yocto system for QEMU using `meta-mender-qemu`:

 ```bash
 bitbake-layers add-layer ../meta-mender/meta-mender-qemu
 ```

Or use any other supported device listed in [meta-mender-community](https://github.com/mendersoftware/meta-mender-community).

#### IV. Configure layer.conf

Now we will edit the `layer.conf` file in your build's `conf` directory. Add Mender to the configuration using `INHERIT` method, set mender artifact name and artifact image fstype:

```
INHERIT += "mender-full"
MENDER_ARTIFACT_NAME = "release-1"
```

As we plan to use [standalone deployment ](https://docs.mender.io/artifact-creation/standalone-deployment) you have to disable `mender-client` deamon: 

```
SYSTEMD_AUTO_ENABLE:pn-mender-client = "disable"
```

If you don't want to use `systemd` as init manager you need to also disable mender systemd feature:

```
MENDER_FEATURES_DISABLE:append = " mender-systemd"
```

Next, choose the target machine for your build. In our case it will be raspberry pi 3 64-bit version:

```
MACHINE = "raspberrypi3-64"
```

Additionally, we need to set variables for raspberry pi:

```
RPI_USE_U_BOOT = "1"

# Users report that raspberry pi cannot boot if UART is not enabled with u-boot.
ENABLE_UART = "1"

# rpi-base.inc removes these as they are normally installed on to the
# vfat boot partition. To be able to update the Linux kernel Mender
# uses an image that resides on the root file system and below line
# ensures that they are installed to /boot
IMAGE_INSTALL:append = " kernel-image kernel-devicetree"

# Mender will build an image called `sdimg` which shall be used instead
# of the `rpi-sdimg`.
IMAGE_FSTYPES:remove = " rpi-sdimg"

# Use the same type here as specified in ARTIFACTIMG_FSTYPE to avoid
# building an unneeded image file.
SDIMG_ROOTFS_TYPE = "ext4"

# Reserve more space than the Mender default for the boot partition,
# as the raspberrypi machines bring some additional things that need
# to be placed there too
MENDER_BOOT_PART_SIZE_MB = "64"
```

If you use another device, check the repository for detailed instructions. You can also find an exemplary `conf/local.conf.sample` file in our `meta-tedge` layer under the `conf` directory.

> You can also add INHERIT += "rm_work" to your `local.conf` file to preserve some disc space!

#### V. Build

There are two ways to build an image: by using a ready-to-use image `core-image-tedge-mender` or by appending `local.conf` file with necessary recipes.

##### V.I Build using image

You can build your system with our image by running the following command:

```bash
bitbake core-image-tedge-mender
```

##### V.II Build appending local.conf

This method requires appending `local.conf` with necessary recipes by using the `IMAGE_INSTALL:append` method:

```
IMAGE_INSTALL:append = " \ 
    tedge \ 
    tedge-bootstrap \
    tedge-inventory \
    tedge-state-scripts \
    tedge-firmware
"
```

You must set fixed uid/gid to avoid permissions problems on the `/data` partition. You can do that by inheriting the `extrausers` class in your image recipe or the `local.conf` file:

```
inherit extrausers
EXTRA_USERS_PARAMS = "\
    groupmod -g 960 mosquitto; \
    usermod -u 961 mosquitto; \
"
```

We recommend switching to `Network Manager`. Append your `local.conf` with the following recipes:

```
IMAGE_INSTALL:append = " \ 
    networkmanager \
    networkmanager-bash-completion \
    networkmanager-nmtui \
"
```

Alternatively, if you are using `Scarthgap` release:

```
IMAGE_INSTALL:append = " \ 
    networkmanager \
    networkmanager-nmtui \
"
```

Then you can use any image delivered by the Yocto team. Thin-edge requires at least `core-image-base` to function correctly:

```bash
bitbake core-image-base
```

#### VI. Flash the device

After a successful build, the images and build artifacts are placed in `tmp/deploy/images/<YOUR-MACHINE>/`. 

There is one Mender disk image, which will have one of the following suffixes:

- `.uefiimg` if the system boots using the UEFI standard (x86 with UEFI or ARM with U-Boot and UEFI emulation) and GRUB bootloader
- `.sdimg` if the system is an ARM system and boots using U-Boot (without UEFI emulation)
- `.biosimg` if the system is an x86 system and boots using the traditional BIOS and GRUB bootloader

> If you built a system for QEMU, you can run it with `../meta-mender/meta-mender-qemu/scripts/mender-qemu` script and log in as the root user.

## Update

Apart from the Mender disc image, each build will produce an artifact with `.mender` suffix. You can use it to deploy a rootfs update. 

To deploy the new Artifact to your device, run the following command in the device terminal:

```bash
mender install <URI>
```

Where `<URI>` can be any file-based storage or an HTTP/HTTPS URL. You can also deliver `.mender` file via ssh. To do that, remember to add ssh service to your build in the `conf/local.conf` file e.g `dropbear`:

```bash
IMAGE_FEATURES:append = " ssh-server-dropbear"
IMAGE_INSTALL:append = " dropbear"
```

Once you run the `mender install` command, it will run the update. Each Artifact that contains thin-edge will also run state scripts that preserve essential data from the `etc/tedge` and `var/tedge` directories.

After a successful update, run the reboot command to boot into the new filesystem.

If you are happy with the deployment, you can make it permanent by running the following command in your device terminal:

```bash
mender commit
```

or rollback to the previous state using 

```bash
mender rollback
```

> **WARNING: Do not commit your deployment without a reboot; you will lose all thin-edge data!** 