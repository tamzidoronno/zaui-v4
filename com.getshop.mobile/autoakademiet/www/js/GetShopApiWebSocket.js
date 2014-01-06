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
        var corrolatingMessage = this.getMessage(jsonObject.id);
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
            if (this.sentMessages[i].id === id) {
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
    addImage : function(id,fileId, silent) {
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

    createSet : function(width,height,id, silent) {
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

    deleteSet : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    getSet : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, silent);
    },

    linkProductToImage : function(bannerSetId,imageId,productId, silent) {
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

    removeImage : function(bannerSetId,fileId, silent) {
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

    saveSet : function(set, silent) {
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
    createContent : function(content, silent) {
        data = {
            args : {
                content : JSON.stringify(content),
            },
            method: 'createContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    deleteContent : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    getAllContent : function(silent) {
        data = {
            args : {
            },
            method: 'getAllContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    getContent : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, silent);
    },

    saveContent : function(id,content, silent) {
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
    getConfiguration : function(silent) {
        data = {
            args : {
            },
            method: 'getConfiguration',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, silent);
    },

    setLayout : function(numberOfColumns, silent) {
        data = {
            args : {
                numberOfColumns : JSON.stringify(numberOfColumns),
            },
            method: 'setLayout',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, silent);
    },

    setType : function(column,type, silent) {
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
    deleteLogo : function(silent) {
        data = {
            args : {
            },
            method: 'deleteLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, silent);
    },

    getLogo : function(silent) {
        data = {
            args : {
            },
            method: 'getLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, silent);
    },

    setLogo : function(fileId, silent) {
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
    addNews : function(newsEntry, silent) {
        data = {
            args : {
                newsEntry : JSON.stringify(newsEntry),
            },
            method: 'addNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    addSubscriber : function(email, silent) {
        data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'addSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    deleteNews : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    getAllNews : function(silent) {
        data = {
            args : {
            },
            method: 'getAllNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    getAllSubscribers : function(silent) {
        data = {
            args : {
            },
            method: 'getAllSubscribers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, silent);
    },

    removeSubscriber : function(subscriberId, silent) {
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
GetShopApiWebSocket.AppManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AppManager.prototype = {
    createApplication : function(appName, silent) {
        data = {
            args : {
                appName : JSON.stringify(appName),
            },
            method: 'createApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    deleteApplication : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    getAllApplicationSubscriptions : function(includeAppSettings, silent) {
        data = {
            args : {
                includeAppSettings : JSON.stringify(includeAppSettings),
            },
            method: 'getAllApplicationSubscriptions',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    getAllApplications : function(silent) {
        data = {
            args : {
            },
            method: 'getAllApplications',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    getApplication : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    getSyncApplications : function(silent) {
        data = {
            args : {
            },
            method: 'getSyncApplications',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    getUnpayedSubscription : function(silent) {
        data = {
            args : {
            },
            method: 'getUnpayedSubscription',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    isSyncToolConnected : function(silent) {
        data = {
            args : {
            },
            method: 'isSyncToolConnected',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    saveApplication : function(settings, silent) {
        data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'saveApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

    setSyncApplication : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setSyncApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.CalendarManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CalendarManager.prototype = {
    addUserToEvent : function(userId,eventId,password,username, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
                password : JSON.stringify(password),
                username : JSON.stringify(username),
            },
            method: 'addUserToEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    applyFilter : function(filters, silent) {
        data = {
            args : {
                filters : JSON.stringify(filters),
            },
            method: 'applyFilter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    confirmEntry : function(entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'confirmEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    createEntry : function(year,month,day, silent) {
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

    deleteEntry : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    getActiveFilters : function(silent) {
        data = {
            args : {
            },
            method: 'getActiveFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    getEntries : function(year,month,day,filters, silent) {
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

    getEntry : function(entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'getEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    getFilters : function(silent) {
        data = {
            args : {
            },
            method: 'getFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    getMonth : function(year,month,includeExtraEvents, silent) {
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

    removeUserFromEvent : function(userId,eventId, silent) {
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

    saveEntry : function(entry, silent) {
        data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    sendReminderToUser : function(byEmail,bySMS,users,text,subject, silent) {
        data = {
            args : {
                byEmail : JSON.stringify(byEmail),
                bySMS : JSON.stringify(bySMS),
                users : JSON.stringify(users),
                text : JSON.stringify(text),
                subject : JSON.stringify(subject),
            },
            method: 'sendReminderToUser',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, silent);
    },

    transferFromWaitingList : function(entryId,userId, silent) {
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

}
GetShopApiWebSocket.CartManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CartManager.prototype = {
    addCoupon : function(coupon, silent) {
        data = {
            args : {
                coupon : JSON.stringify(coupon),
            },
            method: 'addCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    addProduct : function(productId,count,variations, silent) {
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

    applyCouponToCurrentCart : function(code, silent) {
        data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'applyCouponToCurrentCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    calculateTotalCost : function(cart, silent) {
        data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    clear : function(silent) {
        data = {
            args : {
            },
            method: 'clear',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    getCart : function(silent) {
        data = {
            args : {
            },
            method: 'getCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    getCartTotalAmount : function(silent) {
        data = {
            args : {
            },
            method: 'getCartTotalAmount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    getCoupons : function(silent) {
        data = {
            args : {
            },
            method: 'getCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    getShippingCost : function(silent) {
        data = {
            args : {
            },
            method: 'getShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    getShippingPriceBasis : function(silent) {
        data = {
            args : {
            },
            method: 'getShippingPriceBasis',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    getTaxes : function(silent) {
        data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    removeAllCoupons : function(silent) {
        data = {
            args : {
            },
            method: 'removeAllCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    removeCoupon : function(code, silent) {
        data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'removeCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    removeProduct : function(cartItemId, silent) {
        data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
            },
            method: 'removeProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    setAddress : function(address, silent) {
        data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'setAddress',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    setShippingCost : function(shippingCost, silent) {
        data = {
            args : {
                shippingCost : JSON.stringify(shippingCost),
            },
            method: 'setShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, silent);
    },

    updateProductCount : function(cartItemId,count, silent) {
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
GetShopApiWebSocket.ChatManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ChatManager.prototype = {
    closeChat : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'closeChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    getChatters : function(silent) {
        data = {
            args : {
            },
            method: 'getChatters',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    getMessages : function(silent) {
        data = {
            args : {
            },
            method: 'getMessages',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    pingMobileChat : function(chatterid, silent) {
        data = {
            args : {
                chatterid : JSON.stringify(chatterid),
            },
            method: 'pingMobileChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data, silent);
    },

    replyToChat : function(id,message, silent) {
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

    sendMessage : function(message, silent) {
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
    addImageToGallery : function(galleryId,imageId,description,title, silent) {
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

    createImageGallery : function(silent) {
        data = {
            args : {
            },
            method: 'createImageGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    deleteImage : function(entryId, silent) {
        data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'deleteImage',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    getAllImages : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getAllImages',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    getEntry : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, silent);
    },

    saveEntry : function(entry, silent) {
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
    addUserToPartner : function(userId,partner,password, silent) {
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

    findAddressForApplication : function(uuid, silent) {
        data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForApplication',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    findAddressForUUID : function(uuid, silent) {
        data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForUUID',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    getPartnerData : function(partnerId,password, silent) {
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

    getStores : function(code, silent) {
        data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'getStores',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, silent);
    },

    setApplicationList : function(ids,partnerId,password, silent) {
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

}
GetShopApiWebSocket.ListManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ListManager.prototype = {
    addEntry : function(listId,entry,parentPageId, silent) {
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

    clearList : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'clearList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    combineList : function(toListId,newListId, silent) {
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

    createListId : function(silent) {
        data = {
            args : {
            },
            method: 'createListId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    deleteEntry : function(id,listId, silent) {
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

    getAllListsByType : function(type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getAllListsByType',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    getCombinedLists : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getCombinedLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    getList : function(listId, silent) {
        data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    getListEntry : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getListEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    getLists : function(silent) {
        data = {
            args : {
            },
            method: 'getLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    getPageIdByName : function(name, silent) {
        data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    orderEntry : function(id,after,parentId, silent) {
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

    setEntries : function(listId,entries, silent) {
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

    translateEntries : function(entryIds, silent) {
        data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, silent);
    },

    unCombineList : function(fromListId,toRemoveId, silent) {
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

    updateEntry : function(entry, silent) {
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
GetShopApiWebSocket.MessageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MessageManager.prototype = {
    getSmsCount : function(year,month, silent) {
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

    sendMail : function(to,toName,subject,content,from,fromName, silent) {
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

}
GetShopApiWebSocket.MobileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MobileManager.prototype = {
    clearBadged : function(tokenId, silent) {
        data = {
            args : {
                tokenId : JSON.stringify(tokenId),
            },
            method: 'clearBadged',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

    registerToken : function(token, silent) {
        data = {
            args : {
                token : JSON.stringify(token),
            },
            method: 'registerToken',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

    sendMessageToAll : function(message, silent) {
        data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAll',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, silent);
    },

    sendMessageToAllTestUnits : function(message, silent) {
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
    changeOrderStatus : function(id,status, silent) {
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

    createOrder : function(address, silent) {
        data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'createOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    getOrder : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    getOrderByincrementOrderId : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getOrderByincrementOrderId',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    getOrders : function(orderIds,page,pageSize, silent) {
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

    getTaxes : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTaxes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    getTotalAmount : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTotalAmount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    saveOrder : function(order, silent) {
        data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, silent);
    },

    setOrderStatus : function(password,orderId,currency,price,status, silent) {
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

}
GetShopApiWebSocket.PageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PageManager.prototype = {
    addApplication : function(applicationSettingId, silent) {
        data = {
            args : {
                applicationSettingId : JSON.stringify(applicationSettingId),
            },
            method: 'addApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    addApplicationToPage : function(pageId,applicationSettingId,pageArea, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                applicationSettingId : JSON.stringify(applicationSettingId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'addApplicationToPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    addExistingApplicationToPageArea : function(pageId,appId,area, silent) {
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

    changePageLayout : function(pageId,layout, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                layout : JSON.stringify(layout),
            },
            method: 'changePageLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    changePageUserLevel : function(pageId,userLevel, silent) {
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

    clearPageArea : function(pageId,pageArea, silent) {
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

    createPage : function(layout,parentId, silent) {
        data = {
            args : {
                layout : JSON.stringify(layout),
                parentId : JSON.stringify(parentId),
            },
            method: 'createPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    createPageWithId : function(layout,parentId,id, silent) {
        data = {
            args : {
                layout : JSON.stringify(layout),
                parentId : JSON.stringify(parentId),
                id : JSON.stringify(id),
            },
            method: 'createPageWithId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    deleteApplication : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    deletePage : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deletePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getApplicationSettings : function(name, silent) {
        data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getApplicationSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getApplications : function(silent) {
        data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getApplicationsBasedOnApplicationSettingsId : function(appSettingsId, silent) {
        data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
            },
            method: 'getApplicationsBasedOnApplicationSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getApplicationsByPageAreaAndSettingsId : function(appSettingsId,pageArea, silent) {
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

    getApplicationsByType : function(type, silent) {
        data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getApplicationsByType',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getApplicationsForPage : function(pageId, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getApplicationsForPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getPage : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getPagesForApplications : function(appIds, silent) {
        data = {
            args : {
                appIds : JSON.stringify(appIds),
            },
            method: 'getPagesForApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getSecuredSettings : function(applicationInstanceId, silent) {
        data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getSecuredSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    getSecuredSettingsInternal : function(appName, silent) {
        data = {
            args : {
                appName : JSON.stringify(appName),
            },
            method: 'getSecuredSettingsInternal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    removeAllApplications : function(appSettingsId, silent) {
        data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
            },
            method: 'removeAllApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    removeApplication : function(applicationId,pageid, silent) {
        data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                pageid : JSON.stringify(pageid),
            },
            method: 'removeApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    reorderApplication : function(pageId,appId,moveUp, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                appId : JSON.stringify(appId),
                moveUp : JSON.stringify(moveUp),
            },
            method: 'reorderApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    saveApplicationConfiguration : function(config, silent) {
        data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveApplicationConfiguration',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    savePage : function(page, silent) {
        data = {
            args : {
                page : JSON.stringify(page),
            },
            method: 'savePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    search : function(search, silent) {
        data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'search',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    setApplicationSettings : function(settings, silent) {
        data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'setApplicationSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    setApplicationSticky : function(appId,toggle, silent) {
        data = {
            args : {
                appId : JSON.stringify(appId),
                toggle : JSON.stringify(toggle),
            },
            method: 'setApplicationSticky',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    setPageDescription : function(pageId,description, silent) {
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

    setParentPage : function(pageId,parentPageId, silent) {
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

    swapApplication : function(fromAppId,toAppId, silent) {
        data = {
            args : {
                fromAppId : JSON.stringify(fromAppId),
                toAppId : JSON.stringify(toAppId),
            },
            method: 'swapApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    switchApplicationAreas : function(pageId,fromArea,toArea, silent) {
        data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromArea : JSON.stringify(fromArea),
                toArea : JSON.stringify(toArea),
            },
            method: 'switchApplicationAreas',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

    translatePages : function(pages, silent) {
        data = {
            args : {
                pages : JSON.stringify(pages),
            },
            method: 'translatePages',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.InvoiceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InvoiceManager.prototype = {
    createInvoice : function(orderId, silent) {
        data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'createInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.ProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ProductManager.prototype = {
    addImage : function(productId,productImageId,description, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
                productImageId : JSON.stringify(productImageId),
                description : JSON.stringify(description),
            },
            method: 'addImage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    changeStockQuantity : function(productId,count, silent) {
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

    createProduct : function(silent) {
        data = {
            args : {
            },
            method: 'createProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getAllAttributes : function(silent) {
        data = {
            args : {
            },
            method: 'getAllAttributes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getAllProducts : function(silent) {
        data = {
            args : {
            },
            method: 'getAllProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getAttributeSummary : function(silent) {
        data = {
            args : {
            },
            method: 'getAttributeSummary',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getLatestProducts : function(count, silent) {
        data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getPageIdByName : function(productName, silent) {
        data = {
            args : {
                productName : JSON.stringify(productName),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getPrice : function(productId,variations, silent) {
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

    getProduct : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getProductFromApplicationId : function(app_uuid, silent) {
        data = {
            args : {
                app_uuid : JSON.stringify(app_uuid),
            },
            method: 'getProductFromApplicationId',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getProducts : function(productCriteria, silent) {
        data = {
            args : {
                productCriteria : JSON.stringify(productCriteria),
            },
            method: 'getProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    getRandomProducts : function(fetchSize,ignoreProductId, silent) {
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

    getTaxes : function(silent) {
        data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    removeProduct : function(productId, silent) {
        data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'removeProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    saveProduct : function(product, silent) {
        data = {
            args : {
                product : JSON.stringify(product),
            },
            method: 'saveProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    setMainImage : function(productId,imageId, silent) {
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

    setTaxes : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'setTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    translateEntries : function(entryIds, silent) {
        data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, silent);
    },

    updateAttributePool : function(groups, silent) {
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
    getAllEventsFromSession : function(startDate,stopDate,searchSessionId, silent) {
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

    getConnectedUsers : function(startdate,stopDate,filter, silent) {
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

    getOrdersCreated : function(startDate,stopDate, silent) {
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

    getPageViews : function(startDate,stopDate, silent) {
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

    getProductViewed : function(startDate,stopDate, silent) {
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

    getReport : function(startDate,stopDate,type, silent) {
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

    getUserLoggedOn : function(startDate,stopDate, silent) {
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
GetShopApiWebSocket.StoreManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreManager.prototype = {
    connectStoreToPartner : function(partner, silent) {
        data = {
            args : {
                partner : JSON.stringify(partner),
            },
            method: 'connectStoreToPartner',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    createStore : function(hostname,email,password,notify, silent) {
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

    delete : function(silent) {
        data = {
            args : {
            },
            method: 'delete',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    enableExtendedMode : function(toggle,password, silent) {
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

    enableSMSAccess : function(toggle,password, silent) {
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

    getMyStore : function(silent) {
        data = {
            args : {
            },
            method: 'getMyStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    getStoreId : function(silent) {
        data = {
            args : {
            },
            method: 'getStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    initializeStore : function(webAddress,initSessionId, silent) {
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

    isAddressTaken : function(address, silent) {
        data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'isAddressTaken',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    removeDomainName : function(domainName, silent) {
        data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'removeDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    saveStore : function(config, silent) {
        data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    setDeepFreeze : function(mode,password, silent) {
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

    setIntroductionRead : function(silent) {
        data = {
            args : {
            },
            method: 'setIntroductionRead',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    setPrimaryDomainName : function(domainName, silent) {
        data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'setPrimaryDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

    setVIS : function(toggle,password, silent) {
        data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'setVIS',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.UserManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UserManager.prototype = {
    addUserPrivilege : function(userId,managerName,managerFunction, silent) {
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

    createUser : function(user, silent) {
        data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'createUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    deleteUser : function(userId, silent) {
        data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'deleteUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    doEmailExists : function(email, silent) {
        data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'doEmailExists',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    findUsers : function(searchCriteria, silent) {
        data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'findUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getAdministratorCount : function(silent) {
        data = {
            args : {
            },
            method: 'getAdministratorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getAllGroups : function(silent) {
        data = {
            args : {
            },
            method: 'getAllGroups',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getAllUsers : function(silent) {
        data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getCustomersCount : function(silent) {
        data = {
            args : {
            },
            method: 'getCustomersCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getEditorCount : function(silent) {
        data = {
            args : {
            },
            method: 'getEditorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getLoggedOnUser : function(silent) {
        data = {
            args : {
            },
            method: 'getLoggedOnUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getStoresConnectedToMe : function(silent) {
        data = {
            args : {
            },
            method: 'getStoresConnectedToMe',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getUserById : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getUserById',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    getUserList : function(userIds, silent) {
        data = {
            args : {
                userIds : JSON.stringify(userIds),
            },
            method: 'getUserList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    isCaptain : function(id, silent) {
        data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'isCaptain',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    isLoggedIn : function(silent) {
        data = {
            args : {
            },
            method: 'isLoggedIn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    logOn : function(username,password, silent) {
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

    logonUsingKey : function(logonKey, silent) {
        data = {
            args : {
                logonKey : JSON.stringify(logonKey),
            },
            method: 'logonUsingKey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    logout : function(silent) {
        data = {
            args : {
            },
            method: 'logout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    removeGroup : function(groupId, silent) {
        data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    requestAdminRight : function(managerName,managerFunction,applicationInstanceId, silent) {
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

    resetPassword : function(resetCode,username,newPassword, silent) {
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

    saveGroup : function(group, silent) {
        data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'saveGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    saveUser : function(user, silent) {
        data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'saveUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, silent);
    },

    sendResetCode : function(title,text,username, silent) {
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

    updatePassword : function(userId,oldPassword,newPassword, silent) {
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

}
GetShopApiWebSocket.UtilManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UtilManager.prototype = {
    getCompanyFromBrReg : function(companyVatNumber, silent) {
        data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, silent);
    },

}
GetShopApiWebSocket.YouTubeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.YouTubeManager.prototype = {
    searchYoutube : function(searchword, silent) {
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
    this.AppManager = new GetShopApiWebSocket.AppManager(this);
    this.CalendarManager = new GetShopApiWebSocket.CalendarManager(this);
    this.CartManager = new GetShopApiWebSocket.CartManager(this);
    this.ChatManager = new GetShopApiWebSocket.ChatManager(this);
    this.GalleryManager = new GetShopApiWebSocket.GalleryManager(this);
    this.GetShop = new GetShopApiWebSocket.GetShop(this);
    this.ListManager = new GetShopApiWebSocket.ListManager(this);
    this.MessageManager = new GetShopApiWebSocket.MessageManager(this);
    this.MobileManager = new GetShopApiWebSocket.MobileManager(this);
    this.OrderManager = new GetShopApiWebSocket.OrderManager(this);
    this.PageManager = new GetShopApiWebSocket.PageManager(this);
    this.InvoiceManager = new GetShopApiWebSocket.InvoiceManager(this);
    this.ProductManager = new GetShopApiWebSocket.ProductManager(this);
    this.ReportingManager = new GetShopApiWebSocket.ReportingManager(this);
    this.StoreManager = new GetShopApiWebSocket.StoreManager(this);
    this.UserManager = new GetShopApiWebSocket.UserManager(this);
    this.UtilManager = new GetShopApiWebSocket.UtilManager(this);
    this.YouTubeManager = new GetShopApiWebSocket.YouTubeManager(this);
}
