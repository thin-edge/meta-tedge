#Command to execute:    robot -d \results --timestampoutputs --log health_tedge_mapper.html --report NONE health_tedge_mapper.robot

*** Settings ***
Library    Browser
Library    SSHLibrary
Library    DateTime
Library    Dialogs
Library    String
Library    CSVLibrary
Library    OperatingSystem
Suite Setup            Setup
Suite Teardown         Teardown

*** Variables ***
${HOST}            192.168.7.2
${USERNAME}        root
${PASSWORD}
${download_dir}    /home/${USERNAME}
${url_http}        https://${url_tedge}
${url_tedge}       [URL_TEDGE]
${c8y_user}        [USERNAME]
${c8y_pass}        [PASSWORD]

# noticed that when executing tasks, if one fails, the rest is executed anyway
#
# that would make sense if these were really seperate tasks, but as is stands, these are just successive steps of a
# single test
#
# to abort on first failure, use `--exitonfailure`

*** Tasks ***
Set the URL of your Cumulocity IoT tenant
    ${rc}=    Execute Command    sudo tedge config set c8y.url ${url_tedge}    return_stdout=False    return_rc=True    #Set the URL of your Cumulocity IoT tenant
    Should Be Equal    ${rc}    ${0}

Create the certificate
    ${output}    ${stderr}    ${rc}=    Execute Command    sudo tedge cert create --device-id ${DeviceID}   return_stderr=True  return_rc=True
    Should Be Equal    ${rc}    ${0}
    #You can then check the content of that certificate.
    ${output}=    Execute Command    sudo tedge cert show    #You can then check the content of that certificate.
    Should Contain    ${output}    Device certificate: /etc/tedge/device-certs/tedge-certificate.pem
    Should Contain    ${output}    Subject: CN=${DeviceID}, O=Thin Edge, OU=Test Device
    Should Contain    ${output}    Issuer: CN=${DeviceID}, O=Thin Edge, OU=Test Device
    Should Contain    ${output}    Valid from:
    Should Contain    ${output}    Valid up to:
    Should Contain    ${output}    Thumbprint:

tedge cert upload c8y command
    Write   sudo tedge cert upload c8y --user ${c8y_user}
    Write    ${c8y_pass}
    Sleep    3s
    ${output}=    Read
    Should Contain    ${output}    Certificate uploaded successfully.

Connect the device
    ${output}    ${stderr}=    Execute Command    sudo tedge connect c8y    return_stderr=True    #You can then check the content of that certificate.
    Sleep    3s
    Should Contain    ${output}    Checking if systemd is available.
    Should Contain    ${output}    Checking if configuration for requested bridge already exists.
    Should Contain    ${output}    Validating the bridge certificates.
    Should Contain    ${output}    Creating the device in Cumulocity cloud.
    Should Contain    ${output}    Saving configuration for requested bridge.
    Should Contain    ${output}    Restarting mosquitto service.
    Should Contain    ${output}    Awaiting mosquitto to start. This may take up to 5 seconds.
    Should Contain    ${output}    Enabling mosquitto service on reboots.
    Should Contain    ${output}    Successfully created bridge connection!
    Should Contain    ${output}    Sending packets to check connection. This may take up to 2 seconds.
    Should Contain    ${output}    Connection check is successful.
    Should Contain    ${output}    Checking if tedge-mapper is installed.
    Should Contain    ${output}    Starting tedge-mapper-c8y service.
    Should Contain    ${output}    Persisting tedge-mapper-c8y on reboot.
    Should Contain    ${output}    tedge-mapper-c8y service successfully started and enabled!
    Should Contain    ${output}    Enabling software management.
    Should Contain    ${output}    Checking if tedge-agent is installed.
    Should Contain    ${output}    Starting tedge-agent service.
    Should Contain    ${output}    Persisting tedge-agent on reboot.
    Should Contain    ${output}    tedge-agent service successfully started and enabled!

Sending your first telemetry data
    # Set the URL of your Cumulocity IoT tenant
    ${rc}=    Execute Command    tedge mqtt pub tedge/measurements '{"temperature": 21}'    return_stdout=False    return_rc=True
    Should Be Equal    ${rc}    ${0}

*** Keywords ***
Open Connection And Log In
   Open Connection     ${HOST}
   Login               ${USERNAME}        ${PASSWORD}

Create Timestamp    # Creating timestamp to be used for Device ID
        ${timestamp}=    get current date    result_format=%d%m%Y%H%M%S
        log    ${timestamp}
        Set Global Variable    ${timestamp}

Define Device id    # Defining the Device ID, structure is (ST'timestamp') (eg. ST01092022091654)
        ${DeviceID}   Set Variable    ST${timestamp}
        Set Suite Variable    ${DeviceID}

Delete previous certificate
    Execute Command    sudo tedge cert remove

Setup
    Open Connection And Log In
    Create Timestamp
    Define Device id
    Delete previous certificate

Teardown
    Execute Command    sudo tedge disconnect c8y
    Execute Command    sudo tedge cert remove
    Close All Connections

