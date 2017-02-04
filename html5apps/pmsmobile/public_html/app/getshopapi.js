var GetShopApiWebSocket = function(address, port, identifier, persistMessages) {
    this.sentMessages =  [];
    this.messagesToSendJson =  [];
    this.address = address;
    this.persistMessages = persistMessages;

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
    messageCountChangedEvent: null,
    firstResendOfUnsentMessages: false,
    firstUnsentMessages: false,
    transferCompletedFirstTimeAfterUnsentMessageSent: false,
    listeners: [],
    
    connect: function() {
        if (!this.shouldConnect)
            return;
        
        this.shouldConnect = false;
        this.connectedCalled = true;
        var me = this;
        if (this.connectionEstablished === null) {
            this.fireDisconnectedEvent();
        }
        var address = "ws://"+this.address+":"+this.port+"/";
        this.socket = new WebSocket(address);
        this.socket.onopen = $.proxy(this.connected, this);
        this.socket.onclose = function() {
            me.disconnected();
        };
        this.socket.onmessage = function(msg) {
            me.handleMessage(msg);
        };
        
        this.createManagers();
        this.loadUnsentMessages();
    },
    
    setTransferCompletedFirstTimeAfterUnsentMessageSent: function(func) {
        this.transferCompletedFirstTimeAfterUnsentMessageSent = func;
    },
    
    loadUnsentMessages: function() {
        try {
            this.messagesToSendJson = JSON.parse(localStorage.getItem("gs_api_messagetopush"));
        } catch (Ex) {
            this.messagesToSendJson = [];
        }

        this.unsentMessageLoaded = true;
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

        corrolatingMessage.resolveWith({ 'messageId': jsonObject.messageId }, [jsonObject.object]);
        if (this.sentMessages.length === 0 && this.transferCompleted) {
            this.transferCompleted();
        }
        
        if (this.sentMessages.length === 0 && this.transferCompletedFirstTimeAfterUnsentMessageSent && this.firstUnsentMessages) {
            this.transferCompletedFirstTimeAfterUnsentMessageSent();
            this.firstUnsentMessages = false;
        }
    },

    handleIncomingMessage: function(msg) {
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
    },

    reconnect: function() {
        var me = this;
        this.shouldConnect = true;
        exec = function() {
            me.connect();
        };
        setTimeout(exec, 300);
    },
            
    initializeStore: function() {
        if (this.socket.OPEN)
            this.socket.send('initstore:'+this.identifier);
    },
            
    connected: function() {
        this.setSessionId();
        this.initializeStore();
        this.fireConnectedEvent();
        this.connectionEstablished = true;
    },
    
    getUnsentMessageCount: function() {
        try {
            return JSON.parse(localStorage.getItem("gs_api_messagetopush")).length;
        } catch (Ex) {
            return 0;
        }
    },
    
    sendUnsentMessages: function() {
        if (!this.persistMessages) {
            this.unsentMessageLoaded = false;
            return;
        }
        
        this.firstResendOfUnsentMessages = true;
        
        var anySent = false;
        for (var i in this.messagesToSendJson) {
            var msg = this.messagesToSendJson[i];
            this.send(msg);
            anySent = true;
        }
        
        this.firstResendOfUnsentMessages = false;
        this.firstUnsentMessages = true;
        
        if (!anySent) {
            if (this.sentMessages.length === 0 && this.transferCompletedFirstTimeAfterUnsentMessageSent) {
                this.firstUnsentMessages = false;
                this.transferCompletedFirstTimeAfterUnsentMessageSent();
            }
        }
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
        deferred.messageId = message.messageId;
        var messageJson = JSON.stringify(message);
        
        if (this.unsentMessageLoaded && !this.firstResendOfUnsentMessages) {
            if (!this.messagesToSendJson) {
                this.messagesToSendJson = [];
            }
            this.messagesToSendJson.push(message);
            localStorage.setItem("gs_api_messagetopush", JSON.stringify(this.messagesToSendJson));
            this.fireMessageCountChanged();
        }
        
        if (this.sentMessages.length === 0 && this.transferStarted && silent !== true) {
            this.transferStarted();
        }
        this.sentMessages.push(deferred);
        var sendFunc = function(messageJson, me) {
            if (me.socket.readyState !== 1) {
                setTimeout(function() {
                    sendFunc(messageJson, me);
                }, 50);
            } else {
                me.socket.send(messageJson);
            }
        }
        
        sendFunc(messageJson, this);
        
        return deferred;
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


GetShopApiWebSocket.BannerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BannerManager.prototype = {
    'addImage' : function(id,fileId, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                fileId : JSON.stringify(fileId),
            },
            method: 'addImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addSlide' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'addSlide',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createSet' : function(width,height,id, gs_silent) {
        var data = {
            args : {
                width : JSON.stringify(width),
                height : JSON.stringify(height),
                id : JSON.stringify(id),
            },
            method: 'createSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteSet' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteSlide' : function(slideId, gs_silent) {
        var data = {
            args : {
                slideId : JSON.stringify(slideId),
            },
            method: 'deleteSlide',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSet' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSlideById' : function(slideId, gs_silent) {
        var data = {
            args : {
                slideId : JSON.stringify(slideId),
            },
            method: 'getSlideById',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'linkProductToImage' : function(bannerSetId,imageId,productId, gs_silent) {
        var data = {
            args : {
                bannerSetId : JSON.stringify(bannerSetId),
                imageId : JSON.stringify(imageId),
                productId : JSON.stringify(productId),
            },
            method: 'linkProductToImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeImage' : function(bannerSetId,fileId, gs_silent) {
        var data = {
            args : {
                bannerSetId : JSON.stringify(bannerSetId),
                fileId : JSON.stringify(fileId),
            },
            method: 'removeImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveSet' : function(set, gs_silent) {
        var data = {
            args : {
                set : JSON.stringify(set),
            },
            method: 'saveSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setImageForSlide' : function(slideId,fileId, gs_silent) {
        var data = {
            args : {
                slideId : JSON.stringify(slideId),
                fileId : JSON.stringify(fileId),
            },
            method: 'setImageForSlide',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ContentManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ContentManager.prototype = {
    'createContent' : function(content, gs_silent) {
        var data = {
            args : {
                content : JSON.stringify(content),
            },
            method: 'createContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteContent' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllContent' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getContent' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveContent' : function(id,content, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                content : JSON.stringify(content),
            },
            method: 'saveContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.FooterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FooterManager.prototype = {
    'getConfiguration' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getConfiguration',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setLayout' : function(numberOfColumns, gs_silent) {
        var data = {
            args : {
                numberOfColumns : JSON.stringify(numberOfColumns),
            },
            method: 'setLayout',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setType' : function(column,type, gs_silent) {
        var data = {
            args : {
                column : JSON.stringify(column),
                type : JSON.stringify(type),
            },
            method: 'setType',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.LogoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LogoManager.prototype = {
    'deleteLogo' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'deleteLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLogo' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setLogo' : function(fileId, gs_silent) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'setLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.NewsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsManager.prototype = {
    'addNews' : function(newsEntry,newsListId, gs_silent) {
        var data = {
            args : {
                newsEntry : JSON.stringify(newsEntry),
                newsListId : JSON.stringify(newsListId),
            },
            method: 'addNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addSubscriber' : function(email, gs_silent) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'addSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'applyUserFilter' : function(newsListId,userId, gs_silent) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
                userId : JSON.stringify(userId),
            },
            method: 'applyUserFilter',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteNews' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllNews' : function(newsListId, gs_silent) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
            },
            method: 'getAllNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllSubscribers' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllSubscribers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNewsForPage' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getNewsForPage',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNewsUsers' : function(newsListId, gs_silent) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
            },
            method: 'getNewsUsers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isFiltered' : function(newsListId,userId, gs_silent) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
                userId : JSON.stringify(userId),
            },
            method: 'isFiltered',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'publishNews' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'publishNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeSubscriber' : function(subscriberId, gs_silent) {
        var data = {
            args : {
                subscriberId : JSON.stringify(subscriberId),
            },
            method: 'removeSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.AccountingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AccountingManager.prototype = {
    'createCombinedOrderFile' : function(newUsersOnly, gs_silent) {
        var data = {
            args : {
                newUsersOnly : JSON.stringify(newUsersOnly),
            },
            method: 'createCombinedOrderFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createCreditorFile' : function(newOnly, gs_silent) {
        var data = {
            args : {
                newOnly : JSON.stringify(newOnly),
            },
            method: 'createCreditorFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrderFile' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'createOrderFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createUserFile' : function(newOnly, gs_silent) {
        var data = {
            args : {
                newOnly : JSON.stringify(newOnly),
            },
            method: 'createUserFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteFile' : function(fileId, gs_silent) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'deleteFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'downloadOrderFileNewType' : function(configId,start,end, gs_silent) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'downloadOrderFileNewType',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAccountingConfig' : function(configId, gs_silent) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
            },
            method: 'getAccountingConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAccountingManagerConfig' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAccountingManagerConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllConfigs' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllConfigs',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllFiles' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllFiles',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllFilesNotTransferredToAccounting' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllFilesNotTransferredToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFile' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFileById' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFileById',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFileByIdResend' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFileByIdResend',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNewFile' : function(type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getNewFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStats' : function(configId, gs_silent) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
            },
            method: 'getStats',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAsTransferredToAccounting' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'markAsTransferredToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeTransferConfig' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'removeTransferConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveConfig' : function(config, gs_silent) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setAccountingManagerConfig' : function(config, gs_silent) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'setAccountingManagerConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'transferFiles' : function(type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'transferFiles',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'transferFilesToAccounting' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'transferFilesToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.AmestoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AmestoManager.prototype = {
    'syncAllCostumers' : function(hostname, gs_silent) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'syncAllCostumers',
            interfaceName: 'core.amesto.IAmestoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'syncAllOrders' : function(hostname, gs_silent) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'syncAllOrders',
            interfaceName: 'core.amesto.IAmestoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'syncAllStockQuantity' : function(hostname, gs_silent) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'syncAllStockQuantity',
            interfaceName: 'core.amesto.IAmestoManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.GetShopApplicationPool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopApplicationPool.prototype = {
    'deleteApplication' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'deleteApplication',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'get' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'get',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveApplication' : function(application, gs_silent) {
        var data = {
            args : {
                application : JSON.stringify(application),
            },
            method: 'saveApplication',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.StoreApplicationInstancePool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreApplicationInstancePool.prototype = {
    'createNewInstance' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'createNewInstance',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplicationInstance' : function(applicationInstanceId, gs_silent) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getApplicationInstance',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplicationInstances' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'getApplicationInstances',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent);
    },

    'setApplicationSettings' : function(settings, gs_silent) {
        var data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'setApplicationSettings',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.StoreApplicationPool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreApplicationPool.prototype = {
    'activateApplication' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'activateApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'activateModule' : function(module, gs_silent) {
        var data = {
            args : {
                module : JSON.stringify(module),
            },
            method: 'activateModule',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'deactivateApplication' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'deactivateApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getActivatedModules' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getActivatedModules',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getActivatedPaymentApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getActivatedPaymentApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllAvailableModules' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllAvailableModules',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplication' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailableApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAvailableApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailableApplicationsThatIsNotActivated' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAvailableApplicationsThatIsNotActivated',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailableThemeApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAvailableThemeApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPaymentSettingsApplication' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPaymentSettingsApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getShippingApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getShippingApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'getThemeApplication' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getThemeApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'isActivated' : function(appId, gs_silent) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'isActivated',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'setSetting' : function(applicationId,settings, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                settings : JSON.stringify(settings),
            },
            method: 'setSetting',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

    'setThemeApplication' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'setThemeApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.DoorManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DoorManager.prototype = {
    'addCard' : function(multilevelname, personId,card, gs_silent) {
        var data = {
            args : {
                personId : JSON.stringify(personId),
                card : JSON.stringify(card),
            },
            method: 'addCard',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearDoorCache' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'clearDoorCache',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'closeAllForTheDay' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'closeAllForTheDay',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'doorAction' : function(multilevelname, externalId,state, gs_silent) {
        var data = {
            args : {
                externalId : JSON.stringify(externalId),
                state : JSON.stringify(state),
            },
            method: 'doorAction',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'generateDoorLogForAllDoorsFromResult' : function(multilevelname, resultFromArx, gs_silent) {
        var data = {
            args : {
                resultFromArx : JSON.stringify(resultFromArx),
            },
            method: 'generateDoorLogForAllDoorsFromResult',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllAccessCategories' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllAccessCategories',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllDoors' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllDoors',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllPersons' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllPersons',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLogForAllDoor' : function(multilevelname, start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getLogForAllDoor',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLogForDoor' : function(multilevelname, externalId,start,end, gs_silent) {
        var data = {
            args : {
                externalId : JSON.stringify(externalId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getLogForDoor',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPerson' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPerson',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'pmsDoorAction' : function(multilevelname, code,type, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
                type : JSON.stringify(type),
            },
            method: 'pmsDoorAction',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updatePerson' : function(multilevelname, person, gs_silent) {
        var data = {
            args : {
                person : JSON.stringify(person),
            },
            method: 'updatePerson',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.AsanaManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AsanaManager.prototype = {
    'getProjects' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getProjects',
            interfaceName: 'core.asanamanager.IAsanaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTasks' : function(projectId,year,month, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getTasks',
            interfaceName: 'core.asanamanager.IAsanaManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.BamboraManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BamboraManager.prototype = {
    'checkForOrdersToCapture' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.bambora.IBamboraManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCheckoutUrl' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getCheckoutUrl',
            interfaceName: 'core.bambora.IBamboraManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.BigStock = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BigStock.prototype = {
    'addGetShopImageIdToBigStockOrder' : function(downloadUrl,imageId, gs_silent) {
        var data = {
            args : {
                downloadUrl : JSON.stringify(downloadUrl),
                imageId : JSON.stringify(imageId),
            },
            method: 'addGetShopImageIdToBigStockOrder',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailableCredits' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAvailableCredits',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent);
    },

    'purchaseImage' : function(imageId,sizeCode, gs_silent) {
        var data = {
            args : {
                imageId : JSON.stringify(imageId),
                sizeCode : JSON.stringify(sizeCode),
            },
            method: 'purchaseImage',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCreditAccount' : function(credits,password, gs_silent) {
        var data = {
            args : {
                credits : JSON.stringify(credits),
                password : JSON.stringify(password),
            },
            method: 'setCreditAccount',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.BookingEngine = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BookingEngine.prototype = {
    'changeBookingItemAndDateOnBooking' : function(multilevelname, booking,item,start,end, gs_silent) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
                item : JSON.stringify(item),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'changeBookingItemAndDateOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeBookingItemOnBooking' : function(multilevelname, booking,item, gs_silent) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
                item : JSON.stringify(item),
            },
            method: 'changeBookingItemOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeBookingItemType' : function(multilevelname, itemId,newTypeId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
                newTypeId : JSON.stringify(newTypeId),
            },
            method: 'changeBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeDatesOnBooking' : function(multilevelname, bookingId,start,end, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'changeDatesOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeTypeOnBooking' : function(multilevelname, bookingId,itemTypeId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                itemTypeId : JSON.stringify(itemTypeId),
            },
            method: 'changeTypeOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkConsistency' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkConsistency',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'createABookingItemType' : function(multilevelname, name, gs_silent) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'createABookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteBooking' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteBookingItem' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteBookingItemType' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteOpeningHours' : function(multilevelname, repeaterId, gs_silent) {
        var data = {
            args : {
                repeaterId : JSON.stringify(repeaterId),
            },
            method: 'deleteOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllAvailbleItems' : function(multilevelname, start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllAvailbleItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllAvailbleItemsWithBookingConsidered' : function(multilevelname, start,end,bookingid, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                bookingid : JSON.stringify(bookingid),
            },
            method: 'getAllAvailbleItemsWithBookingConsidered',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllBookings' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllBookingsByBookingItem' : function(multilevelname, bookingItemId, gs_silent) {
        var data = {
            args : {
                bookingItemId : JSON.stringify(bookingItemId),
            },
            method: 'getAllBookingsByBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailbleItems' : function(multilevelname, typeId,start,end, gs_silent) {
        var data = {
            args : {
                typeId : JSON.stringify(typeId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAvailbleItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailbleItemsWithBookingConsidered' : function(multilevelname, typeId,start,end,bookingId, gs_silent) {
        var data = {
            args : {
                typeId : JSON.stringify(typeId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getAvailbleItemsWithBookingConsidered',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBooking' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingItem' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingItemType' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingItemTypes' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getBookingItemTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingItems' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getBookingItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getConfig' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDefaultRegistrationRules' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getDefaultRegistrationRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNumberOfAvailable' : function(multilevelname, itemType,start,end, gs_silent) {
        var data = {
            args : {
                itemType : JSON.stringify(itemType),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getNumberOfAvailable',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOpeningHours' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'getOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTimelines' : function(multilevelname, id,startDate,endDate, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                startDate : JSON.stringify(startDate),
                endDate : JSON.stringify(endDate),
            },
            method: 'getTimelines',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveBookingItem' : function(multilevelname, item, gs_silent) {
        var data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'saveBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveDefaultRegistrationRules' : function(multilevelname, rules, gs_silent) {
        var data = {
            args : {
                rules : JSON.stringify(rules),
            },
            method: 'saveDefaultRegistrationRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveOpeningHours' : function(multilevelname, time,itemId, gs_silent) {
        var data = {
            args : {
                time : JSON.stringify(time),
                itemId : JSON.stringify(itemId),
            },
            method: 'saveOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'setConfirmationRequired' : function(multilevelname, confirmationRequired, gs_silent) {
        var data = {
            args : {
                confirmationRequired : JSON.stringify(confirmationRequired),
            },
            method: 'setConfirmationRequired',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateBookingItemType' : function(multilevelname, type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'updateBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.BrainTreeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BrainTreeManager.prototype = {
    'getClientToken' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getClientToken',
            interfaceName: 'core.braintree.IBrainTreeManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'pay' : function(paymentMethodNonce,orderId, gs_silent) {
        var data = {
            args : {
                paymentMethodNonce : JSON.stringify(paymentMethodNonce),
                orderId : JSON.stringify(orderId),
            },
            method: 'pay',
            interfaceName: 'core.braintree.IBrainTreeManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.C3Manager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.C3Manager.prototype = {
    'addForskningsUserPeriode' : function(periode, gs_silent) {
        var data = {
            args : {
                periode : JSON.stringify(periode),
            },
            method: 'addForskningsUserPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addHour' : function(hour, gs_silent) {
        var data = {
            args : {
                hour : JSON.stringify(hour),
            },
            method: 'addHour',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addTimeRate' : function(name,rate, gs_silent) {
        var data = {
            args : {
                name : JSON.stringify(name),
                rate : JSON.stringify(rate),
            },
            method: 'addTimeRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserProjectPeriode' : function(projectPeriode, gs_silent) {
        var data = {
            args : {
                projectPeriode : JSON.stringify(projectPeriode),
            },
            method: 'addUserProjectPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'allowedFixedHourCosts' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'allowedFixedHourCosts',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'allowedNfrHour' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'allowedNfrHour',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'allowedNfrHourCurrentUser' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'allowedNfrHourCurrentUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'allowedNfrOtherCost' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'allowedNfrOtherCost',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'allowedNfrOtherCostCurrentUser' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'allowedNfrOtherCostCurrentUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'assignProjectToCompany' : function(companyId,projectId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                projectId : JSON.stringify(projectId),
            },
            method: 'assignProjectToCompany',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'calculateSum' : function(periodeId, gs_silent) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'calculateSum',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'canAdd' : function(hour, gs_silent) {
        var data = {
            args : {
                hour : JSON.stringify(hour),
            },
            method: 'canAdd',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteForskningsUserPeriode' : function(periodeId, gs_silent) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'deleteForskningsUserPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteProject' : function(projectId, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
            },
            method: 'deleteProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteProjectCost' : function(projectCostId, gs_silent) {
        var data = {
            args : {
                projectCostId : JSON.stringify(projectCostId),
            },
            method: 'deleteProjectCost',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteTimeRate' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteTimeRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteWorkPackage' : function(workPackageId, gs_silent) {
        var data = {
            args : {
                workPackageId : JSON.stringify(workPackageId),
            },
            method: 'deleteWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAcceListForUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getAcceListForUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAccessList' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAccessList',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAccessListByProjectId' : function(projectId, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
            },
            method: 'getAccessListByProjectId',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getActivePeriode' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getActivePeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllProjectsConnectedToCompany' : function(compnayId, gs_silent) {
        var data = {
            args : {
                compnayId : JSON.stringify(compnayId),
            },
            method: 'getAllProjectsConnectedToCompany',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllProjectsConnectedToWorkPackage' : function(wpId, gs_silent) {
        var data = {
            args : {
                wpId : JSON.stringify(wpId),
            },
            method: 'getAllProjectsConnectedToWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBase64ESAExcelReport' : function(start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getBase64ESAExcelReport',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBase64SFIExcelReport' : function(companyId,start,end, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getBase64SFIExcelReport',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBase64SFIExcelReportTotal' : function(companyId,start,end, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getBase64SFIExcelReportTotal',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrentForskningsPeriode' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCurrentForskningsPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getForskningsPeriodesForUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getForskningsPeriodesForUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getGroupInformation' : function(groupId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getGroupInformation',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getHourById' : function(hourId, gs_silent) {
        var data = {
            args : {
                hourId : JSON.stringify(hourId),
            },
            method: 'getHourById',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOtherCost' : function(otherCostId, gs_silent) {
        var data = {
            args : {
                otherCostId : JSON.stringify(otherCostId),
            },
            method: 'getOtherCost',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPeriodes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPeriodes',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPeriodesForProject' : function(projectId, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
            },
            method: 'getPeriodesForProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProject' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProjectCostsForAllUsersInCompany' : function(projectId,from,to, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getProjectCostsForAllUsersInCompany',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProjectCostsForCurrentUser' : function(projectId,from,to, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getProjectCostsForCurrentUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProjects' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getProjects',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getReportForUserProject' : function(userId,projectId,start,end,forWorkPackageId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                projectId : JSON.stringify(projectId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                forWorkPackageId : JSON.stringify(forWorkPackageId),
            },
            method: 'getReportForUserProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoundSum' : function(year, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getRoundSum',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTimeRate' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getTimeRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTimeRates' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getTimeRates',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserProjectPeriodeById' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getUserProjectPeriodeById',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getWorkPackage' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getWorkPackages' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getWorkPackages',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeCompanyAccess' : function(projectId,companyId, gs_silent) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                companyId : JSON.stringify(companyId),
            },
            method: 'removeCompanyAccess',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeContract' : function(companyId,projectId,workPackageId,contractId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                projectId : JSON.stringify(projectId),
                workPackageId : JSON.stringify(workPackageId),
                contractId : JSON.stringify(contractId),
            },
            method: 'removeContract',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveGroupInfo' : function(groupId,type,value, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                type : JSON.stringify(type),
                value : JSON.stringify(value),
            },
            method: 'saveGroupInfo',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveOtherCosts' : function(otherCost, gs_silent) {
        var data = {
            args : {
                otherCost : JSON.stringify(otherCost),
            },
            method: 'saveOtherCosts',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'savePeriode' : function(periode, gs_silent) {
        var data = {
            args : {
                periode : JSON.stringify(periode),
            },
            method: 'savePeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveProject' : function(project, gs_silent) {
        var data = {
            args : {
                project : JSON.stringify(project),
            },
            method: 'saveProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveRate' : function(rate, gs_silent) {
        var data = {
            args : {
                rate : JSON.stringify(rate),
            },
            method: 'saveRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveWorkPackage' : function(workPackage, gs_silent) {
        var data = {
            args : {
                workPackage : JSON.stringify(workPackage),
            },
            method: 'saveWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'search' : function(searchText, gs_silent) {
        var data = {
            args : {
                searchText : JSON.stringify(searchText),
            },
            method: 'search',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setActivePeriode' : function(periodeId, gs_silent) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'setActivePeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setC3RoundSum' : function(year,sum, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                sum : JSON.stringify(sum),
            },
            method: 'setC3RoundSum',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setNfrAccess' : function(access, gs_silent) {
        var data = {
            args : {
                access : JSON.stringify(access),
            },
            method: 'setNfrAccess',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setProjectAccess' : function(companyId,projectId,workPackageId,value, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                projectId : JSON.stringify(projectId),
                workPackageId : JSON.stringify(workPackageId),
                value : JSON.stringify(value),
            },
            method: 'setProjectAccess',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setProjectCust' : function(companyId,projectId,workPackageId,start,end,price,contractId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                projectId : JSON.stringify(projectId),
                workPackageId : JSON.stringify(workPackageId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                price : JSON.stringify(price),
                contractId : JSON.stringify(contractId),
            },
            method: 'setProjectCust',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setRateToUser' : function(userId,rateId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                rateId : JSON.stringify(rateId),
            },
            method: 'setRateToUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.CalendarManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CalendarManager.prototype = {
    'addEvent' : function(event, gs_silent) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'addEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserSilentlyToEvent' : function(eventId,userId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'addUserSilentlyToEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserToEvent' : function(userId,eventId,password,username,source, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
                password : JSON.stringify(password),
                username : JSON.stringify(username),
                source : JSON.stringify(source),
            },
            method: 'addUserToEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserToPageEvent' : function(userId,bookingAppId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                bookingAppId : JSON.stringify(bookingAppId),
            },
            method: 'addUserToPageEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'applyFilter' : function(filters, gs_silent) {
        var data = {
            args : {
                filters : JSON.stringify(filters),
            },
            method: 'applyFilter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'confirmEntry' : function(entryId, gs_silent) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'confirmEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createEntry' : function(year,month,day, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                day : JSON.stringify(day),
            },
            method: 'createEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteEntry' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteLocation' : function(locationId, gs_silent) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'deleteLocation',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getActiveFilters' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getActiveFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllEventsConnectedToPage' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getAllEventsConnectedToPage',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllLocations' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllLocations',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntries' : function(year,month,day,filters, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                day : JSON.stringify(day),
                filters : JSON.stringify(filters),
            },
            method: 'getEntries',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntriesByUserId' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getEntriesByUserId',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntry' : function(entryId, gs_silent) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'getEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEvent' : function(eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventPartitipatedData' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEventPartitipatedData',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEvents' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getEvents',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsGroupedByPageId' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getEventsGroupedByPageId',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFilters' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getHistory' : function(eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getHistory',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMonth' : function(year,month,includeExtraEvents, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                includeExtraEvents : JSON.stringify(includeExtraEvents),
            },
            method: 'getMonth',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMonths' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMonths',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMonthsAfter' : function(year,month, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getMonthsAfter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSignature' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSignature',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'placeOrder' : function(order, gs_silent) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'placeOrder',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeUserFromEvent' : function(userId,eventId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'removeUserFromEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveEntry' : function(entry, gs_silent) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveLocation' : function(location, gs_silent) {
        var data = {
            args : {
                location : JSON.stringify(location),
            },
            method: 'saveLocation',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendReminderToUser' : function(byEmail,bySMS,users,text,subject,eventId,attachment,sendReminderToUser, gs_silent) {
        var data = {
            args : {
                byEmail : JSON.stringify(byEmail),
                bySMS : JSON.stringify(bySMS),
                users : JSON.stringify(users),
                text : JSON.stringify(text),
                subject : JSON.stringify(subject),
                eventId : JSON.stringify(eventId),
                attachment : JSON.stringify(attachment),
                sendReminderToUser : JSON.stringify(sendReminderToUser),
            },
            method: 'sendReminderToUser',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setEventPartitipatedData' : function(eventData, gs_silent) {
        var data = {
            args : {
                eventData : JSON.stringify(eventData),
            },
            method: 'setEventPartitipatedData',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setSignature' : function(userid,signature, gs_silent) {
        var data = {
            args : {
                userid : JSON.stringify(userid),
                signature : JSON.stringify(signature),
            },
            method: 'setSignature',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'transferFromWaitingList' : function(entryId,userId, gs_silent) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                userId : JSON.stringify(userId),
            },
            method: 'transferFromWaitingList',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'transferUser' : function(fromEventId,toEventId,userId, gs_silent) {
        var data = {
            args : {
                fromEventId : JSON.stringify(fromEventId),
                toEventId : JSON.stringify(toEventId),
                userId : JSON.stringify(userId),
            },
            method: 'transferUser',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.CartManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CartManager.prototype = {
    'addCoupon' : function(coupon, gs_silent) {
        var data = {
            args : {
                coupon : JSON.stringify(coupon),
            },
            method: 'addCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addMetaDataToProduct' : function(cartItemId,metaData, gs_silent) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                metaData : JSON.stringify(metaData),
            },
            method: 'addMetaDataToProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addProduct' : function(productId,count,variations, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
                variations : JSON.stringify(variations),
            },
            method: 'addProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addProductItem' : function(productId,count, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'addProductItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addProductWithSource' : function(productId,count,source, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
                source : JSON.stringify(source),
            },
            method: 'addProductWithSource',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'applyCouponToCurrentCart' : function(code, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'applyCouponToCurrentCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'calculateTotalCost' : function(cart, gs_silent) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'calculateTotalCount' : function(cart, gs_silent) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clear' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'clear',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCart' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCartTotalAmount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCartTotalAmount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCoupon' : function(couponCode, gs_silent) {
        var data = {
            args : {
                couponCode : JSON.stringify(couponCode),
            },
            method: 'getCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCouponById' : function(couponId, gs_silent) {
        var data = {
            args : {
                couponId : JSON.stringify(couponId),
            },
            method: 'getCouponById',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCoupons' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPartnershipCoupons' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPartnershipCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getShippingCost' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getShippingPriceBasis' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getShippingPriceBasis',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTaxes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'hasCoupons' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'hasCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeAllCoupons' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'removeAllCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeCartItem' : function(cartItemId, gs_silent) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
            },
            method: 'removeCartItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeCoupon' : function(code, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'removeCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeProduct' : function(cartItemId, gs_silent) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
            },
            method: 'removeProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setAddress' : function(address, gs_silent) {
        var data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'setAddress',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setReference' : function(reference, gs_silent) {
        var data = {
            args : {
                reference : JSON.stringify(reference),
            },
            method: 'setReference',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setShippingCost' : function(shippingCost, gs_silent) {
        var data = {
            args : {
                shippingCost : JSON.stringify(shippingCost),
            },
            method: 'setShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateCartItem' : function(item, gs_silent) {
        var data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'updateCartItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateProductCount' : function(cartItemId,count, gs_silent) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                count : JSON.stringify(count),
            },
            method: 'updateProductCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.CarTuningManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CarTuningManager.prototype = {
    'getCarTuningData' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCarTuningData',
            interfaceName: 'core.cartuning.ICarTuningManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCarTuningData' : function(carList, gs_silent) {
        var data = {
            args : {
                carList : JSON.stringify(carList),
            },
            method: 'saveCarTuningData',
            interfaceName: 'core.cartuning.ICarTuningManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.CertegoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CertegoManager.prototype = {
    'deleteSystem' : function(systemId, gs_silent) {
        var data = {
            args : {
                systemId : JSON.stringify(systemId),
            },
            method: 'deleteSystem',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrders' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getOrders',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSystems' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getSystems',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSystemsFiltered' : function(filterOptions, gs_silent) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getSystemsFiltered',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSystemsForGroup' : function(group, gs_silent) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'getSystemsForGroup',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveOrder' : function(order, gs_silent) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveSystem' : function(system, gs_silent) {
        var data = {
            args : {
                system : JSON.stringify(system),
            },
            method: 'saveSystem',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'search' : function(searchWord, gs_silent) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'search',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ChatManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ChatManager.prototype = {
    'closeChat' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'closeChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getChatters' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getChatters',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMessages' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMessages',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'pingMobileChat' : function(chatterid, gs_silent) {
        var data = {
            args : {
                chatterid : JSON.stringify(chatterid),
            },
            method: 'pingMobileChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'replyToChat' : function(id,message, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                message : JSON.stringify(message),
            },
            method: 'replyToChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessage' : function(message, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessage',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.DBBackupManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DBBackupManager.prototype = {
    'getChanges' : function(className, gs_silent) {
        var data = {
            args : {
                className : JSON.stringify(className),
            },
            method: 'getChanges',
            interfaceName: 'core.dbbackupmanager.IDBBackupManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getChangesById' : function(className,id, gs_silent) {
        var data = {
            args : {
                className : JSON.stringify(className),
                id : JSON.stringify(id),
            },
            method: 'getChangesById',
            interfaceName: 'core.dbbackupmanager.IDBBackupManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDiff' : function(className,id1,id2, gs_silent) {
        var data = {
            args : {
                className : JSON.stringify(className),
                id1 : JSON.stringify(id1),
                id2 : JSON.stringify(id2),
            },
            method: 'getDiff',
            interfaceName: 'core.dbbackupmanager.IDBBackupManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.DibsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DibsManager.prototype = {
    'checkForOrdersToCapture' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.dibs.IDibsManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.EpayManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.EpayManager.prototype = {
    'checkForOrdersToCapture' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.epay.IEpayManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.EventBookingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.EventBookingManager.prototype = {
    'addExternalCertificate' : function(multilevelname, userId,fileId,eventId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                fileId : JSON.stringify(fileId),
                eventId : JSON.stringify(eventId),
            },
            method: 'addExternalCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addLocationFilter' : function(multilevelname, locationId, gs_silent) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'addLocationFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addTypeFilter' : function(multilevelname, bookingItemTypeId, gs_silent) {
        var data = {
            args : {
                bookingItemTypeId : JSON.stringify(bookingItemTypeId),
            },
            method: 'addTypeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserComment' : function(multilevelname, userId,eventId,comment, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
                comment : JSON.stringify(comment),
            },
            method: 'addUserComment',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserToEvent' : function(multilevelname, eventId,userId,silent,source, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                silent : JSON.stringify(silent),
                source : JSON.stringify(source),
            },
            method: 'addUserToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'bookCurrentUserToEvent' : function(multilevelname, eventId,source, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                source : JSON.stringify(source),
            },
            method: 'bookCurrentUserToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'canDownloadCertificate' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'canDownloadCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'cancelEvent' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'cancelEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkToSendReminders' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkToSendReminders',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearEventBookingManagerForAllData' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'clearEventBookingManagerForAllData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearFilters' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'clearFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearLocationFilters' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'clearLocationFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createEvent' : function(multilevelname, event, gs_silent) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'createEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteCertificate' : function(multilevelname, certificateId, gs_silent) {
        var data = {
            args : {
                certificateId : JSON.stringify(certificateId),
            },
            method: 'deleteCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteEvent' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteExternalCertificates' : function(multilevelname, userId,fileId,eventId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                fileId : JSON.stringify(fileId),
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteExternalCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteLocation' : function(multilevelname, locationId, gs_silent) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'deleteLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteReminderTemplate' : function(multilevelname, templateId, gs_silent) {
        var data = {
            args : {
                templateId : JSON.stringify(templateId),
            },
            method: 'deleteReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteSubLocation' : function(multilevelname, subLocationId, gs_silent) {
        var data = {
            args : {
                subLocationId : JSON.stringify(subLocationId),
            },
            method: 'deleteSubLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteUserComment' : function(multilevelname, userId,eventId,commentId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
                commentId : JSON.stringify(commentId),
            },
            method: 'deleteUserComment',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getActiveLocations' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getActiveLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllEvents' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllLocations' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingItemTypeByPageId' : function(multilevelname, pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getBookingItemTypeByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingItemTypes' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getBookingItemTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingTypeMetaData' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingTypeMetaData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingsByPageId' : function(multilevelname, pageId,showOnlyNew, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                showOnlyNew : JSON.stringify(showOnlyNew),
            },
            method: 'getBookingsByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCertificate' : function(multilevelname, certificateId, gs_silent) {
        var data = {
            args : {
                certificateId : JSON.stringify(certificateId),
            },
            method: 'getCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCertificateForEvent' : function(multilevelname, eventId,userId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getCertificateForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCertificates' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEvent' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventByPageId' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventLog' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventLog',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEvents' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsByLocation' : function(multilevelname, locationId, gs_silent) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'getEventsByLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsByType' : function(multilevelname, eventTypeId, gs_silent) {
        var data = {
            args : {
                eventTypeId : JSON.stringify(eventTypeId),
            },
            method: 'getEventsByType',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsForDay' : function(multilevelname, year,month,day, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                day : JSON.stringify(day),
            },
            method: 'getEventsForDay',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsForUser' : function(multilevelname, userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getEventsForUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsWhereEndDateBetween' : function(multilevelname, from,to, gs_silent) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getEventsWhereEndDateBetween',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getExternalCertificates' : function(multilevelname, userId,eventId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'getExternalCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFilteredLocations' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getFilteredLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFromDateTimeFilter' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getFromDateTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLocation' : function(multilevelname, locationId, gs_silent) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'getLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMyEvents' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getMyEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPriceForEventType' : function(multilevelname, bookingItemTypeId, gs_silent) {
        var data = {
            args : {
                bookingItemTypeId : JSON.stringify(bookingItemTypeId),
            },
            method: 'getPriceForEventType',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPriceForEventTypeAndUserId' : function(multilevelname, eventId,userId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getPriceForEventTypeAndUserId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getReminder' : function(multilevelname, reminderId, gs_silent) {
        var data = {
            args : {
                reminderId : JSON.stringify(reminderId),
            },
            method: 'getReminder',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getReminderTemplate' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getReminderTemplates' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getReminderTemplates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getReminders' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getReminders',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSource' : function(multilevelname, eventId,userId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getSource',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStatistic' : function(multilevelname, startDate,stopDate,groupIds,eventTypeIds, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                groupIds : JSON.stringify(groupIds),
                eventTypeIds : JSON.stringify(eventTypeIds),
            },
            method: 'getStatistic',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStatisticGroupedByLocations' : function(multilevelname, startDate,stopDate,groupIds,eventTypeIds, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                groupIds : JSON.stringify(groupIds),
                eventTypeIds : JSON.stringify(eventTypeIds),
            },
            method: 'getStatisticGroupedByLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getToDateTimeFilter' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getToDateTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUsersForEvent' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getUsersForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUsersForEventWaitinglist' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getUsersForEventWaitinglist',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isUserSignedUpForEvent' : function(multilevelname, eventId,userId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'isUserSignedUpForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAsReady' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'markAsReady',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markQuestBackSent' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'markQuestBackSent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'moveUserToEvent' : function(multilevelname, userId,fromEventId,toEventId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                fromEventId : JSON.stringify(fromEventId),
                toEventId : JSON.stringify(toEventId),
            },
            method: 'moveUserToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeUserFromEvent' : function(multilevelname, eventId,userId,silent, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                silent : JSON.stringify(silent),
            },
            method: 'removeUserFromEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveBookingTypeMetaData' : function(multilevelname, bookingItemTypeMetadata, gs_silent) {
        var data = {
            args : {
                bookingItemTypeMetadata : JSON.stringify(bookingItemTypeMetadata),
            },
            method: 'saveBookingTypeMetaData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCertificate' : function(multilevelname, certificate, gs_silent) {
        var data = {
            args : {
                certificate : JSON.stringify(certificate),
            },
            method: 'saveCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveEvent' : function(multilevelname, event, gs_silent) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'saveEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveLocation' : function(multilevelname, location, gs_silent) {
        var data = {
            args : {
                location : JSON.stringify(location),
            },
            method: 'saveLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveReminderTemplate' : function(multilevelname, template, gs_silent) {
        var data = {
            args : {
                template : JSON.stringify(template),
            },
            method: 'saveReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendDiplomas' : function(multilevelname, reminder,userid,base64, gs_silent) {
        var data = {
            args : {
                reminder : JSON.stringify(reminder),
                userid : JSON.stringify(userid),
                base64 : JSON.stringify(base64),
            },
            method: 'sendDiplomas',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendReminder' : function(multilevelname, reminder, gs_silent) {
        var data = {
            args : {
                reminder : JSON.stringify(reminder),
            },
            method: 'sendReminder',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setParticipationStatus' : function(multilevelname, eventId,userId,status, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                status : JSON.stringify(status),
            },
            method: 'setParticipationStatus',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setTimeFilter' : function(multilevelname, from,to, gs_silent) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'setTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'startScheduler' : function(multilevelname, scheduler, gs_silent) {
        var data = {
            args : {
                scheduler : JSON.stringify(scheduler),
            },
            method: 'startScheduler',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleHide' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'toggleHide',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleLocked' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'toggleLocked',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'transferUserFromWaitingToEvent' : function(multilevelname, userId,eventId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'transferUserFromWaitingToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'unCancelEvent' : function(multilevelname, eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'unCancelEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.FileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FileManager.prototype = {
    'addFileEntry' : function(listId,entry, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
            },
            method: 'addFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteFileEntry' : function(fileId, gs_silent) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'deleteFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFile' : function(fileId, gs_silent) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'getFile',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFiles' : function(listId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getFiles',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'renameFileEntry' : function(fileId,newName, gs_silent) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
                newName : JSON.stringify(newName),
            },
            method: 'renameFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.FtpManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FtpManager.prototype = {
}
GetShopApiWebSocket.GalleryManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GalleryManager.prototype = {
    'addImageToGallery' : function(galleryId,imageId,description,title, gs_silent) {
        var data = {
            args : {
                galleryId : JSON.stringify(galleryId),
                imageId : JSON.stringify(imageId),
                description : JSON.stringify(description),
                title : JSON.stringify(title),
            },
            method: 'addImageToGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createImageGallery' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'createImageGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteImage' : function(entryId, gs_silent) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'deleteImage',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllImages' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getAllImages',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntry' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveEntry' : function(entry, gs_silent) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.GetShop = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShop.prototype = {
    'addUserToPartner' : function(userId,partner,password, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                partner : JSON.stringify(partner),
                password : JSON.stringify(password),
            },
            method: 'addUserToPartner',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'createWebPage' : function(webpageData, gs_silent) {
        var data = {
            args : {
                webpageData : JSON.stringify(webpageData),
            },
            method: 'createWebPage',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'findAddressForApplication' : function(uuid, gs_silent) {
        var data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForApplication',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'findAddressForUUID' : function(uuid, gs_silent) {
        var data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForUUID',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBase64EncodedPDFWebPage' : function(urlToPage, gs_silent) {
        var data = {
            args : {
                urlToPage : JSON.stringify(urlToPage),
            },
            method: 'getBase64EncodedPDFWebPage',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPartnerData' : function(partnerId,password, gs_silent) {
        var data = {
            args : {
                partnerId : JSON.stringify(partnerId),
                password : JSON.stringify(password),
            },
            method: 'getPartnerData',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStores' : function(code, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'getStores',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveSmsCallback' : function(smsResponses, gs_silent) {
        var data = {
            args : {
                smsResponses : JSON.stringify(smsResponses),
            },
            method: 'saveSmsCallback',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'setApplicationList' : function(ids,partnerId,password, gs_silent) {
        var data = {
            args : {
                ids : JSON.stringify(ids),
                partnerId : JSON.stringify(partnerId),
                password : JSON.stringify(password),
            },
            method: 'setApplicationList',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

    'startStoreFromStore' : function(startData, gs_silent) {
        var data = {
            args : {
                startData : JSON.stringify(startData),
            },
            method: 'startStoreFromStore',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.GetShopLockManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopLockManager.prototype = {
    'accessEvent' : function(multilevelname, id,code,domain, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                code : JSON.stringify(code),
                domain : JSON.stringify(domain),
            },
            method: 'accessEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkIfAllIsOk' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkIfAllIsOk',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteAllDevices' : function(multilevelname, password,source, gs_silent) {
        var data = {
            args : {
                password : JSON.stringify(password),
                source : JSON.stringify(source),
            },
            method: 'deleteAllDevices',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteLock' : function(multilevelname, code,lockId, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
                lockId : JSON.stringify(lockId),
            },
            method: 'deleteLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllLocks' : function(multilevelname, serverSource, gs_silent) {
        var data = {
            args : {
                serverSource : JSON.stringify(serverSource),
            },
            method: 'getAllLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCodeForLock' : function(multilevelname, lockId, gs_silent) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'getCodeForLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCodesInUse' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getCodesInUse',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMasterCodes' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getMasterCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUpdatesOnLock' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getUpdatesOnLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'openLock' : function(multilevelname, lockId, gs_silent) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'openLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'pingLock' : function(multilevelname, lockId, gs_silent) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'pingLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'pushCode' : function(multilevelname, id,door,code,start,end, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                door : JSON.stringify(door),
                code : JSON.stringify(code),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'pushCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'refreshAllLocks' : function(multilevelname, source, gs_silent) {
        var data = {
            args : {
                source : JSON.stringify(source),
            },
            method: 'refreshAllLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'refreshLock' : function(multilevelname, lockId, gs_silent) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'refreshLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeAllUnusedLocks' : function(multilevelname, source, gs_silent) {
        var data = {
            args : {
                source : JSON.stringify(source),
            },
            method: 'removeAllUnusedLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeCodeOnLock' : function(multilevelname, lockId,room, gs_silent) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
                room : JSON.stringify(room),
            },
            method: 'removeCodeOnLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveMastercodes' : function(multilevelname, codes, gs_silent) {
        var data = {
            args : {
                codes : JSON.stringify(codes),
            },
            method: 'saveMastercodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setMasterCode' : function(multilevelname, slot,code, gs_silent) {
        var data = {
            args : {
                slot : JSON.stringify(slot),
                code : JSON.stringify(code),
            },
            method: 'setMasterCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'stopUpdatesOnLock' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'stopUpdatesOnLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.InformationScreenManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InformationScreenManager.prototype = {
    'addSlider' : function(slider,tvId, gs_silent) {
        var data = {
            args : {
                slider : JSON.stringify(slider),
                tvId : JSON.stringify(tvId),
            },
            method: 'addSlider',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteSlider' : function(sliderId,tvId, gs_silent) {
        var data = {
            args : {
                sliderId : JSON.stringify(sliderId),
                tvId : JSON.stringify(tvId),
            },
            method: 'deleteSlider',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getHolders' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getHolders',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getInformationScreens' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getInformationScreens',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNews' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getNews',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getScreen' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getScreen',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTypes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getTypes',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'registerTv' : function(customerId, gs_silent) {
        var data = {
            args : {
                customerId : JSON.stringify(customerId),
            },
            method: 'registerTv',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveTv' : function(tv, gs_silent) {
        var data = {
            args : {
                tv : JSON.stringify(tv),
            },
            method: 'saveTv',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ListManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ListManager.prototype = {
    'addEntry' : function(listId,entry,parentPageId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
                parentPageId : JSON.stringify(parentPageId),
            },
            method: 'addEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearList' : function(listId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'clearList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'combineList' : function(toListId,newListId, gs_silent) {
        var data = {
            args : {
                toListId : JSON.stringify(toListId),
                newListId : JSON.stringify(newListId),
            },
            method: 'combineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createListId' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'createListId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createMenuList' : function(menuApplicationId, gs_silent) {
        var data = {
            args : {
                menuApplicationId : JSON.stringify(menuApplicationId),
            },
            method: 'createMenuList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteEntry' : function(id,listId, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                listId : JSON.stringify(listId),
            },
            method: 'deleteEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteMenu' : function(appId,listId, gs_silent) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
                listId : JSON.stringify(listId),
            },
            method: 'deleteMenu',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllListsByType' : function(type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getAllListsByType',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCombinedLists' : function(listId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getCombinedLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntryByPageId' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEntryByPageId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getJSTreeNode' : function(nodeId, gs_silent) {
        var data = {
            args : {
                nodeId : JSON.stringify(nodeId),
            },
            method: 'getJSTreeNode',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getJsTree' : function(name, gs_silent) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getJsTree',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getList' : function(listId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getListEntry' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getListEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLists' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMenues' : function(applicationInstanceId, gs_silent) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getMenues',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPageIdByName' : function(name, gs_silent) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'orderEntry' : function(id,after,parentId, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                after : JSON.stringify(after),
                parentId : JSON.stringify(parentId),
            },
            method: 'orderEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveJsTree' : function(name,list, gs_silent) {
        var data = {
            args : {
                name : JSON.stringify(name),
                list : JSON.stringify(list),
            },
            method: 'saveJsTree',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveMenu' : function(appId,listId,entries,name, gs_silent) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
                listId : JSON.stringify(listId),
                entries : JSON.stringify(entries),
                name : JSON.stringify(name),
            },
            method: 'saveMenu',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setEntries' : function(listId,entries, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entries : JSON.stringify(entries),
            },
            method: 'setEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'translateEntries' : function(entryIds, gs_silent) {
        var data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'unCombineList' : function(fromListId,toRemoveId, gs_silent) {
        var data = {
            args : {
                fromListId : JSON.stringify(fromListId),
                toRemoveId : JSON.stringify(toRemoveId),
            },
            method: 'unCombineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateEntry' : function(entry, gs_silent) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'updateEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.MecaManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MecaManager.prototype = {
    'answerControlRequest' : function(carId,answer, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                answer : JSON.stringify(answer),
            },
            method: 'answerControlRequest',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'answerServiceRequest' : function(carId,answer, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                answer : JSON.stringify(answer),
            },
            method: 'answerServiceRequest',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'callMe' : function(cellPhone, gs_silent) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
            },
            method: 'callMe',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createFleet' : function(fleet, gs_silent) {
        var data = {
            args : {
                fleet : JSON.stringify(fleet),
            },
            method: 'createFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteCar' : function(carId, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'deleteCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteFleet' : function(fleetId, gs_silent) {
        var data = {
            args : {
                fleetId : JSON.stringify(fleetId),
            },
            method: 'deleteFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBase64ExcelReport' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getBase64ExcelReport',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCar' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCarByPageId' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getCarByPageId',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCarsByCellphone' : function(cellPhone, gs_silent) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
            },
            method: 'getCarsByCellphone',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCarsForMecaFleet' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getCarsForMecaFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCarsPKKList' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCarsPKKList',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCarsServiceList' : function(needService, gs_silent) {
        var data = {
            args : {
                needService : JSON.stringify(needService),
            },
            method: 'getCarsServiceList',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFleetByCar' : function(car, gs_silent) {
        var data = {
            args : {
                car : JSON.stringify(car),
            },
            method: 'getFleetByCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFleetPageId' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getFleetPageId',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFleets' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getFleets',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markControlAsCompleted' : function(carId, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'markControlAsCompleted',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'noShowPkk' : function(carId, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'noShowPkk',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'noShowService' : function(carId, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'noShowService',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'notifyByPush' : function(phoneNumber,message, gs_silent) {
        var data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                message : JSON.stringify(message),
            },
            method: 'notifyByPush',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'registerDeviceToCar' : function(deviceId,cellPhone, gs_silent) {
        var data = {
            args : {
                deviceId : JSON.stringify(deviceId),
                cellPhone : JSON.stringify(cellPhone),
            },
            method: 'registerDeviceToCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'requestNextControl' : function(carId,date, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'requestNextControl',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'requestNextService' : function(carId,date, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'requestNextService',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'resetServiceInterval' : function(carId,date,kilometers, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
                kilometers : JSON.stringify(kilometers),
            },
            method: 'resetServiceInterval',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'runNotificationCheck' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'runNotificationCheck',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveFleetCar' : function(pageId,car, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                car : JSON.stringify(car),
            },
            method: 'saveFleetCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveMecaFleetSettings' : function(settings, gs_silent) {
        var data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'saveMecaFleetSettings',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendEmail' : function(cellPhone,message, gs_silent) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
                message : JSON.stringify(message),
            },
            method: 'sendEmail',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendInvite' : function(mecaCarId, gs_silent) {
        var data = {
            args : {
                mecaCarId : JSON.stringify(mecaCarId),
            },
            method: 'sendInvite',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendKilometerRequest' : function(carId, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'sendKilometerRequest',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendKilometers' : function(cellPhone,kilometers, gs_silent) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
                kilometers : JSON.stringify(kilometers),
            },
            method: 'sendKilometers',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendNotificationToStoreOwner' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'sendNotificationToStoreOwner',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'suggestDate' : function(carId,date, gs_silent) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'suggestDate',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.MessageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MessageManager.prototype = {
    'collectEmail' : function(email, gs_silent) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'collectEmail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCollectedEmails' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCollectedEmails',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMailMessage' : function(mailMessageId, gs_silent) {
        var data = {
            args : {
                mailMessageId : JSON.stringify(mailMessageId),
            },
            method: 'getMailMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMailSent' : function(from,to,toEmailAddress, gs_silent) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
                toEmailAddress : JSON.stringify(toEmailAddress),
            },
            method: 'getMailSent',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSmsCount' : function(year,month, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getSmsCount',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSmsMessage' : function(smsMessageId, gs_silent) {
        var data = {
            args : {
                smsMessageId : JSON.stringify(smsMessageId),
            },
            method: 'getSmsMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMail' : function(to,toName,subject,content,from,fromName, gs_silent) {
        var data = {
            args : {
                to : JSON.stringify(to),
                toName : JSON.stringify(toName),
                subject : JSON.stringify(subject),
                content : JSON.stringify(content),
                from : JSON.stringify(from),
                fromName : JSON.stringify(fromName),
            },
            method: 'sendMail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMailWithAttachments' : function(to,toName,subject,content,from,fromName,attachments, gs_silent) {
        var data = {
            args : {
                to : JSON.stringify(to),
                toName : JSON.stringify(toName),
                subject : JSON.stringify(subject),
                content : JSON.stringify(content),
                from : JSON.stringify(from),
                fromName : JSON.stringify(fromName),
                attachments : JSON.stringify(attachments),
            },
            method: 'sendMailWithAttachments',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessageToStoreOwner' : function(message,subject, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
                subject : JSON.stringify(subject),
            },
            method: 'sendMessageToStoreOwner',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.NewsLetterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsLetterManager.prototype = {
    'sendNewsLetter' : function(group, gs_silent) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'sendNewsLetter',
            interfaceName: 'core.messagemanager.INewsLetterManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendNewsLetterPreview' : function(group, gs_silent) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'sendNewsLetterPreview',
            interfaceName: 'core.messagemanager.INewsLetterManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.MobileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MobileManager.prototype = {
    'clearBadged' : function(tokenId, gs_silent) {
        var data = {
            args : {
                tokenId : JSON.stringify(tokenId),
            },
            method: 'clearBadged',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'registerToken' : function(token, gs_silent) {
        var data = {
            args : {
                token : JSON.stringify(token),
            },
            method: 'registerToken',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessageToAll' : function(message, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAll',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessageToAllTestUnits' : function(message, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAllTestUnits',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.OrderManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.OrderManager.prototype = {
    'addClosedPeriode' : function(closed, gs_silent) {
        var data = {
            args : {
                closed : JSON.stringify(closed),
            },
            method: 'addClosedPeriode',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addProductToOrder' : function(orderId,productId,count, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'addProductToOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeOrderStatus' : function(id,status, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                status : JSON.stringify(status),
            },
            method: 'changeOrderStatus',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeOrderType' : function(orderId,paymentTypeId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                paymentTypeId : JSON.stringify(paymentTypeId),
            },
            method: 'changeOrderType',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkForOrdersToAutoPay' : function(daysToTryAfterOrderHasStarted, gs_silent) {
        var data = {
            args : {
                daysToTryAfterOrderHasStarted : JSON.stringify(daysToTryAfterOrderHasStarted),
            },
            method: 'checkForOrdersToAutoPay',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkForOrdersToCapture' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrder' : function(address, gs_silent) {
        var data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'createOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrderByCustomerReference' : function(referenceKey, gs_silent) {
        var data = {
            args : {
                referenceKey : JSON.stringify(referenceKey),
            },
            method: 'createOrderByCustomerReference',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrderForUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'createOrderForUser',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createRegisterCardOrder' : function(paymentType, gs_silent) {
        var data = {
            args : {
                paymentType : JSON.stringify(paymentType),
            },
            method: 'createRegisterCardOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'creditOrder' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'creditOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteAllVirtualOrders' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'deleteAllVirtualOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'forceDeleteOrder' : function(orderId,password, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                password : JSON.stringify(password),
            },
            method: 'forceDeleteOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllOrdersForUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getAllOrdersForUser',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllOrdersOnProduct' : function(productId, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'getAllOrdersOnProduct',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUnpaidInvoices' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllUnpaidInvoices',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getItemDates' : function(start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getItemDates',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMostSoldProducts' : function(numberOfProducts, gs_silent) {
        var data = {
            args : {
                numberOfProducts : JSON.stringify(numberOfProducts),
            },
            method: 'getMostSoldProducts',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMyPrefferedPaymentMethod' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMyPrefferedPaymentMethod',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrder' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrderByReference' : function(referenceId, gs_silent) {
        var data = {
            args : {
                referenceId : JSON.stringify(referenceId),
            },
            method: 'getOrderByReference',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrderByincrementOrderId' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getOrderByincrementOrderId',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrderSecure' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrderSecure',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrders' : function(orderIds,page,pageSize, gs_silent) {
        var data = {
            args : {
                orderIds : JSON.stringify(orderIds),
                page : JSON.stringify(page),
                pageSize : JSON.stringify(pageSize),
            },
            method: 'getOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersFiltered' : function(filterOptions, gs_silent) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getOrdersFiltered',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersFromPeriode' : function(start,end,statistics, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                statistics : JSON.stringify(statistics),
            },
            method: 'getOrdersFromPeriode',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersNotTransferredToAccountingSystem' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getOrdersNotTransferredToAccountingSystem',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersPaid' : function(paymentId,userId,from,to, gs_silent) {
        var data = {
            args : {
                paymentId : JSON.stringify(paymentId),
                userId : JSON.stringify(userId),
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getOrdersPaid',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersToCapture' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPageCount' : function(pageSize,searchWord, gs_silent) {
        var data = {
            args : {
                pageSize : JSON.stringify(pageSize),
                searchWord : JSON.stringify(searchWord),
            },
            method: 'getPageCount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSalesNumber' : function(year, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getSalesNumber',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSalesStatistics' : function(startDate,endDate,type, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                endDate : JSON.stringify(endDate),
                type : JSON.stringify(type),
            },
            method: 'getSalesStatistics',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStorePreferredPayementMethod' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getStorePreferredPayementMethod',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTaxes' : function(order, gs_silent) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTaxes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTotalAmount' : function(order, gs_silent) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTotalAmount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTotalAmountExTaxes' : function(order, gs_silent) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTotalAmountExTaxes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTotalForOrderById' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getTotalForOrderById',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTotalSalesAmount' : function(year,month,week,day,type, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                week : JSON.stringify(week),
                day : JSON.stringify(day),
                type : JSON.stringify(type),
            },
            method: 'getTotalSalesAmount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserPrefferedPaymentMethod' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getUserPrefferedPaymentMethod',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logTransactionEntry' : function(orderId,entry, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                entry : JSON.stringify(entry),
            },
            method: 'logTransactionEntry',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAsInvoicePayment' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'markAsInvoicePayment',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAsPaid' : function(orderId,date, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                date : JSON.stringify(date),
            },
            method: 'markAsPaid',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'payWithCard' : function(orderId,cardId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                cardId : JSON.stringify(cardId),
            },
            method: 'payWithCard',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'printInvoice' : function(orderId,printerId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                printerId : JSON.stringify(printerId),
            },
            method: 'printInvoice',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveOrder' : function(order, gs_silent) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'searchForOrders' : function(searchWord,page,pageSize, gs_silent) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
                page : JSON.stringify(page),
                pageSize : JSON.stringify(pageSize),
            },
            method: 'searchForOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendReciept' : function(orderId,email, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                email : JSON.stringify(email),
            },
            method: 'sendReciept',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setExternalRefOnCartItem' : function(cartItem,externalId, gs_silent) {
        var data = {
            args : {
                cartItem : JSON.stringify(cartItem),
                externalId : JSON.stringify(externalId),
            },
            method: 'setExternalRefOnCartItem',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setOrderStatus' : function(password,orderId,currency,price,status, gs_silent) {
        var data = {
            args : {
                password : JSON.stringify(password),
                orderId : JSON.stringify(orderId),
                currency : JSON.stringify(currency),
                price : JSON.stringify(price),
                status : JSON.stringify(status),
            },
            method: 'setOrderStatus',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateCountForOrderLine' : function(cartItemId,orderId,count, gs_silent) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                orderId : JSON.stringify(orderId),
                count : JSON.stringify(count),
            },
            method: 'updateCountForOrderLine',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updatePriceForOrderLine' : function(cartItemId,orderId,price, gs_silent) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                orderId : JSON.stringify(orderId),
                price : JSON.stringify(price),
            },
            method: 'updatePriceForOrderLine',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PageManager.prototype = {
    'addApplication' : function(applicationId,pageCellId,pageId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                pageCellId : JSON.stringify(pageCellId),
                pageId : JSON.stringify(pageId),
            },
            method: 'addApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addComment' : function(pageComment, gs_silent) {
        var data = {
            args : {
                pageComment : JSON.stringify(pageComment),
            },
            method: 'addComment',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addExistingApplicationToPageArea' : function(pageId,appId,area, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                appId : JSON.stringify(appId),
                area : JSON.stringify(area),
            },
            method: 'addExistingApplicationToPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addLayoutCell' : function(pageId,incell,beforecell,direction,area, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                incell : JSON.stringify(incell),
                beforecell : JSON.stringify(beforecell),
                direction : JSON.stringify(direction),
                area : JSON.stringify(area),
            },
            method: 'addLayoutCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changePageUserLevel' : function(pageId,userLevel, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                userLevel : JSON.stringify(userLevel),
            },
            method: 'changePageUserLevel',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearPage' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'clearPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearPageArea' : function(pageId,pageArea, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'clearPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createHeaderFooter' : function(type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'createHeaderFooter',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createModal' : function(modalName, gs_silent) {
        var data = {
            args : {
                modalName : JSON.stringify(modalName),
            },
            method: 'createModal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createNewRow' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'createNewRow',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createPage' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'createPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteApplication' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteComment' : function(commentId, gs_silent) {
        var data = {
            args : {
                commentId : JSON.stringify(commentId),
            },
            method: 'deleteComment',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deletePage' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deletePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'dropCell' : function(pageId,cellId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'dropCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'flattenMobileLayout' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'flattenMobileLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplications' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplicationsBasedOnApplicationSettingsId' : function(appSettingsId, gs_silent) {
        var data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
            },
            method: 'getApplicationsBasedOnApplicationSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplicationsByPageAreaAndSettingsId' : function(appSettingsId,pageArea, gs_silent) {
        var data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'getApplicationsByPageAreaAndSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplicationsByType' : function(type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getApplicationsByType',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getApplicationsForPage' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getApplicationsForPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCell' : function(pageId,cellId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'getCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getComments' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getComments',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLeftSideBarNames' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLeftSideBarNames',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLooseCell' : function(pageId,cellId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'getLooseCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMobileBody' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getMobileBody',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMobileLink' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMobileLink',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getModalNames' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getModalNames',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPage' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPagesForApplication' : function(appId, gs_silent) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'getPagesForApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSecuredSettings' : function(applicationInstanceId, gs_silent) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getSecuredSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSecuredSettingsInternal' : function(appName, gs_silent) {
        var data = {
            args : {
                appName : JSON.stringify(appName),
            },
            method: 'getSecuredSettingsInternal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'linkPageCell' : function(pageId,cellId,link, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                link : JSON.stringify(link),
            },
            method: 'linkPageCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'moveCell' : function(pageId,cellId,up, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                up : JSON.stringify(up),
            },
            method: 'moveCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'moveCellMobile' : function(pageId,cellId,moveUp, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                moveUp : JSON.stringify(moveUp),
            },
            method: 'moveCellMobile',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeAppFromCell' : function(pageId,cellid, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellid : JSON.stringify(cellid),
            },
            method: 'removeAppFromCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'resetMobileLayout' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'resetMobileLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'restoreLayout' : function(pageId,fromTime, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromTime : JSON.stringify(fromTime),
            },
            method: 'restoreLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveApplicationConfiguration' : function(config, gs_silent) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveApplicationConfiguration',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCell' : function(pageId,cell, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cell : JSON.stringify(cell),
            },
            method: 'saveCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCellPosition' : function(pageId,cellId,data, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                data : JSON.stringify(data),
            },
            method: 'saveCellPosition',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveMobileLink' : function(link, gs_silent) {
        var data = {
            args : {
                link : JSON.stringify(link),
            },
            method: 'saveMobileLink',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'savePage' : function(page, gs_silent) {
        var data = {
            args : {
                page : JSON.stringify(page),
            },
            method: 'savePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'savePageCellGroupAccess' : function(pageId,cellId,groupAccess, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                groupAccess : JSON.stringify(groupAccess),
            },
            method: 'savePageCellGroupAccess',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'savePageCellSettings' : function(pageId,cellId,settings, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                settings : JSON.stringify(settings),
            },
            method: 'savePageCellSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCarouselConfig' : function(pageId,cellId,config, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                config : JSON.stringify(config),
            },
            method: 'setCarouselConfig',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCellMode' : function(pageId,cellId,mode, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                mode : JSON.stringify(mode),
            },
            method: 'setCellMode',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCellName' : function(pageId,cellId,cellName, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                cellName : JSON.stringify(cellName),
            },
            method: 'setCellName',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setPageDescription' : function(pageId,description, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                description : JSON.stringify(description),
            },
            method: 'setPageDescription',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setParentPage' : function(pageId,parentPageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                parentPageId : JSON.stringify(parentPageId),
            },
            method: 'setParentPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setStylesOnCell' : function(pageId,cellId,styles,innerStyles,width, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                styles : JSON.stringify(styles),
                innerStyles : JSON.stringify(innerStyles),
                width : JSON.stringify(width),
            },
            method: 'setStylesOnCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setWidth' : function(pageId,cellId,outerWidth,outerWidthWithMargins, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                outerWidth : JSON.stringify(outerWidth),
                outerWidthWithMargins : JSON.stringify(outerWidthWithMargins),
            },
            method: 'setWidth',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'startLoadPage' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'startLoadPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'swapAppWithCell' : function(pageId,fromCellId,toCellId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromCellId : JSON.stringify(fromCellId),
                toCellId : JSON.stringify(toCellId),
            },
            method: 'swapAppWithCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleHiddenOnMobile' : function(pageId,cellId,hide, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                hide : JSON.stringify(hide),
            },
            method: 'toggleHiddenOnMobile',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleLeftSideBar' : function(pageId,columnName, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                columnName : JSON.stringify(columnName),
            },
            method: 'toggleLeftSideBar',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'togglePinArea' : function(pageId,cellId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'togglePinArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'translatePages' : function(pages, gs_silent) {
        var data = {
            args : {
                pages : JSON.stringify(pages),
            },
            method: 'translatePages',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateCellLayout' : function(layout,pageId,cellId, gs_silent) {
        var data = {
            args : {
                layout : JSON.stringify(layout),
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'updateCellLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.InvoiceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InvoiceManager.prototype = {
    'createInvoice' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'createInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBase64EncodedInvoice' : function(orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getBase64EncodedInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.LasGruppenPDFGenerator = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LasGruppenPDFGenerator.prototype = {
    'generatePdf' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'generatePdf',
            interfaceName: 'core.pdf.ILasGruppenPDFGenerator',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PkkControlManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PkkControlManager.prototype = {
    'getPkkControlData' : function(licensePlate, gs_silent) {
        var data = {
            args : {
                licensePlate : JSON.stringify(licensePlate),
            },
            method: 'getPkkControlData',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPkkControls' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPkkControls',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'registerPkkControl' : function(data, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'registerPkkControl',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removePkkControl' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'removePkkControl',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PmsEventManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsEventManager.prototype = {
    'createEvent' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'createEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteEntry' : function(multilevelname, entryId,day, gs_silent) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                day : JSON.stringify(day),
            },
            method: 'deleteEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntry' : function(multilevelname, entryId,day, gs_silent) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                day : JSON.stringify(day),
            },
            method: 'getEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEntryShort' : function(multilevelname, shortId,day, gs_silent) {
        var data = {
            args : {
                shortId : JSON.stringify(shortId),
                day : JSON.stringify(day),
            },
            method: 'getEntryShort',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventEntries' : function(multilevelname, filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getEventEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventList' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getEventList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveEntry' : function(multilevelname, entry,day, gs_silent) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
                day : JSON.stringify(day),
            },
            method: 'saveEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PmsInvoiceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsInvoiceManager.prototype = {
    'clearOrder' : function(multilevelname, bookingId,orderId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'clearOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrder' : function(multilevelname, bookingId,filter, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'createOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrderOnUnsettledAmount' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'createOrderOnUnsettledAmount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createPeriodeInvoice' : function(multilevelname, start,end,amount,roomId, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                amount : JSON.stringify(amount),
                roomId : JSON.stringify(roomId),
            },
            method: 'createPeriodeInvoice',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createRegisterCardOrder' : function(multilevelname, item, gs_silent) {
        var data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'createRegisterCardOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'creditOrder' : function(multilevelname, bookingId,orderId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'creditOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'generateStatistics' : function(multilevelname, filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'generateStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDiscountsForUser' : function(multilevelname, userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getDiscountsForUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPreferredPaymentMethod' : function(multilevelname, bookingId,filter, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'getPreferredPaymentMethod',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSubscriptionOverview' : function(multilevelname, start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getSubscriptionOverview',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isRoomPaidFor' : function(multilevelname, pmsRoomId, gs_silent) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'isRoomPaidFor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markOrderAsPaid' : function(multilevelname, bookingId,orderId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'markOrderAsPaid',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeOrderLinesOnOrdersForBooking' : function(multilevelname, id,roomIds, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                roomIds : JSON.stringify(roomIds),
            },
            method: 'removeOrderLinesOnOrdersForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveDiscounts' : function(multilevelname, discounts, gs_silent) {
        var data = {
            args : {
                discounts : JSON.stringify(discounts),
            },
            method: 'saveDiscounts',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendRecieptOrInvoice' : function(multilevelname, orderId,email,bookingId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                email : JSON.stringify(email),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'sendRecieptOrInvoice',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'validateAllInvoiceToDates' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'validateAllInvoiceToDates',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PmsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsManager.prototype = {
    'addAddonToCurrentBooking' : function(multilevelname, itemtypeId, gs_silent) {
        var data = {
            args : {
                itemtypeId : JSON.stringify(itemtypeId),
            },
            method: 'addAddonToCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addAddonsToBooking' : function(multilevelname, type,roomId,remove, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
                roomId : JSON.stringify(roomId),
                remove : JSON.stringify(remove),
            },
            method: 'addAddonsToBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addBookingItem' : function(multilevelname, bookingId,item,start,end, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                item : JSON.stringify(item),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'addBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addBookingItemType' : function(multilevelname, bookingId,item,start,end,guestInfoFromRoom, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                item : JSON.stringify(item),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                guestInfoFromRoom : JSON.stringify(guestInfoFromRoom),
            },
            method: 'addBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addCartItemToRoom' : function(multilevelname, item,pmsBookingRoomId,addedBy, gs_silent) {
        var data = {
            args : {
                item : JSON.stringify(item),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
                addedBy : JSON.stringify(addedBy),
            },
            method: 'addCartItemToRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addComment' : function(multilevelname, bookingId,comment, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                comment : JSON.stringify(comment),
            },
            method: 'addComment',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addProductToRoom' : function(multilevelname, productId,pmsRoomId,count, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                pmsRoomId : JSON.stringify(pmsRoomId),
                count : JSON.stringify(count),
            },
            method: 'addProductToRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addRepeatingData' : function(multilevelname, data, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'addRepeatingData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeDates' : function(multilevelname, roomId,bookingId,start,end, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                bookingId : JSON.stringify(bookingId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'changeDates',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeInvoiceDate' : function(multilevelname, roomId,newDate, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                newDate : JSON.stringify(newDate),
            },
            method: 'changeInvoiceDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkDoorStatusControl' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkDoorStatusControl',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkForRoomsToClose' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkForRoomsToClose',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkIfGuestHasArrived' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkIfGuestHasArrived',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'closeItem' : function(multilevelname, id,start,end,source, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                source : JSON.stringify(source),
            },
            method: 'closeItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'completeCareTakerJob' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'completeCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'completeCurrentBooking' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'completeCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'confirmBooking' : function(multilevelname, bookingId,message, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'confirmBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createAllVirtualOrders' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'createAllVirtualOrders',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createChannel' : function(multilevelname, channel, gs_silent) {
        var data = {
            args : {
                channel : JSON.stringify(channel),
            },
            method: 'createChannel',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createNewPricePlan' : function(multilevelname, code, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'createNewPricePlan',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createOrder' : function(multilevelname, bookingId,filter, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'createOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createPrepaymentOrder' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'createPrepaymentOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteAllBookings' : function(multilevelname, code, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'deleteAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteBooking' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteDeliveryLogEntry' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteDeliveryLogEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deletePricePlan' : function(multilevelname, code, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'deletePricePlan',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'doNotification' : function(multilevelname, key,bookingId, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'doNotification',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'endRoom' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'endRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'endRoomWithDate' : function(multilevelname, pmsRoomId,date, gs_silent) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
                date : JSON.stringify(date),
            },
            method: 'endRoomWithDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'failedChargeCard' : function(multilevelname, orderId,bookingId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'failedChargeCard',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'forceMarkRoomAsCleaned' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'forceMarkRoomAsCleaned',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'generateNewCodeForRoom' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'generateNewCodeForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAdditionalInfo' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'getAdditionalInfo',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAdditionalTypeInformation' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAdditionalTypeInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAdditionalTypeInformationById' : function(multilevelname, typeId, gs_silent) {
        var data = {
            args : {
                typeId : JSON.stringify(typeId),
            },
            method: 'getAdditionalTypeInformationById',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAddonsAvailable' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAddonsAvailable',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAddonsForRoom' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'getAddonsForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAddonsWithDiscount' : function(multilevelname, pmsBookingRoomId, gs_silent) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getAddonsWithDiscount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllAdditionalInformationOnRooms' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllAdditionalInformationOnRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllBookings' : function(multilevelname, state, gs_silent) {
        var data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllBookingsForLoggedOnUser' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllBookingsForLoggedOnUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllBookingsUnsecure' : function(multilevelname, state, gs_silent) {
        var data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getAllBookingsUnsecure',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllRoomTypes' : function(multilevelname, start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllRoomTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllRoomsNeedCleaningToday' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllRoomsNeedCleaningToday',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllRoomsThatHasAddonsOfType' : function(multilevelname, type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getAllRoomsThatHasAddonsOfType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAvailabilityForType' : function(multilevelname, bookingItemId,startTime,endTime,intervalInMinutes, gs_silent) {
        var data = {
            args : {
                bookingItemId : JSON.stringify(bookingItemId),
                startTime : JSON.stringify(startTime),
                endTime : JSON.stringify(endTime),
                intervalInMinutes : JSON.stringify(intervalInMinutes),
            },
            method: 'getAvailabilityForType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBooking' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingFromBookingEngineId' : function(multilevelname, bookingEngineId, gs_silent) {
        var data = {
            args : {
                bookingEngineId : JSON.stringify(bookingEngineId),
            },
            method: 'getBookingFromBookingEngineId',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingFromRoom' : function(multilevelname, pmsBookingRoomId, gs_silent) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getBookingFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingFromRoomIgnoreDeleted' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'getBookingFromRoomIgnoreDeleted',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBookingWithOrderId' : function(multilevelname, orderId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getBookingWithOrderId',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCareTakerJob' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCareTakerJobs' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getCareTakerJobs',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getChannelMatrix' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getChannelMatrix',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCleaningStatistics' : function(multilevelname, start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getCleaningStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getConferenceData' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getConfiguration' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getContract' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getContract',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrenctContract' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getCurrenctContract',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrentBooking' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDefaultDateRange' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getDefaultDateRange',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDefaultMessage' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getDefaultMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDeliveryLog' : function(multilevelname, productIds,start,end, gs_silent) {
        var data = {
            args : {
                productIds : JSON.stringify(productIds),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getDeliveryLog',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDeliveryLogByView' : function(multilevelname, viewId,start,end, gs_silent) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getDeliveryLogByView',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFutureConferenceData' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getFutureConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getIntervalAvailability' : function(multilevelname, filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getIntervalAvailability',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getItemsForView' : function(multilevelname, viewId,date, gs_silent) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
                date : JSON.stringify(date),
            },
            method: 'getItemsForView',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLogEntries' : function(multilevelname, filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getLogEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMyRooms' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getMyRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNumberOfAvailable' : function(multilevelname, itemType,start,end, gs_silent) {
        var data = {
            args : {
                itemType : JSON.stringify(itemType),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getNumberOfAvailable',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPrices' : function(multilevelname, start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getPrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPricesByCode' : function(multilevelname, code,start,end, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getPricesByCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoomForItem' : function(multilevelname, itemId,atTime, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
                atTime : JSON.stringify(atTime),
            },
            method: 'getRoomForItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoomsNeedingCheckoutCleaning' : function(multilevelname, day, gs_silent) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingCheckoutCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoomsNeedingIntervalCleaning' : function(multilevelname, day, gs_silent) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingIntervalCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoomsNeedingIntervalCleaningSimple' : function(multilevelname, day, gs_silent) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingIntervalCleaningSimple',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSimpleInventoryList' : function(multilevelname, roomName, gs_silent) {
        var data = {
            args : {
                roomName : JSON.stringify(roomName),
            },
            method: 'getSimpleInventoryList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSimpleRooms' : function(multilevelname, filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getSimpleRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStatistics' : function(multilevelname, filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getpriceCodes' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getpriceCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'hourlyProcessor' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'hourlyProcessor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'initBookingRules' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'initBookingRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isClean' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'isClean',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isUsedToday' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'isUsedToday',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logEntry' : function(multilevelname, logText,bookingId,itemId, gs_silent) {
        var data = {
            args : {
                logText : JSON.stringify(logText),
                bookingId : JSON.stringify(bookingId),
                itemId : JSON.stringify(itemId),
            },
            method: 'logEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logEntryObject' : function(multilevelname, log, gs_silent) {
        var data = {
            args : {
                log : JSON.stringify(log),
            },
            method: 'logEntryObject',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAddonDelivered' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'markAddonDelivered',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markKeyDeliveredForAllEndedRooms' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'markKeyDeliveredForAllEndedRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markRoomAsCleaned' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'markRoomAsCleaned',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'massUpdatePrices' : function(multilevelname, price,bookingId, gs_silent) {
        var data = {
            args : {
                price : JSON.stringify(price),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'massUpdatePrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'mergeBookingsOnOrders' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'mergeBookingsOnOrders',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'processor' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'processor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeAddonFromRoom' : function(multilevelname, id,pmsBookingRooms, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                pmsBookingRooms : JSON.stringify(pmsBookingRooms),
            },
            method: 'removeAddonFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeAddonFromRoomById' : function(multilevelname, addonId,roomId, gs_silent) {
        var data = {
            args : {
                addonId : JSON.stringify(addonId),
                roomId : JSON.stringify(roomId),
            },
            method: 'removeAddonFromRoomById',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeCareTakerJob' : function(multilevelname, jobId, gs_silent) {
        var data = {
            args : {
                jobId : JSON.stringify(jobId),
            },
            method: 'removeCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeChannel' : function(multilevelname, channel, gs_silent) {
        var data = {
            args : {
                channel : JSON.stringify(channel),
            },
            method: 'removeChannel',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeFromBooking' : function(multilevelname, bookingId,roomId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                roomId : JSON.stringify(roomId),
            },
            method: 'removeFromBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeFromCurrentBooking' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'removeFromCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'reportMissingInventory' : function(multilevelname, inventories,itemId,roomId, gs_silent) {
        var data = {
            args : {
                inventories : JSON.stringify(inventories),
                itemId : JSON.stringify(itemId),
                roomId : JSON.stringify(roomId),
            },
            method: 'reportMissingInventory',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'resetPriceForRoom' : function(multilevelname, pmsRoomId, gs_silent) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'resetPriceForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'returnedKey' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'returnedKey',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveAdditionalTypeInformation' : function(multilevelname, info, gs_silent) {
        var data = {
            args : {
                info : JSON.stringify(info),
            },
            method: 'saveAdditionalTypeInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveBooking' : function(multilevelname, booking, gs_silent) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
            },
            method: 'saveBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCareTakerJob' : function(multilevelname, job, gs_silent) {
        var data = {
            args : {
                job : JSON.stringify(job),
            },
            method: 'saveCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveConferenceData' : function(multilevelname, data, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'saveConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveConfiguration' : function(multilevelname, notifications, gs_silent) {
        var data = {
            args : {
                notifications : JSON.stringify(notifications),
            },
            method: 'saveConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendCode' : function(multilevelname, phoneNumber,roomId, gs_silent) {
        var data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                roomId : JSON.stringify(roomId),
            },
            method: 'sendCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendConfirmation' : function(multilevelname, email,bookingId, gs_silent) {
        var data = {
            args : {
                email : JSON.stringify(email),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'sendConfirmation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessage' : function(multilevelname, bookingId,email,title,message, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                email : JSON.stringify(email),
                title : JSON.stringify(title),
                message : JSON.stringify(message),
            },
            method: 'sendMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessageToAllTodaysGuests' : function(multilevelname, message, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAllTodaysGuests',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMissingPayment' : function(multilevelname, orderId,bookingId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'sendMissingPayment',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendPaymentLink' : function(multilevelname, orderId,bookingId,email,prefix,phone, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                bookingId : JSON.stringify(bookingId),
                email : JSON.stringify(email),
                prefix : JSON.stringify(prefix),
                phone : JSON.stringify(phone),
            },
            method: 'sendPaymentLink',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendStatistics' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'sendStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setBooking' : function(multilevelname, addons, gs_silent) {
        var data = {
            args : {
                addons : JSON.stringify(addons),
            },
            method: 'setBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setBookingItem' : function(multilevelname, roomId,bookingId,itemId,split, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                bookingId : JSON.stringify(bookingId),
                itemId : JSON.stringify(itemId),
                split : JSON.stringify(split),
            },
            method: 'setBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setBookingItemAndDate' : function(multilevelname, roomId,itemId,split,start,end, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                itemId : JSON.stringify(itemId),
                split : JSON.stringify(split),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'setBookingItemAndDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setGuestOnRoom' : function(multilevelname, guests,bookingId,roomId, gs_silent) {
        var data = {
            args : {
                guests : JSON.stringify(guests),
                bookingId : JSON.stringify(bookingId),
                roomId : JSON.stringify(roomId),
            },
            method: 'setGuestOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setNewCleaningIntervalOnRoom' : function(multilevelname, roomId,interval, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                interval : JSON.stringify(interval),
            },
            method: 'setNewCleaningIntervalOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setNewRoomType' : function(multilevelname, roomId,bookingId,newType, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                bookingId : JSON.stringify(bookingId),
                newType : JSON.stringify(newType),
            },
            method: 'setNewRoomType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setPrices' : function(multilevelname, code,prices, gs_silent) {
        var data = {
            args : {
                code : JSON.stringify(code),
                prices : JSON.stringify(prices),
            },
            method: 'setPrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'splitBooking' : function(multilevelname, roomIds, gs_silent) {
        var data = {
            args : {
                roomIds : JSON.stringify(roomIds),
            },
            method: 'splitBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'startBooking' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'startBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleAddon' : function(multilevelname, itemId, gs_silent) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'toggleAddon',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'tryAddToEngine' : function(multilevelname, pmsBookingRoomId, gs_silent) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'tryAddToEngine',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'unConfirmBooking' : function(multilevelname, bookingId,message, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'unConfirmBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'undeleteBooking' : function(multilevelname, bookingId, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'undeleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateAdditionalInformationOnRooms' : function(multilevelname, info, gs_silent) {
        var data = {
            args : {
                info : JSON.stringify(info),
            },
            method: 'updateAdditionalInformationOnRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateAddons' : function(multilevelname, items,bookingId, gs_silent) {
        var data = {
            args : {
                items : JSON.stringify(items),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'updateAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateAddonsBasedOnGuestCount' : function(multilevelname, pmsRoomId, gs_silent) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'updateAddonsBasedOnGuestCount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateAddonsCountToBooking' : function(multilevelname, type,roomId,count, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
                roomId : JSON.stringify(roomId),
                count : JSON.stringify(count),
            },
            method: 'updateAddonsCountToBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateRepeatingDataForBooking' : function(multilevelname, data,bookingId, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'updateRepeatingDataForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateRoomByUser' : function(multilevelname, bookingId,room, gs_silent) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                room : JSON.stringify(room),
            },
            method: 'updateRoomByUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PmsManagerProcessor = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsManagerProcessor.prototype = {
    'doProcessing' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'doProcessing',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManagerProcessor',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PrintManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PrintManager.prototype = {
    'getPrintJobs' : function(printerId, gs_silent) {
        var data = {
            args : {
                printerId : JSON.stringify(printerId),
            },
            method: 'getPrintJobs',
            interfaceName: 'core.printmanager.IPrintManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.StorePrintManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StorePrintManager.prototype = {
    'deletePrinter' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deletePrinter',
            interfaceName: 'core.printmanager.IStorePrintManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPrinters' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPrinters',
            interfaceName: 'core.printmanager.IStorePrintManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'savePrinter' : function(printer, gs_silent) {
        var data = {
            args : {
                printer : JSON.stringify(printer),
            },
            method: 'savePrinter',
            interfaceName: 'core.printmanager.IStorePrintManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ProductManager.prototype = {
    'changeStockQuantity' : function(productId,count, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'changeStockQuantity',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'copyProduct' : function(fromProductId,newName, gs_silent) {
        var data = {
            args : {
                fromProductId : JSON.stringify(fromProductId),
                newName : JSON.stringify(newName),
            },
            method: 'copyProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createProduct' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'createProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createProductList' : function(listName, gs_silent) {
        var data = {
            args : {
                listName : JSON.stringify(listName),
            },
            method: 'createProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteCategory' : function(categoryId, gs_silent) {
        var data = {
            args : {
                categoryId : JSON.stringify(categoryId),
            },
            method: 'deleteCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteProductList' : function(listId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'deleteProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllCategories' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllCategories',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllProducts' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllProductsLight' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllProductsLight',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCategory' : function(categoryId, gs_silent) {
        var data = {
            args : {
                categoryId : JSON.stringify(categoryId),
            },
            method: 'getCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLatestProducts' : function(count, gs_silent) {
        var data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPageIdByName' : function(productName, gs_silent) {
        var data = {
            args : {
                productName : JSON.stringify(productName),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPrice' : function(productId,variations, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                variations : JSON.stringify(variations),
            },
            method: 'getPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProduct' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductByPage' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProductByPage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductFromApplicationId' : function(app_uuid, gs_silent) {
        var data = {
            args : {
                app_uuid : JSON.stringify(app_uuid),
            },
            method: 'getProductFromApplicationId',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductList' : function(listId, gs_silent) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductLists' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getProductLists',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProducts' : function(productCriteria, gs_silent) {
        var data = {
            args : {
                productCriteria : JSON.stringify(productCriteria),
            },
            method: 'getProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRandomProducts' : function(fetchSize,ignoreProductId, gs_silent) {
        var data = {
            args : {
                fetchSize : JSON.stringify(fetchSize),
                ignoreProductId : JSON.stringify(ignoreProductId),
            },
            method: 'getRandomProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTaxes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeProduct' : function(productId, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'removeProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCategory' : function(categories, gs_silent) {
        var data = {
            args : {
                categories : JSON.stringify(categories),
            },
            method: 'saveCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveProduct' : function(product, gs_silent) {
        var data = {
            args : {
                product : JSON.stringify(product),
            },
            method: 'saveProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveProductList' : function(productList, gs_silent) {
        var data = {
            args : {
                productList : JSON.stringify(productList),
            },
            method: 'saveProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'search' : function(searchWord,pageSize,page, gs_silent) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
                pageSize : JSON.stringify(pageSize),
                page : JSON.stringify(page),
            },
            method: 'search',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setMainImage' : function(productId,imageId, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                imageId : JSON.stringify(imageId),
            },
            method: 'setMainImage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setProductDynamicPrice' : function(productId,count, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'setProductDynamicPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setTaxes' : function(group, gs_silent) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'setTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'translateEntries' : function(entryIds, gs_silent) {
        var data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.PullServerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PullServerManager.prototype = {
    'getPullMessages' : function(keyId,storeId, gs_silent) {
        var data = {
            args : {
                keyId : JSON.stringify(keyId),
                storeId : JSON.stringify(storeId),
            },
            method: 'getPullMessages',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markMessageAsReceived' : function(messageId,storeId, gs_silent) {
        var data = {
            args : {
                messageId : JSON.stringify(messageId),
                storeId : JSON.stringify(storeId),
            },
            method: 'markMessageAsReceived',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'savePullMessage' : function(pullMessage, gs_silent) {
        var data = {
            args : {
                pullMessage : JSON.stringify(pullMessage),
            },
            method: 'savePullMessage',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.QuestBackManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.QuestBackManager.prototype = {
    'answerQuestions' : function(testId,applicationId,pageId,answers, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                applicationId : JSON.stringify(applicationId),
                pageId : JSON.stringify(pageId),
                answers : JSON.stringify(answers),
            },
            method: 'answerQuestions',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'assignUserToTest' : function(testId,userId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
            },
            method: 'assignUserToTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createTemplatePageIfNotExists' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'createTemplatePageIfNotExists',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createTest' : function(testName, gs_silent) {
        var data = {
            args : {
                testName : JSON.stringify(testName),
            },
            method: 'createTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteTest' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'deleteTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllTests' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllTests',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getBestCategoryResultForCompany' : function(userId,catId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                catId : JSON.stringify(catId),
            },
            method: 'getBestCategoryResultForCompany',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCategories' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCategories',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCategoriesForTest' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getCategoriesForTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompanyScoreForTestForCurrentUser' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getCompanyScoreForTestForCurrentUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNextQuestionPage' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getNextQuestionPage',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOptionsByPageId' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getOptionsByPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPageId' : function(questionId, gs_silent) {
        var data = {
            args : {
                questionId : JSON.stringify(questionId),
            },
            method: 'getPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProgress' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getProgress',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProgressForUser' : function(userId,testId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getProgressForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getQuestion' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getQuestion',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getQuestionTitle' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getQuestionTitle',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getResult' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getResult',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getResultRequirement' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getResultRequirement',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getResultWithReference' : function(testId,referenceId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                referenceId : JSON.stringify(referenceId),
            },
            method: 'getResultWithReference',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getScoreForTest' : function(userId,testId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getScoreForTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTest' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTestResult' : function(testId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getTestResult',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTestResultForUser' : function(testId,userId, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
            },
            method: 'getTestResultForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTestResults' : function(userId,testId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getTestResults',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTests' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getTests',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTestsForUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getTestsForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTypeByPageId' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getTypeByPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'hasAnswered' : function(pageId,testId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                testId : JSON.stringify(testId),
            },
            method: 'hasAnswered',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isQuestBackSent' : function(userId,testId,reference, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
                reference : JSON.stringify(reference),
            },
            method: 'isQuestBackSent',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'questionTreeChanged' : function(applicationId, gs_silent) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'questionTreeChanged',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveQuestBackResultRequirement' : function(requirement, gs_silent) {
        var data = {
            args : {
                requirement : JSON.stringify(requirement),
            },
            method: 'saveQuestBackResultRequirement',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveTest' : function(test, gs_silent) {
        var data = {
            args : {
                test : JSON.stringify(test),
            },
            method: 'saveTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendQuestBack' : function(testId,userId,reference,event, gs_silent) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
                reference : JSON.stringify(reference),
                event : JSON.stringify(event),
            },
            method: 'sendQuestBack',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ReportingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ReportingManager.prototype = {
    'getAllEventsFromSession' : function(startDate,stopDate,searchSessionId, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                searchSessionId : JSON.stringify(searchSessionId),
            },
            method: 'getAllEventsFromSession',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getConnectedUsers' : function(startdate,stopDate,filter, gs_silent) {
        var data = {
            args : {
                startdate : JSON.stringify(startdate),
                stopDate : JSON.stringify(stopDate),
                filter : JSON.stringify(filter),
            },
            method: 'getConnectedUsers',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersCreated' : function(startDate,stopDate, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getOrdersCreated',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPageViews' : function(startDate,stopDate, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getPageViews',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductViewed' : function(startDate,stopDate, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getProductViewed',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getReport' : function(startDate,stopDate,type, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                type : JSON.stringify(type),
            },
            method: 'getReport',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserLoggedOn' : function(startDate,stopDate, gs_silent) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getUserLoggedOn',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ResturantManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ResturantManager.prototype = {
    'addCartItems' : function(cartItems,tableId, gs_silent) {
        var data = {
            args : {
                cartItems : JSON.stringify(cartItems),
                tableId : JSON.stringify(tableId),
            },
            method: 'addCartItems',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeToDifferentSession' : function(sessionId,tableId, gs_silent) {
        var data = {
            args : {
                sessionId : JSON.stringify(sessionId),
                tableId : JSON.stringify(tableId),
            },
            method: 'changeToDifferentSession',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'completePayment' : function(paymentMethodId,cartItemIds, gs_silent) {
        var data = {
            args : {
                paymentMethodId : JSON.stringify(paymentMethodId),
                cartItemIds : JSON.stringify(cartItemIds),
            },
            method: 'completePayment',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createRoom' : function(roomName, gs_silent) {
        var data = {
            args : {
                roomName : JSON.stringify(roomName),
            },
            method: 'createRoom',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createTable' : function(roomId,tableId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                tableId : JSON.stringify(tableId),
            },
            method: 'createTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createTableSession' : function(tableId, gs_silent) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'createTableSession',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteTable' : function(tableId, gs_silent) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'deleteTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllSessions' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllSessions',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllSessionsForTable' : function(tableId, gs_silent) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getAllSessionsForTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrentTableData' : function(tableId, gs_silent) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getCurrentTableData',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoomById' : function(roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'getRoomById',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRooms' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getRooms',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getTableById' : function(tableId, gs_silent) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getTableById',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isOrderPriceCorrect' : function(paymentMethodId,cartItems,price, gs_silent) {
        var data = {
            args : {
                paymentMethodId : JSON.stringify(paymentMethodId),
                cartItems : JSON.stringify(cartItems),
                price : JSON.stringify(price),
            },
            method: 'isOrderPriceCorrect',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'payOnRoom' : function(room,cartItemsIds, gs_silent) {
        var data = {
            args : {
                room : JSON.stringify(room),
                cartItemsIds : JSON.stringify(cartItemsIds),
            },
            method: 'payOnRoom',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.SalesManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SalesManager.prototype = {
    'findCustomer' : function(key,type, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
                type : JSON.stringify(type),
            },
            method: 'findCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCustomer' : function(orgId, gs_silent) {
        var data = {
            args : {
                orgId : JSON.stringify(orgId),
            },
            method: 'getCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEvent' : function(eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsForCustomer' : function(orgId, gs_silent) {
        var data = {
            args : {
                orgId : JSON.stringify(orgId),
            },
            method: 'getEventsForCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsForDay' : function(day, gs_silent) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getEventsForDay',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLatestCustomer' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLatestCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLatestEvent' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLatestEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCustomer' : function(customer, gs_silent) {
        var data = {
            args : {
                customer : JSON.stringify(customer),
            },
            method: 'saveCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveEvent' : function(event, gs_silent) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'saveEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.SearchManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SearchManager.prototype = {
    'search' : function(searchWord, gs_silent) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'search',
            interfaceName: 'core.searchmanager.ISearchManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.SedoxProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SedoxProductManager.prototype = {
    'addCommentToUser' : function(userId,comment, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                comment : JSON.stringify(comment),
            },
            method: 'addCommentToUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addCreditToSlave' : function(slaveId,amount, gs_silent) {
        var data = {
            args : {
                slaveId : JSON.stringify(slaveId),
                amount : JSON.stringify(amount),
            },
            method: 'addCreditToSlave',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addFileToProduct' : function(base64EncodedFile,fileName,fileType,productId,options, gs_silent) {
        var data = {
            args : {
                base64EncodedFile : JSON.stringify(base64EncodedFile),
                fileName : JSON.stringify(fileName),
                fileType : JSON.stringify(fileType),
                productId : JSON.stringify(productId),
                options : JSON.stringify(options),
            },
            method: 'addFileToProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addFileToProductAsync' : function(sedoxBinaryFile,fileType,fileName,productId, gs_silent) {
        var data = {
            args : {
                sedoxBinaryFile : JSON.stringify(sedoxBinaryFile),
                fileType : JSON.stringify(fileType),
                fileName : JSON.stringify(fileName),
                productId : JSON.stringify(productId),
            },
            method: 'addFileToProductAsync',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addReference' : function(productId,reference, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                reference : JSON.stringify(reference),
            },
            method: 'addReference',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addSlaveToUser' : function(masterUserId,slaveUserId, gs_silent) {
        var data = {
            args : {
                masterUserId : JSON.stringify(masterUserId),
                slaveUserId : JSON.stringify(slaveUserId),
            },
            method: 'addSlaveToUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserCredit' : function(id,description,amount, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
                description : JSON.stringify(description),
                amount : JSON.stringify(amount),
            },
            method: 'addUserCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeDeveloperStatus' : function(userId,disabled, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                disabled : JSON.stringify(disabled),
            },
            method: 'changeDeveloperStatus',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearManager' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'clearManager',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createSedoxProduct' : function(sedoxProduct,base64encodedOriginalFile,originalFileName,forSlaveId,origin,comment,useCredit,options,reference, gs_silent) {
        var data = {
            args : {
                sedoxProduct : JSON.stringify(sedoxProduct),
                base64encodedOriginalFile : JSON.stringify(base64encodedOriginalFile),
                originalFileName : JSON.stringify(originalFileName),
                forSlaveId : JSON.stringify(forSlaveId),
                origin : JSON.stringify(origin),
                comment : JSON.stringify(comment),
                useCredit : JSON.stringify(useCredit),
                options : JSON.stringify(options),
                reference : JSON.stringify(reference),
            },
            method: 'createSedoxProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'finishUpload' : function(forSlaveId,sharedProduct,useCredit,comment,originalFile,cmdEncryptedFile,options,base64EncodeString,originalFileName,origin,fromUserId,referenceId, gs_silent) {
        var data = {
            args : {
                forSlaveId : JSON.stringify(forSlaveId),
                sharedProduct : JSON.stringify(sharedProduct),
                useCredit : JSON.stringify(useCredit),
                comment : JSON.stringify(comment),
                originalFile : JSON.stringify(originalFile),
                cmdEncryptedFile : JSON.stringify(cmdEncryptedFile),
                options : JSON.stringify(options),
                base64EncodeString : JSON.stringify(base64EncodeString),
                originalFileName : JSON.stringify(originalFileName),
                origin : JSON.stringify(origin),
                fromUserId : JSON.stringify(fromUserId),
                referenceId : JSON.stringify(referenceId),
            },
            method: 'finishUpload',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUsers' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUsersAsTreeNodes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllUsersAsTreeNodes',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUsersWithNegativeCreditLimit' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllUsersWithNegativeCreditLimit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrentUserCreditHistory' : function(filterData, gs_silent) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getCurrentUserCreditHistory',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrentUserCreditHistoryCount' : function(filterData, gs_silent) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getCurrentUserCreditHistoryCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDevelopers' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getDevelopers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getExtraInformationForFile' : function(productId,fileId, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
            },
            method: 'getExtraInformationForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFileNotProcessedToDayCount' : function(daysBack, gs_silent) {
        var data = {
            args : {
                daysBack : JSON.stringify(daysBack),
            },
            method: 'getFileNotProcessedToDayCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLatestProductsList' : function(count, gs_silent) {
        var data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProductsList',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getNextFileId' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getNextFileId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrders' : function(filterData, gs_silent) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOrders',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getOrdersPageCount' : function(filterData, gs_silent) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOrdersPageCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPriceForProduct' : function(productId,files, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'getPriceForProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductById' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProductById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductIds' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getProductIds',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductsByDaysBack' : function(day, gs_silent) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getProductsByDaysBack',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductsFirstUploadedByCurrentUser' : function(filterData, gs_silent) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getProductsFirstUploadedByCurrentUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getProductsFirstUploadedByCurrentUserTotalPages' : function(filterData, gs_silent) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getProductsFirstUploadedByCurrentUserTotalPages',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSedoxProductByMd5Sum' : function(md5sum, gs_silent) {
        var data = {
            args : {
                md5sum : JSON.stringify(md5sum),
            },
            method: 'getSedoxProductByMd5Sum',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSedoxUserAccount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getSedoxUserAccount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSedoxUserAccountById' : function(userid, gs_silent) {
        var data = {
            args : {
                userid : JSON.stringify(userid),
            },
            method: 'getSedoxUserAccountById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSharedProductById' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSharedProductById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSlaves' : function(masterUserId, gs_silent) {
        var data = {
            args : {
                masterUserId : JSON.stringify(masterUserId),
            },
            method: 'getSlaves',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStatistic' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getStatistic',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUploadHistory' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getUploadHistory',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserFileDownloadCount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getUserFileDownloadCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserFileUploadCount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getUserFileUploadCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'invokeCreditUpdate' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'invokeCreditUpdate',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'login' : function(emailAddress,password, gs_silent) {
        var data = {
            args : {
                emailAddress : JSON.stringify(emailAddress),
                password : JSON.stringify(password),
            },
            method: 'login',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAsFinished' : function(productId,finished, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                finished : JSON.stringify(finished),
            },
            method: 'markAsFinished',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'notifyForCustomer' : function(productId,extraText, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                extraText : JSON.stringify(extraText),
            },
            method: 'notifyForCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'purchaseOnlyForCustomer' : function(productId,files, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'purchaseOnlyForCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'purchaseProduct' : function(productId,files, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'purchaseProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'refreshEvcCredit' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'refreshEvcCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeBinaryFileFromProduct' : function(productId,fileId, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
            },
            method: 'removeBinaryFileFromProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeSlaveFromMaster' : function(slaveId, gs_silent) {
        var data = {
            args : {
                slaveId : JSON.stringify(slaveId),
            },
            method: 'removeSlaveFromMaster',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'requestSpecialFile' : function(productId,comment, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                comment : JSON.stringify(comment),
            },
            method: 'requestSpecialFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'search' : function(search, gs_silent) {
        var data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'search',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'searchForUsers' : function(searchString, gs_silent) {
        var data = {
            args : {
                searchString : JSON.stringify(searchString),
            },
            method: 'searchForUsers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'searchUserFiles' : function(search, gs_silent) {
        var data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'searchUserFiles',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendProductByMail' : function(productId,extraText,files, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                extraText : JSON.stringify(extraText),
                files : JSON.stringify(files),
            },
            method: 'sendProductByMail',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendProductToDifferentEmail' : function(productId,emailAddress,files,extraText, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                emailAddress : JSON.stringify(emailAddress),
                files : JSON.stringify(files),
                extraText : JSON.stringify(extraText),
            },
            method: 'sendProductToDifferentEmail',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setChecksum' : function(productId,checksum, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                checksum : JSON.stringify(checksum),
            },
            method: 'setChecksum',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCreditAllowedLimist' : function(userId,creditlimit, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                creditlimit : JSON.stringify(creditlimit),
            },
            method: 'setCreditAllowedLimist',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setEvcId' : function(userId,evcId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                evcId : JSON.stringify(evcId),
            },
            method: 'setEvcId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setExtraInformationForFile' : function(productId,fileId,text, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
                text : JSON.stringify(text),
            },
            method: 'setExtraInformationForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setFixedPrice' : function(userId,price, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                price : JSON.stringify(price),
            },
            method: 'setFixedPrice',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setPushoverId' : function(pushover, gs_silent) {
        var data = {
            args : {
                pushover : JSON.stringify(pushover),
            },
            method: 'setPushoverId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setPushoverIdForUser' : function(pushover,userId, gs_silent) {
        var data = {
            args : {
                pushover : JSON.stringify(pushover),
                userId : JSON.stringify(userId),
            },
            method: 'setPushoverIdForUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setSpecialRequestsForFile' : function(productId,fileId,dpf,egr,decat,vmax,adblue,dtc,flaps, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
                dpf : JSON.stringify(dpf),
                egr : JSON.stringify(egr),
                decat : JSON.stringify(decat),
                vmax : JSON.stringify(vmax),
                adblue : JSON.stringify(adblue),
                dtc : JSON.stringify(dtc),
                flaps : JSON.stringify(flaps),
            },
            method: 'setSpecialRequestsForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setType' : function(productId,type, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                type : JSON.stringify(type),
            },
            method: 'setType',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sync' : function(option, gs_silent) {
        var data = {
            args : {
                option : JSON.stringify(option),
            },
            method: 'sync',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'syncFromMagento' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'syncFromMagento',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleAllowNegativeCredit' : function(userId,allow, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                allow : JSON.stringify(allow),
            },
            method: 'toggleAllowNegativeCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleAllowWindowsApp' : function(userId,allow, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                allow : JSON.stringify(allow),
            },
            method: 'toggleAllowWindowsApp',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleBadCustomer' : function(userId,badCustomer, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                badCustomer : JSON.stringify(badCustomer),
            },
            method: 'toggleBadCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleIsNorwegian' : function(userId,isNorwegian, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                isNorwegian : JSON.stringify(isNorwegian),
            },
            method: 'toggleIsNorwegian',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'togglePassiveSlaveMode' : function(userId,toggle, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                toggle : JSON.stringify(toggle),
            },
            method: 'togglePassiveSlaveMode',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleSaleableProduct' : function(productId,saleable, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                saleable : JSON.stringify(saleable),
            },
            method: 'toggleSaleableProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleStartStop' : function(productId,toggle, gs_silent) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                toggle : JSON.stringify(toggle),
            },
            method: 'toggleStartStop',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'transferCreditToSlave' : function(slaveId,amount, gs_silent) {
        var data = {
            args : {
                slaveId : JSON.stringify(slaveId),
                amount : JSON.stringify(amount),
            },
            method: 'transferCreditToSlave',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateEvcCreditAccounts' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'updateEvcCreditAccounts',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.SimpleEventManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SimpleEventManager.prototype = {
    'addUserToEvent' : function(pageId,userId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                userId : JSON.stringify(userId),
            },
            method: 'addUserToEvent',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteEvent' : function(eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteEvent',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllEvents' : function(listPageId, gs_silent) {
        var data = {
            args : {
                listPageId : JSON.stringify(listPageId),
            },
            method: 'getAllEvents',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventById' : function(eventId, gs_silent) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventById',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventByPageId' : function(pageId, gs_silent) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEventByPageId',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEventsInFuture' : function(listPageId, gs_silent) {
        var data = {
            args : {
                listPageId : JSON.stringify(listPageId),
            },
            method: 'getEventsInFuture',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveEvent' : function(simpleEvent, gs_silent) {
        var data = {
            args : {
                simpleEvent : JSON.stringify(simpleEvent),
            },
            method: 'saveEvent',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.StoreManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreManager.prototype = {
    'autoCreateStore' : function(hostname, gs_silent) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'autoCreateStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createStore' : function(hostname,email,password,notify, gs_silent) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
                email : JSON.stringify(email),
                password : JSON.stringify(password),
                notify : JSON.stringify(notify),
            },
            method: 'createStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'delete' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'delete',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'enableExtendedMode' : function(toggle,password, gs_silent) {
        var data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'enableExtendedMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'enableSMSAccess' : function(toggle,password, gs_silent) {
        var data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'enableSMSAccess',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'generateStoreId' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'generateStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllEnvironments' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllEnvironments',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCurrentSession' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCurrentSession',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getKey' : function(key, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
            },
            method: 'getKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getKeySecure' : function(key,password, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
                password : JSON.stringify(password),
            },
            method: 'getKeySecure',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMultiLevelNames' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMultiLevelNames',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMyStore' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMyStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStoreId' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'initializeStore' : function(webAddress,initSessionId, gs_silent) {
        var data = {
            args : {
                webAddress : JSON.stringify(webAddress),
                initSessionId : JSON.stringify(initSessionId),
            },
            method: 'initializeStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'initializeStoreByStoreId' : function(storeId,initSessionId, gs_silent) {
        var data = {
            args : {
                storeId : JSON.stringify(storeId),
                initSessionId : JSON.stringify(initSessionId),
            },
            method: 'initializeStoreByStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isAddressTaken' : function(address, gs_silent) {
        var data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'isAddressTaken',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isProductMode' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'isProductMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'receiveSyncData' : function(json, gs_silent) {
        var data = {
            args : {
                json : JSON.stringify(json),
            },
            method: 'receiveSyncData',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeDomainName' : function(domainName, gs_silent) {
        var data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'removeDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeKey' : function(key, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
            },
            method: 'removeKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveKey' : function(key,value,secure, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
                value : JSON.stringify(value),
                secure : JSON.stringify(secure),
            },
            method: 'saveKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveStore' : function(config, gs_silent) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setImageIdToFavicon' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setImageIdToFavicon',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setIntroductionRead' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'setIntroductionRead',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setIsTemplate' : function(storeId,isTemplate, gs_silent) {
        var data = {
            args : {
                storeId : JSON.stringify(storeId),
                isTemplate : JSON.stringify(isTemplate),
            },
            method: 'setIsTemplate',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setPrimaryDomainName' : function(domainName, gs_silent) {
        var data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'setPrimaryDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setSessionLanguage' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setSessionLanguage',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setStoreIdentifier' : function(identifier, gs_silent) {
        var data = {
            args : {
                identifier : JSON.stringify(identifier),
            },
            method: 'setStoreIdentifier',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'syncData' : function(environment,username,password, gs_silent) {
        var data = {
            args : {
                environment : JSON.stringify(environment),
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'syncData',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'toggleIgnoreBookingErrors' : function(password, gs_silent) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'toggleIgnoreBookingErrors',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.TrackAndTraceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.TrackAndTraceManager.prototype = {
    'addDeliveryTaskToDestionation' : function(destionatId,task, gs_silent) {
        var data = {
            args : {
                destionatId : JSON.stringify(destionatId),
                task : JSON.stringify(task),
            },
            method: 'addDeliveryTaskToDestionation',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addDriverToRoute' : function(userId,routeId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                routeId : JSON.stringify(routeId),
            },
            method: 'addDriverToRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeCountedDriverCopies' : function(taskId,orderReference,quantity, gs_silent) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                orderReference : JSON.stringify(orderReference),
                quantity : JSON.stringify(quantity),
            },
            method: 'changeCountedDriverCopies',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeQuantity' : function(taskId,orderReference,quantity, gs_silent) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                orderReference : JSON.stringify(orderReference),
                quantity : JSON.stringify(quantity),
            },
            method: 'changeQuantity',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteRoute' : function(routeId, gs_silent) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'deleteRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllRoutes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllRoutes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getDestinationById' : function(destinationId, gs_silent) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
            },
            method: 'getDestinationById',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getExceptions' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getExceptions',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getExport' : function(routeId, gs_silent) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'getExport',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLoadStatus' : function(statusId, gs_silent) {
        var data = {
            args : {
                statusId : JSON.stringify(statusId),
            },
            method: 'getLoadStatus',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLoadStatuses' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLoadStatuses',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMyRoutes' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getMyRoutes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPooledDestiontions' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPooledDestiontions',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getRoutesById' : function(routeId, gs_silent) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'getRoutesById',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'loadData' : function(base64,fileName, gs_silent) {
        var data = {
            args : {
                base64 : JSON.stringify(base64),
                fileName : JSON.stringify(fileName),
            },
            method: 'loadData',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markAsDeliverd' : function(taskId, gs_silent) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
            },
            method: 'markAsDeliverd',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markTaskWithExceptionDeliverd' : function(taskId,exceptionId, gs_silent) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                exceptionId : JSON.stringify(exceptionId),
            },
            method: 'markTaskWithExceptionDeliverd',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'moveDesitinationToPool' : function(routeId,destinationId, gs_silent) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                destinationId : JSON.stringify(destinationId),
            },
            method: 'moveDesitinationToPool',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'moveDestinationFromPoolToRoute' : function(destId,routeId, gs_silent) {
        var data = {
            args : {
                destId : JSON.stringify(destId),
                routeId : JSON.stringify(routeId),
            },
            method: 'moveDestinationFromPoolToRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveDestination' : function(destination, gs_silent) {
        var data = {
            args : {
                destination : JSON.stringify(destination),
            },
            method: 'saveDestination',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveException' : function(exception, gs_silent) {
        var data = {
            args : {
                exception : JSON.stringify(exception),
            },
            method: 'saveException',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveRoute' : function(route, gs_silent) {
        var data = {
            args : {
                route : JSON.stringify(route),
            },
            method: 'saveRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCagesOrPalletCount' : function(taskId,quantity, gs_silent) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                quantity : JSON.stringify(quantity),
            },
            method: 'setCagesOrPalletCount',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setDesitionationException' : function(destinationId,exceptionId,lon,lat, gs_silent) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
                exceptionId : JSON.stringify(exceptionId),
                lon : JSON.stringify(lon),
                lat : JSON.stringify(lat),
            },
            method: 'setDesitionationException',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setSequence' : function(exceptionId,sequence, gs_silent) {
        var data = {
            args : {
                exceptionId : JSON.stringify(exceptionId),
                sequence : JSON.stringify(sequence),
            },
            method: 'setSequence',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'unsetSkippedReason' : function(destinationId, gs_silent) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
            },
            method: 'unsetSkippedReason',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.UserManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UserManager.prototype = {
    'addComment' : function(userId,comment, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                comment : JSON.stringify(comment),
            },
            method: 'addComment',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addGroupToUser' : function(userId,groupId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'addGroupToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addMetaData' : function(userId,key,value, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                key : JSON.stringify(key),
                value : JSON.stringify(value),
            },
            method: 'addMetaData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addUserPrivilege' : function(userId,managerName,managerFunction, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                managerName : JSON.stringify(managerName),
                managerFunction : JSON.stringify(managerFunction),
            },
            method: 'addUserPrivilege',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'assignCompanyToGroup' : function(company,groupId, gs_silent) {
        var data = {
            args : {
                company : JSON.stringify(company),
                groupId : JSON.stringify(groupId),
            },
            method: 'assignCompanyToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'assignCompanyToUser' : function(company,userId, gs_silent) {
        var data = {
            args : {
                company : JSON.stringify(company),
                userId : JSON.stringify(userId),
            },
            method: 'assignCompanyToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'assignMetaDataToVirtualSessionUser' : function(key,value, gs_silent) {
        var data = {
            args : {
                key : JSON.stringify(key),
                value : JSON.stringify(value),
            },
            method: 'assignMetaDataToVirtualSessionUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'assignReferenceToCompany' : function(companyId,companyReference, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                companyReference : JSON.stringify(companyReference),
            },
            method: 'assignReferenceToCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'canCreateUser' : function(user, gs_silent) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'canCreateUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'cancelImpersonating' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'cancelImpersonating',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkIfFieldOnUserIsOkey' : function(field,value, gs_silent) {
        var data = {
            args : {
                field : JSON.stringify(field),
                value : JSON.stringify(value),
            },
            method: 'checkIfFieldOnUserIsOkey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkUserNameAndPassword' : function(username,password, gs_silent) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'checkUserNameAndPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'clearUserManagerForAllData' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'clearUserManagerForAllData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'confirmCompanyOwner' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'confirmCompanyOwner',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'createUser' : function(user, gs_silent) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'createUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteCompany' : function(companyId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'deleteCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteExtraAddressToGroup' : function(groupId,addressId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                addressId : JSON.stringify(addressId),
            },
            method: 'deleteExtraAddressToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'deleteUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'doEmailExists' : function(email, gs_silent) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'doEmailExists',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'doesUserExistsOnReferenceNumber' : function(number, gs_silent) {
        var data = {
            args : {
                number : JSON.stringify(number),
            },
            method: 'doesUserExistsOnReferenceNumber',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'findUsers' : function(searchCriteria, gs_silent) {
        var data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'findUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'forceCompanyOwner' : function(userId,isCompanyOwner, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                isCompanyOwner : JSON.stringify(isCompanyOwner),
            },
            method: 'forceCompanyOwner',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAdministratorCount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAdministratorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllCompanies' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllCompanies',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllCompaniesForGroup' : function(groupId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getAllCompaniesForGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllCompanyFiltered' : function(filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllCompanyFiltered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllGroups' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllGroups',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllGroupsFiletered' : function(filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllGroupsFiletered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUsers' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUsersFiltered' : function(filter, gs_silent) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllUsersFiltered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllUsersWithCommentToApp' : function(appId, gs_silent) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'getAllUsersWithCommentToApp',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompaniesConnectedToGroupCount' : function(groupId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getCompaniesConnectedToGroupCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompany' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompanyByReference' : function(companyReference, gs_silent) {
        var data = {
            args : {
                companyReference : JSON.stringify(companyReference),
            },
            method: 'getCompanyByReference',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCustomersCount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCustomersCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getEditorCount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getEditorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getGroup' : function(groupId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLoggedOnUser' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getLoggedOnUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getLogins' : function(year, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getLogins',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getPingoutTime' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getPingoutTime',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUnconfirmedCompanyOwners' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getUnconfirmedCompanyOwners',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserById' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getUserById',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserList' : function(userIds, gs_silent) {
        var data = {
            args : {
                userIds : JSON.stringify(userIds),
            },
            method: 'getUserList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUserWithPermissionCheck' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getUserWithPermissionCheck',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUsersBasedOnGroupId' : function(groupId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getUsersBasedOnGroupId',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUsersByCompanyId' : function(companyId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'getUsersByCompanyId',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getUsersByType' : function(type, gs_silent) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getUsersByType',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'impersonateUser' : function(userId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'impersonateUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isCaptain' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'isCaptain',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isImpersonating' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'isImpersonating',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isLoggedIn' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'isLoggedIn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logLogout' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'logLogout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logOn' : function(username,password, gs_silent) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'logOn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'loginWithPincode' : function(username,password,pinCode, gs_silent) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
                pinCode : JSON.stringify(pinCode),
            },
            method: 'loginWithPincode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logonUsingKey' : function(logonKey, gs_silent) {
        var data = {
            args : {
                logonKey : JSON.stringify(logonKey),
            },
            method: 'logonUsingKey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logonUsingRefNumber' : function(refCode, gs_silent) {
        var data = {
            args : {
                refCode : JSON.stringify(refCode),
            },
            method: 'logonUsingRefNumber',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'logout' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'logout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'mergeUsers' : function(userIds,properties, gs_silent) {
        var data = {
            args : {
                userIds : JSON.stringify(userIds),
                properties : JSON.stringify(properties),
            },
            method: 'mergeUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeComment' : function(userId,commentId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                commentId : JSON.stringify(commentId),
            },
            method: 'removeComment',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeGroup' : function(groupId, gs_silent) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeGroupFromUser' : function(userId,groupId, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroupFromUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeMetaData' : function(userId,key, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                key : JSON.stringify(key),
            },
            method: 'removeMetaData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeUserFromCompany' : function(companyId,userId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                userId : JSON.stringify(userId),
            },
            method: 'removeUserFromCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'requestAdminRight' : function(managerName,managerFunction,applicationInstanceId, gs_silent) {
        var data = {
            args : {
                managerName : JSON.stringify(managerName),
                managerFunction : JSON.stringify(managerFunction),
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'requestAdminRight',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'requestNewPincode' : function(username,password, gs_silent) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'requestNewPincode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'resetPassword' : function(resetCode,username,newPassword, gs_silent) {
        var data = {
            args : {
                resetCode : JSON.stringify(resetCode),
                username : JSON.stringify(username),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'resetPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveCompany' : function(company, gs_silent) {
        var data = {
            args : {
                company : JSON.stringify(company),
            },
            method: 'saveCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveExtraAddressToGroup' : function(group,address, gs_silent) {
        var data = {
            args : {
                group : JSON.stringify(group),
                address : JSON.stringify(address),
            },
            method: 'saveExtraAddressToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveGroup' : function(group, gs_silent) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'saveGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveUser' : function(user, gs_silent) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'saveUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'searchForCompanies' : function(searchWord, gs_silent) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'searchForCompanies',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'searchForGroup' : function(searchCriteria, gs_silent) {
        var data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'searchForGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendResetCode' : function(title,text,username, gs_silent) {
        var data = {
            args : {
                title : JSON.stringify(title),
                text : JSON.stringify(text),
                username : JSON.stringify(username),
            },
            method: 'sendResetCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setPasswordDirect' : function(userId,encryptedPassword, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                encryptedPassword : JSON.stringify(encryptedPassword),
            },
            method: 'setPasswordDirect',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'setSessionCompany' : function(companyId, gs_silent) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'setSessionCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updatePassword' : function(userId,oldPassword,newPassword, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                oldPassword : JSON.stringify(oldPassword),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'updatePassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateUserCounter' : function(counter,password, gs_silent) {
        var data = {
            args : {
                counter : JSON.stringify(counter),
                password : JSON.stringify(password),
            },
            method: 'updateUserCounter',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'upgradeUserToGetShopAdmin' : function(password, gs_silent) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'upgradeUserToGetShopAdmin',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.ImageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ImageManager.prototype = {
    'getBase64EncodedImageLocally' : function(imageId, gs_silent) {
        var data = {
            args : {
                imageId : JSON.stringify(imageId),
            },
            method: 'getBase64EncodedImageLocally',
            interfaceName: 'core.utils.IImageManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.UtilManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UtilManager.prototype = {
    'getBase64EncodedPDFWebPage' : function(urlToPage, gs_silent) {
        var data = {
            args : {
                urlToPage : JSON.stringify(urlToPage),
            },
            method: 'getBase64EncodedPDFWebPage',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompaniesFromBrReg' : function(search, gs_silent) {
        var data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'getCompaniesFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompanyFree' : function(companyVatNumber, gs_silent) {
        var data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFree',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCompanyFromBrReg' : function(companyVatNumber, gs_silent) {
        var data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getFile' : function(id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFile',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getStartupCount' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getStartupCount',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'isInProductionMode' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'isInProductionMode',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveFile' : function(file, gs_silent) {
        var data = {
            args : {
                file : JSON.stringify(file),
            },
            method: 'saveFile',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.UUIDSecurityManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UUIDSecurityManager.prototype = {
    'grantAccess' : function(userId,uuid,read,write, gs_silent) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                uuid : JSON.stringify(uuid),
                read : JSON.stringify(read),
                write : JSON.stringify(write),
            },
            method: 'grantAccess',
            interfaceName: 'core.uuidsecuritymanager.IUUIDSecurityManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'hasAccess' : function(uuid,read,write, gs_silent) {
        var data = {
            args : {
                uuid : JSON.stringify(uuid),
                read : JSON.stringify(read),
                write : JSON.stringify(write),
            },
            method: 'hasAccess',
            interfaceName: 'core.uuidsecuritymanager.IUUIDSecurityManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.WebManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.WebManager.prototype = {
    'htmlGet' : function(url, gs_silent) {
        var data = {
            args : {
                url : JSON.stringify(url),
            },
            method: 'htmlGet',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'htmlGetJson' : function(url, gs_silent) {
        var data = {
            args : {
                url : JSON.stringify(url),
            },
            method: 'htmlGetJson',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'htmlPost' : function(url,data,jsonPost,encoding, gs_silent) {
        var data = {
            args : {
                url : JSON.stringify(url),
                data : JSON.stringify(data),
                jsonPost : JSON.stringify(jsonPost),
                encoding : JSON.stringify(encoding),
            },
            method: 'htmlPost',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'htmlPostBasicAuth' : function(url,data,jsonPost,encoding,auth, gs_silent) {
        var data = {
            args : {
                url : JSON.stringify(url),
                data : JSON.stringify(data),
                jsonPost : JSON.stringify(jsonPost),
                encoding : JSON.stringify(encoding),
                auth : JSON.stringify(auth),
            },
            method: 'htmlPostBasicAuth',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'htmlPostJson' : function(url,data,encoding, gs_silent) {
        var data = {
            args : {
                url : JSON.stringify(url),
                data : JSON.stringify(data),
                encoding : JSON.stringify(encoding),
            },
            method: 'htmlPostJson',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.WubookManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.WubookManager.prototype = {
    'addBooking' : function(multilevelname, rcode, gs_silent) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'addBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addNewBookingsPastDays' : function(multilevelname, daysback, gs_silent) {
        var data = {
            args : {
                daysback : JSON.stringify(daysback),
            },
            method: 'addNewBookingsPastDays',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'addRestriction' : function(multilevelname, restriction, gs_silent) {
        var data = {
            args : {
                restriction : JSON.stringify(restriction),
            },
            method: 'addRestriction',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'checkForNoShowsAndMark' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'checkForNoShowsAndMark',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteAllRooms' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'deleteAllRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteBooking' : function(multilevelname, rcode, gs_silent) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'deleteRestriction' : function(multilevelname, id, gs_silent) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteRestriction',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'doubleCheckDeletedBookings' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'doubleCheckDeletedBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'fetchAllBookings' : function(multilevelname, daysback, gs_silent) {
        var data = {
            args : {
                daysback : JSON.stringify(daysback),
            },
            method: 'fetchAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'fetchBooking' : function(multilevelname, rcode, gs_silent) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'fetchBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'fetchBookingCodes' : function(multilevelname, daysback, gs_silent) {
        var data = {
            args : {
                daysback : JSON.stringify(daysback),
            },
            method: 'fetchBookingCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'fetchNewBookings' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'fetchNewBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllRestriction' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getAllRestriction',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getWubookRoomData' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getWubookRoomData',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'insertAllRooms' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'insertAllRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markCCInvalid' : function(multilevelname, rcode, gs_silent) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'markCCInvalid',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'markNoShow' : function(multilevelname, rcode, gs_silent) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'markNoShow',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveWubookRoomData' : function(multilevelname, res, gs_silent) {
        var data = {
            args : {
                res : JSON.stringify(res),
            },
            method: 'saveWubookRoomData',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'testConnection' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'testConnection',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updateAvailability' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'updateAvailability',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'updatePrices' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'updatePrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocket.YouTubeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.YouTubeManager.prototype = {
    'searchYoutube' : function(searchword, gs_silent) {
        var data = {
            args : {
                searchword : JSON.stringify(searchword),
            },
            method: 'searchYoutube',
            interfaceName: 'core.youtubemanager.IYouTubeManager',
        };
        return this.communication.send(data, gs_silent);
    },

}

GetShopApiWebSocket.prototype.createManagers = function() {
    this.BannerManager = new GetShopApiWebSocket.BannerManager(this);
    this.ContentManager = new GetShopApiWebSocket.ContentManager(this);
    this.FooterManager = new GetShopApiWebSocket.FooterManager(this);
    this.LogoManager = new GetShopApiWebSocket.LogoManager(this);
    this.NewsManager = new GetShopApiWebSocket.NewsManager(this);
    this.AccountingManager = new GetShopApiWebSocket.AccountingManager(this);
    this.AmestoManager = new GetShopApiWebSocket.AmestoManager(this);
    this.GetShopApplicationPool = new GetShopApiWebSocket.GetShopApplicationPool(this);
    this.StoreApplicationInstancePool = new GetShopApiWebSocket.StoreApplicationInstancePool(this);
    this.StoreApplicationPool = new GetShopApiWebSocket.StoreApplicationPool(this);
    this.DoorManager = new GetShopApiWebSocket.DoorManager(this);
    this.AsanaManager = new GetShopApiWebSocket.AsanaManager(this);
    this.BamboraManager = new GetShopApiWebSocket.BamboraManager(this);
    this.BigStock = new GetShopApiWebSocket.BigStock(this);
    this.BookingEngine = new GetShopApiWebSocket.BookingEngine(this);
    this.BrainTreeManager = new GetShopApiWebSocket.BrainTreeManager(this);
    this.C3Manager = new GetShopApiWebSocket.C3Manager(this);
    this.CalendarManager = new GetShopApiWebSocket.CalendarManager(this);
    this.CartManager = new GetShopApiWebSocket.CartManager(this);
    this.CarTuningManager = new GetShopApiWebSocket.CarTuningManager(this);
    this.CertegoManager = new GetShopApiWebSocket.CertegoManager(this);
    this.ChatManager = new GetShopApiWebSocket.ChatManager(this);
    this.DBBackupManager = new GetShopApiWebSocket.DBBackupManager(this);
    this.DibsManager = new GetShopApiWebSocket.DibsManager(this);
    this.EpayManager = new GetShopApiWebSocket.EpayManager(this);
    this.EventBookingManager = new GetShopApiWebSocket.EventBookingManager(this);
    this.FileManager = new GetShopApiWebSocket.FileManager(this);
    this.FtpManager = new GetShopApiWebSocket.FtpManager(this);
    this.GalleryManager = new GetShopApiWebSocket.GalleryManager(this);
    this.GetShop = new GetShopApiWebSocket.GetShop(this);
    this.GetShopLockManager = new GetShopApiWebSocket.GetShopLockManager(this);
    this.InformationScreenManager = new GetShopApiWebSocket.InformationScreenManager(this);
    this.ListManager = new GetShopApiWebSocket.ListManager(this);
    this.MecaManager = new GetShopApiWebSocket.MecaManager(this);
    this.MessageManager = new GetShopApiWebSocket.MessageManager(this);
    this.NewsLetterManager = new GetShopApiWebSocket.NewsLetterManager(this);
    this.MobileManager = new GetShopApiWebSocket.MobileManager(this);
    this.OrderManager = new GetShopApiWebSocket.OrderManager(this);
    this.PageManager = new GetShopApiWebSocket.PageManager(this);
    this.InvoiceManager = new GetShopApiWebSocket.InvoiceManager(this);
    this.LasGruppenPDFGenerator = new GetShopApiWebSocket.LasGruppenPDFGenerator(this);
    this.PkkControlManager = new GetShopApiWebSocket.PkkControlManager(this);
    this.PmsEventManager = new GetShopApiWebSocket.PmsEventManager(this);
    this.PmsInvoiceManager = new GetShopApiWebSocket.PmsInvoiceManager(this);
    this.PmsManager = new GetShopApiWebSocket.PmsManager(this);
    this.PmsManagerProcessor = new GetShopApiWebSocket.PmsManagerProcessor(this);
    this.PrintManager = new GetShopApiWebSocket.PrintManager(this);
    this.StorePrintManager = new GetShopApiWebSocket.StorePrintManager(this);
    this.ProductManager = new GetShopApiWebSocket.ProductManager(this);
    this.PullServerManager = new GetShopApiWebSocket.PullServerManager(this);
    this.QuestBackManager = new GetShopApiWebSocket.QuestBackManager(this);
    this.ReportingManager = new GetShopApiWebSocket.ReportingManager(this);
    this.ResturantManager = new GetShopApiWebSocket.ResturantManager(this);
    this.SalesManager = new GetShopApiWebSocket.SalesManager(this);
    this.SearchManager = new GetShopApiWebSocket.SearchManager(this);
    this.SedoxProductManager = new GetShopApiWebSocket.SedoxProductManager(this);
    this.SimpleEventManager = new GetShopApiWebSocket.SimpleEventManager(this);
    this.StoreManager = new GetShopApiWebSocket.StoreManager(this);
    this.TrackAndTraceManager = new GetShopApiWebSocket.TrackAndTraceManager(this);
    this.UserManager = new GetShopApiWebSocket.UserManager(this);
    this.ImageManager = new GetShopApiWebSocket.ImageManager(this);
    this.UtilManager = new GetShopApiWebSocket.UtilManager(this);
    this.UUIDSecurityManager = new GetShopApiWebSocket.UUIDSecurityManager(this);
    this.WebManager = new GetShopApiWebSocket.WebManager(this);
    this.WubookManager = new GetShopApiWebSocket.WubookManager(this);
    this.YouTubeManager = new GetShopApiWebSocket.YouTubeManager(this);
}