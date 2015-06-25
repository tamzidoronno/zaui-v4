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
    
    connect: function() {
        if (!this.shouldConnect)
            return;
        
        this.shouldConnect = false;
        this.connectedCalled = true;
        var me = this;
        if (this.connectionEstablished === null) {
            this.fireDisconnectedEvent();
        }
        var address = "ws://"+this.address+":31330/";
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
            
    handleMessage: function(msg) {
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
        this.initializeStore();
        this.fireConnectedEvent();
        this.connectionEstablished = true;
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
        if (this.socket.OPEN)
            this.socket.send(messageJson);
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
GetShopApiWebSocket.HotelBookingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.HotelBookingManager.prototype = {
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

    'getSmsLog' : function(silent) {
        data = {
            args : {
            },
            method: 'getSmsLog',
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
    'addApplication' : function(applicationId,pageCellId, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                pageCellId : JSON.stringify(pageCellId),
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

    'getPagesForApplications' : function(appIds, silent) {
        data = {
            args : {
                appIds : JSON.stringify(appIds),
            },
            method: 'getPagesForApplications',
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

    'getAllAttributes' : function(silent) {
        data = {
            args : {
            },
            method: 'getAllAttributes',
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

    'getAttributeSummary' : function(silent) {
        data = {
            args : {
            },
            method: 'getAttributeSummary',
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

    'updateAttributePool' : function(groups, silent) {
        data = {
            args : {
                groups : JSON.stringify(groups),
            },
            method: 'updateAttributePool',
            interfaceName: 'core.productmanager.IProductManager',
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

    'createSedoxProduct' : function(sedoxProduct,base64encodedOriginalFile,originalFileName,forSlaveId,origin, silent) {
        data = {
            args : {
                sedoxProduct : JSON.stringify(sedoxProduct),
                base64encodedOriginalFile : JSON.stringify(base64encodedOriginalFile),
                originalFileName : JSON.stringify(originalFileName),
                forSlaveId : JSON.stringify(forSlaveId),
                origin : JSON.stringify(origin),
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

    'getProductsFirstUploadedByCurrentUser' : function(silent) {
        data = {
            args : {
            },
            method: 'getProductsFirstUploadedByCurrentUser',
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

}
GetShopApiWebSocket.StoreManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreManager.prototype = {
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

    'cancelImpersonating' : function(silent) {
        data = {
            args : {
            },
            method: 'cancelImpersonating',
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
    this.GetShopApplicationPool = new GetShopApiWebSocket.GetShopApplicationPool(this);
    this.StoreApplicationInstancePool = new GetShopApiWebSocket.StoreApplicationInstancePool(this);
    this.StoreApplicationPool = new GetShopApiWebSocket.StoreApplicationPool(this);
    this.BigStock = new GetShopApiWebSocket.BigStock(this);
    this.BrainTreeManager = new GetShopApiWebSocket.BrainTreeManager(this);
    this.CalendarManager = new GetShopApiWebSocket.CalendarManager(this);
    this.CartManager = new GetShopApiWebSocket.CartManager(this);
    this.CarTuningManager = new GetShopApiWebSocket.CarTuningManager(this);
    this.ChatManager = new GetShopApiWebSocket.ChatManager(this);
    this.GalleryManager = new GetShopApiWebSocket.GalleryManager(this);
    this.GetShop = new GetShopApiWebSocket.GetShop(this);
    this.HotelBookingManager = new GetShopApiWebSocket.HotelBookingManager(this);
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
    this.ProductManager = new GetShopApiWebSocket.ProductManager(this);
    this.ReportingManager = new GetShopApiWebSocket.ReportingManager(this);
    this.SedoxProductManager = new GetShopApiWebSocket.SedoxProductManager(this);
    this.StoreManager = new GetShopApiWebSocket.StoreManager(this);
    this.UserManager = new GetShopApiWebSocket.UserManager(this);
    this.UtilManager = new GetShopApiWebSocket.UtilManager(this);
    this.YouTubeManager = new GetShopApiWebSocket.YouTubeManager(this);
}