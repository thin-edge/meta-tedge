# Enable mqtt plugin
PACKAGECONFIG[mqtt] = "--with-libmosquitto,--without-libmosquitto,mosquitto"

do_install:append() {
    install -d ${D}${sysconfdir}/collectd/collectd.conf.d
    rm -f ${D}${sysconfdir}/collectd.conf
    ln -sf -r ${D}${datadir}/contrib/collectd/collectd.conf ${D}${sysconfdir}/collectd.conf
}
