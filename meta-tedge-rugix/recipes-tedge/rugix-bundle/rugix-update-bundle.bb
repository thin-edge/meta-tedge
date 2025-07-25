inherit rugix-bundle

RUGIX_BUNDLE_PAYLOADS = "boot system"

RUGIX_PAYLOAD_boot[image] = "core-image-tedge-rugix"
RUGIX_PAYLOAD_boot[partition] = "2"

RUGIX_PAYLOAD_system[image] = "core-image-tedge-rugix"
RUGIX_PAYLOAD_system[partition] = "4"
