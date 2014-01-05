/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var papp = {
    // Application Constructor
    initialize: function() {
        console.log("Initializing");
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
        document.addEventListener("resume", App.programResumed, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicity call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        console.log("DEVIC EIS READY");
        papp.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var pushNotification = window.plugins.pushNotification;
        var device = window.device;
        App.start();
        
        if (device.platform == 'android' || device.platform == 'Android')
        {
            pushNotification.register(
                App.pushNotificationSuccess,
                App.pushNotificationError, 
                {
                    "senderID": "377883426248",
                    "ecb": "App.onNotificationGCM"
                }
            );
        }
        else
        {
            pushNotification.register(
                $.proxy(App.tokenHandler, App),
                $.proxy(App.pushNotificationError, App), 
                {
                    "badge": "true",
                    "sound": "true",
                    "alert": "true",
                    "ecb": "App.onNotificationApple"
                }
            );
        }

        
    }
};
