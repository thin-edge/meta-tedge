# meta-tedge

This is Openembedded layer of [thin-edge.io](https://github.com/thin-edge/thin-edge.io).

## What is and isn't supported?

- which init managers
- what package format (deb, rpm, apk)
- versions of thin-edge
- versions of Yocto
- what rust versions are required
- which versions of optional components

## Yocto releases support

The `meta-tedge` supports Yocto version 3.4 **Honister** and 4.0 **Kirkstone**. They operate under the same branch. 

## Layer Dependencies

It depends on `meta-networking`, `meta-python` and `meta-oe` layers which are part of `meta-openembedded` layer. Since version 0.9.0 the layers requires `meta-rust` to meet the requirements of the rust version in thin-edge. Before 0.9.0, the `meta-rust` layer must be attached only to Honister distro.

**Note:** Remember to fetch proper branch of `meta-openembedded` layer according to the Yocto version used by the
project.

## Installation

> If you are not familiar with building Yocto distribution or you have not configured your build host yet, we strongly
> recommend to look into [official yocto documentation](https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html)
> as the installation process will now skip all information that were mentioned there! For workspace organization or
> raspberry pi distribution, we also recommend this [guide](https://github.com/jynik/ready-set-yocto)

Clone current version of `meta-tedge` using:

```bash
git clone https://github.com/thin-edge/meta-tedge.git
```

Add `meta-tedge` layer to your current build using subcommand `add-layer`:

```bash
bitbake-layers add-layer /path/to/your/directory/meta-tedge
```

Activate systemd as default init manager by adding following lines to `conf/local.conf`:

```
INIT_MANAGER="systemd"
```

Build `tedge` by running following command:

```bash
bitbake tedge-full-image
```

Alternatively, add all recipes from `recipes-tedge` to your image and run the build. The `meta-tedge` requires at least
`core-image-minimal` to operate correctly. 

## Update tedge using mender client

You can update `thin-edge` using mender client that is delivered to Yocto with [meta-mender](https://github.com/mendersoftware/meta-mender). 
Download and configure mender using their [guide](https://docs.mender.io/operating-system-updates-yocto-project/build-for-demo). You can find information about supported devices in [meta-mender-raspberrypi](https://github.com/mendersoftware/meta-mender/tree/master/meta-mender-raspberrypi) and [meta-mender-community](https://github.com/mendersoftware/meta-mender-community)

Turn off mender client deamon by adding following line to `conf/local.conf` if you are going to use standalone mode:

```
SYSTEMD_AUTO_ENABLE:pn-mender-client = "disable"
```

Add `tedge-state-scripts` to your `conf/local.conf` with following line:

```
IMAGE_INSTALL:append = " tedge-state-scripts" 
```

or by building image:
```bash
bitbake core-image-tedge-mender
```

## Update Script

> Note: Update script works only for version 0.8.1 and below.

The `meta-tedge` layer is equipped with `update-layer.sh` script that is used to update the layer's version.
Additionaly, it can be used by users to set desired version that is not available in current releases. The script is
still a "work in progress" concept, that is why some requirements must be met before the user can run it:

- it requires `cargo bitbake` to be installed in the system
- user must provide the path to desired version of `thin-edge` repository as a script parameter
- overall structure of `thin-edge` must be the same, i.e. number of independent binaries cannot change. All current
  packages of the layer are the one located in `meta-tedge/recipes-tedge` directory. You can check if structure of
  `thin-edge` has changed by building it locally: `cargo` will print all available binaries

**Warning:** script does not inform if layer was updated successfuly or not. If script "froze", it could be caused by
building the project by `cargo bitbake`: do not interrupt that process.

## License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE.txt) for more information.
