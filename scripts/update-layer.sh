#!/bin/sh

set -e

# absolute path
script_dir=$(dirname "$(readlink -f "$0")")
layer_dir=$(dirname "$script_dir")

thin_edge_path=$1

if [ ! "$thin_edge_path" ]; then
    echo "Usage: ./update-layer.sh THIN_EDGE_DIR"
    exit
fi

# create directory for new recipes
mkdir -p "$layer_dir"/recipes

# Create new recipes
for package in "$layer_dir"/recipes-tedge/*; do
    package=$(basename "$package" | sed 's/-/_/g')

    package_dir=$(find "$thin_edge_path" -path "**/$package/Cargo.toml")
    package_dir=$(dirname "$package_dir")

    cd "$package_dir" || exit

    cargo bitbake >/dev/null 2>&1

    mv ./*.bb "$layer_dir/recipes" >/dev/null 2>&1

    cd - > /dev/null || exit
done

# Current md5 sum of License file
md5_sum=$(md5sum "$thin_edge_path"/LICENSE.txt | cut -f 1 -d " ")

# Replace the filename compatible with Yocto versioning convention
for file in "$layer_dir"/recipes/*; do
    new_file="$(echo "$file" | sed -r 's/_/-/g ; s|-([^-]*)$|\_\1|')"
    [ "${file}" != "${new_file}" ] && mv "$file" "$new_file"
done

# Fill the license field
sed -r -i "s@Apache-2.0;md5=generateme@LICENSE.txt;md5=$md5_sum@" "$layer_dir"/recipes/*.bb

# # Replace old .bb files with the new ones.
for file in "$layer_dir"/recipes/*; do
    recipe=$(basename "$file" | cut -f1 -d"_")
    target_dir="$layer_dir/recipes-tedge/$recipe"

    if [ -d "$target_dir" ]; then
        rm -f "$target_dir"/*.bb
        cp "$file" "$target_dir"
    fi
done

# Remove scripts files
rm -f -r "$layer_dir"/recipes
