/**
 *  Hubitat to ST Notification Pusher
 *
 *  Copyright 2018 mattw01
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
metadata {
	definition (name: "Hubitat to ST Notification Pusher", namespace: "mattw01", author: "mattw01") {
        capability "Notification"
        capability "Speech Synthesis"
	}
    preferences() {
        section("SmartApp Data"){
            input "smartAppURL", "text", required: true, title: "ST SmartApp URL (endpointURL)"
            input "smartAppSecret", "text", required: true, title: "ST SmartApp Secret (endpointSecret)"
            input "notificationPrefix", "text", required: false, title: "Notification Prefix Text"
        }
    }
}
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def deviceNotification(notification)
{
	log.debug "Received 'deviceNotification' request with text '${notification}'"
    sendCommand(notification)
    sendEvent(name: "notification", value: notification, descriptionText: "Notified by deviceNotification '" + notification + "'")
}
def speak(text)
{
	log.debug "Received 'speak' request with text '${text}'"
    sendCommand(text)
    sendEvent(name: "spoke", value: text, descriptionText: "Notified by speak '" + text + "'")
}

def playText(text)
{
	log.debug "Received 'playText' request with text '${text}'"
    sendCommand(text)
    sendEvent(name: "playText", value: text, descriptionText: "Notified by playText '" + text + "'")
}



def sendCommand(cmd)
{
    def msg = cmd
    if(settings.notificationPrefix) // Add the notification prefix
    	msg = settings.notificationPrefix + cmd
    def params = [
        uri: "${settings.smartAppURL}notification/",
        query: [ message : "${msg}",
                access_token : "${settings.smartAppSecret}" ]
    ]
    log.debug "params: ${params}"
    try {
        httpGet(params) { resp ->
            resp.headers.each {
            log.debug "Response: ${it.name} : ${it.value}"
        }
        log.debug "response contentType: ${resp.contentType}"
        log.debug "response data: ${resp.data}"
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}