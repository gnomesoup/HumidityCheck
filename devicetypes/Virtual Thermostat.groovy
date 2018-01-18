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
        capabilty "Thermostat"
        capabilty "configuration"
        capability "Polling"
        capability "Sensor"

        attribute "thermostatFanState", "string"
    }

    // simulator metadata
    simulator {
		    status "off"			: "command: 4003, payload: 00"
		    status "heat"			: "command: 4003, payload: 01"
		    status "cool"			: "command: 4003, payload: 02"
		    status "auto"			: "command: 4003, payload: 03"
		    status "emergencyHeat"	: "command: 4003, payload: 04"


		    status "heat 60"        : "command: 4303, payload: 01 09 3C"
		    status "heat 68"        : "command: 4303, payload: 01 09 44"
		    status "heat 72"        : "command: 4303, payload: 01 09 48"

		    status "cool 72"        : "command: 4303, payload: 02 09 48"
		    status "cool 76"        : "command: 4303, payload: 02 09 4C"
		    status "cool 80"        : "command: 4303, payload: 02 09 50"

		    status "temp 58"        : "command: 3105, payload: 01 2A 02 44"
		    status "temp 62"        : "command: 3105, payload: 01 2A 02 6C"
		    status "temp 70"        : "command: 3105, payload: 01 2A 02 BC"
		    status "temp 74"        : "command: 3105, payload: 01 2A 02 E4"
		    status "temp 78"        : "command: 3105, payload: 01 2A 03 0C"
		    status "temp 82"        : "command: 3105, payload: 01 2A 03 34"

		    status "idle"			: "command: 4203, payload: 00"
		    status "heating"		: "command: 4203, payload: 01"
		    status "cooling"		: "command: 4203, payload: 02"
		    status "fan only"		: "command: 4203, payload: 03"
		    status "pending heat"	: "command: 4203, payload: 04"
		    status "pending cool"	: "command: 4203, payload: 05"
		    status "vent economizer": "command: 4203, payload: 06"

		    reply "2502": "command: 2503, payload: FF"
    }

	  tiles {
		    valueTile("temperature", "device.temperature", width: 2, height: 2) {
			      state("temperature", label:'${currentValue}°',
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
    }
}
