
do_install:append () {
    echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/tedge, /etc/tedge/sm-plugins/[a-zA-Z0-9]*, /bin/sync, /sbin/init, /usr/bin/tedge-write /etc/*, /usr/bin/tedge-write /data/*" >> "${D}${sysconfdir}/sudoers.d/tedge"
}
