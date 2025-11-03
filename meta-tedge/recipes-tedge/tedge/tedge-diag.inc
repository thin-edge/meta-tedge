do_install:append () {
    install -d ${D}${datadir}/tedge/diag-plugins
    install -m 0755 ${S}/configuration/contrib/diag-plugins/* ${D}${datadir}/tedge/diag-plugins/
}

FILES:${PN} += "\
    ${datadir}/tedge/diag-plugins/01_tedge.sh \
    ${datadir}/tedge/diag-plugins/02_os.sh \
    ${datadir}/tedge/diag-plugins/03_mqtt.sh \
    ${datadir}/tedge/diag-plugins/04_workflow.sh \
    ${datadir}/tedge/diag-plugins/05_entities.sh \
    ${datadir}/tedge/diag-plugins/06_internal.sh \
    ${datadir}/tedge/diag-plugins/07_mosquitto.sh \
    ${datadir}/tedge/diag-plugins/template.sh.ignore \
"
