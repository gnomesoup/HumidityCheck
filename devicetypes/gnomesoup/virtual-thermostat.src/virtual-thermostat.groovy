/*
 * Copyright 2018 Michael J Pfammatter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata{
    definition (name: "Virtual Thermostat", namespace: "gnomesoup", author: "Michael J Pfammatter") {
        capability "Actuator"
        capability "Temperature Measurement"
        capability "Thermostat"
        capability "Refresh"
        capability "Configuration"
        capability "Sensor"

        // attribute "thermostatFanState", "string"
        command "tempUp"
        command "tempDown"
        // command "setVirtualTemperature", ["number"]
        command "setOperatingState", ["string"]
        // command "setMode", ["string"]
    }
    simulator {
        // TODO: define status and reply messages here
    }

	  tiles(scale: 2) {
        multiAttributeTile(name:"thermostatFull", type:"thermostat", width:6, height:4) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("temp", label:'${currentValue}', unit:"dF", defaultState: true)
            }
            tileAttribute("device.temperature", key: "VALUE_CONTROL") {
                attributeState("VALUE_UP", action: "tempUp")
                attributeState("VALUE_DOWN", action: "tempDown")
            }
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState("idle", backgroundColor:"#00A0DC")
                attributeState("heating", backgroundColor:"#e86d13")
                attributeState("cooling", backgroundColor:"#00A0DC")
            }
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
                attributeState("off", label:'${name}')
                attributeState("heat", label:'${name}')
                attributeState("cool", label:'${name}')
                attributeState("auto", label:'${name}')
            }
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
                attributeState("heatingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
            }
            tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
                attributeState("coolingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
            }
        }
    }
}

def installed() {
    log.trace "Executing 'installed'"
    initialize()
    done()
}

def configure() {
    log.trace "Executing 'configure'"
    initialize()
    done()
}

private initialize() {
    log.trace "Executin 'initialize'"
    sendEvent(name: "temperature", value: 75.0, unit: "dF")
    sendEvent(name: "heatingSetpoint", value: 70.0, unit: "dF")
    sendEvent(name: "coolingSetpoint", value: 80.0, unit: "dF")
    sendEvent(name: "thermostatOperatingState", value: "idle")
    // sendEvent(name: "thermostatMode", value: "heat")
}

private void done() {
    log.trace "---- DONE ----"
}

def heatingSetpointUp() {
    def hsp = device.current
}
def tempUp() {
    def hsp = device.currentValue("heatingSetpoint") + 1
    log.debug "Setting heatingSetpoint to: $hsp"
    sendEvent(name:"thermostatSetpoint", value: temp, unit: "dF")
    sendEvent(name:"heatingSetpoint", value: hsp, unit: "dF")
}

def tempDown() {
    def hsp = device.currentValue("heatingSetpoint") - 1
    log.debug "Setting heatingSetpoint to: $hsp"
    sendEvent(name:"thermostatSetpoint", value: temp, unit: "dF")
    sendEvent(name:"heatingSetpoint", value: hsp, unit: "dF")
}

def setOperatingState() {
}
