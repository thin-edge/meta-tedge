#!/bin/sh

thin_edge_path=$1

if [ ! "$thin_edge_path" ]; then
    echo "Usage: ./update-layer.sh THIN_EDGE_DIR"
    exit
fi

layer_dir=$(pwd)

mkdir recipes

for package in recipes-tedge/*; do
    package=$(basename "$package" | sed 's/-/_/g')

    package_dir=$(find "$thin_edge_path" -path "**/$package/Cargo.toml")
    package_dir=$(dirname "$package_dir")

    cd "$package_dir" || exit

    cargo bitbake
    mv *.bb "$layer_dir/recipes"

    cd - || exit
done
