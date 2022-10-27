#!/bin/sh

thin_edge_path=$1 

if [ ! "$thin_edge_path" ]; then
    echo "Usage: ./update-layer.sh THIN_EDGE_DIR"
    exit
fi

# absolute path
layer_dir=${PWD%/*}

# create directory for new recipes
mkdir -p $layer_dir/recipes

# Create new recipes
for package in $layer_dir/recipes-tedge/*; do
    package=$(basename "$package" | sed 's/-/_/g') 

    package_dir=$(find "$thin_edge_path" -path "**/$package/Cargo.toml") 
    package_dir=$(dirname "$package_dir") 

    cd "$package_dir" || exit 

    cargo bitbake >/dev/null 2>&1

    mv *.bb "$layer_dir/recipes" >/dev/null 2>&1

    cd - || exit 
done

# Current md5 sum of License file
md5_sum=`md5sum $thin_edge_path/LICENSE.txt | cut -f 1 -d " "` 

# Replace the filename compatible with Yocto versioning convention 
ls $layer_dir/recipes | while read file ; do
    new_file="$(echo ${file} | sed -r 's/_/-/g ; s|-([^-]*)$|\_\1|')" ;
    [ "${file}" != "${new_file}" ] && mv "$layer_dir/recipes/${file}" "$layer_dir/recipes/${new_file}" ;
done 

# Fill the license field
sed -r -i "s@Apache-2.0;md5=generateme@LICENSE.txt;md5=$md5_sum@" $layer_dir/recipes/*.bb 

# Get new version
version=`ls $layer_dir/recipes/*.bb | shuf -n 1| sed -n 's/.*_\(.*\)\..*/\1/p'` 

# Replace old .bb files with the new ones.
ls $layer_dir/recipes | while read file ; do 
    for directory in $layer_dir/recipes-tedge/*; do
        
        current_recipe="$(echo $file | cut -f1 -d"_")" 
        current_folder="$(basename $directory)" 

        if [ "${current_recipe}" = "${current_folder}" ]; then
            rm -f $directory/*.bb 
            cp "$layer_dir/recipes/${file}" "${directory}" 
        fi
    done
done

# Remove scripts files
rm -f -r $layer_dir/recipes 

exit 
