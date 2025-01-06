# meta-tedge

This meta layer contains all the recipes needed to build [thin-edge.io](https://thin-edge.io) into a [Yocto](https://www.yoctoproject.org) image.

## How to start

Check out the [kas folder and README](./kas/README.md) for some example projects which make use of the thin-edge.io layer to create an image which includes Over-the-Air update support. The projects use [kas](https://github.com/siemens/kas) to make it easy to setup your Yocto environment (e.g. checkout all of the required layers) and build your image in a single commands.

For more user-friendly documentation, check out the [official thin-edge.io documentation](https://thin-edge.github.io/thin-edge.io/extend/firmware-management/building-image/yocto/).

## Maintenance strategy

The repository follows the release-named branch strategy. Only LTS releases are supported by the thin-edge team. If you want to maintain other Yocto releases, feel free to create a ticket or read [contributing](#contributing) and prepare a pull request! 

| Yocto Release | thin-edge version | Branch Name | Branch Status |
| :- | :- | :- | :- |
| Kirkstone | 1.x | kirkstone | Active and maintained |
| Scarthgap | 1.x | scarthgap | Active and maintained |

## Contributing

This project welcomes contributions and suggestions. If you would like to contribute to `meta-tedge`, please read our guide on how to best get started [contributing code or documentation](https://github.com/thin-edge/thin-edge.io/blob/main/CONTRIBUTING.md).

## License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE.txt) for more information.
