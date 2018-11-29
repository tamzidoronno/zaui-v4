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
    this.createManagers();
};

GetShopApiWebSocket.prototype = {
    globalErrorHandler: false,
    messageCountChangedEvent: null,
    
    connect: function() {       
        // Does nothing, only to support backwards compab
    },

    setGlobalErrorHandler: function(globalErrorHandler) {
        this.globalErrorHandler = globalErrorHandler;
    },
  
    getUnsentMessageCount: function() {
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            return messagePersister.getUnsetMessageCount();
        }
        
        return 0;
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
        
    send: function(message, silent, resending) {
        var deferred = $.Deferred();
        var address = "https://"+this.address + "/scripts/api.php";
        
        if (this.address.includes('.local.')) {
            address = "http://"+this.address + "/scripts/api.php";
        }
        
        if (localStorage.getItem("username") && localStorage.getItem("password")) {
            message.username = JSON.stringify(localStorage.getItem("username"));
            message.password = JSON.stringify(localStorage.getItem("password"));
        }
        
        if (typeof(resending) == "undefined") {
            resending = false;
        }
        
        var me = this;
        var xhr = new XMLHttpRequest();
        xhr.open('POST', address);
        xhr.setRequestHeader('Content-Type', 'text/plain');
        
        var dataToSend = message;
        $.ajax({
            type: "POST",
            url: address,
            data: message,
            success: function(res) {
                if (resending && typeof(messagePersister) !== "undefined") {
                    messagePersister.markAsSent(message);
                    if (typeof(me.messageCountChangedEvent) !== "undefined") {
                        me.messageCountChangedEvent();
                    }
                }
                deferred.resolve(res);
            },
            error: function(res) {
                deferred.reject();
                
                if (res.status == 400) {
                    var data = JSON.parse(res.responseText);
                    for (var i in data) {

                        var error = {
                            errorCode : data[i]
                        };

                        if (typeof(me.globalErrorHandler) !== "undefined") {
                            me.globalErrorHandler(error);
                        }
                    }
                } else {
                    if (typeof(messagePersister) != null && messagePersister && !resending) {
                        if (typeof(me.messageCountChangedEvent) !== "undefined") {
                            me.messageCountChangedEvent();
                        }
                        
                        messagePersister.persist(dataToSend);
                    }
                }
            },
            dataType: 'json'
          });
        
        return deferred;
    },
  
    sendUnsentMessages: function(incommingMessage) {
        if (typeof(messagePersister) != null && messagePersister) {
            var messages = messagePersister.getAllUnsentMessages();
            console.log(messages);
            for (var i in messages) {
                var message = messages[i];
                this.send(message, false, true);
            }
        }
        
        var me = this;
        setTimeout(function() {
           me.sendUnsentMessages();
        }, 120000);
    },
    
}