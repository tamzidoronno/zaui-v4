var GetShopApiWebSocket = function(address, port, identifier, persistMessages) {
    this.sentMessages =  [];
    this.messagesToSendJson =  [];
    this.address = address;

    if (typeof(port) === "undefined" || !port) {
        this.port = "31330";
    } else {
        this.port = port;
    }
    
    if (typeof(identifier) === "undefined" || !identifier) {
        this.identifier = this.address;
    } else {
        this.identifier = identifier; 
    }
};

GetShopApiWebSocket.prototype = {
    websocket: null,
    connectionEstablished: null,
    transferCompleted: null,
    transferStarted: null,
    shouldConnect: true,
    sessionId: false,
    unsentMessageLoaded: false,
    globalErrorHandler: false,
    messageCountChangedEvent: null,
    listeners: [],
    currentlySendingMessageInProgress: false,
    isInitialized: false,
    ignoreReconnectOnce: false,
    
    connect: function() {
        alert(this.address);
        this.createManagers();
    },

    setGlobalErrorHandler: function(globalErrorHandler) {
        this.globalErrorHandler = globalErrorHandler;
    },
    
    disconnect: function() {
        if (this.socket !== null) {
            this.socket.close();
            this.socket = null;
        }
        
        this.ignoreReconnectOnce = true;
        this.shouldConnect = true;
        this.connectionEstablished = null;
        this.currentlySendingMessageInProgress = false;
        this.sentMessages = [];
    },
    
    resetConnection: function() {
        this.disconnect()
        this.connect();
    },
    
    guid: function() {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
    },

    addListener : function(dataObjectName, callback, scope) {
        var listenObject = {
            gs_session_scope: scope,
            dataObjectName : dataObjectName,
            callback: callback
        }
        
        this.listeners.push(listenObject);
    },

    handleMessage: function(msg) {
        var data = msg.data;
        var jsonObject = JSON.parse(data);
        
        var corrolatingMessage = this.getMessage(jsonObject.messageId);
        
        if (typeof(corrolatingMessage) === "undefined") {
            this.handleIncomingMessage(jsonObject);
            return;
        }

        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            messagePersister.markAsSent(corrolatingMessage);
            this.fireMessageCountChanged();
        }

        if (this.globalErrorHandler && jsonObject && jsonObject.object && jsonObject.object.errorCode) {
            this.globalErrorHandler(jsonObject.object);
        } else {
            corrolatingMessage.resolveWith({ 'messageId': jsonObject.messageId }, [jsonObject.object]);
        }
        
        if (this.sentMessages.length === 0 && this.transferCompleted) {
            this.transferCompleted();
        }
        
        if (this.sentMessages.length === 0 && this.transferCompletedFirstTimeAfterUnsentMessageSent && this.firstUnsentMessages) {
            this.transferCompletedFirstTimeAfterUnsentMessageSent();
            this.firstUnsentMessages = false;
        }
    },

    handleIncomingMessage: function(msg) {
        this.currentlySendingMessageInProgress = false;
        
        if ( typeof msg === "object") {
            this.sendUnsentMessages();
            return;
        }
        
        var dataObject = JSON.parse(msg);
        for (var i in this.listeners) {
            var listener = this.listeners[i];
            if (listener.dataObjectName === dataObject.coninicalName) {
                if (listener.gs_session_scope) {
                    listener.callback.apply(listener.gs_session_scope, [dataObject.payLoad]);
                } else {
                    listener.callback(dataObject.payLoad);
                }
            }
        }
        
        this.sendUnsentMessages();
    },

    reconnect: function() {
        if (this.ignoreReconnectOnce) {
            this.ignoreReconnectOnce = false;
            return;
        }
        
        var me = this;
        this.shouldConnect = true;
        exec = function() {
            me.connect();
        };
        setTimeout(exec, 300);
    },
            
    initializeStore: function() {
        if (this.socket.OPEN) {
            this.socket.send('initstore:'+this.identifier);
            this.isInitialized = true;
        }
    },
            
    connected: function() {
        this.setSessionId();
        this.initializeStore();
        this.fireConnectedEvent();
        this.connectionEstablished = true;
        this.sendUnsentMessages();
    },
    
    getUnsentMessageCount: function() {
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            return messagePersister.getUnsetMessageCount();
        }
        
        return 0;
    },

    setSessionId: function() {
        if (sessionStorage.getItem("getshop.sessionId")) {
            this.sessionId = sessionStorage.getItem("getshop.sessionId");
        }
        
        if (!this.sessionId) {
            this.sessionId = this.guid();
            sessionStorage.setItem("getshop.sessionId", this.sessionId);
        }
        
        if (this.socket.OPEN)
            this.socket.send('sessionid:'+this.sessionId);
    },
          
    disconnected: function() {
        this.sentMessages = [];
        
        this.fireDisconnectedEvent();
        this.connectionEstablished = false;
        this.firstUnsentMessages = false;
        this.reconnect();
    },

    setInitConnectionFailed: function(callback) {
        this.initConnectionFailed = callback;
    },
    fireDisconnectedEvent: function() {
        if (this.connectionEstablished === null || this.connectionEstablished && typeof(this.disconnectedCallback) === "function") {
            if (this.disconnectedCallback) {
                this.disconnectedCallback();
            }
        }
    },
            
    fireConnectedEvent: function() {
        if (this.connectionEstablished === null || !this.connectionEstablished && typeof(this.connectedCallback) === "function") {
            if (this.connectedCallback) {
                this.connectedCallback();
            }
        }
    },
    
    fireMessageCountChanged: function() {
        if (this.messageCountChangedEvent) {
            this.messageCountChangedEvent();
        }
    },
    
    setMessageCountChangedEvent: function(func) {
        this.messageCountChangedEvent = func;
    },
            
    setDisconnectedEvent: function(callback) {
        this.disconnectedCallback = callback;
    },
            
    setConnectedEvent: function(callback) {
        this.connectedCallback = callback;
    },
        
    send: function(message, silent) {
        var deferred = $.Deferred();
        message.messageId = this.makeid();
        
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            messagePersister.persist(message);
            this.fireMessageCountChanged();
        }
        
        deferred.messageId = message.messageId;
        deferred.messageSent = false;
        
        if (this.sentMessages.length === 0 && this.transferStarted && silent !== true) {
            this.transferStarted();
        }
        
        this.sentMessages.push(deferred);
        this.sendUnsentMessages(message);
        
        return deferred;
    },
    
    getLoginMessage: function() {
        if (localStorage.getItem("username") && localStorage.getItem("password")) {
            var data = {
                args : {
                    username : JSON.stringify(localStorage.getItem("username")),
                    password : JSON.stringify(localStorage.getItem("password")),
                },
                method: 'logOn',
                interfaceName: 'core.usermanager.IUserManager',
            };
            
            data.messageId = this.makeid();
            return JSON.stringify(data);
        }
        
        return false;
    },
    
    sendUnsentMessages: function(incommingMessage) {
        if (!this.isInitialized)
            return;
        
        var loginMessage = this.getLoginMessage();
        
        var sendFunc = function(messageJson, me) { 
            if (me.socket == null || me.socket.readyState !== 1) {
                setTimeout(function() {
                    sendFunc(messageJson, me);
                }, 50);
            } else {
                if (loginMessage) {
                    me.socket.send(loginMessage);
                }
                
                
                me.socket.send(messageJson);
                var msg = JSON.parse(messageJson);
                var deffered = me.inSentMessages(msg.messageId);
                deffered.messageSent = true;
                me.currentlySendingMessageInProgress = true;
            }
        }
        
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            var allUnsetMessages = messagePersister.getAllUnsentMessages();
            
            for (var k in allUnsetMessages) {
                var unsentMessage = allUnsetMessages[k];
                
                if (this.currentlySendingMessageInProgress) {
                    return;
                }

                var messageJson2 = JSON.stringify(unsentMessage);
                var deferred2 = $.Deferred();
                deferred2.messageId = unsentMessage.messageId;

                var alreadySentMessage = this.inSentMessages(unsentMessage.messageId);
                
                if (alreadySentMessage && alreadySentMessage.messageSent) {
                    continue;
                }
                
                if (!alreadySentMessage) {
                    this.sentMessages.push(deferred2);
                } 

                sendFunc(messageJson2, this);
            }
        } else {
            if (incommingMessage !== null && typeof(incommingMessage) !== "undefined") {
                var messageJson2 = JSON.stringify(incommingMessage);
                var deferred2 = $.Deferred();
                deferred2.messageId = incommingMessage.messageId;
                this.sentMessages.push(deferred2);
                sendFunc(messageJson2, this);
            }
        }
    },
    
    inSentMessages: function(msgId) {
        for (var i in this.sentMessages) {
            if (this.sentMessages[i].messageId === msgId) {
                return this.sentMessages[i];
            }
        }
        
        return null;
    },

    getMessage: function(id) {
        for (var i=0;i<this.messagesToSendJson.length; i++) {
            if (this.messagesToSendJson[i].messageId === id) {
                this.messagesToSendJson.splice(i, 1);
            }
        }
        
        if (this.persistMessages) {
            localStorage.setItem("gs_api_messagetopush", JSON.stringify(this.messagesToSendJson));
            this.fireMessageCountChanged();
        }
        
        for (var i=0;i<this.sentMessages.length; i++) {
            if (this.sentMessages[i].messageId === id) {
                var message = this.sentMessages[i];
                this.sentMessages.splice(i, 1);
                return message;
            }
        }
    },
            
    makeid :  function () {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for( var i=0; i < 35; i++ )
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
}
