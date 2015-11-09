/**
 *  Action on Knock
 *
 *  Author: erik@nomitch.com
 *  Date: 11/07/15
 *
 *  Take an action when someone knocks on a door.
 */

definition(
    name: "Action on Knock",
    namespace: "eriknomitch",
    author: "erik@nomitch.com",
    description: "Take an Action when someone knocks on a door.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
  section("When Someone Knocks?") {
    input name: "knockSensor", type: "capability.accelerationSensor", title: "Where?"
  }

  section("But not when they open this door?") {
    input name: "openSensor", type: "capability.contactSensor", title: "Where?"
  }

  section("Knock Delay (defaults to 5s)?") {
    input name: "knockDelay", type: "number", title: "How Long?", required: false
  }

  section("Notifications") {
    input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
    input "phone", "phone", title: "Send a Text Message?", required: false
  }
}

def installed() {
  init()
}

def updated() {
  unsubscribe()
  init()
}

def init() {
  state.lastClosed = 0
  subscribe(knockSensor, "acceleration.active", handleEvent)
  subscribe(openSensor, "contact.closed", doorClosed)
}

def doorClosed(evt) {
  state.lastClosed = now()
}

def doorKnock() {
  if((openSensor.latestValue("contact") == "closed") &&
     (now() - (60 * 1000) > state.lastClosed)) {
    log.debug("${knockSensor.label ?: knockSensor.name} detected a knock.")
    send("${knockSensor.label ?: knockSensor.name} detected a knock.")
  }

  else {
    log.debug("${knockSensor.label ?: knockSensor.name} knocked, but looks like it was just someone opening the door.")
  }
}

def handleEvent(evt) {
  def delay = knockDelay ?: 5
  runIn(delay, "doorKnock")
}

private send(msg) {
  if(sendPushMessage != "No") {
    log.debug("Sending push message")
    sendPush(msg)
  }

  if(phone) {
    log.debug("Sending text message")
    sendSms(phone, msg)
  }

  def params = [
      uri: "http://home.nomitch.com",
      path: "/test"
  ]

  try {
      httpGet(params) { resp ->
          resp.headers.each {
             log.debug "${it.name} : ${it.value}"
          }
          log.debug "response contentType: ${resp.contentType}"
          log.debug "response data: ${resp.data}"
      }
  } catch (e) {
      log.error "something went wrong: $e"
  }

  log.debug(msg)
}

