/**
 *  ST_AnyThing_AquariumController.groovy
 *
 *  Copyright 2016 Michael J. Pfammatter
 *  Copyright 2014 Dan G Ogorchock
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
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2015-01-03  Dan & Daniel   Original Creation
 *    2016-02-15  GnomeSoup      Switch to Aquarium Control
 *
 */
 
metadata {
	definition (name: "ST_AnyThing_AquariumController", namespace: "GnomeSoup", author: "Michael Pfammatter") {
		capability "Configuration"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Switch"
        capability "Sensor"
		capability "Polling"
        capability "waterSensor"
        capability "Refresh"

		attribute "t_Aquarium", "string"
        attribute "t_Air", "string"
        attribute "h_Air", "string"
        attribute "HighLow", "string"
	}

    simulator {
 
    }


    // Preferences
	preferences {
    	input "temphumidSampleRate", "number", title: "Temperature/Humidity Sensor Sampling Interval (seconds)", description: "Sampling Interval (seconds)", defaultValue: 30, required: true, displayDuringSetup: true
    }

	// Tile Definitions
	tiles(scale: 2) {
    
        multiAttributeTile(name:"richTemp", type:"generic", width: 6, height: 4) {
			tileAttribute("device.t_Aquarium", key: "PRIMARY_CONTROL") {
                attributeState("default",label:'${currentValue}°',
				backgroundColors:[
					[value: 70, color: "#153591"],
					[value: 75, color: "#1e9cbb"],
					[value: 78, color: "#90d2a7"],
					[value: 80, color: "#44b621"],
					[value: 82, color: "#f1d801"],
					[value: 85, color: "#d04e00"],
					[value: 90, color: "#bc2323"]
				])
            }
            tileAttribute("device.HighLow", key: "SECONDARY_CONTROL") {
            	attributeState("default", label:'${currentValue}')
            }
		}
        valueTile("t_Air", "device.t_Air", width: 2, height: 2) {
			state("temperature", label:'${currentValue}° Air', unit:"dF", 
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        valueTile("h_Air", "device.h_Air", width: 2, height: 2) {
        	state("humidity", label:'${currentValue}% Air', unit:"%")
        }
        standardTile("switch", "device.switch", width: 2, height:2, canChangeIcon: true) {
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
        standardTile("float", "device.float", width:2, height:2, canChangeIcon:true) {
        	state "wet", icon:"st.alarm.water.dry", backgroundColor:"#53a7c0"
    		state "dry", icon:"st.alarm.water.wet", backgroundColor:"#bc2323"
        }
		standardTile("configure", "device.configure", width: 1, height: 1, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
        standardTile("refresh", "device.refresh", width:1, height:1, decoration: "flat") {
        	state "refresh", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        main(["richTemp"])
        details(["richTemp","t_Air","float","switch","h_Air","configure","refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
    def msg = zigbee.parse(description)?.text
    log.debug "Parse got '${msg}'"

    def parts = msg.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null

    name = value != "ping" ? name : null
	
    def result = createEvent(name: name, value: value)
	
    if(name == "t_Aquarium") {
    	log.debug "state.aquaHigh = $state.aquaHigh"
        log.debug "state.aquaLow = $state.aquaLow"
    	state.HighLowChange = false
    	if(value > state.aquaHigh || !state.aquaHigh) {
        	log.debug "Updating high temperature"
            state.aquaHigh = value
            log.debug "state.aquaHigh = $state.aquaHigh"
            state.HighLowChange = true
        }
        if(value < state.aquaLow || !state.aquaLow) {
        	log.debug "Updating low temperature"
            state.aquaLow = value
            log.debug "state.aquaLow = $state.aquaLow"
            state.HighLowChange = true
        }  
    }
    if(state.HighLowChange) {
    	def result2 = createEvent(name: "HighLow", value: "High: ${state.aquaHigh}° Low: ${state.aquaLow}°")
        log.debug "result: $result"
        log.debug "result2: $result2"
        
        return [result, result2]
    }
    else {
    	log.debug "result: $result"
    	
    	result
    }
}

// handle commands

def on() {
	log.debug "Executing 'switch on'"
	zigbee.smartShield(text: "switch on").format()
}

def off() {
	log.debug "Executing 'switch off'"
	zigbee.smartShield(text: "switch off").format()
}

def poll() {
	//temporarily implement poll() to issue a configure() command to send the polling interval settings to the arduino
	configure()
}


def configure() {
	log.debug "Executing 'configure'"
	log.debug "temphumid " + temphumidSampleRate
	[
        "delay 1000",
        zigbee.smartShield(text: "temphumid " + temphumidSampleRate).format()
    ]
}

def refresh() {
	log.debug "Executing 'refresh'"
    log.debug "Reseting 'state.aquaHigh' and 'state.aquaLow' to 'null'"
	state.aquaHigh = null
    state.aquaLow = null
}