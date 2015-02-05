/**
 *  Humidity Control
 *
 *  Copyright 2015 Michael Pfammatter
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
definition(
    name: "Humidity Control",
    namespace: "GnomeSoup",
    author: "Michael Pfammatter",
    description: "Turn on a humidifier when the moisture level drops below 40% and off again when the moisture level reaches 45%",
    category: "Health & Wellness",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When the humidity level changes on...") {
		// Gather input for humidity
        input "sensor1", "capability.relativeHumidityMeasurement", title: "Find a humidity sensor"
	}
    section("Control this device"){
    	input "switch1", "capability.switch", title: "Find a humidifier";
        //input "power1", "capability.powerMeter", title: "And it's power consumption"
    }
    section("When the humidity level reaches...") {
		// Gather max and min for humidity
        input "max", "number", title: "Turn off when humidity reaches";
        input "min", "number", title: "Turn on when humidity drops to"
	}
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone", "phone", title: "Send a Text Message?", required: false
    }
}

def installed() {
	intitalize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	intitalize()
}

def intitalize() {
	subscribe(sensor1, "humidity", humidityHandler)
}

def humidityHandler(evt) {
	log.trace "humidity: $evt.value, $evt"
    
    def humHigh = max
    def humLow = min
    def myHumidity = evt.value
    def switchState = switch1.latestValue("switch")
    //def switchPower = power1.latestValue("power")
    log.debug "Switch state:$switchState"
    switchState = (switchState == null) ? "off" : "on"
    def powerState = power1.latestValaue("power")
    log.debug "Power state:$powerState"
    powerState = if (powerState == null) ? 11 : powerState
    if (powerState < 1) {
    	send("The humidifier needs water!")
    }
    if (evt.doubleValue >= humHigh && switchState == "on") {
    	log.debug "Turning humidifier off. Humidity is at $myHumidity."
        switch1?.off()
    } else if (evt.doubleValue <= humLow && switchState == "off") {
   		log.debug "Turning humidifier on. Humidity is at $myHumidity."
        switch1?.on()
    } else {
    	log.debug "Humidity is fine at $myHumidity"
    }
}

private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phone ) {
        log.debug( "sending text message" )
        sendSms( phone, msg )
    }

    log.debug msg
}
