/**
 *  Blink(1) When
 *
 *  Author: enomitch@gmail.com
 *  Date: 2015-08-19
 *  Code: https://github.com/eriknomitch/smart-things/smartapps/eriknomitch/blink1-when.src/blink1-when.groovy
 *
 * Copyright (C) 2015 Erik Nomitch <enomitch@gmail.com>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

definition(
    name: "Blink(1) When",
    namespace: "eriknomitch",
    author: "Erik Nomitch",
    description: "Send a Pushover notification from SmartThings when a device event occurs.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Office/office5-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Office/office5-icn@2x.png")

preferences
{
    section("Devices...") {
        input "switches", "capability.switch", title: "Which Switches?", multiple: true, required: false
        input "motionSensors", "capability.motionSensor", title: "Which Motion Sensors?", multiple: true, required: false
        input "contactSensors", "capability.contactSensor", title: "Which Contact Sensors?", multiple: true, required: false
        input "presenceSensors", "capability.presenceSensor", title: "Which Presence Sensors?", multiple: true, required: false
        input "accelerationSensors", "capability.accelerationSensor", title: "Which Acceleration Sensors?", multiple: true, required: false
        input "locks", "capability.lock", title: "Which Locks?", multiple: true, required: false
    }
    section("Application...") {
        input "push", "enum", title: "SmartThings App Notification?", required: true, multiple: false,
        metadata :[
           values: [ 'No', 'Yes' ]
        ]
     }
    section("Blink(1)...") {
        input "color", "enum", title: "Color", required: true,
        metadata :[
           values: [ 'Red', 'Green', 'Blue' ]
        ]
    }
}

def installed()
{
    log.debug "'Blink(1) When' installed with settings: ${settings}"
    initialize()
}

def updated()
{
    log.debug "'Blink(1) When' updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize()
{
    /**
     * You can customize each of these to only receive one type of notification
     * by subscribing only to the individual event for each type. Additional
     * logic would be required in the Preferences section and the device handler.
     */

    if (switches) {
        // switch.on or switch.off
        subscribe(switches, "switch", handler)
    }
    if (motionSensors) {
        // motion.active or motion.inactive
        subscribe(motionSensors, "motion", handler)
    }
    if (contactSensors) {
        // contact.open or contact.closed
        subscribe(contactSensors, "contact", handler)
    }
    if (presenceSensors) {
        // presence.present or 'presence.not present'  (Why the space? It is dumb.)
        subscribe(presenceSensors, "presence", handler)
    }
    if (accelerationSensors) {
        // acceleration.active or acceleration.inactive
        subscribe(accelerationSensors, "acceleration", handler)
    }
    if (locks) {
        // lock.locked or lock.unlocked
        subscribe(locks, "lock", handler)
    }
}

def handler(evt) {
    #log.debug "$evt.displayName is $evt.value"

    //if (push == "Yes")
    //{
        //sendPush("${evt.displayName} is ${evt.value} [Sent from 'Blink(1) When']");
    //}

    // Define the initial postBody keys and values for all messages
    def postBody = [
        color: "${evt.color}"
    ]

    // Prepare the package to be sent
    def params = [
        uri: "http://10.0.0.135:4567/blink1",
        body: postBody
    ]

    log.debug postBody

    httpPost(params){
        response ->
            if(response.status != 200)
            {
                sendPush("ERROR: 'Blink(1) When' received HTTP error ${response.status}. Check your keys!")
                log.error "Received HTTP error ${response.status}. Check your keys!"
            }
            else
            {
                log.debug "HTTP response received [$response.status]"
            }
    }
}
