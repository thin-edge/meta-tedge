def tedge_feature_is_enabled(feature, if_true, if_false, d):
    return if_true if feature in tedge_features_list(d) else if_false

def tedge_features_list(d):
    enabled = d.getVar('TEDGE_FEATURES_ENABLE')
    if enabled is None:
        return ""
    else:
        enabled = list(dict.fromkeys(enabled.split()))

    disabled = d.getVar('TEDGE_FEATURES_DISABLE')
    if disabled is None:
        disabled = []
    else:
        disabled = list(dict.fromkeys(disabled.split()))

    return [feature for feature in enabled if feature not in disabled]

def tedge_features(d, separator=" "):
    return separator.join(tedge_features_list(d))

TEDGE_FEATURES = "${@tedge_features(d)}"
DISTROOVERRIDES:append = ":${@tedge_features(d, separator=':')}"

python() {
    available_features = {
        'tedge-collectd',
        'tedge-docker',
        'tedge-p11-kit'
    }

    if tedge_features(d) != d.getVar('TEDGE_FEATURES'):
        bb.fatal("Do not assign anything to TEDGE_FEATURES. Use TEDGE_FEATURES_ENABLE and TEDGE_FEATURES_DISABLE.")

    for feature in d.getVar('TEDGE_FEATURES').split():
        if feature not in available_features:
            bb.fatal("%s from TEDGE_FEATURES_ENABLE or TEDGE_FEATURES_DISABLE is not a valid tedge feature."
                     % feature)
}
