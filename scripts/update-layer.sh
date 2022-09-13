#!/bin/sh

thin_edge_path=$1 

if [ ! "$thin_edge_path" ]; then
    echo "Usage: ./update-layer.sh THIN_EDGE_DIR"
    exit
fi

layer_dir=$(pwd) 

mkdir recipes
# Create new recipes
for package in recipes-tedge/*; do
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

cd recipes

# Replace the filename compatible with Yocto versioning convention 
find . -type f -name '*.bb' | while read file ; do
    new_file="$(echo ${file} | sed -r 's/_/-/g ; s|-([^-]*)$|\_\1|')" ;
    [ "${file}" != "${new_file}" ] && mv "${file}" "${new_file}" ;
done 

# Fill the license field
sed -r -i "s@Apache-2.0;md5=generateme@LICENSE.txt;md5=$md5_sum@" *.bb 

# Get new version
version=`ls *.bb | shuf -n 1| sed -n 's/.*_\(.*\)\..*/\1/p'` 

cd - 

# Replace version in .bbappend files
for directory in recipes-tedge/*; do
    find $directory -type f -name '*.bbappend' | while read file ; do 
        new_file="$(echo ${file} | sed -e 's/[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}/'$version'/g')" 
        [ "${file}" != "${new_file}" ] && mv "${file}" "${new_file}" 
    done
done

# Replace old .bb files with the new ones.
ls $layer_dir/recipes | while read file ; do 
    for directory in recipes-tedge/*; do
        
        current_recipe="$(echo $file | cut -f1 -d"_")" 
        current_folder="$(basename $directory)" 

        if [ "${current_recipe}" = "${current_folder}" ]; then
            rm -f $layer_dir/$directory/*.bb 
            cp "$layer_dir/recipes/${file}" "${directory}" 
        fi
    done
done

# Remove scripts files
rm -f -r $layer_dir/recipes 

# Update README
sed -i -e "s/[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}/'$version'/" README.md

exit 