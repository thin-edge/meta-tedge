# Enable mqtt plugin
PACKAGECONFIG[mqtt] = "--with-libmosquitto,--without-libmosquitto,mosquitto"

do_install:append() {
    install -d ${D}${sysconfdir}/collectd/collectd.conf.d
}
