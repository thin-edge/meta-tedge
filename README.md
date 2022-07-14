## Introduction
This is Openembedded layer of [thin-edge.io](https://github.com/thin-edge/thin-edge.io) for version 0.7.2.  

## Dependencies

`meta-tedge` operates on **Yocto version 4.0 "Kirkstone"**
It bases on `meta-networking` and `meta-oe` which are part of `meta-openembedded` layer. 

## Installation
> If you are not familiar with building Yocto distribution or you have not configured your build host yet, we strongly recommend to look into [official yocto documentation](https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html) as the installation process will now skip all information that were mentioned there! For workspace organization or raspberry pi distribution, we also recommend this [guide](https://github.com/jynik/ready-set-yocto)

Clone current version of `meta-tedge` using : 
```bash
$ git clone https://github.com/Bravo555/meta-tedge.git
```
Add `meta-tedge` layer to your current build using subcommand `add-layer` :
```bash
$ bitbake-layers add-layer /path/to/your/directory/meta-tedge
```

Activate `Systemd` as default init manager by adding following lines to `local.conf` : 
```
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
```

Build `tedge` by running following command :
```bash
$ bitbake tedge-image  
```

## Current progress

Currently, minimal thin-edge installation is completed i.e `tedge` and `tedge-mapper` modules. 

TODO: Adding remaining modules and plugins to `meta-tedge`
