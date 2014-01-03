GetShopApiWebSocket = function(address) {
    this.sentMessages =  [];
    this.address = address;
};

GetShopApiWebSocket.prototype = {
    websocket: null,
    connectionEstablished: null,
    
    connect: function() {
        
        var me = this;
        if (this.connectionEstablished === null) {
            this.fireDisconnectedEvent();
        }
        var address = "ws://"+this.address+":31330/";
        this.socket = new WebSocket(address);
        this.socket.onopen = function() {
            me.connected();
        };
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
    },
            
    reconnect: function() {
        var me = this;
        exec = function() {
            me.connect();
        };
        setTimeout(exec, 300);
    },
            
    initializeStore: function() {
        this.socket.send('initstore:'+this.address);
    },
            
    connected: function() {
        this.initializeStore();
        this.fireConnectedEvent();
        this.connectionEstablished = true;
    },
            
    disconnected: function() {
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
        
    send: function(message) {
        var deferred = $.Deferred();
        message.messageId = this.makeid();
        deferred.messageId = message.messageId;
        messageJson = JSON.stringify(message);
        this.sentMessages.push(deferred);
        this.socket.send(messageJson);
        return deferred;
    },

    getMessage: function(id) {
        for (i=0;i<this.sentMessages.length; i++) {
            if (this.sentMessages[i].id === id) {
                var message = this.sentMessages[i];
                this.sentMessages.pop(i);
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
};

GetShopApiWebSocket.BannerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BannerManager.prototype = {
    addImage : function(id,fileId) {
        data = {
            args : {
                id : id,
                fileId : fileId,
            },
            method: 'addImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

    createSet : function(width,height,id) {
        data = {
            args : {
                width : width,
                height : height,
                id : id,
            },
            method: 'createSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

    deleteSet : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deleteSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

    getSet : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

    linkProductToImage : function(bannerSetId,imageId,productId) {
        data = {
            args : {
                bannerSetId : bannerSetId,
                imageId : imageId,
                productId : productId,
            },
            method: 'linkProductToImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

    removeImage : function(bannerSetId,fileId) {
        data = {
            args : {
                bannerSetId : bannerSetId,
                fileId : fileId,
            },
            method: 'removeImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

    saveSet : function(set) {
        data = {
            args : {
                set : set,
            },
            method: 'saveSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.ContentManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ContentManager.prototype = {
    createContent : function(content) {
        data = {
            args : {
                content : content,
            },
            method: 'createContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data);
    },

    deleteContent : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deleteContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data);
    },

    getAllContent : function() {
        data = {
            args : {
            },
            method: 'getAllContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data);
    },

    getContent : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data);
    },

    saveContent : function(id,content) {
        data = {
            args : {
                id : id,
                content : content,
            },
            method: 'saveContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.FooterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FooterManager.prototype = {
    getConfiguration : function() {
        data = {
            args : {
            },
            method: 'getConfiguration',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data);
    },

    setLayout : function(numberOfColumns) {
        data = {
            args : {
                numberOfColumns : numberOfColumns,
            },
            method: 'setLayout',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data);
    },

    setType : function(column,type) {
        data = {
            args : {
                column : column,
                type : type,
            },
            method: 'setType',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.LogoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LogoManager.prototype = {
    deleteLogo : function() {
        data = {
            args : {
            },
            method: 'deleteLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data);
    },

    getLogo : function() {
        data = {
            args : {
            },
            method: 'getLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data);
    },

    setLogo : function(fileId) {
        data = {
            args : {
                fileId : fileId,
            },
            method: 'setLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.NewsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsManager.prototype = {
    addNews : function(news) {
        data = {
            args : {
                news : news,
            },
            method: 'addNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data);
    },

    addSubscriber : function(email) {
        data = {
            args : {
                email : email,
            },
            method: 'addSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data);
    },

    deleteNews : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deleteNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data);
    },

    getAllNews : function() {
        data = {
            args : {
            },
            method: 'getAllNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data);
    },

    getAllSubscribers : function() {
        data = {
            args : {
            },
            method: 'getAllSubscribers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data);
    },

    removeSubscriber : function(subscriberId) {
        data = {
            args : {
                subscriberId : subscriberId,
            },
            method: 'removeSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.AppManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AppManager.prototype = {
    createApplication : function(appName) {
        data = {
            args : {
                appName : appName,
            },
            method: 'createApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    deleteApplication : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deleteApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    getAllApplicationSubscriptions : function(includeAppSettings) {
        data = {
            args : {
                includeAppSettings : includeAppSettings,
            },
            method: 'getAllApplicationSubscriptions',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    getAllApplications : function() {
        data = {
            args : {
            },
            method: 'getAllApplications',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    getApplication : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    getSyncApplications : function() {
        data = {
            args : {
            },
            method: 'getSyncApplications',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    getUnpayedSubscription : function() {
        data = {
            args : {
            },
            method: 'getUnpayedSubscription',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    isSyncToolConnected : function() {
        data = {
            args : {
            },
            method: 'isSyncToolConnected',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    saveApplication : function(settings) {
        data = {
            args : {
                settings : settings,
            },
            method: 'saveApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

    setSyncApplication : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'setSyncApplication',
            interfaceName: 'core.appmanager.IAppManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.CalendarManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CalendarManager.prototype = {
    addUserToEvent : function(userId,eventId,password,username) {
        data = {
            args : {
                userId : userId,
                eventId : eventId,
                password : password,
                username : username,
            },
            method: 'addUserToEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    applyFilter : function(filters) {
        data = {
            args : {
                filters : filters,
            },
            method: 'applyFilter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    confirmEntry : function(entryId) {
        data = {
            args : {
                entryId : entryId,
            },
            method: 'confirmEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    createEntry : function(year,month,day) {
        data = {
            args : {
                year : year,
                month : month,
                day : day,
            },
            method: 'createEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    deleteEntry : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deleteEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    getActiveFilters : function() {
        data = {
            args : {
            },
            method: 'getActiveFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    getEntries : function(year,month,day,filters) {
        data = {
            args : {
                year : year,
                month : month,
                day : day,
                filters : filters,
            },
            method: 'getEntries',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    getEntry : function(entryId) {
        data = {
            args : {
                entryId : entryId,
            },
            method: 'getEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    getFilters : function() {
        data = {
            args : {
            },
            method: 'getFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    getMonth : function(year,month,includeExtraEvents) {
        data = {
            args : {
                year : year,
                month : month,
                includeExtraEvents : includeExtraEvents,
            },
            method: 'getMonth',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    removeUserFromEvent : function(userId,eventId) {
        data = {
            args : {
                userId : userId,
                eventId : eventId,
            },
            method: 'removeUserFromEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    saveEntry : function(entry) {
        data = {
            args : {
                entry : entry,
            },
            method: 'saveEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

    sendReminderToUser : function(byEmail,bySMS,users,text,subject) {
        data = {
            args : {
                byEmail : byEmail,
                bySMS : bySMS,
                users : users,
                text : text,
                subject : subject,
            },
            method: 'sendReminderToUser',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.CartManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CartManager.prototype = {
    addProduct : function(productId,count,variations) {
        data = {
            args : {
                productId : productId,
                count : count,
                variations : variations,
            },
            method: 'addProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    calculateTotalCost : function(cart) {
        data = {
            args : {
                cart : cart,
            },
            method: 'calculateTotalCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    clear : function() {
        data = {
            args : {
            },
            method: 'clear',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    getCart : function() {
        data = {
            args : {
            },
            method: 'getCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    getCartTotalAmount : function() {
        data = {
            args : {
            },
            method: 'getCartTotalAmount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    getShippingCost : function() {
        data = {
            args : {
            },
            method: 'getShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    getShippingPriceBasis : function() {
        data = {
            args : {
            },
            method: 'getShippingPriceBasis',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    removeProduct : function(cartItemId) {
        data = {
            args : {
                cartItemId : cartItemId,
            },
            method: 'removeProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    setAddress : function(address) {
        data = {
            args : {
                address : address,
            },
            method: 'setAddress',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    setShippingCost : function(shippingCost) {
        data = {
            args : {
                shippingCost : shippingCost,
            },
            method: 'setShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

    updateProductCount : function(cartItemId,count) {
        data = {
            args : {
                cartItemId : cartItemId,
                count : count,
            },
            method: 'updateProductCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.ChatManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ChatManager.prototype = {
    closeChat : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'closeChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data);
    },

    getChatters : function() {
        data = {
            args : {
            },
            method: 'getChatters',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data);
    },

    getMessages : function() {
        data = {
            args : {
            },
            method: 'getMessages',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data);
    },

    pingMobileChat : function(chatterid) {
        data = {
            args : {
                chatterid : chatterid,
            },
            method: 'pingMobileChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data);
    },

    replyToChat : function(id,message) {
        data = {
            args : {
                id : id,
                message : message,
            },
            method: 'replyToChat',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data);
    },

    sendMessage : function(message) {
        data = {
            args : {
                message : message,
            },
            method: 'sendMessage',
            interfaceName: 'core.chat.IChatManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.GalleryManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GalleryManager.prototype = {
    addImageToGallery : function(galleryId,imageId,description,title) {
        data = {
            args : {
                galleryId : galleryId,
                imageId : imageId,
                description : description,
                title : title,
            },
            method: 'addImageToGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data);
    },

    createImageGallery : function() {
        data = {
            args : {
            },
            method: 'createImageGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data);
    },

    deleteImage : function(entryId) {
        data = {
            args : {
                entryId : entryId,
            },
            method: 'deleteImage',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data);
    },

    getAllImages : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getAllImages',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data);
    },

    getEntry : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data);
    },

    saveEntry : function(entry) {
        data = {
            args : {
                entry : entry,
            },
            method: 'saveEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.GetShop = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShop.prototype = {
    addUserToPartner : function(userId,partner,password) {
        data = {
            args : {
                userId : userId,
                partner : partner,
                password : password,
            },
            method: 'addUserToPartner',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

    connectStoreToPartner : function(partner) {
        data = {
            args : {
                partner : partner,
            },
            method: 'connectStoreToPartner',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

    findAddressForApplication : function(uuid) {
        data = {
            args : {
                uuid : uuid,
            },
            method: 'findAddressForApplication',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

    findAddressForUUID : function(uuid) {
        data = {
            args : {
                uuid : uuid,
            },
            method: 'findAddressForUUID',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

    getPartnerId : function() {
        data = {
            args : {
            },
            method: 'getPartnerId',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

    getStores : function(code) {
        data = {
            args : {
                code : code,
            },
            method: 'getStores',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

    getStoresConnectedToMe : function() {
        data = {
            args : {
            },
            method: 'getStoresConnectedToMe',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.ListManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ListManager.prototype = {
    addEntry : function(listId,entry,parentPageId) {
        data = {
            args : {
                listId : listId,
                entry : entry,
                parentPageId : parentPageId,
            },
            method: 'addEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    clearList : function(listId) {
        data = {
            args : {
                listId : listId,
            },
            method: 'clearList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    combineList : function(toListId,newListId) {
        data = {
            args : {
                toListId : toListId,
                newListId : newListId,
            },
            method: 'combineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    createListId : function() {
        data = {
            args : {
            },
            method: 'createListId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    deleteEntry : function(id,listId) {
        data = {
            args : {
                id : id,
                listId : listId,
            },
            method: 'deleteEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    getCombinedLists : function(listId) {
        data = {
            args : {
                listId : listId,
            },
            method: 'getCombinedLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    getList : function(listId) {
        data = {
            args : {
                listId : listId,
            },
            method: 'getList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    getListEntry : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getListEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    getLists : function() {
        data = {
            args : {
            },
            method: 'getLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    orderEntry : function(id,after,parentId) {
        data = {
            args : {
                id : id,
                after : after,
                parentId : parentId,
            },
            method: 'orderEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    translateEntries : function(entryIds) {
        data = {
            args : {
                entryIds : entryIds,
            },
            method: 'translateEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    unCombineList : function(fromListId,toRemoveId) {
        data = {
            args : {
                fromListId : fromListId,
                toRemoveId : toRemoveId,
            },
            method: 'unCombineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

    updateEntry : function(entry) {
        data = {
            args : {
                entry : entry,
            },
            method: 'updateEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.MessageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MessageManager.prototype = {
    getSmsCount : function(year,month) {
        data = {
            args : {
                year : year,
                month : month,
            },
            method: 'getSmsCount',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data);
    },

    sendMail : function(to,toName,subject,content,from,fromName) {
        data = {
            args : {
                to : to,
                toName : toName,
                subject : subject,
                content : content,
                from : from,
                fromName : fromName,
            },
            method: 'sendMail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.OrderManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.OrderManager.prototype = {
    createOrder : function(address) {
        data = {
            args : {
                address : address,
            },
            method: 'createOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data);
    },

    getOrder : function(orderId) {
        data = {
            args : {
                orderId : orderId,
            },
            method: 'getOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data);
    },

    getOrders : function(orderIds,page,pageSize) {
        data = {
            args : {
                orderIds : orderIds,
                page : page,
                pageSize : pageSize,
            },
            method: 'getOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data);
    },

    saveOrder : function(order) {
        data = {
            args : {
                order : order,
            },
            method: 'saveOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data);
    },

    setOrderStatus : function(password,orderId,currency,price,status) {
        data = {
            args : {
                password : password,
                orderId : orderId,
                currency : currency,
                price : price,
                status : status,
            },
            method: 'setOrderStatus',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.PageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PageManager.prototype = {
    addApplication : function(applicationSettingId) {
        data = {
            args : {
                applicationSettingId : applicationSettingId,
            },
            method: 'addApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    addApplicationToPage : function(pageId,applicationSettingId,pageArea) {
        data = {
            args : {
                pageId : pageId,
                applicationSettingId : applicationSettingId,
                pageArea : pageArea,
            },
            method: 'addApplicationToPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    addExistingApplicationToPageArea : function(pageId,appId,area) {
        data = {
            args : {
                pageId : pageId,
                appId : appId,
                area : area,
            },
            method: 'addExistingApplicationToPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    changePageLayout : function(pageId,layout) {
        data = {
            args : {
                pageId : pageId,
                layout : layout,
            },
            method: 'changePageLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    changePageUserLevel : function(pageId,userLevel) {
        data = {
            args : {
                pageId : pageId,
                userLevel : userLevel,
            },
            method: 'changePageUserLevel',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    clearPageArea : function(pageId,pageArea) {
        data = {
            args : {
                pageId : pageId,
                pageArea : pageArea,
            },
            method: 'clearPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    createPage : function(layout,parentId) {
        data = {
            args : {
                layout : layout,
                parentId : parentId,
            },
            method: 'createPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    createPageWithId : function(layout,parentId,id) {
        data = {
            args : {
                layout : layout,
                parentId : parentId,
                id : id,
            },
            method: 'createPageWithId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    deleteApplication : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deleteApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    deletePage : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'deletePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getApplicationSettings : function(name) {
        data = {
            args : {
                name : name,
            },
            method: 'getApplicationSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getApplications : function() {
        data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getApplicationsBasedOnApplicationSettingsId : function(appSettingsId) {
        data = {
            args : {
                appSettingsId : appSettingsId,
            },
            method: 'getApplicationsBasedOnApplicationSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getApplicationsByPageAreaAndSettingsId : function(appSettingsId,pageArea) {
        data = {
            args : {
                appSettingsId : appSettingsId,
                pageArea : pageArea,
            },
            method: 'getApplicationsByPageAreaAndSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getApplicationsByType : function(type) {
        data = {
            args : {
                type : type,
            },
            method: 'getApplicationsByType',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getApplicationsForPage : function(pageId) {
        data = {
            args : {
                pageId : pageId,
            },
            method: 'getApplicationsForPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getPage : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getPagesForApplications : function(appIds) {
        data = {
            args : {
                appIds : appIds,
            },
            method: 'getPagesForApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    getSecuredSettings : function(appName) {
        data = {
            args : {
                appName : appName,
            },
            method: 'getSecuredSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    removeAllApplications : function(appSettingsId) {
        data = {
            args : {
                appSettingsId : appSettingsId,
            },
            method: 'removeAllApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    removeApplication : function(applicationId,pageid) {
        data = {
            args : {
                applicationId : applicationId,
                pageid : pageid,
            },
            method: 'removeApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    reorderApplication : function(pageId,appId,moveUp) {
        data = {
            args : {
                pageId : pageId,
                appId : appId,
                moveUp : moveUp,
            },
            method: 'reorderApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    saveApplicationConfiguration : function(config) {
        data = {
            args : {
                config : config,
            },
            method: 'saveApplicationConfiguration',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    setApplicationSettings : function(settings) {
        data = {
            args : {
                settings : settings,
            },
            method: 'setApplicationSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    setApplicationSticky : function(appId,toggle) {
        data = {
            args : {
                appId : appId,
                toggle : toggle,
            },
            method: 'setApplicationSticky',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    setPageDescription : function(pageId,description) {
        data = {
            args : {
                pageId : pageId,
                description : description,
            },
            method: 'setPageDescription',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    setParentPage : function(pageId,parentPageId) {
        data = {
            args : {
                pageId : pageId,
                parentPageId : parentPageId,
            },
            method: 'setParentPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    swapApplication : function(fromAppId,toAppId) {
        data = {
            args : {
                fromAppId : fromAppId,
                toAppId : toAppId,
            },
            method: 'swapApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

    translatePages : function(pages) {
        data = {
            args : {
                pages : pages,
            },
            method: 'translatePages',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.ProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ProductManager.prototype = {
    addAttributeGroupToProduct : function(productId,attributeGroup,attribute) {
        data = {
            args : {
                productId : productId,
                attributeGroup : attributeGroup,
                attribute : attribute,
            },
            method: 'addAttributeGroupToProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    addImage : function(productId,productImageId,description) {
        data = {
            args : {
                productId : productId,
                productImageId : productImageId,
                description : description,
            },
            method: 'addImage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    changeStockQuantity : function(productId,count) {
        data = {
            args : {
                productId : productId,
                count : count,
            },
            method: 'changeStockQuantity',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    createProduct : function() {
        data = {
            args : {
            },
            method: 'createProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    deleteAttribute : function(groupName,attribute) {
        data = {
            args : {
                groupName : groupName,
                attribute : attribute,
            },
            method: 'deleteAttribute',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    deleteGroup : function(groupName) {
        data = {
            args : {
                groupName : groupName,
            },
            method: 'deleteGroup',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getAttributeSummary : function() {
        data = {
            args : {
            },
            method: 'getAttributeSummary',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getLatestProducts : function(count) {
        data = {
            args : {
                count : count,
            },
            method: 'getLatestProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getPrice : function(productId,variations) {
        data = {
            args : {
                productId : productId,
                variations : variations,
            },
            method: 'getPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getProduct : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getProductFromApplicationId : function(app_uuid) {
        data = {
            args : {
                app_uuid : app_uuid,
            },
            method: 'getProductFromApplicationId',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getProducts : function(productCriteria) {
        data = {
            args : {
                productCriteria : productCriteria,
            },
            method: 'getProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    getRandomProducts : function(fetchSize,ignoreProductId) {
        data = {
            args : {
                fetchSize : fetchSize,
                ignoreProductId : ignoreProductId,
            },
            method: 'getRandomProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    removeAttributeGroupFromProduct : function(productId,attributeGroupId) {
        data = {
            args : {
                productId : productId,
                attributeGroupId : attributeGroupId,
            },
            method: 'removeAttributeGroupFromProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    removeProduct : function(productId) {
        data = {
            args : {
                productId : productId,
            },
            method: 'removeProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    renameAttribute : function(groupName,oldAttributeName,newAttributeName) {
        data = {
            args : {
                groupName : groupName,
                oldAttributeName : oldAttributeName,
                newAttributeName : newAttributeName,
            },
            method: 'renameAttribute',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    renameAttributeGroupName : function(oldName,newName) {
        data = {
            args : {
                oldName : oldName,
                newName : newName,
            },
            method: 'renameAttributeGroupName',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    saveProduct : function(product) {
        data = {
            args : {
                product : product,
            },
            method: 'saveProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    setMainImage : function(productId,imageId) {
        data = {
            args : {
                productId : productId,
                imageId : imageId,
            },
            method: 'setMainImage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

    translateEntries : function(entryIds) {
        data = {
            args : {
                entryIds : entryIds,
            },
            method: 'translateEntries',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.ReportingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ReportingManager.prototype = {
    getAllEventsFromSession : function(startDate,stopDate,searchSessionId) {
        data = {
            args : {
                startDate : startDate,
                stopDate : stopDate,
                searchSessionId : searchSessionId,
            },
            method: 'getAllEventsFromSession',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

    getConnectedUsers : function(startdate,stopDate,filter) {
        data = {
            args : {
                startdate : startdate,
                stopDate : stopDate,
                filter : filter,
            },
            method: 'getConnectedUsers',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

    getOrdersCreated : function(startDate,stopDate) {
        data = {
            args : {
                startDate : startDate,
                stopDate : stopDate,
            },
            method: 'getOrdersCreated',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

    getPageViews : function(startDate,stopDate) {
        data = {
            args : {
                startDate : startDate,
                stopDate : stopDate,
            },
            method: 'getPageViews',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

    getProductViewed : function(startDate,stopDate) {
        data = {
            args : {
                startDate : startDate,
                stopDate : stopDate,
            },
            method: 'getProductViewed',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

    getReport : function(startDate,stopDate,type) {
        data = {
            args : {
                startDate : startDate,
                stopDate : stopDate,
                type : type,
            },
            method: 'getReport',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

    getUserLoggedOn : function(startDate,stopDate) {
        data = {
            args : {
                startDate : startDate,
                stopDate : stopDate,
            },
            method: 'getUserLoggedOn',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.StoreManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreManager.prototype = {
    createStore : function(hostname,email,password) {
        data = {
            args : {
                hostname : hostname,
                email : email,
                password : password,
            },
            method: 'createStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    delete : function() {
        data = {
            args : {
            },
            method: 'delete',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    enableExtendedMode : function(toggle,password) {
        data = {
            args : {
                toggle : toggle,
                password : password,
            },
            method: 'enableExtendedMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    enableSMSAccess : function(toggle,password) {
        data = {
            args : {
                toggle : toggle,
                password : password,
            },
            method: 'enableSMSAccess',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    getMyStore : function() {
        data = {
            args : {
            },
            method: 'getMyStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    getStoreId : function() {
        data = {
            args : {
            },
            method: 'getStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    initializeStore : function(webAddress,initSessionId) {
        data = {
            args : {
                webAddress : webAddress,
                initSessionId : initSessionId,
            },
            method: 'initializeStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    isAddressTaken : function(address) {
        data = {
            args : {
                address : address,
            },
            method: 'isAddressTaken',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    removeDomainName : function(domainName) {
        data = {
            args : {
                domainName : domainName,
            },
            method: 'removeDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    saveStore : function(config) {
        data = {
            args : {
                config : config,
            },
            method: 'saveStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    setIntroductionRead : function() {
        data = {
            args : {
            },
            method: 'setIntroductionRead',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    setPrimaryDomainName : function(domainName) {
        data = {
            args : {
                domainName : domainName,
            },
            method: 'setPrimaryDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

    setVIS : function(toggle,password) {
        data = {
            args : {
                toggle : toggle,
                password : password,
            },
            method: 'setVIS',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data);
    },

}
GetShopApiWebSocket.UserManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UserManager.prototype = {
    createUser : function(user) {
        data = {
            args : {
                user : user,
            },
            method: 'createUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    deleteUser : function(userId) {
        data = {
            args : {
                userId : userId,
            },
            method: 'deleteUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    findUsers : function(searchCriteria) {
        data = {
            args : {
                searchCriteria : searchCriteria,
            },
            method: 'findUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getAdministratorCount : function() {
        data = {
            args : {
            },
            method: 'getAdministratorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getAllGroups : function() {
        data = {
            args : {
            },
            method: 'getAllGroups',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getAllUsers : function() {
        data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getCustomersCount : function() {
        data = {
            args : {
            },
            method: 'getCustomersCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getEditorCount : function() {
        data = {
            args : {
            },
            method: 'getEditorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getLoggedOnUser : function() {
        data = {
            args : {
            },
            method: 'getLoggedOnUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getUserById : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'getUserById',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    getUserList : function(userIds) {
        data = {
            args : {
                userIds : userIds,
            },
            method: 'getUserList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    isCaptain : function(id) {
        data = {
            args : {
                id : id,
            },
            method: 'isCaptain',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    isLoggedIn : function() {
        data = {
            args : {
            },
            method: 'isLoggedIn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    logOn : function(username,password) {
        data = {
            args : {
                username : username,
                password : password,
            },
            method: 'logOn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    logonUsingKey : function(logonKey) {
        data = {
            args : {
                logonKey : logonKey,
            },
            method: 'logonUsingKey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    logout : function() {
        data = {
            args : {
            },
            method: 'logout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    removeGroup : function(groupId) {
        data = {
            args : {
                groupId : groupId,
            },
            method: 'removeGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    resetPassword : function(resetCode,username,newPassword) {
        data = {
            args : {
                resetCode : resetCode,
                username : username,
                newPassword : newPassword,
            },
            method: 'resetPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    saveGroup : function(group) {
        data = {
            args : {
                group : group,
            },
            method: 'saveGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    saveUser : function(user) {
        data = {
            args : {
                user : user,
            },
            method: 'saveUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    sendResetCode : function(title,text,username) {
        data = {
            args : {
                title : title,
                text : text,
                username : username,
            },
            method: 'sendResetCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
    },

    updatePassword : function(userId,oldPassword,newPassword) {
        data = {
            args : {
                userId : userId,
                oldPassword : oldPassword,
                newPassword : newPassword,
            },
            method: 'updatePassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data);
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
    this.OrderManager = new GetShopApiWebSocket.OrderManager(this);
    this.PageManager = new GetShopApiWebSocket.PageManager(this);
    this.ProductManager = new GetShopApiWebSocket.ProductManager(this);
    this.ReportingManager = new GetShopApiWebSocket.ReportingManager(this);
    this.StoreManager = new GetShopApiWebSocket.StoreManager(this);
    this.UserManager = new GetShopApiWebSocket.UserManager(this);
}
