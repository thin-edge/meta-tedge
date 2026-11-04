# RAUC development PKI

**Do not use these files for production devices.** The private key is public, so anyone can sign a bundle which a device with this keyring will install.

These files are used when no signer is configured (see `RAUC_KEY_FILE`, `RAUC_CERT_FILE` and `RAUC_KEYRING_FILE` in [kas/README.md](../../../../kas/README.md#signing-rauc-bundles)), so the images build without any setup.

|File|Description|
|--|--|
|`ca.cert.pem`|"Test Org rauc CA Development" CA certificate from [meta-rauc-community](https://github.com/rauc/meta-rauc-community) (`meta-rauc-raspberrypi`). Previously built images trust this CA, so bundles signed with the development key can still be installed on them.|
|`development-1.cert.pem`|Development signer certificate, issued by `ca.cert.pem`|
|`development-1.key.pem`|Development signer private key|
