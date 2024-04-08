# meta-tedge

This meta layer contains all the recipes needed to build [thin-edge.io](https://thin-edge.io) into [Yocto](https://www.yoctoproject.org) image.

## How to start

For a quick guide on how to use `meta-tedge`, check our docs, where you can learn how to [build your first image](docs/build-yocto.md) or [integrate thin-edge with Mender](docs/install-mender.md). If you want to learn how to perform Over-the-Air (OTA) updates using Yocto, check [official thin-edge.io documentation](https://thin-edge.github.io/thin-edge.io/extend/firmware-management/building-image/yocto/).

Also, you can check [meta-tedge-project](https://github.com/thin-edge/meta-tedge-project), based on [kas project](https://github.com/siemens/kas), to set up and build a Yocto image with thin-edge installed using only one command!

## Maintenance strategy

The repository follows the release-named branch strategy. Only LTS releases are supported by the thin-edge team. If you want to maintain other Yocto releases, feel free to create a ticket or read [contributing](#contributing) and prepare a pull request! 

| Yocto Release | thin-edge version | Branch Name | Branch Status |
| :- | :- | :- | :- |
| Scarthgap | 1.0.1 | scarthgap | Active and maintained |
| Kirkstone | 1.0.1 | kirkstone | Active and maintained |

## Dependencies

`Meta-tedge` depends on `meta-networking`, `meta-python` and `meta-oe` layers that are part of the `meta-openembedded` layer. Up to the Kirkstone release, the layer requires `meta-rust` to meet the requirements of the rust version in thin-edge.

**Note** Currently [git-lfs](https://github.com/git-lfs/git-lfs) is required to build the layer, however this dependency will be removed in the near future. Until then, please install git-lfs following the [official git-lfs instructions for linux](https://github.com/git-lfs/git-lfs/blob/main/INSTALLING.md).

## Contributing

This project welcomes contributions and suggestions. If you would like to contribute to `meta-tedge`, please read our guide on how to best get started [contributing code or documentation](https://github.com/thin-edge/thin-edge.io/blob/main/CONTRIBUTING.md).

## License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE.txt) for more information.
