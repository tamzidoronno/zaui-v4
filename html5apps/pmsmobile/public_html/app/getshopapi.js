GetShopApiWebSocket = function(address) {
    this.sentMessages =  [];
    this.address = address;
};

GetShopApiWebSocket.prototype = {
    websocket: null,
    connectionEstablished: null,
    transferCompleted: null,
    transferStarted: null,
    shouldConnect: true,
    sessionId: false,
    
    connect: function() {
        if (!this.shouldConnect)
            return;
        
        this.shouldConnect = false;
        this.connectedCalled = true;
        var me = this;
        if (this.connectionEstablished === null) {
            this.fireDisconnectedEvent();
        }
        var address = "ws://"+this.address+":31332/";
        this.socket = new WebSocket(address);
        this.socket.onopen = $.proxy(this.connected, this);
        this.socket.onclose = function() {
            me.disconnected();
        };
        this.socket.onmessage = function(msg) {
            me.handleMessage(msg);
        };
        
        this.createManagers();
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

    handleMessage: function(msg) {
        console.log(msg);
        var data = msg.data;
        var jsonObject = JSON.parse(data);
        var corrolatingMessage = this.getMessage(jsonObject.messageId);
        corrolatingMessage.resolve(jsonObject.object);
        if (this.sentMessages.length === 0 && this.transferCompleted) {
            this.transferCompleted();
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
            this.socket.send('initstore:'+this.address);
    },
            
    connected: function() {
        this.setSessionId();
        this.initializeStore();
        this.fireConnectedEvent();
        this.connectionEstablished = true;
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
        this.reconnect();
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
        messageJson = JSON.stringify(message);
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
        for (i=0;i<this.sentMessages.length; i++) {
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
    'addImage' : function(id,fileId, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                fileId : JSON.stringify(fileId),
            },
            method: 'addImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    'createSet' : function(width,height,id, silent) {
        data = {
            args : {
                width : JSON.stringify(width),
                height : JSON.stringify(height),
                id : JSON.stringify(id),
            },
            method: 'createSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteSet' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    'getSet' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    'linkProductToImage' : function(bannerSetId,imageId,productId, silent) {
        data = {
            args : {
                bannerSetId : JSON.stringify(bannerSetId),
                imageId : JSON.stringify(imageId),
                productId : JSON.stringify(productId),
            },
            method: 'linkProductToImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    'removeImage' : function(bannerSetId,fileId, silent) {
        data = {
            args : {
                bannerSetId : JSON.stringify(bannerSetId),
                fileId : JSON.stringify(fileId),
            },
            method: 'removeImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    'saveSet' : function(set, silent) {
        data = {
            args : {
                set : JSON.stringify(set),
            },
            method: 'saveSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.ContentManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ContentManager.prototype = {
    'createContent' : function(content, silent) {
        data = {
            args : {
                content : JSON.stringify(content),
            },
            method: 'createContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteContent' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllContent' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    'getContent' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    'saveContent' : function(id,content, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                content : JSON.stringify(content),
            },
            method: 'saveContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.FooterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FooterManager.prototype = {
    'getConfiguration' : function(silent) {
        data = {
            args : {
            },
            method: 'getConfiguration',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, silent);
    },

    'setLayout' : function(numberOfColumns, silent) {
        data = {
            args : {
                numberOfColumns : JSON.stringify(numberOfColumns),
            },
            method: 'setLayout',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, silent);
    },

    'setType' : function(column,type, silent) {
        data = {
            args : {
                column : JSON.stringify(column),
                type : JSON.stringify(type),
            },
            method: 'setType',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.LogoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LogoManager.prototype = {
    'deleteLogo' : function(silent) {
        data = {
            args : {
            },
            method: 'deleteLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, silent);
    },

    'getLogo' : function(silent) {
        data = {
            args : {
            },
            method: 'getLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, silent);
    },

    'setLogo' : function(fileId, silent) {
        data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'setLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.NewsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsManager.prototype = {
    'addNews' : function(newsEntry, silent) {
        data = {
            args : {
                newsEntry : JSON.stringify(newsEntry),
            },
            method: 'addNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'addSubscriber' : function(email, silent) {
        data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'addSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteNews' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllNews' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllSubscribers' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllSubscribers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'getNewsForPage' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getNewsForPage',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'publishNews' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'publishNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    'removeSubscriber' : function(subscriberId, silent) {
        data = {
            args : {
                subscriberId : JSON.stringify(subscriberId),
            },
            method: 'removeSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.AccountingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AccountingManager.prototype = {
    'createCombinedOrderFile' : function(newUsersOnly, silent) {
        data = {
            args : {
                newUsersOnly : JSON.stringify(newUsersOnly),
            },
            method: 'createCombinedOrderFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'createOrderFile' : function(silent) {
        data = {
            args : {
            },
            method: 'createOrderFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'createUserFile' : function(newOnly, silent) {
        data = {
            args : {
                newOnly : JSON.stringify(newOnly),
            },
            method: 'createUserFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'getAccountingManagerConfig' : function(silent) {
        data = {
            args : {
            },
            method: 'getAccountingManagerConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllFiles' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllFiles',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllFilesNotTransferredToAccounting' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllFilesNotTransferredToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'getFile' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'markAsTransferredToAccounting' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'markAsTransferredToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'setAccountingManagerConfig' : function(config, silent) {
        data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'setAccountingManagerConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

    'transferFilesToAccounting' : function(silent) {
        data = {
            args : {
            },
            method: 'transferFilesToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.GetShopApplicationPool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopApplicationPool.prototype = {
    'deleteApplication' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'deleteApplication',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'get' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'get',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'saveApplication' : function(application, silent) {
        data = {
            args : {
                application : JSON.stringify(application),
            },
            method: 'saveApplication',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.StoreApplicationInstancePool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreApplicationInstancePool.prototype = {
    'createNewInstance' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'createNewInstance',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, silent);
    },

    'getApplicationInstance' : function(applicationInstanceId, silent) {
        data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getApplicationInstance',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, silent);
    },

    'getApplicationInstances' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'getApplicationInstances',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, silent);
    },

    'setApplicationSettings' : function(settings, silent) {
        data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'setApplicationSettings',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.StoreApplicationPool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreApplicationPool.prototype = {
    'activateApplication' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'activateApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'activateModule' : function(module, silent) {
        data = {
            args : {
                module : JSON.stringify(module),
            },
            method: 'activateModule',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'deactivateApplication' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'deactivateApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getActivatedModules' : function(silent) {
        data = {
            args : {
            },
            method: 'getActivatedModules',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getActivatedPaymentApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getActivatedPaymentApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getAllAvailableModules' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllAvailableModules',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getApplication' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getAvailableApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getAvailableApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getAvailableApplicationsThatIsNotActivated' : function(silent) {
        data = {
            args : {
            },
            method: 'getAvailableApplicationsThatIsNotActivated',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getAvailableThemeApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getAvailableThemeApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getPaymentSettingsApplication' : function(silent) {
        data = {
            args : {
            },
            method: 'getPaymentSettingsApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getShippingApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getShippingApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'getThemeApplication' : function(silent) {
        data = {
            args : {
            },
            method: 'getThemeApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'isActivated' : function(appId, silent) {
        data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'isActivated',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'setSetting' : function(applicationId,settings, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                settings : JSON.stringify(settings),
            },
            method: 'setSetting',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

    'setThemeApplication' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'setThemeApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.DoorManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DoorManager.prototype = {
    'addCard' : function(multilevelname, personId,card, silent) {
        data = {
            args : {
                personId : JSON.stringify(personId),
                card : JSON.stringify(card),
            },
            method: 'addCard',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'clearDoorCache' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'clearDoorCache',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'doorAction' : function(multilevelname, externalId,state, silent) {
        data = {
            args : {
                externalId : JSON.stringify(externalId),
                state : JSON.stringify(state),
            },
            method: 'doorAction',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'generateDoorLogForAllDoorsFromResult' : function(multilevelname, resultFromArx, silent) {
        data = {
            args : {
                resultFromArx : JSON.stringify(resultFromArx),
            },
            method: 'generateDoorLogForAllDoorsFromResult',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllAccessCategories' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllAccessCategories',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllDoors' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllDoors',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllPersons' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllPersons',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'getLogForAllDoor' : function(multilevelname, start,end, silent) {
        data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getLogForAllDoor',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'getLogForDoor' : function(multilevelname, externalId,start,end, silent) {
        data = {
            args : {
                externalId : JSON.stringify(externalId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getLogForDoor',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'getPerson' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPerson',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

    'updatePerson' : function(multilevelname, person, silent) {
        data = {
            args : {
                person : JSON.stringify(person),
            },
            method: 'updatePerson',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.BigStock = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BigStock.prototype = {
    'addGetShopImageIdToBigStockOrder' : function(downloadUrl,imageId, silent) {
        data = {
            args : {
                downloadUrl : JSON.stringify(downloadUrl),
                imageId : JSON.stringify(imageId),
            },
            method: 'addGetShopImageIdToBigStockOrder',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, silent);
    },

    'getAvailableCredits' : function(silent) {
        data = {
            args : {
            },
            method: 'getAvailableCredits',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, silent);
    },

    'purchaseImage' : function(imageId,sizeCode, silent) {
        data = {
            args : {
                imageId : JSON.stringify(imageId),
                sizeCode : JSON.stringify(sizeCode),
            },
            method: 'purchaseImage',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, silent);
    },

    'setCreditAccount' : function(credits,password, silent) {
        data = {
            args : {
                credits : JSON.stringify(credits),
                password : JSON.stringify(password),
            },
            method: 'setCreditAccount',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.BookingEngine = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BookingEngine.prototype = {
    'changeBookingItemOnBooking' : function(multilevelname, booking,item, silent) {
        data = {
            args : {
                booking : JSON.stringify(booking),
                item : JSON.stringify(item),
            },
            method: 'changeBookingItemOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'changeDatesOnBooking' : function(multilevelname, bookingId,start,end, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'changeDatesOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'changeTypeOnBooking' : function(multilevelname, bookingId,itemTypeId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                itemTypeId : JSON.stringify(itemTypeId),
            },
            method: 'changeTypeOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'checkConsistency' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'checkConsistency',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'createABookingItemType' : function(multilevelname, name, silent) {
        data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'createABookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'deleteBookingItem' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'deleteBookingItemType' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'deleteOpeningHours' : function(multilevelname, repeaterId, silent) {
        data = {
            args : {
                repeaterId : JSON.stringify(repeaterId),
            },
            method: 'deleteOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getAllAvailbleItems' : function(multilevelname, start,end, silent) {
        data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllAvailbleItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getAllBookings' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getAllBookingsByBookingItem' : function(multilevelname, bookingItemId, silent) {
        data = {
            args : {
                bookingItemId : JSON.stringify(bookingItemId),
            },
            method: 'getAllBookingsByBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getAvailbleItems' : function(multilevelname, typeId,start,end, silent) {
        data = {
            args : {
                typeId : JSON.stringify(typeId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAvailbleItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getBookingItem' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getBookingItemType' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getBookingItemTypes' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getBookingItemTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getBookingItems' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getBookingItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getConfig' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getDefaultRegistrationRules' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getDefaultRegistrationRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getNumberOfAvailable' : function(multilevelname, itemType,start,end, silent) {
        data = {
            args : {
                itemType : JSON.stringify(itemType),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getNumberOfAvailable',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'getOpeningHours' : function(multilevelname, itemId, silent) {
        data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'getOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'saveBookingItem' : function(multilevelname, item, silent) {
        data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'saveBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'saveDefaultRegistrationRules' : function(multilevelname, rules, silent) {
        data = {
            args : {
                rules : JSON.stringify(rules),
            },
            method: 'saveDefaultRegistrationRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'saveOpeningHours' : function(multilevelname, time,itemId, silent) {
        data = {
            args : {
                time : JSON.stringify(time),
                itemId : JSON.stringify(itemId),
            },
            method: 'saveOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'setConfirmationRequired' : function(multilevelname, confirmationRequired, silent) {
        data = {
            args : {
                confirmationRequired : JSON.stringify(confirmationRequired),
            },
            method: 'setConfirmationRequired',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

    'updateBookingItemType' : function(multilevelname, type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'updateBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.BrainTreeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BrainTreeManager.prototype = {
    'getClientToken' : function(silent) {
        data = {
            args : {
            },
            method: 'getClientToken',
            interfaceName: 'core.braintree.IBrainTreeManager',
        };
        return this.communication.send(data, silent);
    },

    'pay' : function(paymentMethodNonce,orderId, silent) {
        data = {
            args : {
                paymentMethodNonce : JSON.stringify(paymentMethodNonce),
                orderId : JSON.stringify(orderId),
            },
            method: 'pay',
            interfaceName: 'core.braintree.IBrainTreeManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.CalendarManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CalendarManager.prototype = {
    'addEvent' : function(event, silent) {
        data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'addEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'addUserSilentlyToEvent' : function(eventId,userId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'addUserSilentlyToEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'addUserToEvent' : function(userId,eventId,password,username,source, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'addUserToPageEvent' : function(userId,bookingAppId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                bookingAppId : JSON.stringify(bookingAppId),
            },
            method: 'addUserToPageEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'applyFilter' : function(filters, silent) {
        data = {
            args : {
                filters : JSON.stringify(filters),
            },
            method: 'applyFilter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'confirmEntry' : function(entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'confirmEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'createEntry' : function(year,month,day, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                day : JSON.stringify(day),
            },
            method: 'createEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteEntry' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteLocation' : function(locationId, silent) {
        data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'deleteLocation',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getActiveFilters' : function(silent) {
        data = {
            args : {
            },
            method: 'getActiveFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllEventsConnectedToPage' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getAllEventsConnectedToPage',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllLocations' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllLocations',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEntries' : function(year,month,day,filters, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                day : JSON.stringify(day),
                filters : JSON.stringify(filters),
            },
            method: 'getEntries',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEntriesByUserId' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getEntriesByUserId',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEntry' : function(entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'getEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEvent' : function(eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventPartitipatedData' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEventPartitipatedData',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEvents' : function(silent) {
        data = {
            args : {
            },
            method: 'getEvents',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventsGroupedByPageId' : function(silent) {
        data = {
            args : {
            },
            method: 'getEventsGroupedByPageId',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getFilters' : function(silent) {
        data = {
            args : {
            },
            method: 'getFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getHistory' : function(eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getHistory',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getMonth' : function(year,month,includeExtraEvents, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                includeExtraEvents : JSON.stringify(includeExtraEvents),
            },
            method: 'getMonth',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getMonths' : function(silent) {
        data = {
            args : {
            },
            method: 'getMonths',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getMonthsAfter' : function(year,month, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getMonthsAfter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'getSignature' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSignature',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'placeOrder' : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'placeOrder',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'removeUserFromEvent' : function(userId,eventId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'removeUserFromEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'saveEntry' : function(entry, silent) {
        data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'saveLocation' : function(location, silent) {
        data = {
            args : {
                location : JSON.stringify(location),
            },
            method: 'saveLocation',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'sendReminderToUser' : function(byEmail,bySMS,users,text,subject,eventId,attachment,sendReminderToUser, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'setEventPartitipatedData' : function(eventData, silent) {
        data = {
            args : {
                eventData : JSON.stringify(eventData),
            },
            method: 'setEventPartitipatedData',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'setSignature' : function(userid,signature, silent) {
        data = {
            args : {
                userid : JSON.stringify(userid),
                signature : JSON.stringify(signature),
            },
            method: 'setSignature',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'transferFromWaitingList' : function(entryId,userId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
                userId : JSON.stringify(userId),
            },
            method: 'transferFromWaitingList',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    'transferUser' : function(fromEventId,toEventId,userId, silent) {
        data = {
            args : {
                fromEventId : JSON.stringify(fromEventId),
                toEventId : JSON.stringify(toEventId),
                userId : JSON.stringify(userId),
            },
            method: 'transferUser',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.CartManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CartManager.prototype = {
    'addCoupon' : function(coupon, silent) {
        data = {
            args : {
                coupon : JSON.stringify(coupon),
            },
            method: 'addCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'addMetaDataToProduct' : function(cartItemId,metaData, silent) {
        data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                metaData : JSON.stringify(metaData),
            },
            method: 'addMetaDataToProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'addProduct' : function(productId,count,variations, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
                variations : JSON.stringify(variations),
            },
            method: 'addProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'addProductItem' : function(productId,count, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'addProductItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'applyCouponToCurrentCart' : function(code, silent) {
        data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'applyCouponToCurrentCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'calculateTotalCost' : function(cart, silent) {
        data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'calculateTotalCount' : function(cart, silent) {
        data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'clear' : function(silent) {
        data = {
            args : {
            },
            method: 'clear',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'getCart' : function(silent) {
        data = {
            args : {
            },
            method: 'getCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'getCartTotalAmount' : function(silent) {
        data = {
            args : {
            },
            method: 'getCartTotalAmount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'getCoupons' : function(silent) {
        data = {
            args : {
            },
            method: 'getCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'getShippingCost' : function(silent) {
        data = {
            args : {
            },
            method: 'getShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'getShippingPriceBasis' : function(silent) {
        data = {
            args : {
            },
            method: 'getShippingPriceBasis',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'getTaxes' : function(silent) {
        data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'removeAllCoupons' : function(silent) {
        data = {
            args : {
            },
            method: 'removeAllCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'removeCoupon' : function(code, silent) {
        data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'removeCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'removeProduct' : function(cartItemId, silent) {
        data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
            },
            method: 'removeProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'setAddress' : function(address, silent) {
        data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'setAddress',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'setReference' : function(reference, silent) {
        data = {
            args : {
                reference : JSON.stringify(reference),
            },
            method: 'setReference',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'setShippingCost' : function(shippingCost, silent) {
        data = {
            args : {
                shippingCost : JSON.stringify(shippingCost),
            },
            method: 'setShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    'updateProductCount' : function(cartItemId,count, silent) {
        data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                count : JSON.stringify(count),
            },
            method: 'updateProductCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.CarTuningManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CarTuningManager.prototype = {
    'getCarTuningData' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCarTuningData',
            interfaceName: 'core.cartuning.ICarTuningManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCarTuningData' : function(carList, silent) {
        data = {
            args : {
                carList : JSON.stringify(carList),
            },
            method: 'saveCarTuningData',
            interfaceName: 'core.cartuning.ICarTuningManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.CertegoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CertegoManager.prototype = {
    'deleteSystem' : function(systemId, silent) {
        data = {
            args : {
                systemId : JSON.stringify(systemId),
            },
            method: 'deleteSystem',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrders' : function(silent) {
        data = {
            args : {
            },
            method: 'getOrders',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

    'getSystems' : function(silent) {
        data = {
            args : {
            },
            method: 'getSystems',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

    'getSystemsForGroup' : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'getSystemsForGroup',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

    'saveOrder' : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

    'saveSystem' : function(system, silent) {
        data = {
            args : {
                system : JSON.stringify(system),
            },
            method: 'saveSystem',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

    'search' : function(searchWord, silent) {
        data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'search',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.ChatManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ChatManager.prototype = {
    'closeChat' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'closeChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    'getChatters' : function(silent) {
        data = {
            args : {
            },
            method: 'getChatters',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    'getMessages' : function(silent) {
        data = {
            args : {
            },
            method: 'getMessages',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    'pingMobileChat' : function(chatterid, silent) {
        data = {
            args : {
                chatterid : JSON.stringify(chatterid),
            },
            method: 'pingMobileChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    'replyToChat' : function(id,message, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                message : JSON.stringify(message),
            },
            method: 'replyToChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    'sendMessage' : function(message, silent) {
        data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessage',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.EventBookingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.EventBookingManager.prototype = {
    'addExternalCertificate' : function(multilevelname, userId,fileId,eventId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                fileId : JSON.stringify(fileId),
                eventId : JSON.stringify(eventId),
            },
            method: 'addExternalCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'addLocationFilter' : function(multilevelname, locationId, silent) {
        data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'addLocationFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'addUserComment' : function(multilevelname, userId,eventId,comment, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
                comment : JSON.stringify(comment),
            },
            method: 'addUserComment',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'addUserToEvent' : function(multilevelname, eventId,userId,silent,source, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'bookCurrentUserToEvent' : function(multilevelname, eventId,source, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
                source : JSON.stringify(source),
            },
            method: 'bookCurrentUserToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'canDownloadCertificate' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'canDownloadCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'cancelEvent' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'cancelEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'checkToSendReminders' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'checkToSendReminders',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'clearFilters' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'clearFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'createEvent' : function(multilevelname, event, silent) {
        data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'createEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteCertificate' : function(multilevelname, certificateId, silent) {
        data = {
            args : {
                certificateId : JSON.stringify(certificateId),
            },
            method: 'deleteCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteEvent' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteExternalCertificates' : function(multilevelname, userId,fileId,eventId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                fileId : JSON.stringify(fileId),
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteExternalCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteLocation' : function(multilevelname, locationId, silent) {
        data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'deleteLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteReminderTemplate' : function(multilevelname, templateId, silent) {
        data = {
            args : {
                templateId : JSON.stringify(templateId),
            },
            method: 'deleteReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllEvents' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllLocations' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getBookingItemTypes' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getBookingItemTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getBookingTypeMetaData' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingTypeMetaData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getBookingsByPageId' : function(multilevelname, pageId,showOnlyNew, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                showOnlyNew : JSON.stringify(showOnlyNew),
            },
            method: 'getBookingsByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getCertificate' : function(multilevelname, certificateId, silent) {
        data = {
            args : {
                certificateId : JSON.stringify(certificateId),
            },
            method: 'getCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getCertificates' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getEvent' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventLog' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventLog',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getEvents' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventsForUser' : function(multilevelname, userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getEventsForUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventsWhereEndDateBetween' : function(multilevelname, from,to, silent) {
        data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getEventsWhereEndDateBetween',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getExternalCertificates' : function(multilevelname, userId,eventId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'getExternalCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getFilteredLocations' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getFilteredLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getFromDateTimeFilter' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getFromDateTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getLocation' : function(multilevelname, locationId, silent) {
        data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'getLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getMyEvents' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getMyEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getReminder' : function(multilevelname, reminderId, silent) {
        data = {
            args : {
                reminderId : JSON.stringify(reminderId),
            },
            method: 'getReminder',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getReminderTemplate' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getReminderTemplates' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getReminderTemplates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getReminders' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getReminders',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getSource' : function(multilevelname, eventId,userId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getSource',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getToDateTimeFilter' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getToDateTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getUsersForEvent' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getUsersForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'getUsersForEventWaitinglist' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getUsersForEventWaitinglist',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'isUserSignedUpForEvent' : function(multilevelname, eventId,userId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'isUserSignedUpForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'markAsReady' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'markAsReady',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'markQuestBackSent' : function(multilevelname, eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'markQuestBackSent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'removeUserFromEvent' : function(multilevelname, eventId,userId,silent, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                silent : JSON.stringify(silent),
            },
            method: 'removeUserFromEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'saveBookingTypeMetaData' : function(multilevelname, bookingItemTypeMetadata, silent) {
        data = {
            args : {
                bookingItemTypeMetadata : JSON.stringify(bookingItemTypeMetadata),
            },
            method: 'saveBookingTypeMetaData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCertificate' : function(multilevelname, certificate, silent) {
        data = {
            args : {
                certificate : JSON.stringify(certificate),
            },
            method: 'saveCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'saveEvent' : function(multilevelname, event, silent) {
        data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'saveEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'saveLocation' : function(multilevelname, location, silent) {
        data = {
            args : {
                location : JSON.stringify(location),
            },
            method: 'saveLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'saveReminderTemplate' : function(multilevelname, template, silent) {
        data = {
            args : {
                template : JSON.stringify(template),
            },
            method: 'saveReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'sendReminder' : function(multilevelname, reminder, silent) {
        data = {
            args : {
                reminder : JSON.stringify(reminder),
            },
            method: 'sendReminder',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'setParticipationStatus' : function(multilevelname, eventId,userId,status, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                status : JSON.stringify(status),
            },
            method: 'setParticipationStatus',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'setTimeFilter' : function(multilevelname, from,to, silent) {
        data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'setTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'startScheduler' : function(multilevelname, scheduler, silent) {
        data = {
            args : {
                scheduler : JSON.stringify(scheduler),
            },
            method: 'startScheduler',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

    'transferUserFromWaitingToEvent' : function(multilevelname, userId,eventId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'transferUserFromWaitingToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.FileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FileManager.prototype = {
    'addFileEntry' : function(listId,entry, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
            },
            method: 'addFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteFileEntry' : function(fileId, silent) {
        data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'deleteFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, silent);
    },

    'getFile' : function(fileId, silent) {
        data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'getFile',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, silent);
    },

    'getFiles' : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getFiles',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, silent);
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
    'addImageToGallery' : function(galleryId,imageId,description,title, silent) {
        data = {
            args : {
                galleryId : JSON.stringify(galleryId),
                imageId : JSON.stringify(imageId),
                description : JSON.stringify(description),
                title : JSON.stringify(title),
            },
            method: 'addImageToGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    'createImageGallery' : function(silent) {
        data = {
            args : {
            },
            method: 'createImageGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteImage' : function(entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'deleteImage',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllImages' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getAllImages',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    'getEntry' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    'saveEntry' : function(entry, silent) {
        data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.GetShop = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShop.prototype = {
    'addUserToPartner' : function(userId,partner,password, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                partner : JSON.stringify(partner),
                password : JSON.stringify(password),
            },
            method: 'addUserToPartner',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'createWebPage' : function(webpageData, silent) {
        data = {
            args : {
                webpageData : JSON.stringify(webpageData),
            },
            method: 'createWebPage',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'findAddressForApplication' : function(uuid, silent) {
        data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForApplication',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'findAddressForUUID' : function(uuid, silent) {
        data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForUUID',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'getPartnerData' : function(partnerId,password, silent) {
        data = {
            args : {
                partnerId : JSON.stringify(partnerId),
                password : JSON.stringify(password),
            },
            method: 'getPartnerData',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'getStores' : function(code, silent) {
        data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'getStores',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'saveSmsCallback' : function(smsResponses, silent) {
        data = {
            args : {
                smsResponses : JSON.stringify(smsResponses),
            },
            method: 'saveSmsCallback',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'setApplicationList' : function(ids,partnerId,password, silent) {
        data = {
            args : {
                ids : JSON.stringify(ids),
                partnerId : JSON.stringify(partnerId),
                password : JSON.stringify(password),
            },
            method: 'setApplicationList',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    'startStoreFromStore' : function(startData, silent) {
        data = {
            args : {
                startData : JSON.stringify(startData),
            },
            method: 'startStoreFromStore',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.GetShopLockManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopLockManager.prototype = {
    'pushCode' : function(id,door,code,start,end, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                door : JSON.stringify(door),
                code : JSON.stringify(code),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'pushCode',
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.InformationScreenManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InformationScreenManager.prototype = {
    'addSlider' : function(slider,tvId, silent) {
        data = {
            args : {
                slider : JSON.stringify(slider),
                tvId : JSON.stringify(tvId),
            },
            method: 'addSlider',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteSlider' : function(sliderId,tvId, silent) {
        data = {
            args : {
                sliderId : JSON.stringify(sliderId),
                tvId : JSON.stringify(tvId),
            },
            method: 'deleteSlider',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'getHolders' : function(silent) {
        data = {
            args : {
            },
            method: 'getHolders',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'getInformationScreens' : function(silent) {
        data = {
            args : {
            },
            method: 'getInformationScreens',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'getNews' : function(silent) {
        data = {
            args : {
            },
            method: 'getNews',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'getScreen' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getScreen',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'getTypes' : function(silent) {
        data = {
            args : {
            },
            method: 'getTypes',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'registerTv' : function(customerId, silent) {
        data = {
            args : {
                customerId : JSON.stringify(customerId),
            },
            method: 'registerTv',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

    'saveTv' : function(tv, silent) {
        data = {
            args : {
                tv : JSON.stringify(tv),
            },
            method: 'saveTv',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.ListManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ListManager.prototype = {
    'addEntry' : function(listId,entry,parentPageId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
                parentPageId : JSON.stringify(parentPageId),
            },
            method: 'addEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'clearList' : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'clearList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'combineList' : function(toListId,newListId, silent) {
        data = {
            args : {
                toListId : JSON.stringify(toListId),
                newListId : JSON.stringify(newListId),
            },
            method: 'combineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'createListId' : function(silent) {
        data = {
            args : {
            },
            method: 'createListId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'createMenuList' : function(menuApplicationId, silent) {
        data = {
            args : {
                menuApplicationId : JSON.stringify(menuApplicationId),
            },
            method: 'createMenuList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteEntry' : function(id,listId, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                listId : JSON.stringify(listId),
            },
            method: 'deleteEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteMenu' : function(appId,listId, silent) {
        data = {
            args : {
                appId : JSON.stringify(appId),
                listId : JSON.stringify(listId),
            },
            method: 'deleteMenu',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllListsByType' : function(type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getAllListsByType',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getCombinedLists' : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getCombinedLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getJsTree' : function(name, silent) {
        data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getJsTree',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getList' : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getListEntry' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getListEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getLists' : function(silent) {
        data = {
            args : {
            },
            method: 'getLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getMenues' : function(applicationInstanceId, silent) {
        data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getMenues',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'getPageIdByName' : function(name, silent) {
        data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'orderEntry' : function(id,after,parentId, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                after : JSON.stringify(after),
                parentId : JSON.stringify(parentId),
            },
            method: 'orderEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'saveJsTree' : function(name,list, silent) {
        data = {
            args : {
                name : JSON.stringify(name),
                list : JSON.stringify(list),
            },
            method: 'saveJsTree',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'saveMenu' : function(appId,listId,entries,name, silent) {
        data = {
            args : {
                appId : JSON.stringify(appId),
                listId : JSON.stringify(listId),
                entries : JSON.stringify(entries),
                name : JSON.stringify(name),
            },
            method: 'saveMenu',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'setEntries' : function(listId,entries, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
                entries : JSON.stringify(entries),
            },
            method: 'setEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'translateEntries' : function(entryIds, silent) {
        data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'unCombineList' : function(fromListId,toRemoveId, silent) {
        data = {
            args : {
                fromListId : JSON.stringify(fromListId),
                toRemoveId : JSON.stringify(toRemoveId),
            },
            method: 'unCombineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    'updateEntry' : function(entry, silent) {
        data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'updateEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.MecaApi = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MecaApi.prototype = {
    'addVehicle' : function(phoneNumber,vehicle, silent) {
        data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                vehicle : JSON.stringify(vehicle),
            },
            method: 'addVehicle',
            interfaceName: 'core.mecamanager.IMecaApi',
        };
        return this.communication.send(data, silent);
    },

    'changePassword' : function(phoneNumber,oldPassword,newPassword1,newPassword2, silent) {
        data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                oldPassword : JSON.stringify(oldPassword),
                newPassword1 : JSON.stringify(newPassword1),
                newPassword2 : JSON.stringify(newPassword2),
            },
            method: 'changePassword',
            interfaceName: 'core.mecamanager.IMecaApi',
        };
        return this.communication.send(data, silent);
    },

    'createAccount' : function(phoneNumber, silent) {
        data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
            },
            method: 'createAccount',
            interfaceName: 'core.mecamanager.IMecaApi',
        };
        return this.communication.send(data, silent);
    },

    'login' : function(phoneNumber,password, silent) {
        data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                password : JSON.stringify(password),
            },
            method: 'login',
            interfaceName: 'core.mecamanager.IMecaApi',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.MessageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MessageManager.prototype = {
    'collectEmail' : function(email, silent) {
        data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'collectEmail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, silent);
    },

    'getCollectedEmails' : function(silent) {
        data = {
            args : {
            },
            method: 'getCollectedEmails',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, silent);
    },

    'getMailMessage' : function(mailMessageId, silent) {
        data = {
            args : {
                mailMessageId : JSON.stringify(mailMessageId),
            },
            method: 'getMailMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, silent);
    },

    'getSmsCount' : function(year,month, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getSmsCount',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, silent);
    },

    'getSmsMessage' : function(smsMessageId, silent) {
        data = {
            args : {
                smsMessageId : JSON.stringify(smsMessageId),
            },
            method: 'getSmsMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, silent);
    },

    'sendMail' : function(to,toName,subject,content,from,fromName, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'sendMailWithAttachments' : function(to,toName,subject,content,from,fromName,attachments, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.NewsLetterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsLetterManager.prototype = {
    'sendNewsLetter' : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'sendNewsLetter',
            interfaceName: 'core.messagemanager.INewsLetterManager',
        };
        return this.communication.send(data, silent);
    },

    'sendNewsLetterPreview' : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'sendNewsLetterPreview',
            interfaceName: 'core.messagemanager.INewsLetterManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.MobileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MobileManager.prototype = {
    'clearBadged' : function(tokenId, silent) {
        data = {
            args : {
                tokenId : JSON.stringify(tokenId),
            },
            method: 'clearBadged',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

    'registerToken' : function(token, silent) {
        data = {
            args : {
                token : JSON.stringify(token),
            },
            method: 'registerToken',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

    'sendMessageToAll' : function(message, silent) {
        data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAll',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

    'sendMessageToAllTestUnits' : function(message, silent) {
        data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAllTestUnits',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.OrderManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.OrderManager.prototype = {
    'addProductToOrder' : function(orderId,productId,count, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'addProductToOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'changeOrderStatus' : function(id,status, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                status : JSON.stringify(status),
            },
            method: 'changeOrderStatus',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'changeOrderType' : function(orderId,paymentTypeId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
                paymentTypeId : JSON.stringify(paymentTypeId),
            },
            method: 'changeOrderType',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'checkForOrdersToAutoPay' : function(silent) {
        data = {
            args : {
            },
            method: 'checkForOrdersToAutoPay',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'checkForOrdersToCapture' : function(silent) {
        data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'createOrder' : function(address, silent) {
        data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'createOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'createOrderByCustomerReference' : function(referenceKey, silent) {
        data = {
            args : {
                referenceKey : JSON.stringify(referenceKey),
            },
            method: 'createOrderByCustomerReference',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'createOrderForUser' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'createOrderForUser',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'creditOrder' : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'creditOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllOrdersForUser' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getAllOrdersForUser',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllOrdersOnProduct' : function(productId, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'getAllOrdersOnProduct',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getMostSoldProducts' : function(numberOfProducts, silent) {
        data = {
            args : {
                numberOfProducts : JSON.stringify(numberOfProducts),
            },
            method: 'getMostSoldProducts',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrder' : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrderByReference' : function(referenceId, silent) {
        data = {
            args : {
                referenceId : JSON.stringify(referenceId),
            },
            method: 'getOrderByReference',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrderByincrementOrderId' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getOrderByincrementOrderId',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrderSecure' : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrderSecure',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrders' : function(orderIds,page,pageSize, silent) {
        data = {
            args : {
                orderIds : JSON.stringify(orderIds),
                page : JSON.stringify(page),
                pageSize : JSON.stringify(pageSize),
            },
            method: 'getOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrdersFiltered' : function(filterOptions, silent) {
        data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getOrdersFiltered',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrdersFromPeriode' : function(start,end,statistics, silent) {
        data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                statistics : JSON.stringify(statistics),
            },
            method: 'getOrdersFromPeriode',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrdersNotTransferredToAccountingSystem' : function(silent) {
        data = {
            args : {
            },
            method: 'getOrdersNotTransferredToAccountingSystem',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrdersToCapture' : function(silent) {
        data = {
            args : {
            },
            method: 'getOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getPageCount' : function(pageSize,searchWord, silent) {
        data = {
            args : {
                pageSize : JSON.stringify(pageSize),
                searchWord : JSON.stringify(searchWord),
            },
            method: 'getPageCount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getSalesNumber' : function(year, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getSalesNumber',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getSalesStatistics' : function(startDate,endDate,type, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                endDate : JSON.stringify(endDate),
                type : JSON.stringify(type),
            },
            method: 'getSalesStatistics',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getTaxes' : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTaxes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getTotalAmount' : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTotalAmount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getTotalForOrderById' : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getTotalForOrderById',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'getTotalSalesAmount' : function(year,month,week,day,type, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'logTransactionEntry' : function(orderId,entry, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
                entry : JSON.stringify(entry),
            },
            method: 'logTransactionEntry',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'saveOrder' : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'searchForOrders' : function(searchWord,page,pageSize, silent) {
        data = {
            args : {
                searchWord : JSON.stringify(searchWord),
                page : JSON.stringify(page),
                pageSize : JSON.stringify(pageSize),
            },
            method: 'searchForOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'setOrderStatus' : function(password,orderId,currency,price,status, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'updateCountForOrderLine' : function(cartItemId,orderId,count, silent) {
        data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                orderId : JSON.stringify(orderId),
                count : JSON.stringify(count),
            },
            method: 'updateCountForOrderLine',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    'updatePriceForOrderLine' : function(cartItemId,orderId,price, silent) {
        data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                orderId : JSON.stringify(orderId),
                price : JSON.stringify(price),
            },
            method: 'updatePriceForOrderLine',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.PageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PageManager.prototype = {
    'addApplication' : function(applicationId,pageCellId,pageId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                pageCellId : JSON.stringify(pageCellId),
                pageId : JSON.stringify(pageId),
            },
            method: 'addApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'addExistingApplicationToPageArea' : function(pageId,appId,area, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                appId : JSON.stringify(appId),
                area : JSON.stringify(area),
            },
            method: 'addExistingApplicationToPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'addLayoutCell' : function(pageId,incell,beforecell,direction,area, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'changePageUserLevel' : function(pageId,userLevel, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                userLevel : JSON.stringify(userLevel),
            },
            method: 'changePageUserLevel',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'clearPage' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'clearPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'clearPageArea' : function(pageId,pageArea, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'clearPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'createHeaderFooter' : function(type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'createHeaderFooter',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'createModal' : function(modalName, silent) {
        data = {
            args : {
                modalName : JSON.stringify(modalName),
            },
            method: 'createModal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'createNewRow' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'createNewRow',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'createPage' : function(silent) {
        data = {
            args : {
            },
            method: 'createPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteApplication' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'deletePage' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deletePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'dropCell' : function(pageId,cellId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'dropCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'flattenMobileLayout' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'flattenMobileLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getApplications' : function(silent) {
        data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getApplicationsBasedOnApplicationSettingsId' : function(appSettingsId, silent) {
        data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
            },
            method: 'getApplicationsBasedOnApplicationSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getApplicationsByPageAreaAndSettingsId' : function(appSettingsId,pageArea, silent) {
        data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'getApplicationsByPageAreaAndSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getApplicationsByType' : function(type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getApplicationsByType',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getApplicationsForPage' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getApplicationsForPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getCell' : function(pageId,cellId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'getCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getLeftSideBarNames' : function(silent) {
        data = {
            args : {
            },
            method: 'getLeftSideBarNames',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getMobileBody' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getMobileBody',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getMobileLink' : function(silent) {
        data = {
            args : {
            },
            method: 'getMobileLink',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getModalNames' : function(silent) {
        data = {
            args : {
            },
            method: 'getModalNames',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getPage' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getPagesForApplication' : function(appId, silent) {
        data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'getPagesForApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getSecuredSettings' : function(applicationInstanceId, silent) {
        data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getSecuredSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'getSecuredSettingsInternal' : function(appName, silent) {
        data = {
            args : {
                appName : JSON.stringify(appName),
            },
            method: 'getSecuredSettingsInternal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'linkPageCell' : function(pageId,cellId,link, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                link : JSON.stringify(link),
            },
            method: 'linkPageCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'moveCell' : function(pageId,cellId,up, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                up : JSON.stringify(up),
            },
            method: 'moveCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'moveCellMobile' : function(pageId,cellId,moveUp, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                moveUp : JSON.stringify(moveUp),
            },
            method: 'moveCellMobile',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'removeAppFromCell' : function(pageId,cellid, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellid : JSON.stringify(cellid),
            },
            method: 'removeAppFromCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'resetMobileLayout' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'resetMobileLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'restoreLayout' : function(pageId,fromTime, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromTime : JSON.stringify(fromTime),
            },
            method: 'restoreLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'saveApplicationConfiguration' : function(config, silent) {
        data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveApplicationConfiguration',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCell' : function(pageId,cell, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cell : JSON.stringify(cell),
            },
            method: 'saveCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCellPosition' : function(pageId,cellId,data, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                data : JSON.stringify(data),
            },
            method: 'saveCellPosition',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'saveMobileLink' : function(link, silent) {
        data = {
            args : {
                link : JSON.stringify(link),
            },
            method: 'saveMobileLink',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'savePage' : function(page, silent) {
        data = {
            args : {
                page : JSON.stringify(page),
            },
            method: 'savePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'savePageCellSettings' : function(pageId,cellId,settings, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                settings : JSON.stringify(settings),
            },
            method: 'savePageCellSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'setCarouselConfig' : function(pageId,cellId,config, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                config : JSON.stringify(config),
            },
            method: 'setCarouselConfig',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'setCellMode' : function(pageId,cellId,mode, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                mode : JSON.stringify(mode),
            },
            method: 'setCellMode',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'setCellName' : function(pageId,cellId,cellName, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                cellName : JSON.stringify(cellName),
            },
            method: 'setCellName',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'setPageDescription' : function(pageId,description, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                description : JSON.stringify(description),
            },
            method: 'setPageDescription',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'setParentPage' : function(pageId,parentPageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                parentPageId : JSON.stringify(parentPageId),
            },
            method: 'setParentPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'setStylesOnCell' : function(pageId,cellId,styles,innerStyles,width, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'setWidth' : function(pageId,cellId,outerWidth,outerWidthWithMargins, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                outerWidth : JSON.stringify(outerWidth),
                outerWidthWithMargins : JSON.stringify(outerWidthWithMargins),
            },
            method: 'setWidth',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'swapAppWithCell' : function(pageId,fromCellId,toCellId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromCellId : JSON.stringify(fromCellId),
                toCellId : JSON.stringify(toCellId),
            },
            method: 'swapAppWithCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleHiddenOnMobile' : function(pageId,cellId,hide, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                hide : JSON.stringify(hide),
            },
            method: 'toggleHiddenOnMobile',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleLeftSideBar' : function(pageId,columnName, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                columnName : JSON.stringify(columnName),
            },
            method: 'toggleLeftSideBar',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'togglePinArea' : function(pageId,cellId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'togglePinArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'translatePages' : function(pages, silent) {
        data = {
            args : {
                pages : JSON.stringify(pages),
            },
            method: 'translatePages',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    'updateCellLayout' : function(layout,pageId,cellId, silent) {
        data = {
            args : {
                layout : JSON.stringify(layout),
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'updateCellLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.InvoiceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InvoiceManager.prototype = {
    'createInvoice' : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'createInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, silent);
    },

    'getBase64EncodedInvoice' : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getBase64EncodedInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.LasGruppenPDFGenerator = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LasGruppenPDFGenerator.prototype = {
    'generatePdf' : function(silent) {
        data = {
            args : {
            },
            method: 'generatePdf',
            interfaceName: 'core.pdf.ILasGruppenPDFGenerator',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.PkkControlManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PkkControlManager.prototype = {
    'getPkkControlData' : function(licensePlate, silent) {
        data = {
            args : {
                licensePlate : JSON.stringify(licensePlate),
            },
            method: 'getPkkControlData',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, silent);
    },

    'getPkkControls' : function(silent) {
        data = {
            args : {
            },
            method: 'getPkkControls',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, silent);
    },

    'registerPkkControl' : function(data, silent) {
        data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'registerPkkControl',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, silent);
    },

    'removePkkControl' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'removePkkControl',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.PmsEventManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsEventManager.prototype = {
    'createEvent' : function(multilevelname, id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'createEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteEntry' : function(multilevelname, entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'deleteEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, silent);
    },

    'getEntry' : function(multilevelname, entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'getEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, silent);
    },

    'getEntryShort' : function(multilevelname, shortId, silent) {
        data = {
            args : {
                shortId : JSON.stringify(shortId),
            },
            method: 'getEntryShort',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventEntries' : function(multilevelname, filter, silent) {
        data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getEventEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, silent);
    },

    'saveEntry' : function(multilevelname, entry, silent) {
        data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.PmsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsManager.prototype = {
    'addAddonToCurrentBooking' : function(multilevelname, itemtypeId, silent) {
        data = {
            args : {
                itemtypeId : JSON.stringify(itemtypeId),
            },
            method: 'addAddonToCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'addBookingItem' : function(multilevelname, bookingId,item,start,end, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'addBookingItemType' : function(multilevelname, bookingId,item,start,end, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                item : JSON.stringify(item),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'addBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'addComment' : function(multilevelname, bookingId,comment, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                comment : JSON.stringify(comment),
            },
            method: 'addComment',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'addRepeatingData' : function(multilevelname, data, silent) {
        data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'addRepeatingData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'changeDates' : function(multilevelname, roomId,bookingId,start,end, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'checkDoorStatusControl' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'checkDoorStatusControl',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'completeCurrentBooking' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'completeCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'confirmBooking' : function(multilevelname, bookingId,message, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'confirmBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'createOrder' : function(multilevelname, bookingId,filter, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'createOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'createPrepaymentOrder' : function(multilevelname, bookingId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'createPrepaymentOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteBooking' : function(multilevelname, bookingId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'doNotification' : function(multilevelname, key,bookingId, silent) {
        data = {
            args : {
                key : JSON.stringify(key),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'doNotification',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllAdditionalInformationOnRooms' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllAdditionalInformationOnRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllBookings' : function(multilevelname, state, silent) {
        data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllBookingsForLoggedOnUser' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getAllBookingsForLoggedOnUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllBookingsUnsecure' : function(multilevelname, state, silent) {
        data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getAllBookingsUnsecure',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllRoomTypes' : function(multilevelname, start,end, silent) {
        data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllRoomTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getAvailabilityForType' : function(multilevelname, bookingItemId,startTime,endTime,intervalInMinutes, silent) {
        data = {
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
        return this.communication.send(data, silent);
    },

    'getBooking' : function(multilevelname, bookingId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getBookingFromRoom' : function(multilevelname, pmsBookingRoomId, silent) {
        data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getBookingFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getConfiguration' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getContract' : function(multilevelname, bookingId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getContract',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getCurrenctContract' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getCurrenctContract',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getCurrentBooking' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getDefaultDateRange' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'getDefaultDateRange',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getDefaultMessage' : function(multilevelname, bookingId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getDefaultMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getIntervalAvailability' : function(multilevelname, filter, silent) {
        data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getIntervalAvailability',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getLogEntries' : function(multilevelname, filter, silent) {
        data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getLogEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getNumberOfAvailable' : function(multilevelname, itemType,start,end, silent) {
        data = {
            args : {
                itemType : JSON.stringify(itemType),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getNumberOfAvailable',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getPrices' : function(multilevelname, start,end, silent) {
        data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getPrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getRoomForItem' : function(multilevelname, itemId,atTime, silent) {
        data = {
            args : {
                itemId : JSON.stringify(itemId),
                atTime : JSON.stringify(atTime),
            },
            method: 'getRoomForItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getRoomsNeedingCheckoutCleaning' : function(multilevelname, day, silent) {
        data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingCheckoutCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getRoomsNeedingIntervalCleaning' : function(multilevelname, day, silent) {
        data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingIntervalCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getSimpleRooms' : function(multilevelname, filter, silent) {
        data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getSimpleRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'getStatistics' : function(multilevelname, filter, silent) {
        data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'handleDoorControl' : function(multilevelname, doorId,accessLogs, silent) {
        data = {
            args : {
                doorId : JSON.stringify(doorId),
                accessLogs : JSON.stringify(accessLogs),
            },
            method: 'handleDoorControl',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'initBookingRules' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'initBookingRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'isClean' : function(multilevelname, itemId, silent) {
        data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'isClean',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'logEntry' : function(multilevelname, logText,bookingId,itemId, silent) {
        data = {
            args : {
                logText : JSON.stringify(logText),
                bookingId : JSON.stringify(bookingId),
                itemId : JSON.stringify(itemId),
            },
            method: 'logEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'markRoomAsCleaned' : function(multilevelname, itemId, silent) {
        data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'markRoomAsCleaned',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'processor' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'processor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'removeFromBooking' : function(multilevelname, bookingId,roomId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                roomId : JSON.stringify(roomId),
            },
            method: 'removeFromBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'removeFromCurrentBooking' : function(multilevelname, roomId, silent) {
        data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'removeFromCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'returnedKey' : function(multilevelname, roomId, silent) {
        data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'returnedKey',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'saveBooking' : function(multilevelname, booking, silent) {
        data = {
            args : {
                booking : JSON.stringify(booking),
            },
            method: 'saveBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'saveConfiguration' : function(multilevelname, notifications, silent) {
        data = {
            args : {
                notifications : JSON.stringify(notifications),
            },
            method: 'saveConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'sendMessageToAllTodaysGuests' : function(multilevelname, message, silent) {
        data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAllTodaysGuests',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'setBooking' : function(multilevelname, addons, silent) {
        data = {
            args : {
                addons : JSON.stringify(addons),
            },
            method: 'setBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'setBookingItem' : function(multilevelname, roomId,bookingId,itemId, silent) {
        data = {
            args : {
                roomId : JSON.stringify(roomId),
                bookingId : JSON.stringify(bookingId),
                itemId : JSON.stringify(itemId),
            },
            method: 'setBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'setGuestOnRoom' : function(multilevelname, guests,bookingId,roomId, silent) {
        data = {
            args : {
                guests : JSON.stringify(guests),
                bookingId : JSON.stringify(bookingId),
                roomId : JSON.stringify(roomId),
            },
            method: 'setGuestOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'setNewCleaningIntervalOnRoom' : function(multilevelname, roomId,interval, silent) {
        data = {
            args : {
                roomId : JSON.stringify(roomId),
                interval : JSON.stringify(interval),
            },
            method: 'setNewCleaningIntervalOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'setNewRoomType' : function(multilevelname, roomId,bookingId,newType, silent) {
        data = {
            args : {
                roomId : JSON.stringify(roomId),
                bookingId : JSON.stringify(bookingId),
                newType : JSON.stringify(newType),
            },
            method: 'setNewRoomType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'setPrices' : function(multilevelname, prices, silent) {
        data = {
            args : {
                prices : JSON.stringify(prices),
            },
            method: 'setPrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'startBooking' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'startBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleAddon' : function(multilevelname, itemId, silent) {
        data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'toggleAddon',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'unConfirmBooking' : function(multilevelname, bookingId,message, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'unConfirmBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'undeleteBooking' : function(multilevelname, bookingId, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'undeleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'updateRepeatingDataForBooking' : function(multilevelname, data,bookingId, silent) {
        data = {
            args : {
                data : JSON.stringify(data),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'updateRepeatingDataForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

    'updateRoomByUser' : function(multilevelname, bookingId,room, silent) {
        data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                room : JSON.stringify(room),
            },
            method: 'updateRoomByUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.PmsManagerProcessor = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsManagerProcessor.prototype = {
    'doProcessing' : function(multilevelname, silent) {
        data = {
            args : {
            },
            method: 'doProcessing',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManagerProcessor',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.ProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ProductManager.prototype = {
    'changeStockQuantity' : function(productId,count, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'changeStockQuantity',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'createProduct' : function(silent) {
        data = {
            args : {
            },
            method: 'createProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'createProductList' : function(listName, silent) {
        data = {
            args : {
                listName : JSON.stringify(listName),
            },
            method: 'createProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteCategory' : function(categoryId, silent) {
        data = {
            args : {
                categoryId : JSON.stringify(categoryId),
            },
            method: 'deleteCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteProductList' : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'deleteProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllCategories' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllCategories',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllProducts' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllProductsLight' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllProductsLight',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getCategory' : function(categoryId, silent) {
        data = {
            args : {
                categoryId : JSON.stringify(categoryId),
            },
            method: 'getCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getLatestProducts' : function(count, silent) {
        data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getPageIdByName' : function(productName, silent) {
        data = {
            args : {
                productName : JSON.stringify(productName),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getPrice' : function(productId,variations, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                variations : JSON.stringify(variations),
            },
            method: 'getPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProduct' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductByPage' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProductByPage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductFromApplicationId' : function(app_uuid, silent) {
        data = {
            args : {
                app_uuid : JSON.stringify(app_uuid),
            },
            method: 'getProductFromApplicationId',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductList' : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductLists' : function(silent) {
        data = {
            args : {
            },
            method: 'getProductLists',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProducts' : function(productCriteria, silent) {
        data = {
            args : {
                productCriteria : JSON.stringify(productCriteria),
            },
            method: 'getProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getRandomProducts' : function(fetchSize,ignoreProductId, silent) {
        data = {
            args : {
                fetchSize : JSON.stringify(fetchSize),
                ignoreProductId : JSON.stringify(ignoreProductId),
            },
            method: 'getRandomProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getTaxes' : function(silent) {
        data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'removeProduct' : function(productId, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'removeProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCategory' : function(categories, silent) {
        data = {
            args : {
                categories : JSON.stringify(categories),
            },
            method: 'saveCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'saveProduct' : function(product, silent) {
        data = {
            args : {
                product : JSON.stringify(product),
            },
            method: 'saveProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'saveProductList' : function(productList, silent) {
        data = {
            args : {
                productList : JSON.stringify(productList),
            },
            method: 'saveProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'search' : function(searchWord,pageSize,page, silent) {
        data = {
            args : {
                searchWord : JSON.stringify(searchWord),
                pageSize : JSON.stringify(pageSize),
                page : JSON.stringify(page),
            },
            method: 'search',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setMainImage' : function(productId,imageId, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                imageId : JSON.stringify(imageId),
            },
            method: 'setMainImage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setProductDynamicPrice' : function(productId,count, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'setProductDynamicPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setTaxes' : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'setTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    'translateEntries' : function(entryIds, silent) {
        data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.PullServerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PullServerManager.prototype = {
    'getPullMessages' : function(keyId,storeId, silent) {
        data = {
            args : {
                keyId : JSON.stringify(keyId),
                storeId : JSON.stringify(storeId),
            },
            method: 'getPullMessages',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, silent);
    },

    'markMessageAsReceived' : function(messageId,storeId, silent) {
        data = {
            args : {
                messageId : JSON.stringify(messageId),
                storeId : JSON.stringify(storeId),
            },
            method: 'markMessageAsReceived',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, silent);
    },

    'savePullMessage' : function(pullMessage, silent) {
        data = {
            args : {
                pullMessage : JSON.stringify(pullMessage),
            },
            method: 'savePullMessage',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.QuestBackManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.QuestBackManager.prototype = {
    'answerQuestions' : function(testId,applicationId,pageId,answers, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
                applicationId : JSON.stringify(applicationId),
                pageId : JSON.stringify(pageId),
                answers : JSON.stringify(answers),
            },
            method: 'answerQuestions',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'assignUserToTest' : function(testId,userId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
            },
            method: 'assignUserToTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'createTemplatePageIfNotExists' : function(silent) {
        data = {
            args : {
            },
            method: 'createTemplatePageIfNotExists',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'createTest' : function(testName, silent) {
        data = {
            args : {
                testName : JSON.stringify(testName),
            },
            method: 'createTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteTest' : function(testId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'deleteTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllTests' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllTests',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getBestCategoryResultForCompany' : function(userId,catId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                catId : JSON.stringify(catId),
            },
            method: 'getBestCategoryResultForCompany',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getCategories' : function(silent) {
        data = {
            args : {
            },
            method: 'getCategories',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getNextQuestionPage' : function(testId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getNextQuestionPage',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getPageId' : function(questionId, silent) {
        data = {
            args : {
                questionId : JSON.stringify(questionId),
            },
            method: 'getPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getProgress' : function(testId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getProgress',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getQuestion' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getQuestion',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getQuestionTitle' : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getQuestionTitle',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getResultRequirement' : function(silent) {
        data = {
            args : {
            },
            method: 'getResultRequirement',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getTest' : function(testId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getTestResult' : function(testId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getTestResult',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getTestResultForUser' : function(testId,userId, silent) {
        data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
            },
            method: 'getTestResultForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getTestResults' : function(userId,testId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getTestResults',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'getTests' : function(silent) {
        data = {
            args : {
            },
            method: 'getTests',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'hasAnswered' : function(pageId,testId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                testId : JSON.stringify(testId),
            },
            method: 'hasAnswered',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'questionTreeChanged' : function(applicationId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'questionTreeChanged',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'saveQuestBackResultRequirement' : function(requirement, silent) {
        data = {
            args : {
                requirement : JSON.stringify(requirement),
            },
            method: 'saveQuestBackResultRequirement',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

    'saveTest' : function(test, silent) {
        data = {
            args : {
                test : JSON.stringify(test),
            },
            method: 'saveTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.ReportingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ReportingManager.prototype = {
    'getAllEventsFromSession' : function(startDate,stopDate,searchSessionId, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                searchSessionId : JSON.stringify(searchSessionId),
            },
            method: 'getAllEventsFromSession',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

    'getConnectedUsers' : function(startdate,stopDate,filter, silent) {
        data = {
            args : {
                startdate : JSON.stringify(startdate),
                stopDate : JSON.stringify(stopDate),
                filter : JSON.stringify(filter),
            },
            method: 'getConnectedUsers',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrdersCreated' : function(startDate,stopDate, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getOrdersCreated',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

    'getPageViews' : function(startDate,stopDate, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getPageViews',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductViewed' : function(startDate,stopDate, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getProductViewed',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

    'getReport' : function(startDate,stopDate,type, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                type : JSON.stringify(type),
            },
            method: 'getReport',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

    'getUserLoggedOn' : function(startDate,stopDate, silent) {
        data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getUserLoggedOn',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.SalesManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SalesManager.prototype = {
    'findCustomer' : function(key,type, silent) {
        data = {
            args : {
                key : JSON.stringify(key),
                type : JSON.stringify(type),
            },
            method: 'findCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'getCustomer' : function(orgId, silent) {
        data = {
            args : {
                orgId : JSON.stringify(orgId),
            },
            method: 'getCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'getEvent' : function(eventId, silent) {
        data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventsForCustomer' : function(orgId, silent) {
        data = {
            args : {
                orgId : JSON.stringify(orgId),
            },
            method: 'getEventsForCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'getEventsForDay' : function(day, silent) {
        data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getEventsForDay',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'getLatestCustomer' : function(silent) {
        data = {
            args : {
            },
            method: 'getLatestCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'getLatestEvent' : function(silent) {
        data = {
            args : {
            },
            method: 'getLatestEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCustomer' : function(customer, silent) {
        data = {
            args : {
                customer : JSON.stringify(customer),
            },
            method: 'saveCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

    'saveEvent' : function(event, silent) {
        data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'saveEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.SedoxProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SedoxProductManager.prototype = {
    'addCreditToSlave' : function(slaveId,amount, silent) {
        data = {
            args : {
                slaveId : JSON.stringify(slaveId),
                amount : JSON.stringify(amount),
            },
            method: 'addCreditToSlave',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'addFileToProduct' : function(base64EncodedFile,fileName,fileType,productId, silent) {
        data = {
            args : {
                base64EncodedFile : JSON.stringify(base64EncodedFile),
                fileName : JSON.stringify(fileName),
                fileType : JSON.stringify(fileType),
                productId : JSON.stringify(productId),
            },
            method: 'addFileToProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'addReference' : function(productId,reference, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                reference : JSON.stringify(reference),
            },
            method: 'addReference',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'addSlaveToUser' : function(masterUserId,slaveUserId, silent) {
        data = {
            args : {
                masterUserId : JSON.stringify(masterUserId),
                slaveUserId : JSON.stringify(slaveUserId),
            },
            method: 'addSlaveToUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'addUserCredit' : function(id,description,amount, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
                description : JSON.stringify(description),
                amount : JSON.stringify(amount),
            },
            method: 'addUserCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'changeDeveloperStatus' : function(userId,disabled, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                disabled : JSON.stringify(disabled),
            },
            method: 'changeDeveloperStatus',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'createSedoxProduct' : function(sedoxProduct,base64encodedOriginalFile,originalFileName,forSlaveId,origin,comment,useCredit,options, silent) {
        data = {
            args : {
                sedoxProduct : JSON.stringify(sedoxProduct),
                base64encodedOriginalFile : JSON.stringify(base64encodedOriginalFile),
                originalFileName : JSON.stringify(originalFileName),
                forSlaveId : JSON.stringify(forSlaveId),
                origin : JSON.stringify(origin),
                comment : JSON.stringify(comment),
                useCredit : JSON.stringify(useCredit),
                options : JSON.stringify(options),
            },
            method: 'createSedoxProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllUsersWithNegativeCreditLimit' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllUsersWithNegativeCreditLimit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getCurrentUserCreditHistory' : function(filterData, silent) {
        data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getCurrentUserCreditHistory',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getCurrentUserCreditHistoryCount' : function(filterData, silent) {
        data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getCurrentUserCreditHistoryCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getDevelopers' : function(silent) {
        data = {
            args : {
            },
            method: 'getDevelopers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getExtraInformationForFile' : function(productId,fileId, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
            },
            method: 'getExtraInformationForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getFileNotProcessedToDayCount' : function(daysBack, silent) {
        data = {
            args : {
                daysBack : JSON.stringify(daysBack),
            },
            method: 'getFileNotProcessedToDayCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getLatestProductsList' : function(count, silent) {
        data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProductsList',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrders' : function(filterData, silent) {
        data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOrders',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getOrdersPageCount' : function(filterData, silent) {
        data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOrdersPageCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getPriceForProduct' : function(productId,files, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'getPriceForProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductById' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProductById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductIds' : function(silent) {
        data = {
            args : {
            },
            method: 'getProductIds',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductsByDaysBack' : function(day, silent) {
        data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getProductsByDaysBack',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductsFirstUploadedByCurrentUser' : function(filterData, silent) {
        data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getProductsFirstUploadedByCurrentUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getProductsFirstUploadedByCurrentUserTotalPages' : function(filterData, silent) {
        data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getProductsFirstUploadedByCurrentUserTotalPages',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getSedoxProductByMd5Sum' : function(md5sum, silent) {
        data = {
            args : {
                md5sum : JSON.stringify(md5sum),
            },
            method: 'getSedoxProductByMd5Sum',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getSedoxUserAccount' : function(silent) {
        data = {
            args : {
            },
            method: 'getSedoxUserAccount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getSedoxUserAccountById' : function(userid, silent) {
        data = {
            args : {
                userid : JSON.stringify(userid),
            },
            method: 'getSedoxUserAccountById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getSharedProductById' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSharedProductById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getSlaves' : function(masterUserId, silent) {
        data = {
            args : {
                masterUserId : JSON.stringify(masterUserId),
            },
            method: 'getSlaves',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getStatistic' : function(silent) {
        data = {
            args : {
            },
            method: 'getStatistic',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getUploadHistory' : function(silent) {
        data = {
            args : {
            },
            method: 'getUploadHistory',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getUserFileDownloadCount' : function(silent) {
        data = {
            args : {
            },
            method: 'getUserFileDownloadCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'getUserFileUploadCount' : function(silent) {
        data = {
            args : {
            },
            method: 'getUserFileUploadCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'login' : function(emailAddress,password, silent) {
        data = {
            args : {
                emailAddress : JSON.stringify(emailAddress),
                password : JSON.stringify(password),
            },
            method: 'login',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'markAsFinished' : function(productId,finished, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                finished : JSON.stringify(finished),
            },
            method: 'markAsFinished',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'notifyForCustomer' : function(productId,extraText, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                extraText : JSON.stringify(extraText),
            },
            method: 'notifyForCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'purchaseOnlyForCustomer' : function(productId,files, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'purchaseOnlyForCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'purchaseProduct' : function(productId,files, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'purchaseProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'removeBinaryFileFromProduct' : function(productId,fileId, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
            },
            method: 'removeBinaryFileFromProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'requestSpecialFile' : function(productId,comment, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                comment : JSON.stringify(comment),
            },
            method: 'requestSpecialFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'search' : function(search, silent) {
        data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'search',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'searchForUsers' : function(searchString, silent) {
        data = {
            args : {
                searchString : JSON.stringify(searchString),
            },
            method: 'searchForUsers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'sendProductByMail' : function(productId,extraText,files, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                extraText : JSON.stringify(extraText),
                files : JSON.stringify(files),
            },
            method: 'sendProductByMail',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setChecksum' : function(productId,checksum, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                checksum : JSON.stringify(checksum),
            },
            method: 'setChecksum',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setExtraInformationForFile' : function(productId,fileId,text, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
                text : JSON.stringify(text),
            },
            method: 'setExtraInformationForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setFixedPrice' : function(userId,price, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                price : JSON.stringify(price),
            },
            method: 'setFixedPrice',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setPushoverId' : function(pushover, silent) {
        data = {
            args : {
                pushover : JSON.stringify(pushover),
            },
            method: 'setPushoverId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'setPushoverIdForUser' : function(pushover,userId, silent) {
        data = {
            args : {
                pushover : JSON.stringify(pushover),
                userId : JSON.stringify(userId),
            },
            method: 'setPushoverIdForUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'sync' : function(option, silent) {
        data = {
            args : {
                option : JSON.stringify(option),
            },
            method: 'sync',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'syncFromMagento' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'syncFromMagento',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleAllowNegativeCredit' : function(userId,allow, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                allow : JSON.stringify(allow),
            },
            method: 'toggleAllowNegativeCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleAllowWindowsApp' : function(userId,allow, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                allow : JSON.stringify(allow),
            },
            method: 'toggleAllowWindowsApp',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleBadCustomer' : function(userId,badCustomer, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                badCustomer : JSON.stringify(badCustomer),
            },
            method: 'toggleBadCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleIsNorwegian' : function(userId,isNorwegian, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                isNorwegian : JSON.stringify(isNorwegian),
            },
            method: 'toggleIsNorwegian',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'togglePassiveSlaveMode' : function(userId,toggle, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                toggle : JSON.stringify(toggle),
            },
            method: 'togglePassiveSlaveMode',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleSaleableProduct' : function(productId,saleable, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                saleable : JSON.stringify(saleable),
            },
            method: 'toggleSaleableProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleStartStop' : function(productId,toggle, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                toggle : JSON.stringify(toggle),
            },
            method: 'toggleStartStop',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'transferCreditToSlave' : function(slaveId,amount, silent) {
        data = {
            args : {
                slaveId : JSON.stringify(slaveId),
                amount : JSON.stringify(amount),
            },
            method: 'transferCreditToSlave',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

    'updateEvcCreditAccounts' : function(silent) {
        data = {
            args : {
            },
            method: 'updateEvcCreditAccounts',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.StoreManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreManager.prototype = {
    'autoCreateStore' : function(hostname, silent) {
        data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'autoCreateStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'createStore' : function(hostname,email,password,notify, silent) {
        data = {
            args : {
                hostname : JSON.stringify(hostname),
                email : JSON.stringify(email),
                password : JSON.stringify(password),
                notify : JSON.stringify(notify),
            },
            method: 'createStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'delete' : function(silent) {
        data = {
            args : {
            },
            method: 'delete',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'enableExtendedMode' : function(toggle,password, silent) {
        data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'enableExtendedMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'enableSMSAccess' : function(toggle,password, silent) {
        data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'enableSMSAccess',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'generateStoreId' : function(silent) {
        data = {
            args : {
            },
            method: 'generateStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllEnvironments' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllEnvironments',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'getKey' : function(key, silent) {
        data = {
            args : {
                key : JSON.stringify(key),
            },
            method: 'getKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'getKeySecure' : function(key,password, silent) {
        data = {
            args : {
                key : JSON.stringify(key),
                password : JSON.stringify(password),
            },
            method: 'getKeySecure',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'getMultiLevelNames' : function(silent) {
        data = {
            args : {
            },
            method: 'getMultiLevelNames',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'getMyStore' : function(silent) {
        data = {
            args : {
            },
            method: 'getMyStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'getStoreId' : function(silent) {
        data = {
            args : {
            },
            method: 'getStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'initializeStore' : function(webAddress,initSessionId, silent) {
        data = {
            args : {
                webAddress : JSON.stringify(webAddress),
                initSessionId : JSON.stringify(initSessionId),
            },
            method: 'initializeStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'initializeStoreByStoreId' : function(storeId,initSessionId, silent) {
        data = {
            args : {
                storeId : JSON.stringify(storeId),
                initSessionId : JSON.stringify(initSessionId),
            },
            method: 'initializeStoreByStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'isAddressTaken' : function(address, silent) {
        data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'isAddressTaken',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'isProductMode' : function(silent) {
        data = {
            args : {
            },
            method: 'isProductMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'receiveSyncData' : function(json, silent) {
        data = {
            args : {
                json : JSON.stringify(json),
            },
            method: 'receiveSyncData',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'removeDomainName' : function(domainName, silent) {
        data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'removeDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'removeKey' : function(key, silent) {
        data = {
            args : {
                key : JSON.stringify(key),
            },
            method: 'removeKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'saveKey' : function(key,value,secure, silent) {
        data = {
            args : {
                key : JSON.stringify(key),
                value : JSON.stringify(value),
                secure : JSON.stringify(secure),
            },
            method: 'saveKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'saveStore' : function(config, silent) {
        data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'setDeepFreeze' : function(mode,password, silent) {
        data = {
            args : {
                mode : JSON.stringify(mode),
                password : JSON.stringify(password),
            },
            method: 'setDeepFreeze',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'setImageIdToFavicon' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setImageIdToFavicon',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'setIntroductionRead' : function(silent) {
        data = {
            args : {
            },
            method: 'setIntroductionRead',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'setIsTemplate' : function(storeId,isTemplate, silent) {
        data = {
            args : {
                storeId : JSON.stringify(storeId),
                isTemplate : JSON.stringify(isTemplate),
            },
            method: 'setIsTemplate',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'setPrimaryDomainName' : function(domainName, silent) {
        data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'setPrimaryDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'setSessionLanguage' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setSessionLanguage',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'syncData' : function(environment,username,password, silent) {
        data = {
            args : {
                environment : JSON.stringify(environment),
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'syncData',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    'toggleIgnoreBookingErrors' : function(password, silent) {
        data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'toggleIgnoreBookingErrors',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.UserManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UserManager.prototype = {
    'addComment' : function(userId,comment, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                comment : JSON.stringify(comment),
            },
            method: 'addComment',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'addGroupToUser' : function(userId,groupId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'addGroupToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'addMetaData' : function(userId,key,value, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                key : JSON.stringify(key),
                value : JSON.stringify(value),
            },
            method: 'addMetaData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'addUserPrivilege' : function(userId,managerName,managerFunction, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                managerName : JSON.stringify(managerName),
                managerFunction : JSON.stringify(managerFunction),
            },
            method: 'addUserPrivilege',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'assignCompanyToGroup' : function(company,groupId, silent) {
        data = {
            args : {
                company : JSON.stringify(company),
                groupId : JSON.stringify(groupId),
            },
            method: 'assignCompanyToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'assignCompanyToUser' : function(company,userId, silent) {
        data = {
            args : {
                company : JSON.stringify(company),
                userId : JSON.stringify(userId),
            },
            method: 'assignCompanyToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'assignReferenceToCompany' : function(companyId,companyReference, silent) {
        data = {
            args : {
                companyId : JSON.stringify(companyId),
                companyReference : JSON.stringify(companyReference),
            },
            method: 'assignReferenceToCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'cancelImpersonating' : function(silent) {
        data = {
            args : {
            },
            method: 'cancelImpersonating',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'checkUserNameAndPassword' : function(username,password, silent) {
        data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'checkUserNameAndPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'confirmCompanyOwner' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'confirmCompanyOwner',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'createUser' : function(user, silent) {
        data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'createUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteCompany' : function(companyId, silent) {
        data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'deleteCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteExtraAddressToGroup' : function(groupId,addressId, silent) {
        data = {
            args : {
                groupId : JSON.stringify(groupId),
                addressId : JSON.stringify(addressId),
            },
            method: 'deleteExtraAddressToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'deleteUser' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'deleteUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'doEmailExists' : function(email, silent) {
        data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'doEmailExists',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'doesUserExistsOnReferenceNumber' : function(number, silent) {
        data = {
            args : {
                number : JSON.stringify(number),
            },
            method: 'doesUserExistsOnReferenceNumber',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'findUsers' : function(searchCriteria, silent) {
        data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'findUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getAdministratorCount' : function(silent) {
        data = {
            args : {
            },
            method: 'getAdministratorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllCompanies' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllCompanies',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllGroups' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllGroups',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllUsers' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllUsersFiltered' : function(filter, silent) {
        data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllUsersFiltered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getAllUsersWithCommentToApp' : function(appId, silent) {
        data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'getAllUsersWithCommentToApp',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getCompany' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getCompanyByReference' : function(companyReference, silent) {
        data = {
            args : {
                companyReference : JSON.stringify(companyReference),
            },
            method: 'getCompanyByReference',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getCustomersCount' : function(silent) {
        data = {
            args : {
            },
            method: 'getCustomersCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getEditorCount' : function(silent) {
        data = {
            args : {
            },
            method: 'getEditorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getGroup' : function(groupId, silent) {
        data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getLoggedOnUser' : function(silent) {
        data = {
            args : {
            },
            method: 'getLoggedOnUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getLogins' : function(year, silent) {
        data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getLogins',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getUnconfirmedCompanyOwners' : function(silent) {
        data = {
            args : {
            },
            method: 'getUnconfirmedCompanyOwners',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getUserById' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getUserById',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getUserList' : function(userIds, silent) {
        data = {
            args : {
                userIds : JSON.stringify(userIds),
            },
            method: 'getUserList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getUsersBasedOnGroupId' : function(groupId, silent) {
        data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getUsersBasedOnGroupId',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getUsersByCompanyId' : function(companyId, silent) {
        data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'getUsersByCompanyId',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'getUsersByType' : function(type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getUsersByType',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'impersonateUser' : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'impersonateUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'isCaptain' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'isCaptain',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'isImpersonating' : function(silent) {
        data = {
            args : {
            },
            method: 'isImpersonating',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'isLoggedIn' : function(silent) {
        data = {
            args : {
            },
            method: 'isLoggedIn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'logOn' : function(username,password, silent) {
        data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'logOn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'loginWithPincode' : function(username,password,pinCode, silent) {
        data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
                pinCode : JSON.stringify(pinCode),
            },
            method: 'loginWithPincode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'logonUsingKey' : function(logonKey, silent) {
        data = {
            args : {
                logonKey : JSON.stringify(logonKey),
            },
            method: 'logonUsingKey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'logout' : function(silent) {
        data = {
            args : {
            },
            method: 'logout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'removeComment' : function(userId,commentId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                commentId : JSON.stringify(commentId),
            },
            method: 'removeComment',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'removeGroup' : function(groupId, silent) {
        data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'removeGroupFromUser' : function(userId,groupId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroupFromUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'removeMetaData' : function(userId,key, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                key : JSON.stringify(key),
            },
            method: 'removeMetaData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'requestAdminRight' : function(managerName,managerFunction,applicationInstanceId, silent) {
        data = {
            args : {
                managerName : JSON.stringify(managerName),
                managerFunction : JSON.stringify(managerFunction),
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'requestAdminRight',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'requestNewPincode' : function(username,password, silent) {
        data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'requestNewPincode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'resetPassword' : function(resetCode,username,newPassword, silent) {
        data = {
            args : {
                resetCode : JSON.stringify(resetCode),
                username : JSON.stringify(username),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'resetPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'saveCompany' : function(company, silent) {
        data = {
            args : {
                company : JSON.stringify(company),
            },
            method: 'saveCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'saveExtraAddressToGroup' : function(group,address, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
                address : JSON.stringify(address),
            },
            method: 'saveExtraAddressToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'saveGroup' : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'saveGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'saveUser' : function(user, silent) {
        data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'saveUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'searchForGroup' : function(searchCriteria, silent) {
        data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'searchForGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'sendResetCode' : function(title,text,username, silent) {
        data = {
            args : {
                title : JSON.stringify(title),
                text : JSON.stringify(text),
                username : JSON.stringify(username),
            },
            method: 'sendResetCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'updatePassword' : function(userId,oldPassword,newPassword, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                oldPassword : JSON.stringify(oldPassword),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'updatePassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    'upgradeUserToGetShopAdmin' : function(password, silent) {
        data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'upgradeUserToGetShopAdmin',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.UtilManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UtilManager.prototype = {
    'getBase64EncodedPDFWebPage' : function(urlToPage, silent) {
        data = {
            args : {
                urlToPage : JSON.stringify(urlToPage),
            },
            method: 'getBase64EncodedPDFWebPage',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'getCompaniesFromBrReg' : function(search, silent) {
        data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'getCompaniesFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'getCompanyFree' : function(companyVatNumber, silent) {
        data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFree',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'getCompanyFromBrReg' : function(companyVatNumber, silent) {
        data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'getFile' : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFile',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'getStartupCount' : function(silent) {
        data = {
            args : {
            },
            method: 'getStartupCount',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'isInProductionMode' : function(silent) {
        data = {
            args : {
            },
            method: 'isInProductionMode',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

    'saveFile' : function(file, silent) {
        data = {
            args : {
                file : JSON.stringify(file),
            },
            method: 'saveFile',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.UUIDSecurityManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UUIDSecurityManager.prototype = {
    'grantAccess' : function(userId,uuid,read,write, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                uuid : JSON.stringify(uuid),
                read : JSON.stringify(read),
                write : JSON.stringify(write),
            },
            method: 'grantAccess',
            interfaceName: 'core.uuidsecuritymanager.IUUIDSecurityManager',
        };
        return this.communication.send(data, silent);
    },

    'hasAccess' : function(uuid,read,write, silent) {
        data = {
            args : {
                uuid : JSON.stringify(uuid),
                read : JSON.stringify(read),
                write : JSON.stringify(write),
            },
            method: 'hasAccess',
            interfaceName: 'core.uuidsecuritymanager.IUUIDSecurityManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.YouTubeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.YouTubeManager.prototype = {
    'searchYoutube' : function(searchword, silent) {
        data = {
            args : {
                searchword : JSON.stringify(searchword),
            },
            method: 'searchYoutube',
            interfaceName: 'core.youtubemanager.IYouTubeManager',
        };
        return this.communication.send(data, silent);
    },

}

GetShopApiWebSocket.prototype.createManagers = function() {
    this.BannerManager = new GetShopApiWebSocket.BannerManager(this);
    this.ContentManager = new GetShopApiWebSocket.ContentManager(this);
    this.FooterManager = new GetShopApiWebSocket.FooterManager(this);
    this.LogoManager = new GetShopApiWebSocket.LogoManager(this);
    this.NewsManager = new GetShopApiWebSocket.NewsManager(this);
    this.AccountingManager = new GetShopApiWebSocket.AccountingManager(this);
    this.GetShopApplicationPool = new GetShopApiWebSocket.GetShopApplicationPool(this);
    this.StoreApplicationInstancePool = new GetShopApiWebSocket.StoreApplicationInstancePool(this);
    this.StoreApplicationPool = new GetShopApiWebSocket.StoreApplicationPool(this);
    this.DoorManager = new GetShopApiWebSocket.DoorManager(this);
    this.BigStock = new GetShopApiWebSocket.BigStock(this);
    this.BookingEngine = new GetShopApiWebSocket.BookingEngine(this);
    this.BrainTreeManager = new GetShopApiWebSocket.BrainTreeManager(this);
    this.CalendarManager = new GetShopApiWebSocket.CalendarManager(this);
    this.CartManager = new GetShopApiWebSocket.CartManager(this);
    this.CarTuningManager = new GetShopApiWebSocket.CarTuningManager(this);
    this.CertegoManager = new GetShopApiWebSocket.CertegoManager(this);
    this.ChatManager = new GetShopApiWebSocket.ChatManager(this);
    this.EventBookingManager = new GetShopApiWebSocket.EventBookingManager(this);
    this.FileManager = new GetShopApiWebSocket.FileManager(this);
    this.FtpManager = new GetShopApiWebSocket.FtpManager(this);
    this.GalleryManager = new GetShopApiWebSocket.GalleryManager(this);
    this.GetShop = new GetShopApiWebSocket.GetShop(this);
    this.GetShopLockManager = new GetShopApiWebSocket.GetShopLockManager(this);
    this.InformationScreenManager = new GetShopApiWebSocket.InformationScreenManager(this);
    this.ListManager = new GetShopApiWebSocket.ListManager(this);
    this.MecaApi = new GetShopApiWebSocket.MecaApi(this);
    this.MessageManager = new GetShopApiWebSocket.MessageManager(this);
    this.NewsLetterManager = new GetShopApiWebSocket.NewsLetterManager(this);
    this.MobileManager = new GetShopApiWebSocket.MobileManager(this);
    this.OrderManager = new GetShopApiWebSocket.OrderManager(this);
    this.PageManager = new GetShopApiWebSocket.PageManager(this);
    this.InvoiceManager = new GetShopApiWebSocket.InvoiceManager(this);
    this.LasGruppenPDFGenerator = new GetShopApiWebSocket.LasGruppenPDFGenerator(this);
    this.PkkControlManager = new GetShopApiWebSocket.PkkControlManager(this);
    this.PmsEventManager = new GetShopApiWebSocket.PmsEventManager(this);
    this.PmsManager = new GetShopApiWebSocket.PmsManager(this);
    this.PmsManagerProcessor = new GetShopApiWebSocket.PmsManagerProcessor(this);
    this.ProductManager = new GetShopApiWebSocket.ProductManager(this);
    this.PullServerManager = new GetShopApiWebSocket.PullServerManager(this);
    this.QuestBackManager = new GetShopApiWebSocket.QuestBackManager(this);
    this.ReportingManager = new GetShopApiWebSocket.ReportingManager(this);
    this.SalesManager = new GetShopApiWebSocket.SalesManager(this);
    this.SedoxProductManager = new GetShopApiWebSocket.SedoxProductManager(this);
    this.StoreManager = new GetShopApiWebSocket.StoreManager(this);
    this.UserManager = new GetShopApiWebSocket.UserManager(this);
    this.UtilManager = new GetShopApiWebSocket.UtilManager(this);
    this.UUIDSecurityManager = new GetShopApiWebSocket.UUIDSecurityManager(this);
    this.YouTubeManager = new GetShopApiWebSocket.YouTubeManager(this);
}