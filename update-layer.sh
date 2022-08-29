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

# Current md5 sum of License file
md5_sum=`md5sum $thin_edge_path/LICENSE.txt | cut -f 1 -d " "`

cd recipes

# Replace the filename compatible with Yocto versioning convention 
find . -type f -name '*.bb' | while read file ; do
new_file="$(echo ${file} | sed -r 's/_/-/g ; s|-([^-]*)$|\_\1|')" ;

[ "${file}" != "${new_file}" ] && mv "${file}" "${new_file}" ;
done 

# Fill the license field
sed -r -i "s@Apache-2.0;md5=generateme@LICENSE.txt;md5=$md5_sum@" *.bb

cd -


