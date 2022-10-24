## Introduction
This is Openembedded layer of [thin-edge.io](https://github.com/thin-edge/thin-edge.io) for version 0.7.5.  

## Yocto releases support

The `meta-tedge` is supported for Yocto version 3.4 **Honister** and 4.0 **Kirkstone**. They operates under the same branch. 

## Layer Dependencies

It bases on `meta-networking`, `meta-python` and `meta-oe` layers which are part of `meta-openembedded` layer. 

**Note:** Remember to fetch proper branch of `meta-openembedded` layer according to the Yocto version used by the project. 

## Installation
> If you are not familiar with building Yocto distribution or you have not configured your build host yet, we strongly recommend to look into [official yocto documentation](https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html) as the installation process will now skip all information that were mentioned there! For workspace organization or raspberry pi distribution, we also recommend this [guide](https://github.com/jynik/ready-set-yocto)

Clone current version of `meta-tedge` using : 
```bash
$ git clone https://github.com/thin-edge/meta-tedge.git
```
Add `meta-tedge` layer to your current build using subcommand `add-layer` :
```bash
$ bitbake-layers add-layer /path/to/your/directory/meta-tedge
```

Activate `Systemd` as default init manager by adding following lines to `conf/local.conf` : 
```
INIT_MANAGER="systemd"
```

Build `tedge` by running following command :
```bash
$ bitbake tedge-full-image  
```

Alternatively, add all recipes from `recipes-tedge` to your image and run the build. The `meta-tedge` requires at least `core-image-minimal` to operate correctly.

## Update Script
The `meta-tedge` layer is equipped with `update-layer.sh` script that is used to update layer's version. Additionaly, it can be used by users to set desired version that is not available in current releases. The script is still a "work in progress" concept, that is why some requirements must be met before user can run it:
- script must be started in script directory
- it requires `cargo bitbake` to be installed in the system
- user must provide the path to desired version of `thin-edge` repository as a script parameter
- overall structure of `thin-edge` must be the same, i.e. number of independent binaries cannot change. All current packages of the layer are the one located in `meta-tedge/recipes-tedge` directory. You can check if structure of `thin-edge` has changed by building it locally: `cargo` will print all available binaries

**Warning:** script does not inform if layer was updated successfuly or not. If script "froze", it could be caused by building the project by `cargo bitbake`: do not interrupt that process. 

## Copyright

MIT/Apache-2.0 - following the lead of [thin-edge.io](https://github.com/thin-edge/thin-edge.io)
