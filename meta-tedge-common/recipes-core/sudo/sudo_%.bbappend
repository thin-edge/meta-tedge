SUDOERS_FILE = "${D}/${sysconfdir}/sudoers.d/tedge"

do_install:append () {
    echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/tedge, /etc/tedge/sm-plugins/[a-zA-Z0-9]*, /bin/sync, /sbin/init" >> "${SUDOERS_FILE}"
    echo "tedge  ALL = (ALL) NOPASSWD: /bin/systemctl, /bin/journalctl" >> "${SUDOERS_FILE}"
    echo "tedge  ALL = (ALL) NOPASSWD: /usr/bin/tedge-write /etc/*, /usr/bin/tedge-write /data/*" >> "${SUDOERS_FILE}"
    echo "tedge  ALL = (ALL) NOPASSWD: /usr/share/tedge/log-plugins/[a-zA-Z0-9]*" >> "${SUDOERS_FILE}"
    echo "tedge  ALL = (ALL) NOPASSWD: /usr/share/tedge/config-plugins/[a-zA-Z0-9]*" >> "${SUDOERS_FILE}"
}
