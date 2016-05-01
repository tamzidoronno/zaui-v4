/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

thundashop.Namespace.Register('getshop.WebSocketClient');

getshop.WebSocketClient = {
    client: false, 
    listeners: [],
    
    connected: function() {
    },
    
    disconnected: function() {
        getshop.WebSocketClient.client = false;
        setTimeout(getshop.WebSocketClient.getClient, 1000);
    },
    
    handleMessage: function(msg) {
        var dataObject = JSON.parse(JSON.parse(msg.data));
        
        for (var i in getshop.WebSocketClient.listeners) {
            var listener = getshop.WebSocketClient.listeners[i];
            if (listener.dataObjectName === dataObject.coninicalName) {
                listener.callback(dataObject.payLoad);
            }
        }
    },
    
    getClient: function() {
        var me = getshop.WebSocketClient;
        
        if (!getshop.WebSocketClient.client) {
            this.socket = new WebSocket("ws://"+window.location.host+":31330/");
            this.socket.onopen = $.proxy(this.connected, this);
            this.socket.onclose = function() {
                me.disconnected();
            };
            this.socket.onmessage = function(msg) {
                me.handleMessage(msg);
            };
        }
        
        return getshop.WebSocketClient.client;
    },
    
    addListener : function(dataObjectName, callback) {
        getshop.WebSocketClient.getClient();
        var listenObject = {
            dataObjectName : dataObjectName,
            callback: callback
        }
        
        getshop.WebSocketClient.listeners.push(listenObject);
   }
};