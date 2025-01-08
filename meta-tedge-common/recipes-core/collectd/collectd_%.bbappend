# Enable mqtt plugin
PACKAGECONFIG[mqtt] = "--with-libmosquitto,--without-libmosquitto,mosquitto"

do_install:append() {
    install -d ${D}${sysconfdir}/collectd/collectd.conf.d
    # The collectd.conf file is provided by a symlink in the tedge recipe
    rm -f ${D}${sysconfdir}/collectd.conf
}
