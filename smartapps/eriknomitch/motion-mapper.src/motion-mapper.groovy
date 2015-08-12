/**
 *  Motion Mapper
 *
 *  Copyright 2015 Erik Nomitch
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
    name: "Motion Mapper",
    namespace: "eriknomitch",
    author: "Erik Nomitch",
    description: "Motion Map description...",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Office/office5-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Office/office5-icn@2x.png")

preferences {
    section("Configure Motion Sensors") {
        input "arduino", "capability.switch"
        input "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
        input(name: "led", type: "enum", title: "Light LED", options: ["White", "Green", "Blue"])
     }
}

def installed() {
        log.debug "Installed with settings: ${settings}"
        initialize()
}

def updated() {
        log.debug "Updated with settings: ${settings}"

        unsubscribe()
        initialize()
}

def initialize() {
        // TODO: subscribe to attributes, devices, locations, etc.
    subscribe(motion, "motion.active", motionHandler)
}

def motionHandler(evt) {
        log.debug "motionHandler: ${evt.value}"

        //zigbee.smartShield(text: "motion led ${evt.value}").format()
}
