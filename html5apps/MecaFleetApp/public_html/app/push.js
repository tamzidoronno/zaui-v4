/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global cordova:false */
/* globals window */

/*!
 * Module dependencies.
 */

PushNotificationSettings = {
    registrationId : ""
};

var initPush = function() {
    
    var push = PushNotification.init({
        android: {
            senderID: "377883426248"
        },
        
        ios: {
            alert: "true",
            badge: "true",
            sound: "true",
            "clearBadge": "true"
        },
        
        windows: {}
    });

    push.on('registration', function(data) {
        PushNotificationSettings.registrationId = data.registrationId;
    });

    push.on('notification', function(data) {
        pushNotificationReceived();
    });

    push.on('error', function(e) {});
}
