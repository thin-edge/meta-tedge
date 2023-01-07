# Contributing to meta-tedge

## Updating the layer

To update the layer, one needs to update all the recipe files to use a newer
version of thin-edge. To automate the process, `scripts/update-layer.sh` script
can be used, but it's still helpful to know how it works in case project changes
significantly.

### Running the script

The `update-layer.sh` script takes a path to a local thin-edge.io repository,
and then for every recipe in the layer, it tries to find a crate with the same
name. Then it runs `cargo-bitbake` on the crate, generating a new recipe file
for this crate. The new recipe file then replaces the old one.

```shell
scripts/update-layer.sh ~/Desktop/thin-edge.io
```

```text
Wrote: c8y-configuration-plugin_0.8.1.bb
Wrote: c8y-log-plugin_0.8.1.bb
Wrote: tedge_0.8.1.bb
Wrote: tedge-agent_0.8.1.bb
warning: tedge_apama_plugin was not found in the thin-edge.io directory.
Wrote: tedge-apt-plugin_0.8.1.bb
Wrote: tedge-dummy-plugin_0.8.1.bb
Wrote: tedge-mapper_0.8.1.bb
Wrote: tedge-watchdog_0.8.1.bb
```

### Cargo bitbake

[`cargo-bitbake`][2] generates a recipe file from a cargo package. It requires
certain fields to be present in `Cargo.toml`, e.g. `description`, `homepage`,
`repository`, and `license`. For the `SRC_URI` of the package itself, it uses
the `URI` of the git origin remote.

### Recipe update process

To update a recipe:

1. Increment the version of the recipe by changing the version in the filename
    In Yocto, the filename of the recipe is important, as it contains the name
    and version of the recipe. The recipe filename has the following structure:
    `NAME_VERSION.bb`. In case of thin-edge, to update from 0.8.0 to 0.8.1, we
    change the recipe name from `tedge_0.8.0.bb` to `tedge_0.8.1.bb`.

2. Update `SRC_URI` and `SRCREV` for thin-edge.io repository itself
    The `SRC_URI` variable contains a list of URIs used to fetch all the
    necessary source code to build the recipe. In the case of thin-edge, the
    list contains a URI to thin-edge.io repository itself, as well as to all the
    dependencies. There is also a `SRCREV` variable, which is used by the git
    fetcher (a fetcher used to fetch URIs starting with `git://...`) which
    specifies the revision of the software to fetch, in other words, the id of
    the commit.
    In the example of updating from 0.8.0 to 0.8.1, the following changes are
    made:

    ```diff
     SRC_URI += "git://github.com/thin-edge/thin-edge.io.git;protocol=https;nobranch=1;branch=${PV}"
    -SRCREV = "79e3bfa80f96e48546c26a1c9e78a0977632f6d9"
    +SRCREV = "e3f261ca75e0ff99534338fb4068628210853429"
    ```

    Only the `SRCREV` variable changed, because in `SRC_URI` we expand the `PV`
    variable. This variable contains the version of the recipe. As we changed
    the filename of the recipe, the value of the `PV` variable also changed from
    `0.8.0` to `0.8.1`, so after the expansion `SRC_URI` contains
    `branch=0.8.1`. Despite being named `branch`, it works for tags too.

    More details in [Bitbake User Manual][1].

3. Update dependencies
    Next, a recipe contains a list of dependencies used by all the crate. To
    generate the list, use the `cargo-bitbake` utility, and then copy the list
    into the recipe.

4. Add/remove recipes and fix config changes
    Sometimes crates get added and removed or things in the configuration
    directory change. Usually you can try running the layer after step 3 and if
    build succeeds and tests pass, there's a good chance nothing will break, but
    make sure to properly handle addition/removal of crates in the layer.

[1]: https://docs.yoctoproject.org/bitbake/2.2/bitbake-user-manual/bitbake-user-manual-fetching.html#git-fetcher-git
[2]: https://github.com/meta-rust/cargo-bitbake
