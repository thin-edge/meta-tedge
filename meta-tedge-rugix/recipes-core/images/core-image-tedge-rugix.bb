require recipes-core/images/core-image-tedge.bb

IMAGE_INSTALL:append = " \
    tedge-firmware-rugix \
    firmware-auto-rollback \
    tedge-inventory-rugix \
"

# Copy all files from IMAGE_BOOT_FILES into the ROOTFS
ROOTFS_POSTPROCESS_COMMAND += " copy_boot_files_into_rootfs; "

python copy_boot_files_into_rootfs() {
    import os
    import shutil

    # From https://github.com/openembedded/openembedded-core/blob/master/meta/lib/oe/bootfiles.py#L15
    # Replace once it is available in the respective yocto versions
    def get_boot_files(deploy_dir, boot_files):
        import re
        import os
        from glob import glob

        if boot_files is None:
            return None

        # list of tuples (src_name, dst_name)
        deploy_files = []
        for src_entry in re.findall(r'[\w;\-\./\*]+', boot_files):
            if ';' in src_entry:
                dst_entry = tuple(src_entry.split(';'))
                if not dst_entry[0] or not dst_entry[1]:
                    raise ValueError('Malformed boot file entry: %s' % src_entry)
            else:
                dst_entry = (src_entry, src_entry)

            deploy_files.append(dst_entry)

        install_files = []
        for deploy_entry in deploy_files:
            src, dst = deploy_entry
            if '*' in src:
                # by default install files under their basename
                entry_name_fn = os.path.basename
                if dst != src:
                    # unless a target name was given, then treat name
                    # as a directory and append a basename
                    entry_name_fn = lambda name: \
                                    os.path.join(dst,
                                                os.path.basename(name))

                srcs = glob(os.path.join(deploy_dir, src))

                for entry in srcs:
                    src = os.path.relpath(entry, deploy_dir)
                    entry_dst_name = entry_name_fn(entry)
                    install_files.append((src, entry_dst_name))
            else:
                install_files.append((src, dst))

        return install_files

    d = locals()['d']
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    image_rootfs = d.getVar('IMAGE_ROOTFS')
    boot_dir = os.path.join(image_rootfs, 'boot')
    image_boot_files = d.getVar('IMAGE_BOOT_FILES')

    # remove the existing /boot directory
    shutil.rmtree(boot_dir)

    install_files = get_boot_files(deploy_dir, image_boot_files)

    os.makedirs(boot_dir, exist_ok=True)

    for (src, dst) in install_files:
        dst_path = os.path.join(boot_dir, dst)
        src_path = os.path.join(deploy_dir, src)
        os.makedirs(os.path.dirname(dst_path), exist_ok=True)

        print(f"Copying boot file into rootfs. src={src_path}, dst={dst_path}")
        shutil.copy2(src_path, dst_path)
}
