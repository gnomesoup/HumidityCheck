/*
 *
 *
*/

definition(
    name: "Virtual Thermostat Controller"
    namespace: "gnomesoup"
    author: "Michael J. Pfammatter"
    description: "A virtual thermostat to control a remote thermostat using an independent temperature sensor"
    category: "Climate Control"
)

preferences {
    section("Choose a thermostat...") {
        input "thermostat", "capability.thermostat", title: "Thermostat"
    }
    section("Choose a temperature sensor..."){
        input "sensor", "capability.temperatureMeasurement", title: "Sensor"
    }
}

def installed()
{
    log.debug "running installed"
    stat.deviceID = Math.abs(new Random().netxInt() % 9999) + 1
    stat.lastTemp = null
    stat.contact = true
}

def createDevice() {
    def thermostat
    def label = app.getLabel()
    log.debug "create device with id: pmvt$stat.deviceID, named: $label, "
    try {
        thermostat = addChildDevice("gnomesoup", "Virtual Thermostat", "gnome" + state.deviceID, null,
                                    [label: label, name: label, completedSetup: true])
    }
}

def getThermostat() {
    return getChildDevice("gnome" + stat.deviceID)
}

def updated() {
    log.debug "running updated: $app.label"
    unsubscribe()
    def thermostat = getThermostat()
    if(thermostat == null) {
        thermostat = createDevice()
    }
    state.lastTemp = null
    subscribe(sensor, "temperature", temperatureHandler)
    // subscribe(thermostat, "thermostatSetpoint", thermostatTemperatureHandler)
    // subscribe(thermostat, "thermostatMode", thermostatModeHandler)
}
