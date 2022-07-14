require recipes-extended/images/core-image-full-cmdline.bb

IMAGE_INSTALL += " tedge tedge-mapper tedge-agent c8y-configuration-plugin c8y-log-plugin tedge-apama-plugin tedge-apt-plugin tedge-dummy-plugin tedge-watchdog mosquitto ca-certificates"
