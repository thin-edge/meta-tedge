# meta-tedge

This meta layer contains all the recipes needed to build [thin-edge.io](https://thin-edge.io) into [Yocto](https://www.yoctoproject.org) image.

## How to start

Check out the [meta-tedge-project](https://github.com/thin-edge/meta-tedge-project) for some example projects which make use of the thin-edge.io layer to create an image which includes Over-the-Air update support. The projects use [kas](https://github.com/siemens/kas) to make it easy to setup your yocto environment (e.g. checkout all of the required layers) and build your image in a single commands.

For more user-friendly documentation, check out the [official thin-edge.io documentation](https://thin-edge.github.io/thin-edge.io/extend/firmware-management/building-image/yocto/).

## Maintenance strategy

The repository follows the release-named branch strategy. Only LTS releases are supported by the thin-edge team. If you want to maintain other Yocto releases, feel free to create a ticket or read [contributing](#contributing) and prepare a pull request! 

| Yocto Release | thin-edge version | Branch Name | Branch Status |
| :- | :- | :- | :- |
| Scarthgap | 1.0.1 | scarthgap | Active and maintained |
| Kirkstone | 1.1.1 | kirkstone | Active and maintained |

## Dependencies

Currently [git-lfs](https://github.com/git-lfs/git-lfs) is required to build the layer, however this dependency will be removed in the near future. Until then, please install git-lfs following the [official git-lfs instructions for linux](https://github.com/git-lfs/git-lfs/blob/main/INSTALLING.md).

## Contributing

This project welcomes contributions and suggestions. If you would like to contribute to `meta-tedge`, please read our guide on how to best get started [contributing code or documentation](https://github.com/thin-edge/thin-edge.io/blob/main/CONTRIBUTING.md).

## License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE.txt) for more information.
