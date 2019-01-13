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


GetShopApiWebSocket.BannerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BannerManager.prototype = {
    'addImage' : function(id,fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                fileId : JSON.stringify(fileId),
            },
            method: 'addImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addSlide' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'addSlide',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createSet' : function(width,height,id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                width : JSON.stringify(width),
                height : JSON.stringify(height),
                id : JSON.stringify(id),
            },
            method: 'createSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteSet' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteSlide' : function(slideId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slideId : JSON.stringify(slideId),
            },
            method: 'deleteSlide',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSet' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSlideById' : function(slideId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slideId : JSON.stringify(slideId),
            },
            method: 'getSlideById',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'linkProductToImage' : function(bannerSetId,imageId,productId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bannerSetId : JSON.stringify(bannerSetId),
                imageId : JSON.stringify(imageId),
                productId : JSON.stringify(productId),
            },
            method: 'linkProductToImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeImage' : function(bannerSetId,fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bannerSetId : JSON.stringify(bannerSetId),
                fileId : JSON.stringify(fileId),
            },
            method: 'removeImage',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveSet' : function(set, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                set : JSON.stringify(set),
            },
            method: 'saveSet',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setImageForSlide' : function(slideId,fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slideId : JSON.stringify(slideId),
                fileId : JSON.stringify(fileId),
            },
            method: 'setImageForSlide',
            interfaceName: 'app.banner.IBannerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ContentManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ContentManager.prototype = {
    'createContent' : function(content, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                content : JSON.stringify(content),
            },
            method: 'createContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteContent' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllContent' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getContent' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveContent' : function(id,content, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                content : JSON.stringify(content),
            },
            method: 'saveContent',
            interfaceName: 'app.content.IContentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.FooterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FooterManager.prototype = {
    'getConfiguration' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getConfiguration',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setLayout' : function(numberOfColumns, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                numberOfColumns : JSON.stringify(numberOfColumns),
            },
            method: 'setLayout',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setType' : function(column,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                column : JSON.stringify(column),
                type : JSON.stringify(type),
            },
            method: 'setType',
            interfaceName: 'app.footer.IFooterManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.LogoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LogoManager.prototype = {
    'deleteLogo' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'deleteLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogo' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setLogo' : function(fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'setLogo',
            interfaceName: 'app.logo.ILogoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.NewsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsManager.prototype = {
    'addNews' : function(newsEntry,newsListId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newsEntry : JSON.stringify(newsEntry),
                newsListId : JSON.stringify(newsListId),
            },
            method: 'addNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addSubscriber' : function(email, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'addSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'applyUserFilter' : function(newsListId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
                userId : JSON.stringify(userId),
            },
            method: 'applyUserFilter',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeDateOfNews' : function(id,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                date : JSON.stringify(date),
            },
            method: 'changeDateOfNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteNews' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllNews' : function(newsListId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
            },
            method: 'getAllNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllSubscribers' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllSubscribers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNewsForPage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getNewsForPage',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNewsUsers' : function(newsListId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
            },
            method: 'getNewsUsers',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isFiltered' : function(newsListId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newsListId : JSON.stringify(newsListId),
                userId : JSON.stringify(userId),
            },
            method: 'isFiltered',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'publishNews' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'publishNews',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeSubscriber' : function(subscriberId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                subscriberId : JSON.stringify(subscriberId),
            },
            method: 'removeSubscriber',
            interfaceName: 'app.news.INewsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.AccountingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AccountingManager.prototype = {
    'createCombinedOrderFile' : function(newUsersOnly, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newUsersOnly : JSON.stringify(newUsersOnly),
            },
            method: 'createCombinedOrderFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createCreditorFile' : function(newOnly, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newOnly : JSON.stringify(newOnly),
            },
            method: 'createCreditorFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrderFile' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createOrderFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createUserFile' : function(newOnly, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newOnly : JSON.stringify(newOnly),
            },
            method: 'createUserFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteFile' : function(fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'deleteFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'downloadOrderFileNewType' : function(configId,start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'downloadOrderFileNewType',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'forceTransferFiles' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'forceTransferFiles',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccountingConfig' : function(configId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
            },
            method: 'getAccountingConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccountingManagerConfig' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAccountingManagerConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllConfigs' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllConfigs',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllFiles' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllFiles',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllFilesNotTransferredToAccounting' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllFilesNotTransferredToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFile' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFileById' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFileById',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFileByIdResend' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFileByIdResend',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLatestLogEntries' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLatestLogEntries',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNewFile' : function(type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getNewFile',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStats' : function(configId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
            },
            method: 'getStats',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsTransferredToAccounting' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'markAsTransferredToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeTransferConfig' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'removeTransferConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetAllAccounting' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'resetAllAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveConfig' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setAccountingManagerConfig' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'setAccountingManagerConfig',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferAllToNewSystem' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'transferAllToNewSystem',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferFiles' : function(type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'transferFiles',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferFilesToAccounting' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'transferFilesToAccounting',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferSingleOrders' : function(configId,incOrderIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                configId : JSON.stringify(configId),
                incOrderIds : JSON.stringify(incOrderIds),
            },
            method: 'transferSingleOrders',
            interfaceName: 'core.accountingmanager.IAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.AmestoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.AmestoManager.prototype = {
    'syncAllCostumers' : function(hostname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'syncAllCostumers',
            interfaceName: 'core.amesto.IAmestoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'syncAllOrders' : function(hostname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'syncAllOrders',
            interfaceName: 'core.amesto.IAmestoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'syncAllStockQuantity' : function(hostname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'syncAllStockQuantity',
            interfaceName: 'core.amesto.IAmestoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ApacManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ApacManager.prototype = {
    'getAccessList' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAccessList',
            multiLevelName: multilevelname,
            interfaceName: 'core.apacmanager.IApacManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllDoors' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllDoors',
            multiLevelName: multilevelname,
            interfaceName: 'core.apacmanager.IApacManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApacAccess' : function(multilevelname, accessId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                accessId : JSON.stringify(accessId),
            },
            method: 'getApacAccess',
            multiLevelName: multilevelname,
            interfaceName: 'core.apacmanager.IApacManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'grantAccess' : function(multilevelname, apacAccess, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                apacAccess : JSON.stringify(apacAccess),
            },
            method: 'grantAccess',
            multiLevelName: multilevelname,
            interfaceName: 'core.apacmanager.IApacManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAccess' : function(multilevelname, accessId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                accessId : JSON.stringify(accessId),
            },
            method: 'removeAccess',
            multiLevelName: multilevelname,
            interfaceName: 'core.apacmanager.IApacManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendSms' : function(multilevelname, accessId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                accessId : JSON.stringify(accessId),
                message : JSON.stringify(message),
            },
            method: 'sendSms',
            multiLevelName: multilevelname,
            interfaceName: 'core.apacmanager.IApacManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GetShopApplicationPool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopApplicationPool.prototype = {
    'deleteApplication' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'deleteApplication',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'get' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'get',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveApplication' : function(application, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                application : JSON.stringify(application),
            },
            method: 'saveApplication',
            interfaceName: 'core.applications.IGetShopApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.StoreApplicationInstancePool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreApplicationInstancePool.prototype = {
    'createNewInstance' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'createNewInstance',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewInstanceWithId' : function(applicationId,instanceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                instanceId : JSON.stringify(instanceId),
            },
            method: 'createNewInstanceWithId',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationInstance' : function(applicationInstanceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getApplicationInstance',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationInstanceWithModule' : function(applicationInstanceId,moduleName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
                moduleName : JSON.stringify(moduleName),
            },
            method: 'getApplicationInstanceWithModule',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationInstances' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'getApplicationInstances',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setApplicationSettings' : function(settings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'setApplicationSettings',
            interfaceName: 'core.applications.IStoreApplicationInstancePool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.StoreApplicationPool = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreApplicationPool.prototype = {
    'activateApplication' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'activateApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'activateModule' : function(module, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                module : JSON.stringify(module),
            },
            method: 'activateModule',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deactivateApplication' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'deactivateApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getActivatedModules' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getActivatedModules',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getActivatedPaymentApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getActivatedPaymentApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAvailableModules' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllAvailableModules',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplication' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailableApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAvailableApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailableApplicationsThatIsNotActivated' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAvailableApplicationsThatIsNotActivated',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailableThemeApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAvailableThemeApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPaymentSettingsApplication' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPaymentSettingsApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getShippingApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getShippingApplications',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getThemeApplication' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getThemeApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isActivated' : function(appId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'isActivated',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSetting' : function(applicationId,settings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                settings : JSON.stringify(settings),
            },
            method: 'setSetting',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setThemeApplication' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'setThemeApplication',
            interfaceName: 'core.applications.IStoreApplicationPool',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.DoorManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DoorManager.prototype = {
    'addCard' : function(multilevelname, personId,card, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                personId : JSON.stringify(personId),
                card : JSON.stringify(card),
            },
            method: 'addCard',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearDoorCache' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearDoorCache',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'closeAllForTheDay' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'closeAllForTheDay',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'doorAction' : function(multilevelname, externalId,state, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                externalId : JSON.stringify(externalId),
                state : JSON.stringify(state),
            },
            method: 'doorAction',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generateDoorLogForAllDoorsFromResult' : function(multilevelname, resultFromArx, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                resultFromArx : JSON.stringify(resultFromArx),
            },
            method: 'generateDoorLogForAllDoorsFromResult',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAccessCategories' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllAccessCategories',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllDoors' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllDoors',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllPersons' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllPersons',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogForAllDoor' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getLogForAllDoor',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogForDoor' : function(multilevelname, externalId,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMasterCodes' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMasterCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPerson' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPerson',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pmsDoorAction' : function(multilevelname, code,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
                type : JSON.stringify(type),
            },
            method: 'pmsDoorAction',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveMasterCodes' : function(multilevelname, masterCodes, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                masterCodes : JSON.stringify(masterCodes),
            },
            method: 'saveMasterCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePerson' : function(multilevelname, person, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                person : JSON.stringify(person),
            },
            method: 'updatePerson',
            multiLevelName: multilevelname,
            interfaceName: 'core.arx.IDoorManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.BackupManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BackupManager.prototype = {
    'createBackup' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createBackup',
            interfaceName: 'core.backupmanager.IBackupManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.BamboraManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BamboraManager.prototype = {
    'checkForOrdersToCapture' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.bambora.IBamboraManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCheckoutUrl' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getCheckoutUrl',
            interfaceName: 'core.bambora.IBamboraManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.BigStock = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BigStock.prototype = {
    'addGetShopImageIdToBigStockOrder' : function(downloadUrl,imageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                downloadUrl : JSON.stringify(downloadUrl),
                imageId : JSON.stringify(imageId),
            },
            method: 'addGetShopImageIdToBigStockOrder',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailableCredits' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAvailableCredits',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'purchaseImage' : function(imageId,sizeCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                imageId : JSON.stringify(imageId),
                sizeCode : JSON.stringify(sizeCode),
            },
            method: 'purchaseImage',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCreditAccount' : function(credits,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                credits : JSON.stringify(credits),
                password : JSON.stringify(password),
            },
            method: 'setCreditAccount',
            interfaceName: 'core.bigstock.IBigStock',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.BookingEngine = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BookingEngine.prototype = {
    'canAddBooking' : function(multilevelname, bookingsToAdd, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingsToAdd : JSON.stringify(bookingsToAdd),
            },
            method: 'canAddBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'canAddBookings' : function(multilevelname, bookingsToAdd, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingsToAdd : JSON.stringify(bookingsToAdd),
            },
            method: 'canAddBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeBookingItemAndDateOnBooking' : function(multilevelname, booking,item,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeBookingItemOnBooking' : function(multilevelname, booking,item, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
                item : JSON.stringify(item),
            },
            method: 'changeBookingItemOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeBookingItemType' : function(multilevelname, itemId,newTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
                newTypeId : JSON.stringify(newTypeId),
            },
            method: 'changeBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeDatesOnBooking' : function(multilevelname, bookingId,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeSourceOnBooking' : function(multilevelname, bookingId,source, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                source : JSON.stringify(source),
            },
            method: 'changeSourceOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeTypeOnBooking' : function(multilevelname, bookingId,itemTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                itemTypeId : JSON.stringify(itemTypeId),
            },
            method: 'changeTypeOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkConsistency' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkConsistency',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createABookingItemType' : function(multilevelname, name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'createABookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteBooking' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteBookingItem' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteBookingItemType' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteOpeningHours' : function(multilevelname, repeaterId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                repeaterId : JSON.stringify(repeaterId),
            },
            method: 'deleteOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'forceUnassignBookingInfuture' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'forceUnassignBookingInfuture',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAvailbleItems' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllAvailbleItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAvailbleItemsWithBookingConsidered' : function(multilevelname, start,end,bookingid, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllBookings' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllBookingsByBookingItem' : function(multilevelname, bookingItemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemId : JSON.stringify(bookingItemId),
            },
            method: 'getAllBookingsByBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllBookingsByBookingItemInDateRange' : function(multilevelname, bookingItemId,start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemId : JSON.stringify(bookingItemId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllBookingsByBookingItemInDateRange',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailbleItems' : function(multilevelname, typeId,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailbleItemsWithBookingConsidered' : function(multilevelname, typeId,start,end,bookingId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailbleItemsWithBookingConsideredAndShuffling' : function(multilevelname, typeId,start,end,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                typeId : JSON.stringify(typeId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getAvailbleItemsWithBookingConsideredAndShuffling',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItem' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItemType' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItemTypes' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getBookingItemTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItemTypesWithSystemType' : function(multilevelname, systemType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemType : JSON.stringify(systemType),
            },
            method: 'getBookingItemTypesWithSystemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItems' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getBookingItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConfig' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDefaultRegistrationRules' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getDefaultRegistrationRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNumberOfAvailable' : function(multilevelname, itemType,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOpeningHours' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'getOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOpeningHoursWithType' : function(multilevelname, itemId,timePeriodeType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
                timePeriodeType : JSON.stringify(timePeriodeType),
            },
            method: 'getOpeningHoursWithType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTimelines' : function(multilevelname, id,startDate,endDate, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTimelinesDirect' : function(multilevelname, start,end,itemTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                itemTypeId : JSON.stringify(itemTypeId),
            },
            method: 'getTimelinesDirect',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveBookingItem' : function(multilevelname, item, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'saveBookingItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveDefaultRegistrationRules' : function(multilevelname, rules, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rules : JSON.stringify(rules),
            },
            method: 'saveDefaultRegistrationRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveOpeningHours' : function(multilevelname, time,itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                time : JSON.stringify(time),
                itemId : JSON.stringify(itemId),
            },
            method: 'saveOpeningHours',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setConfirmationRequired' : function(multilevelname, confirmationRequired, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                confirmationRequired : JSON.stringify(confirmationRequired),
            },
            method: 'setConfirmationRequired',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateBookingItemType' : function(multilevelname, type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'updateBookingItemType',
            multiLevelName: multilevelname,
            interfaceName: 'core.bookingengine.IBookingEngine',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.BrainTreeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BrainTreeManager.prototype = {
    'getClientToken' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getClientToken',
            interfaceName: 'core.braintree.IBrainTreeManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pay' : function(paymentMethodNonce,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentMethodNonce : JSON.stringify(paymentMethodNonce),
                orderId : JSON.stringify(orderId),
            },
            method: 'pay',
            interfaceName: 'core.braintree.IBrainTreeManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.C3Manager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.C3Manager.prototype = {
    'addForskningsUserPeriode' : function(periode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                periode : JSON.stringify(periode),
            },
            method: 'addForskningsUserPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addHour' : function(hour, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hour : JSON.stringify(hour),
            },
            method: 'addHour',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addTimeRate' : function(name,rate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
                rate : JSON.stringify(rate),
            },
            method: 'addTimeRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserProjectPeriode' : function(projectPeriode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectPeriode : JSON.stringify(projectPeriode),
            },
            method: 'addUserProjectPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'allowedFixedHourCosts' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'allowedFixedHourCosts',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'allowedNfrHour' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'allowedNfrHour',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'allowedNfrHourCurrentUser' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'allowedNfrHourCurrentUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'allowedNfrOtherCost' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'allowedNfrOtherCost',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'allowedNfrOtherCostCurrentUser' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'allowedNfrOtherCostCurrentUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignProjectToCompany' : function(companyId,projectId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                projectId : JSON.stringify(projectId),
            },
            method: 'assignProjectToCompany',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'calculateSum' : function(periodeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'calculateSum',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'canAdd' : function(hour, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hour : JSON.stringify(hour),
            },
            method: 'canAdd',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteC3Periode' : function(periodeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'deleteC3Periode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteForskningsUserPeriode' : function(periodeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'deleteForskningsUserPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteProject' : function(projectId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
            },
            method: 'deleteProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteProjectCost' : function(projectCostId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectCostId : JSON.stringify(projectCostId),
            },
            method: 'deleteProjectCost',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteTimeRate' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteTimeRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteWorkPackage' : function(workPackageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                workPackageId : JSON.stringify(workPackageId),
            },
            method: 'deleteWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAcceListForUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getAcceListForUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccessList' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAccessList',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccessListByProjectId' : function(projectId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
            },
            method: 'getAccessListByProjectId',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getActivePeriode' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getActivePeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllProjectsConnectedToCompany' : function(compnayId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                compnayId : JSON.stringify(compnayId),
            },
            method: 'getAllProjectsConnectedToCompany',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllProjectsConnectedToWorkPackage' : function(wpId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                wpId : JSON.stringify(wpId),
            },
            method: 'getAllProjectsConnectedToWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64ESAExcelReport' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getBase64ESAExcelReport',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64SFIExcelReport' : function(companyId,start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getBase64SFIExcelReport',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64SFIExcelReportTotal' : function(companyId,start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getBase64SFIExcelReportTotal',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentForskningsPeriode' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCurrentForskningsPeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getForskningsPeriodesForUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getForskningsPeriodesForUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getGroupInformation' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getGroupInformation',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getHourById' : function(hourId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hourId : JSON.stringify(hourId),
            },
            method: 'getHourById',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOtherCost' : function(otherCostId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                otherCostId : JSON.stringify(otherCostId),
            },
            method: 'getOtherCost',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPeriodes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPeriodes',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPeriodesForProject' : function(projectId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
            },
            method: 'getPeriodesForProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProject' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProjectCostsForAllUsersInCompany' : function(projectId,from,to, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getProjectCostsForAllUsersInCompany',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProjectCostsForCurrentUser' : function(projectId,from,to, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getProjectCostsForCurrentUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProjects' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getProjects',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReportForUserProject' : function(userId,projectId,start,end,forWorkPackageId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoundSum' : function(year, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getRoundSum',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTimeRate' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getTimeRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTimeRates' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTimeRates',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserProjectPeriodeById' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getUserProjectPeriodeById',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getWorkPackage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getWorkPackages' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getWorkPackages',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeCompanyAccess' : function(projectId,companyId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                projectId : JSON.stringify(projectId),
                companyId : JSON.stringify(companyId),
            },
            method: 'removeCompanyAccess',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeContract' : function(companyId,projectId,workPackageId,contractId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveGroupInfo' : function(groupId,type,value, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                type : JSON.stringify(type),
                value : JSON.stringify(value),
            },
            method: 'saveGroupInfo',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveOtherCosts' : function(otherCost, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                otherCost : JSON.stringify(otherCost),
            },
            method: 'saveOtherCosts',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePeriode' : function(periode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                periode : JSON.stringify(periode),
            },
            method: 'savePeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveProject' : function(project, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                project : JSON.stringify(project),
            },
            method: 'saveProject',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveRate' : function(rate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rate : JSON.stringify(rate),
            },
            method: 'saveRate',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveWorkPackage' : function(workPackage, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                workPackage : JSON.stringify(workPackage),
            },
            method: 'saveWorkPackage',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'search' : function(searchText, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchText : JSON.stringify(searchText),
            },
            method: 'search',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setActivePeriode' : function(periodeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                periodeId : JSON.stringify(periodeId),
            },
            method: 'setActivePeriode',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setC3RoundSum' : function(year,sum, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
                sum : JSON.stringify(sum),
            },
            method: 'setC3RoundSum',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setNfrAccess' : function(access, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                access : JSON.stringify(access),
            },
            method: 'setNfrAccess',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setProjectAccess' : function(companyId,projectId,workPackageId,value, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setProjectCust' : function(companyId,projectId,workPackageId,start,end,price,contractId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setRateToUser' : function(userId,rateId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                rateId : JSON.stringify(rateId),
            },
            method: 'setRateToUser',
            interfaceName: 'core.c3.IC3Manager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.CalendarManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CalendarManager.prototype = {
    'addEvent' : function(event, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'addEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserSilentlyToEvent' : function(eventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'addUserSilentlyToEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserToEvent' : function(userId,eventId,password,username,source, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserToPageEvent' : function(userId,bookingAppId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                bookingAppId : JSON.stringify(bookingAppId),
            },
            method: 'addUserToPageEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'applyFilter' : function(filters, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filters : JSON.stringify(filters),
            },
            method: 'applyFilter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'confirmEntry' : function(entryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'confirmEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createEntry' : function(year,month,day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                day : JSON.stringify(day),
            },
            method: 'createEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteEntry' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteLocation' : function(locationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'deleteLocation',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getActiveFilters' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getActiveFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllEventsConnectedToPage' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getAllEventsConnectedToPage',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllLocations' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllLocations',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntries' : function(year,month,day,filters, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntriesByUserId' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getEntriesByUserId',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntry' : function(entryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'getEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEvent' : function(eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventPartitipatedData' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEventPartitipatedData',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEvents' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEvents',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsGroupedByPageId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEventsGroupedByPageId',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFilters' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getFilters',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getHistory' : function(eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getHistory',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMonth' : function(year,month,includeExtraEvents, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
                includeExtraEvents : JSON.stringify(includeExtraEvents),
            },
            method: 'getMonth',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMonths' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMonths',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMonthsAfter' : function(year,month, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getMonthsAfter',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSignature' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSignature',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'placeOrder' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'placeOrder',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeUserFromEvent' : function(userId,eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'removeUserFromEvent',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveEntry' : function(entry, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveLocation' : function(location, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                location : JSON.stringify(location),
            },
            method: 'saveLocation',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendReminderToUser' : function(byEmail,bySMS,users,text,subject,eventId,attachment,sendReminderToUser, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setEventPartitipatedData' : function(eventData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventData : JSON.stringify(eventData),
            },
            method: 'setEventPartitipatedData',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSignature' : function(userid,signature, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userid : JSON.stringify(userid),
                signature : JSON.stringify(signature),
            },
            method: 'setSignature',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferFromWaitingList' : function(entryId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                userId : JSON.stringify(userId),
            },
            method: 'transferFromWaitingList',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferUser' : function(fromEventId,toEventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fromEventId : JSON.stringify(fromEventId),
                toEventId : JSON.stringify(toEventId),
                userId : JSON.stringify(userId),
            },
            method: 'transferUser',
            interfaceName: 'core.calendar.ICalendarManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.CartManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CartManager.prototype = {
    'addCoupon' : function(coupon, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                coupon : JSON.stringify(coupon),
            },
            method: 'addCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addMetaDataToProduct' : function(cartItemId,metaData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                metaData : JSON.stringify(metaData),
            },
            method: 'addMetaDataToProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addProduct' : function(productId,count,variations, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
                variations : JSON.stringify(variations),
            },
            method: 'addProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addProductItem' : function(productId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'addProductItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addProductWithSource' : function(productId,count,source, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
                source : JSON.stringify(source),
            },
            method: 'addProductWithSource',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'applyCouponToCurrentCart' : function(code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'applyCouponToCurrentCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'calculateTotalCost' : function(cart, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'calculateTotalCount' : function(cart, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'calculateTotalCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clear' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clear',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'filterByDate' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'filterByDate',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCart' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCartTotal' : function(cart, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'getCartTotal',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCartTotalAmount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCartTotalAmount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCoupon' : function(couponCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                couponCode : JSON.stringify(couponCode),
            },
            method: 'getCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCouponById' : function(couponId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                couponId : JSON.stringify(couponId),
            },
            method: 'getCouponById',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCoupons' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPartnershipCoupons' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPartnershipCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getShippingCost' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getShippingPriceBasis' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getShippingPriceBasis',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTaxes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasCoupons' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'hasCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isCartConflictingWithClosedPeriode' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isCartConflictingWithClosedPeriode',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'recalculateMetaData' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'recalculateMetaData',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'recalculateMetaDataCart' : function(cart, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'recalculateMetaDataCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAllCoupons' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'removeAllCoupons',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeCartItem' : function(cartItemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
            },
            method: 'removeCartItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeCoupon' : function(code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'removeCoupon',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeProduct' : function(cartItemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
            },
            method: 'removeProduct',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setAddress' : function(address, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'setAddress',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCart' : function(cart, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cart : JSON.stringify(cart),
            },
            method: 'setCart',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setReference' : function(reference, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                reference : JSON.stringify(reference),
            },
            method: 'setReference',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setShippingCost' : function(shippingCost, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                shippingCost : JSON.stringify(shippingCost),
            },
            method: 'setShippingCost',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateCartItem' : function(item, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'updateCartItem',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateProductCount' : function(cartItemId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                count : JSON.stringify(count),
            },
            method: 'updateProductCount',
            interfaceName: 'core.cartmanager.ICartManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.CarTuningManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CarTuningManager.prototype = {
    'getCarTuningData' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCarTuningData',
            interfaceName: 'core.cartuning.ICarTuningManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCarTuningData' : function(carList, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carList : JSON.stringify(carList),
            },
            method: 'saveCarTuningData',
            interfaceName: 'core.cartuning.ICarTuningManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.CertegoManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CertegoManager.prototype = {
    'deleteSystem' : function(systemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemId : JSON.stringify(systemId),
            },
            method: 'deleteSystem',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrders' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOrders',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSystems' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getSystems',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSystemsFiltered' : function(filterOptions, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getSystemsFiltered',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSystemsForGroup' : function(group, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'getSystemsForGroup',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveOrder' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveSystem' : function(system, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                system : JSON.stringify(system),
            },
            method: 'saveSystem',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'search' : function(searchWord, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'search',
            interfaceName: 'core.certego.ICertegoManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ChecklistManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ChecklistManager.prototype = {
    'getErrors' : function(multilevelname, from,to, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getErrors',
            multiLevelName: multilevelname,
            interfaceName: 'core.checklist.IChecklistManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.DBBackupManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DBBackupManager.prototype = {
    'getChanges' : function(className, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                className : JSON.stringify(className),
            },
            method: 'getChanges',
            interfaceName: 'core.dbbackupmanager.IDBBackupManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getChangesById' : function(className,id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                className : JSON.stringify(className),
                id : JSON.stringify(id),
            },
            method: 'getChangesById',
            interfaceName: 'core.dbbackupmanager.IDBBackupManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDiff' : function(className,id1,id2, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                className : JSON.stringify(className),
                id1 : JSON.stringify(id1),
                id2 : JSON.stringify(id2),
            },
            method: 'getDiff',
            interfaceName: 'core.dbbackupmanager.IDBBackupManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.DibsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.DibsManager.prototype = {
    'checkForOrdersToCapture' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.dibs.IDibsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.EpayManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.EpayManager.prototype = {
    'checkForOrdersToCapture' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.epay.IEpayManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.EventBookingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.EventBookingManager.prototype = {
    'addExternalCertificate' : function(multilevelname, userId,fileId,eventId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addLocationFilter' : function(multilevelname, locationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'addLocationFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addManuallyParticipatedEvent' : function(multilevelname, man, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                man : JSON.stringify(man),
            },
            method: 'addManuallyParticipatedEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addPersonalIdToEvent' : function(multilevelname, eventId,userId,personalId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                personalId : JSON.stringify(personalId),
            },
            method: 'addPersonalIdToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addTypeFilter' : function(multilevelname, bookingItemTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemTypeId : JSON.stringify(bookingItemTypeId),
            },
            method: 'addTypeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserComment' : function(multilevelname, userId,eventId,comment, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserToEvent' : function(multilevelname, eventId,userId,silent,source, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'bookCurrentUserToEvent' : function(multilevelname, eventId,source, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                source : JSON.stringify(source),
            },
            method: 'bookCurrentUserToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'canDownloadCertificate' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'canDownloadCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'cancelEvent' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'cancelEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkToSendReminders' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkToSendReminders',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearEventBookingManagerForAllData' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearEventBookingManagerForAllData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearFilters' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearLocationFilters' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearLocationFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createEvent' : function(multilevelname, event, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'createEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'decodePersonalIds' : function(multilevelname, eventId,privateKey, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                privateKey : JSON.stringify(privateKey),
            },
            method: 'decodePersonalIds',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteCertificate' : function(multilevelname, certificateId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                certificateId : JSON.stringify(certificateId),
            },
            method: 'deleteCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteEvent' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteExternalCertificates' : function(multilevelname, userId,fileId,eventId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteInvoiceGroup' : function(multilevelname, groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'deleteInvoiceGroup',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteLocation' : function(multilevelname, locationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'deleteLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteManullyParticipatedEvent' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteManullyParticipatedEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteReminderTemplate' : function(multilevelname, templateId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                templateId : JSON.stringify(templateId),
            },
            method: 'deleteReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteSubLocation' : function(multilevelname, subLocationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                subLocationId : JSON.stringify(subLocationId),
            },
            method: 'deleteSubLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteUserComment' : function(multilevelname, userId,eventId,commentId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getActiveLocations' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getActiveLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllEvents' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllLocations' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItemTypeByPageId' : function(multilevelname, pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getBookingItemTypeByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingItemTypes' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getBookingItemTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingTypeMetaData' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingTypeMetaData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingsByPageId' : function(multilevelname, pageId,showOnlyNew, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                showOnlyNew : JSON.stringify(showOnlyNew),
            },
            method: 'getBookingsByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCertificate' : function(multilevelname, certificateId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                certificateId : JSON.stringify(certificateId),
            },
            method: 'getCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCertificateForEvent' : function(multilevelname, eventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getCertificateForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCertificates' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompaniesWhereNoCanditasHasCompletedTests' : function(multilevelname, testIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testIds : JSON.stringify(testIds),
            },
            method: 'getCompaniesWhereNoCanditasHasCompletedTests',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEvent' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventByPageId' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventByPageId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventLog' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventLog',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventRequest' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getEventRequest',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEvents' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsByLocation' : function(multilevelname, locationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'getEventsByLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsByType' : function(multilevelname, eventTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventTypeId : JSON.stringify(eventTypeId),
            },
            method: 'getEventsByType',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsForDay' : function(multilevelname, year,month,day, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsForPdf' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEventsForPdf',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsForUser' : function(multilevelname, userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getEventsForUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsWhereEndDateBetween' : function(multilevelname, from,to, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getEventsWhereEndDateBetween',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getExternalCertificates' : function(multilevelname, userId,eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'getExternalCertificates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFilteredLocations' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getFilteredLocations',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFromDateTimeFilter' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getFromDateTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getInterests' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getInterests',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getInvoiceGroup' : function(multilevelname, groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getInvoiceGroup',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getInvoiceGroups' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getInvoiceGroups',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLocation' : function(multilevelname, locationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                locationId : JSON.stringify(locationId),
            },
            method: 'getLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMandatoryCourses' : function(multilevelname, userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getMandatoryCourses',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getManuallyAddedEventParticipant' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getManuallyAddedEventParticipant',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getManuallyAddedEvents' : function(multilevelname, userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getManuallyAddedEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyEvents' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyEvents',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPriceForEventType' : function(multilevelname, bookingItemTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemTypeId : JSON.stringify(bookingItemTypeId),
            },
            method: 'getPriceForEventType',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPriceForEventTypeAndUserId' : function(multilevelname, eventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getPriceForEventTypeAndUserId',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReminder' : function(multilevelname, reminderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                reminderId : JSON.stringify(reminderId),
            },
            method: 'getReminder',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReminderTemplate' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReminderTemplates' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getReminderTemplates',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReminders' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getReminders',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSource' : function(multilevelname, eventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'getSource',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStatistic' : function(multilevelname, startDate,stopDate,groupIds,eventTypeIds, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStatisticGroupedByLocations' : function(multilevelname, startDate,stopDate,groupIds,eventTypeIds, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getToDateTimeFilter' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getToDateTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsersForEvent' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getUsersForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsersForEventWaitinglist' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getUsersForEventWaitinglist',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'handleEventRequest' : function(multilevelname, id,accepted, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                accepted : JSON.stringify(accepted),
            },
            method: 'handleEventRequest',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasCompletedMandatoryEvent' : function(multilevelname, eventTypeId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventTypeId : JSON.stringify(eventTypeId),
                userId : JSON.stringify(userId),
            },
            method: 'hasCompletedMandatoryEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasForcedMandatoryTest' : function(multilevelname, eventTypeId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventTypeId : JSON.stringify(eventTypeId),
                userId : JSON.stringify(userId),
            },
            method: 'hasForcedMandatoryTest',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isUserSignedUpForEvent' : function(multilevelname, eventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'isUserSignedUpForEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isWaitingForConfirmation' : function(multilevelname, eventId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
            },
            method: 'isWaitingForConfirmation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsReady' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'markAsReady',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markQuestBackSent' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'markQuestBackSent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'moveUserToEvent' : function(multilevelname, userId,fromEventId,toEventId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerEventIntrest' : function(multilevelname, interest, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                interest : JSON.stringify(interest),
            },
            method: 'registerEventIntrest',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeInterest' : function(multilevelname, bookingItemTypeId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemTypeId : JSON.stringify(bookingItemTypeId),
                userId : JSON.stringify(userId),
            },
            method: 'removeInterest',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeUserFromEvent' : function(multilevelname, eventId,userId,silent, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveBookingTypeMetaData' : function(multilevelname, bookingItemTypeMetadata, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemTypeMetadata : JSON.stringify(bookingItemTypeMetadata),
            },
            method: 'saveBookingTypeMetaData',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCertificate' : function(multilevelname, certificate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                certificate : JSON.stringify(certificate),
            },
            method: 'saveCertificate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveEvent' : function(multilevelname, event, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'saveEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveGroupInvoicing' : function(multilevelname, invoiceGroup, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                invoiceGroup : JSON.stringify(invoiceGroup),
            },
            method: 'saveGroupInvoicing',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveLocation' : function(multilevelname, location, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                location : JSON.stringify(location),
            },
            method: 'saveLocation',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveReminderTemplate' : function(multilevelname, template, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                template : JSON.stringify(template),
            },
            method: 'saveReminderTemplate',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendDiplomas' : function(multilevelname, reminder,userid,base64, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendReminder' : function(multilevelname, reminder, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                reminder : JSON.stringify(reminder),
            },
            method: 'sendReminder',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setForcedMandatoryAccess' : function(multilevelname, userId,bookingItemIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                bookingItemIds : JSON.stringify(bookingItemIds),
            },
            method: 'setForcedMandatoryAccess',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setGroupInvoiceingStatus' : function(multilevelname, eventId,userId,groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'setGroupInvoiceingStatus',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setParticipationStatus' : function(multilevelname, eventId,userId,status, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setTimeFilter' : function(multilevelname, from,to, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'setTimeFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startScheduler' : function(multilevelname, scheduler, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                scheduler : JSON.stringify(scheduler),
            },
            method: 'startScheduler',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleHide' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'toggleHide',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleLocked' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'toggleLocked',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferUserFromWaitingToEvent' : function(multilevelname, userId,eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                eventId : JSON.stringify(eventId),
            },
            method: 'transferUserFromWaitingToEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'unCancelEvent' : function(multilevelname, eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'unCancelEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.eventbooking.IEventBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ExcelManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ExcelManager.prototype = {
    'getBase64Excel' : function(array, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                array : JSON.stringify(array),
            },
            method: 'getBase64Excel',
            interfaceName: 'core.excelmanager.IExcelManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.FileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.FileManager.prototype = {
    'addFileEntry' : function(listId,entry, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
            },
            method: 'addFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteFileEntry' : function(fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'deleteFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFile' : function(fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'getFile',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFiles' : function(listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getFiles',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'renameFileEntry' : function(fileId,newName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
                newName : JSON.stringify(newName),
            },
            method: 'renameFileEntry',
            interfaceName: 'core.filemanager.IFileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
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
    'addImageToGallery' : function(galleryId,imageId,description,title, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createImageGallery' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createImageGallery',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteImage' : function(entryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'deleteImage',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllImages' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getAllImages',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntry' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveEntry' : function(entry, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'saveEntry',
            interfaceName: 'core.gallerymanager.IGalleryManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GetShop = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShop.prototype = {
    'addLeadHistory' : function(leadId,comment,start,end,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                leadId : JSON.stringify(leadId),
                comment : JSON.stringify(comment),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                userId : JSON.stringify(userId),
            },
            method: 'addLeadHistory',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addToDibsAutoCollect' : function(orderId,storeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                storeId : JSON.stringify(storeId),
            },
            method: 'addToDibsAutoCollect',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserToPartner' : function(userId,partner,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                partner : JSON.stringify(partner),
                password : JSON.stringify(password),
            },
            method: 'addUserToPartner',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'canInvoiceOverEhf' : function(vatNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                vatNumber : JSON.stringify(vatNumber),
            },
            method: 'canInvoiceOverEhf',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeLeadState' : function(leadId,state, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                leadId : JSON.stringify(leadId),
                state : JSON.stringify(state),
            },
            method: 'changeLeadState',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createLead' : function(name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'createLead',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewStore' : function(startData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startData : JSON.stringify(startData),
            },
            method: 'createNewStore',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createWebPage' : function(webpageData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                webpageData : JSON.stringify(webpageData),
            },
            method: 'createWebPage',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'findAddressForApplication' : function(uuid, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForApplication',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'findAddressForUUID' : function(uuid, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                uuid : JSON.stringify(uuid),
            },
            method: 'findAddressForUUID',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64EncodedPDFWebPage' : function(urlToPage, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                urlToPage : JSON.stringify(urlToPage),
            },
            method: 'getBase64EncodedPDFWebPage',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64EncodedPDFWebPageFromHtml' : function(html, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                html : JSON.stringify(html),
            },
            method: 'getBase64EncodedPDFWebPageFromHtml',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLead' : function(leadId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                leadId : JSON.stringify(leadId),
            },
            method: 'getLead',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLeads' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLeads',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersToAutoPayFromDibs' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOrdersToAutoPayFromDibs',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPartnerData' : function(partnerId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                partnerId : JSON.stringify(partnerId),
                password : JSON.stringify(password),
            },
            method: 'getPartnerData',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStores' : function(code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'getStores',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'loadEhfCompanies' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'loadEhfCompanies',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markLeadHistoryCompleted' : function(leadHistoryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                leadHistoryId : JSON.stringify(leadHistoryId),
            },
            method: 'markLeadHistoryCompleted',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveLead' : function(lead, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lead : JSON.stringify(lead),
            },
            method: 'saveLead',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveSmsCallback' : function(smsResponses, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                smsResponses : JSON.stringify(smsResponses),
            },
            method: 'saveSmsCallback',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setApplicationList' : function(ids,partnerId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ids : JSON.stringify(ids),
                partnerId : JSON.stringify(partnerId),
                password : JSON.stringify(password),
            },
            method: 'setApplicationList',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startStoreFromStore' : function(startData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startData : JSON.stringify(startData),
            },
            method: 'startStoreFromStore',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleRemoteEditing' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'toggleRemoteEditing',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'triggerPullRequest' : function(storeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                storeId : JSON.stringify(storeId),
            },
            method: 'triggerPullRequest',
            interfaceName: 'core.getshop.IGetShop',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GetShopAccountingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopAccountingManager.prototype = {
    'canOrderBeTransferredDirect' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'canOrderBeTransferredDirect',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createBankTransferFile' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createBankTransferFile',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNextOrderFile' : function(endDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                endDate : JSON.stringify(endDate),
            },
            method: 'createNextOrderFile',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteFile' : function(fileId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
                password : JSON.stringify(password),
            },
            method: 'deleteFile',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConfigOptions' : function(systemType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemType : JSON.stringify(systemType),
            },
            method: 'getConfigOptions',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConfigs' : function(systemType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemType : JSON.stringify(systemType),
            },
            method: 'getConfigs',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentSystemInvoices' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCurrentSystemInvoices',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentSystemOther' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCurrentSystemOther',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getListOfSystems' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getListOfSystems',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogEntries' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLogEntries',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderFile' : function(fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fileId : JSON.stringify(fileId),
            },
            method: 'getOrderFile',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderFiles' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOrderFiles',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersToIncludeForNextTransfer' : function(endDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                endDate : JSON.stringify(endDate),
            },
            method: 'getOrdersToIncludeForNextTransfer',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPreviouseEndDate' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPreviouseEndDate',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isCurrentSelectedAccountingSystemPrimitive' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isCurrentSelectedAccountingSystemPrimitive',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isCurrentSelectedSupportingDirectTransfer' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isCurrentSelectedSupportingDirectTransfer',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setConfig' : function(systemType,key,value, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemType : JSON.stringify(systemType),
                key : JSON.stringify(key),
                value : JSON.stringify(value),
            },
            method: 'setConfig',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSystemTypeInvoice' : function(systemType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemType : JSON.stringify(systemType),
            },
            method: 'setSystemTypeInvoice',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSystemTypeOther' : function(systemType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                systemType : JSON.stringify(systemType),
            },
            method: 'setSystemTypeOther',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferData' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'transferData',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferDirect' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'transferDirect',
            interfaceName: 'core.getshopaccounting.IGetShopAccountingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GetShopLockManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopLockManager.prototype = {
    'accessEvent' : function(multilevelname, id,code,domain, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addLockLogs' : function(multilevelname, logs,code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                logs : JSON.stringify(logs),
                code : JSON.stringify(code),
            },
            method: 'addLockLogs',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeZWaveId' : function(multilevelname, lockId,newId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
                newId : JSON.stringify(newId),
            },
            method: 'changeZWaveId',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkIfAllIsOk' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkIfAllIsOk',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteAllDevices' : function(multilevelname, password,source, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
                source : JSON.stringify(source),
            },
            method: 'deleteAllDevices',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteLock' : function(multilevelname, code,lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
                lockId : JSON.stringify(lockId),
            },
            method: 'deleteLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'finalizeLocks' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'finalizeLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllLocks' : function(multilevelname, serverSource, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverSource : JSON.stringify(serverSource),
            },
            method: 'getAllLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCodeForLock' : function(multilevelname, lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'getCodeForLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCodesInUse' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCodesInUse',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDevice' : function(multilevelname, deviceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                deviceId : JSON.stringify(deviceId),
            },
            method: 'getDevice',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMasterCodes' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMasterCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUpdatesOnLock' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUpdatesOnLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'openLock' : function(multilevelname, lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'openLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pingLock' : function(multilevelname, lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'pingLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pushCode' : function(multilevelname, id,door,code,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'refreshAllLocks' : function(multilevelname, source, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                source : JSON.stringify(source),
            },
            method: 'refreshAllLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'refreshLock' : function(multilevelname, lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'refreshLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAllUnusedLocks' : function(multilevelname, source, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                source : JSON.stringify(source),
            },
            method: 'removeAllUnusedLocks',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeCodeOnLock' : function(multilevelname, lockId,room, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
                room : JSON.stringify(room),
            },
            method: 'removeCodeOnLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveLock' : function(multilevelname, lock, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lock : JSON.stringify(lock),
            },
            method: 'saveLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveMastercodes' : function(multilevelname, codes, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                codes : JSON.stringify(codes),
            },
            method: 'saveMastercodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setMasterCode' : function(multilevelname, slot,code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slot : JSON.stringify(slot),
                code : JSON.stringify(code),
            },
            method: 'setMasterCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'stopUpdatesOnLock' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'stopUpdatesOnLock',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'triggerFetchingOfCodes' : function(multilevelname, ip,deviceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ip : JSON.stringify(ip),
                deviceId : JSON.stringify(deviceId),
            },
            method: 'triggerFetchingOfCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'triggerMassUpdateOfLockLogs' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'triggerMassUpdateOfLockLogs',
            multiLevelName: multilevelname,
            interfaceName: 'core.getshoplock.IGetShopLockManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GetShopLockSystemManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GetShopLockSystemManager.prototype = {
    'addTransactionEntranceDoor' : function(serverId,lockId,code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                code : JSON.stringify(code),
            },
            method: 'addTransactionEntranceDoor',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addTransactionHistory' : function(tokenId,lockId,timeStamp,userSlot, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tokenId : JSON.stringify(tokenId),
                lockId : JSON.stringify(lockId),
                timeStamp : JSON.stringify(timeStamp),
                userSlot : JSON.stringify(userSlot),
            },
            method: 'addTransactionHistory',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'canShowAccessLog' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'canShowAccessLog',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeCode' : function(groupId,slotId,pinCode,cardId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                slotId : JSON.stringify(slotId),
                pinCode : JSON.stringify(pinCode),
                cardId : JSON.stringify(cardId),
            },
            method: 'changeCode',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeDatesForSlot' : function(groupId,slotId,startDate,endDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                slotId : JSON.stringify(slotId),
                startDate : JSON.stringify(startDate),
                endDate : JSON.stringify(endDate),
            },
            method: 'changeDatesForSlot',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'closeLock' : function(lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'closeLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewLockGroup' : function(name,maxUsersInGroup,codeSize, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
                maxUsersInGroup : JSON.stringify(maxUsersInGroup),
                codeSize : JSON.stringify(codeSize),
            },
            method: 'createNewLockGroup',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createServer' : function(type,hostname,userName,password,givenName,token, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
                hostname : JSON.stringify(hostname),
                userName : JSON.stringify(userName),
                password : JSON.stringify(password),
                givenName : JSON.stringify(givenName),
                token : JSON.stringify(token),
            },
            method: 'createServer',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deactivatePrioritingOfLock' : function(serverId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
            },
            method: 'deactivatePrioritingOfLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteGroup' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'deleteGroup',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteServer' : function(serverId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
            },
            method: 'deleteServer',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generateNewCodesForLock' : function(serverId,lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
            },
            method: 'generateNewCodesForLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccess' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getAccess',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccessHistory' : function(groupId,start,end,groupSlotId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                groupSlotId : JSON.stringify(groupSlotId),
            },
            method: 'getAccessHistory',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccessLog' : function(serverId,lockId,filterOptions, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getAccessLog',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAccessUsers' : function(options, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                options : JSON.stringify(options),
            },
            method: 'getAllAccessUsers',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllGroups' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllGroups',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCodeSize' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCodeSize',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCodesInUse' : function(serverId,lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
            },
            method: 'getCodesInUse',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getGroup' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getGroup',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLock' : function(serverId,lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
            },
            method: 'getLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLockServers' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLockServers',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNameOfGroup' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getNameOfGroup',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNextUnusedCode' : function(groupId,reference,managerName,textReference, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                reference : JSON.stringify(reference),
                managerName : JSON.stringify(managerName),
                textReference : JSON.stringify(textReference),
            },
            method: 'getNextUnusedCode',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'grantAccessDirect' : function(groupId,user, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                user : JSON.stringify(user),
            },
            method: 'grantAccessDirect',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isSlotTakenInUseInAnyGroups' : function(serverId,lockId,slotId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                slotId : JSON.stringify(slotId),
            },
            method: 'isSlotTakenInUseInAnyGroups',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'lockSettingsChanged' : function(lockSettings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockSettings : JSON.stringify(lockSettings),
            },
            method: 'lockSettingsChanged',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markCodeAsUpdatedOnLock' : function(serverId,lockId,slotId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                slotId : JSON.stringify(slotId),
            },
            method: 'markCodeAsUpdatedOnLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markCodeForDeletion' : function(serverId,lockId,slotId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                slotId : JSON.stringify(slotId),
            },
            method: 'markCodeForDeletion',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markCodeForResending' : function(serverId,lockId,slotId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                slotId : JSON.stringify(slotId),
            },
            method: 'markCodeForResending',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'openLock' : function(lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                lockId : JSON.stringify(lockId),
            },
            method: 'openLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pingServers' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'pingServers',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'prioritizeLockUpdate' : function(serverId,lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
            },
            method: 'prioritizeLockUpdate',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAccess' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'removeAccess',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'renameLock' : function(serverId,lockId,name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
                name : JSON.stringify(name),
            },
            method: 'renameLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'renewCodeForSlot' : function(groupId,slotId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                slotId : JSON.stringify(slotId),
            },
            method: 'renewCodeForSlot',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'restCall' : function(serverId,path, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                path : JSON.stringify(path),
            },
            method: 'restCall',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveLocstarLock' : function(serverId,lock, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lock : JSON.stringify(lock),
            },
            method: 'saveLocstarLock',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveUser' : function(user, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'saveUser',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendEmailToCustomer' : function(userId,subject,body, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                subject : JSON.stringify(subject),
                body : JSON.stringify(body),
            },
            method: 'sendEmailToCustomer',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendSmsToCustomer' : function(userId,textMessage, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                textMessage : JSON.stringify(textMessage),
            },
            method: 'sendSmsToCustomer',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCodeSize' : function(codeSize, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                codeSize : JSON.stringify(codeSize),
            },
            method: 'setCodeSize',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setGroupVirtual' : function(groupId,isVirtual, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                isVirtual : JSON.stringify(isVirtual),
            },
            method: 'setGroupVirtual',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setLocksToGroup' : function(groupId,lockIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                lockIds : JSON.stringify(lockIds),
            },
            method: 'setLocksToGroup',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startFetchingOfLocksFromServer' : function(serverId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
            },
            method: 'startFetchingOfLocksFromServer',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleActiveServer' : function(serverId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
            },
            method: 'toggleActiveServer',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'triggerCheckOfCodes' : function(serverId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
            },
            method: 'triggerCheckOfCodes',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'triggerCronTab' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'triggerCronTab',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateConnectionDetails' : function(serverId,hostname,username,password,givenName,token, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                hostname : JSON.stringify(hostname),
                username : JSON.stringify(username),
                password : JSON.stringify(password),
                givenName : JSON.stringify(givenName),
                token : JSON.stringify(token),
            },
            method: 'updateConnectionDetails',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateZwaveRoute' : function(serverId,lockId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                serverId : JSON.stringify(serverId),
                lockId : JSON.stringify(lockId),
            },
            method: 'updateZwaveRoute',
            interfaceName: 'core.getshoplocksystem.IGetShopLockSystemManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GiftCardManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GiftCardManager.prototype = {
    'getAllCards' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllCards',
            interfaceName: 'core.giftcard.IGiftCardManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getGiftCard' : function(giftCardCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                giftCardCode : JSON.stringify(giftCardCode),
            },
            method: 'getGiftCard',
            interfaceName: 'core.giftcard.IGiftCardManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.GdsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.GdsManager.prototype = {
    'deleteDevice' : function(deviceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                deviceId : JSON.stringify(deviceId),
            },
            method: 'deleteDevice',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDevices' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getDevices',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMessageForUser' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMessageForUser',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMessages' : function(tokenId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tokenId : JSON.stringify(tokenId),
            },
            method: 'getMessages',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getQueues' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getQueues',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveDevice' : function(device, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                device : JSON.stringify(device),
            },
            method: 'saveDevice',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageToDevice' : function(deviceId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                deviceId : JSON.stringify(deviceId),
                message : JSON.stringify(message),
            },
            method: 'sendMessageToDevice',
            interfaceName: 'core.gsd.IGdsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.InformationScreenManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InformationScreenManager.prototype = {
    'addSlider' : function(slider,tvId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slider : JSON.stringify(slider),
                tvId : JSON.stringify(tvId),
            },
            method: 'addSlider',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteSlider' : function(sliderId,tvId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                sliderId : JSON.stringify(sliderId),
                tvId : JSON.stringify(tvId),
            },
            method: 'deleteSlider',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getHolders' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getHolders',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getInformationScreens' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getInformationScreens',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNews' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getNews',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getScreen' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getScreen',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTypes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTypes',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerTv' : function(customerId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                customerId : JSON.stringify(customerId),
            },
            method: 'registerTv',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveTv' : function(tv, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tv : JSON.stringify(tv),
            },
            method: 'saveTv',
            interfaceName: 'core.informationscreenmanager.IInformationScreenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ListManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ListManager.prototype = {
    'addEntry' : function(listId,entry,parentPageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
                parentPageId : JSON.stringify(parentPageId),
            },
            method: 'addEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUnsecureEntry' : function(listId,entry, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entry : JSON.stringify(entry),
            },
            method: 'addUnsecureEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'askConfirmationOnEntry' : function(entryId,text, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                text : JSON.stringify(text),
            },
            method: 'askConfirmationOnEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearList' : function(listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'clearList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'combineList' : function(toListId,newListId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                toListId : JSON.stringify(toListId),
                newListId : JSON.stringify(newListId),
            },
            method: 'combineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'confirmEntry' : function(entryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'confirmEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createListId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createListId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createMenuList' : function(menuApplicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                menuApplicationId : JSON.stringify(menuApplicationId),
            },
            method: 'createMenuList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteEntry' : function(id,listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                listId : JSON.stringify(listId),
            },
            method: 'deleteEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteMenu' : function(appId,listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
                listId : JSON.stringify(listId),
            },
            method: 'deleteMenu',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllListsByType' : function(type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getAllListsByType',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCombinedLists' : function(listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getCombinedLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntryByPageId' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEntryByPageId',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getJSTreeNode' : function(nodeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                nodeId : JSON.stringify(nodeId),
            },
            method: 'getJSTreeNode',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getJsTree' : function(name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getJsTree',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getList' : function(listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getListEntry' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getListEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLists' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLists',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMenues' : function(applicationInstanceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getMenues',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPageIdByName' : function(name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'orderEntry' : function(id,after,parentId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                after : JSON.stringify(after),
                parentId : JSON.stringify(parentId),
            },
            method: 'orderEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveJsTree' : function(name,list, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
                list : JSON.stringify(list),
            },
            method: 'saveJsTree',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveMenu' : function(appId,listId,entries,name, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setEntries' : function(listId,entries, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
                entries : JSON.stringify(entries),
            },
            method: 'setEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'translateEntries' : function(entryIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'unCombineList' : function(fromListId,toRemoveId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fromListId : JSON.stringify(fromListId),
                toRemoveId : JSON.stringify(toRemoveId),
            },
            method: 'unCombineList',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateEntry' : function(entry, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
            },
            method: 'updateEntry',
            interfaceName: 'core.listmanager.IListManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.MecaManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MecaManager.prototype = {
    'answerControlRequest' : function(carId,answer, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                answer : JSON.stringify(answer),
            },
            method: 'answerControlRequest',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'answerServiceRequest' : function(carId,answer, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                answer : JSON.stringify(answer),
            },
            method: 'answerServiceRequest',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'callMe' : function(cellPhone, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
            },
            method: 'callMe',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createFleet' : function(fleet, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fleet : JSON.stringify(fleet),
            },
            method: 'createFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteCar' : function(carId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'deleteCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteFleet' : function(fleetId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fleetId : JSON.stringify(fleetId),
            },
            method: 'deleteFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64ExcelReport' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getBase64ExcelReport',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCar' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCarByPageId' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getCarByPageId',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCarsByCellphone' : function(cellPhone, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
            },
            method: 'getCarsByCellphone',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCarsForMecaFleet' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getCarsForMecaFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCarsPKKList' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCarsPKKList',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCarsServiceList' : function(needService, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                needService : JSON.stringify(needService),
            },
            method: 'getCarsServiceList',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFleetByCar' : function(car, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                car : JSON.stringify(car),
            },
            method: 'getFleetByCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFleetPageId' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getFleetPageId',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFleets' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getFleets',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markControlAsCompleted' : function(carId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'markControlAsCompleted',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'noShowPkk' : function(carId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'noShowPkk',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'noShowService' : function(carId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'noShowService',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'notifyByPush' : function(phoneNumber,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                message : JSON.stringify(message),
            },
            method: 'notifyByPush',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerDeviceToCar' : function(deviceId,cellPhone, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                deviceId : JSON.stringify(deviceId),
                cellPhone : JSON.stringify(cellPhone),
            },
            method: 'registerDeviceToCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'requestNextControl' : function(carId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'requestNextControl',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'requestNextService' : function(carId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'requestNextService',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetServiceInterval' : function(carId,date,kilometers, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
                kilometers : JSON.stringify(kilometers),
            },
            method: 'resetServiceInterval',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'runNotificationCheck' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'runNotificationCheck',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveFleet' : function(fleet, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fleet : JSON.stringify(fleet),
            },
            method: 'saveFleet',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveFleetCar' : function(pageId,car, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                car : JSON.stringify(car),
            },
            method: 'saveFleetCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveMecaFleetSettings' : function(settings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'saveMecaFleetSettings',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendEmail' : function(cellPhone,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
                message : JSON.stringify(message),
            },
            method: 'sendEmail',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendInvite' : function(mecaCarId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                mecaCarId : JSON.stringify(mecaCarId),
            },
            method: 'sendInvite',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendKilometerRequest' : function(carId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
            },
            method: 'sendKilometerRequest',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendKilometers' : function(cellPhone,kilometers, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cellPhone : JSON.stringify(cellPhone),
                kilometers : JSON.stringify(kilometers),
            },
            method: 'sendKilometers',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendNotificationToStoreOwner' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'sendNotificationToStoreOwner',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCommentOnCar' : function(carId,comment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                comment : JSON.stringify(comment),
            },
            method: 'setCommentOnCar',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setManuallyControlDate' : function(carId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'setManuallyControlDate',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setManuallyServiceDate' : function(carId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'setManuallyServiceDate',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'suggestDate' : function(carId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                carId : JSON.stringify(carId),
                date : JSON.stringify(date),
            },
            method: 'suggestDate',
            interfaceName: 'core.mecamanager.IMecaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.MekonomenManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MekonomenManager.prototype = {
    'addUserId' : function(userId,mekonomenUserName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                mekonomenUserName : JSON.stringify(mekonomenUserName),
            },
            method: 'addUserId',
            interfaceName: 'core.mekonomen.IMekonomenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMekonomenUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getMekonomenUser',
            interfaceName: 'core.mekonomen.IMekonomenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeConnectionToDatabase' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'removeConnectionToDatabase',
            interfaceName: 'core.mekonomen.IMekonomenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'searchForUser' : function(name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'searchForUser',
            interfaceName: 'core.mekonomen.IMekonomenManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.MessageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MessageManager.prototype = {
    'collectEmail' : function(email, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'collectEmail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllSmsMessages' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllSmsMessages',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCollectedEmails' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCollectedEmails',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getIncomingMessages' : function(pageNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageNumber : JSON.stringify(pageNumber),
            },
            method: 'getIncomingMessages',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMailMessage' : function(mailMessageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                mailMessageId : JSON.stringify(mailMessageId),
            },
            method: 'getMailMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMailSent' : function(from,to,toEmailAddress, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
                toEmailAddress : JSON.stringify(toEmailAddress),
            },
            method: 'getMailSent',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSmsCount' : function(year,month, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getSmsCount',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSmsMessage' : function(smsMessageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                smsMessageId : JSON.stringify(smsMessageId),
            },
            method: 'getSmsMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSmsMessagesSentTo' : function(prefix,phoneNumber,fromDate,toDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                prefix : JSON.stringify(prefix),
                phoneNumber : JSON.stringify(phoneNumber),
                fromDate : JSON.stringify(fromDate),
                toDate : JSON.stringify(toDate),
            },
            method: 'getSmsMessagesSentTo',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveIncomingMessage' : function(message,code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                message : JSON.stringify(message),
                code : JSON.stringify(code),
            },
            method: 'saveIncomingMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendErrorNotify' : function(inText, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                inText : JSON.stringify(inText),
            },
            method: 'sendErrorNotify',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMail' : function(to,toName,subject,content,from,fromName, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMailWithAttachments' : function(to,toName,subject,content,from,fromName,attachments, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageToStoreOwner' : function(message,subject, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                message : JSON.stringify(message),
                subject : JSON.stringify(subject),
            },
            method: 'sendMessageToStoreOwner',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.NewsLetterManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.NewsLetterManager.prototype = {
    'sendNewsLetter' : function(group, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'sendNewsLetter',
            interfaceName: 'core.messagemanager.INewsLetterManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendNewsLetterPreview' : function(group, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'sendNewsLetterPreview',
            interfaceName: 'core.messagemanager.INewsLetterManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.MobileManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.MobileManager.prototype = {
    'clearBadged' : function(tokenId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tokenId : JSON.stringify(tokenId),
            },
            method: 'clearBadged',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerToken' : function(token, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                token : JSON.stringify(token),
            },
            method: 'registerToken',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageToAll' : function(message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAll',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageToAllTestUnits' : function(message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAllTestUnits',
            interfaceName: 'core.mobilemanager.IMobileManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.OAuthManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.OAuthManager.prototype = {
    'getCurrentOAuthSession' : function(oauthSessionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                oauthSessionId : JSON.stringify(oauthSessionId),
            },
            method: 'getCurrentOAuthSession',
            interfaceName: 'core.oauthmanager.IOAuthManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startNewOAuthSession' : function(authAddress,clientId,scope,clientSecretId,tokenAddress, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                authAddress : JSON.stringify(authAddress),
                clientId : JSON.stringify(clientId),
                scope : JSON.stringify(scope),
                clientSecretId : JSON.stringify(clientSecretId),
                tokenAddress : JSON.stringify(tokenAddress),
            },
            method: 'startNewOAuthSession',
            interfaceName: 'core.oauthmanager.IOAuthManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.OcrManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.OcrManager.prototype = {
    'scanOcrFiles' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'scanOcrFiles',
            interfaceName: 'core.ocr.IOcrManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.StoreOcrManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreOcrManager.prototype = {
    'checkForPayments' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForPayments',
            interfaceName: 'core.ocr.IStoreOcrManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccountingId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAccountingId',
            interfaceName: 'core.ocr.IStoreOcrManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllTransactions' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllTransactions',
            interfaceName: 'core.ocr.IStoreOcrManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setAccountId' : function(id,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                password : JSON.stringify(password),
            },
            method: 'setAccountId',
            interfaceName: 'core.ocr.IStoreOcrManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.EhfXmlGenerator = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.EhfXmlGenerator.prototype = {
    'generateXml' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'generateXml',
            interfaceName: 'core.ordermanager.IEhfXmlGenerator',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.OrderManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.OrderManager.prototype = {
    'addClosedPeriode' : function(closed, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                closed : JSON.stringify(closed),
            },
            method: 'addClosedPeriode',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addOrderTransaction' : function(orderId,amount,comment,paymentDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                amount : JSON.stringify(amount),
                comment : JSON.stringify(comment),
                paymentDate : JSON.stringify(paymentDate),
            },
            method: 'addOrderTransaction',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addProductToOrder' : function(orderId,productId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'addProductToOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeAutoClosePeriodesOnZRepport' : function(autoClose, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                autoClose : JSON.stringify(autoClose),
            },
            method: 'changeAutoClosePeriodesOnZRepport',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeOrderStatus' : function(id,status, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                status : JSON.stringify(status),
            },
            method: 'changeOrderStatus',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeOrderType' : function(orderId,paymentTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                paymentTypeId : JSON.stringify(paymentTypeId),
            },
            method: 'changeOrderType',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeProductOnCartItem' : function(orderId,cartItemId,productId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                cartItemId : JSON.stringify(cartItemId),
                productId : JSON.stringify(productId),
            },
            method: 'changeProductOnCartItem',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForOrdersFailedCollecting' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForOrdersFailedCollecting',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForOrdersToAutoPay' : function(daysToTryAfterOrderHasStarted, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                daysToTryAfterOrderHasStarted : JSON.stringify(daysToTryAfterOrderHasStarted),
            },
            method: 'checkForOrdersToAutoPay',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForOrdersToCapture' : function(internalPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                internalPassword : JSON.stringify(internalPassword),
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkGroupInvoicing' : function(password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'checkGroupInvoicing',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'closeOrder' : function(orderId,reason, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                reason : JSON.stringify(reason),
            },
            method: 'closeOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'closeTransactionPeriode' : function(closeDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                closeDate : JSON.stringify(closeDate),
            },
            method: 'closeTransactionPeriode',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrder' : function(address, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'createOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrderByCustomerReference' : function(referenceKey, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                referenceKey : JSON.stringify(referenceKey),
            },
            method: 'createOrderByCustomerReference',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrderForUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'createOrderForUser',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createRegisterCardOrder' : function(paymentType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentType : JSON.stringify(paymentType),
            },
            method: 'createRegisterCardOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'creditOrder' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'creditOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteAllVirtualOrders' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'deleteAllVirtualOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteOrder' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'deleteOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'forceDeleteOrder' : function(orderId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                password : JSON.stringify(password),
            },
            method: 'forceDeleteOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllOrdersForUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getAllOrdersForUser',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllOrdersOnProduct' : function(productId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'getAllOrdersOnProduct',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllTransactionsForInvoices' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllTransactionsForInvoices',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUnpaid' : function(paymentMethod, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentMethod : JSON.stringify(paymentMethod),
            },
            method: 'getAllUnpaid',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUnpaidInvoices' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllUnpaidInvoices',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBankOrderTransactions' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getBankOrderTransactions',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDayEntriesForOrder' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getDayEntriesForOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDayIncomes' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getDayIncomes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEhfXml' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getEhfXml',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getIncrementalOrderIdByOrderId' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getIncrementalOrderIdByOrderId',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getItemDates' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getItemDates',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMostSoldProducts' : function(numberOfProducts, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                numberOfProducts : JSON.stringify(numberOfProducts),
            },
            method: 'getMostSoldProducts',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyPrefferedPaymentMethod' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyPrefferedPaymentMethod',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrder' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderByReference' : function(referenceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                referenceId : JSON.stringify(referenceId),
            },
            method: 'getOrderByReference',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderByincrementOrderId' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getOrderByincrementOrderId',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderByincrementOrderIdAndPassword' : function(id,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                password : JSON.stringify(password),
            },
            method: 'getOrderByincrementOrderIdAndPassword',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderLight' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrderLight',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderManagerSettings' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOrderManagerSettings',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderSecure' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrderSecure',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderWithIdAndPassword' : function(orderId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                password : JSON.stringify(password),
            },
            method: 'getOrderWithIdAndPassword',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrders' : function(orderIds,page,pageSize, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderIds : JSON.stringify(orderIds),
                page : JSON.stringify(page),
                pageSize : JSON.stringify(pageSize),
            },
            method: 'getOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersByFilter' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getOrdersByFilter',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersFiltered' : function(filterOptions, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getOrdersFiltered',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersFromPeriode' : function(start,end,statistics, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                statistics : JSON.stringify(statistics),
            },
            method: 'getOrdersFromPeriode',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersNotTransferredToAccountingSystem' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOrdersNotTransferredToAccountingSystem',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersPaid' : function(paymentId,userId,from,to, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersToCapture' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOverdueInvoices' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOverdueInvoices',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPageCount' : function(pageSize,searchWord, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageSize : JSON.stringify(pageSize),
                searchWord : JSON.stringify(searchWord),
            },
            method: 'getPageCount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPaymentMethodsThatHasOrders' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPaymentMethodsThatHasOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRestToPay' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getRestToPay',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSalesNumber' : function(year, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getSalesNumber',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSalesStatistics' : function(startDate,endDate,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                endDate : JSON.stringify(endDate),
                type : JSON.stringify(type),
            },
            method: 'getSalesStatistics',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStorePreferredPayementMethod' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getStorePreferredPayementMethod',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTaxes' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTaxes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTerminalInformation' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getTerminalInformation',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalAmount' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTotalAmount',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalAmountExTaxes' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'getTotalAmountExTaxes',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalForOrderById' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getTotalForOrderById',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalOutstandingInvoices' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getTotalOutstandingInvoices',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalOutstandingInvoicesOverdue' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getTotalOutstandingInvoicesOverdue',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalSalesAmount' : function(year,month,week,day,type, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserPrefferedPaymentMethod' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getUserPrefferedPaymentMethod',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isConfiguredForEhf' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isConfiguredForEhf',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isLocked' : function(date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                date : JSON.stringify(date),
            },
            method: 'isLocked',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logTransactionEntry' : function(orderId,entry, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                entry : JSON.stringify(entry),
            },
            method: 'logTransactionEntry',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsInvoicePayment' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'markAsInvoicePayment',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsPaid' : function(orderId,date,amount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                date : JSON.stringify(date),
                amount : JSON.stringify(amount),
            },
            method: 'markAsPaid',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsPaidWithPassword' : function(orderId,date,amount,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                date : JSON.stringify(date),
                amount : JSON.stringify(amount),
                password : JSON.stringify(password),
            },
            method: 'markAsPaidWithPassword',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markOrderAsBillabe' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'markOrderAsBillabe',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'mergeAndCreateNewOrder' : function(userId,orderIds,paymentMethod,note, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                orderIds : JSON.stringify(orderIds),
                paymentMethod : JSON.stringify(paymentMethod),
                note : JSON.stringify(note),
            },
            method: 'mergeAndCreateNewOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'payWithCard' : function(orderId,cardId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                cardId : JSON.stringify(cardId),
            },
            method: 'payWithCard',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printInvoice' : function(orderId,printerId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                printerId : JSON.stringify(printerId),
            },
            method: 'printInvoice',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerSentEhf' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'registerSentEhf',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetLastMonthClose' : function(password,start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'resetLastMonthClose',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveOrder' : function(order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'saveOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'searchForOrders' : function(searchWord,page,pageSize, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
                page : JSON.stringify(page),
                pageSize : JSON.stringify(pageSize),
            },
            method: 'searchForOrders',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendReciept' : function(orderId,email, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                email : JSON.stringify(email),
            },
            method: 'sendReciept',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendRecieptWithText' : function(orderId,email,subject,text, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                email : JSON.stringify(email),
                subject : JSON.stringify(subject),
                text : JSON.stringify(text),
            },
            method: 'sendRecieptWithText',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setExternalRefOnCartItem' : function(cartItem,externalId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItem : JSON.stringify(cartItem),
                externalId : JSON.stringify(externalId),
            },
            method: 'setExternalRefOnCartItem',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setNewStartIncrementalOrderId' : function(incrementalOrderId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                incrementalOrderId : JSON.stringify(incrementalOrderId),
                password : JSON.stringify(password),
            },
            method: 'setNewStartIncrementalOrderId',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setOrderStatus' : function(password,orderId,currency,price,status, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startCheckForOrdersToCapture' : function(internalPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                internalPassword : JSON.stringify(internalPassword),
            },
            method: 'startCheckForOrdersToCapture',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateCartItemOnOrder' : function(orderId,cartItem, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                cartItem : JSON.stringify(cartItem),
            },
            method: 'updateCartItemOnOrder',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateCountForOrderLine' : function(cartItemId,orderId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                orderId : JSON.stringify(orderId),
                count : JSON.stringify(count),
            },
            method: 'updateCountForOrderLine',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePriceForOrderLine' : function(cartItemId,orderId,price, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                orderId : JSON.stringify(orderId),
                price : JSON.stringify(price),
            },
            method: 'updatePriceForOrderLine',
            interfaceName: 'core.ordermanager.IOrderManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PageManager.prototype = {
    'accessDenied' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'accessDenied',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addApplication' : function(applicationId,pageCellId,pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
                pageCellId : JSON.stringify(pageCellId),
                pageId : JSON.stringify(pageId),
            },
            method: 'addApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addComment' : function(pageComment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageComment : JSON.stringify(pageComment),
            },
            method: 'addComment',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addExistingApplicationToPageArea' : function(pageId,appId,area, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                appId : JSON.stringify(appId),
                area : JSON.stringify(area),
            },
            method: 'addExistingApplicationToPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addLayoutCell' : function(pageId,incell,beforecell,direction,area, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeModule' : function(moduleId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                moduleId : JSON.stringify(moduleId),
            },
            method: 'changeModule',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changePageUserLevel' : function(pageId,userLevel, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                userLevel : JSON.stringify(userLevel),
            },
            method: 'changePageUserLevel',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearPage' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'clearPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearPageArea' : function(pageId,pageArea, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'clearPageArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createHeaderFooter' : function(type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'createHeaderFooter',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createModal' : function(modalName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                modalName : JSON.stringify(modalName),
            },
            method: 'createModal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewRow' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'createNewRow',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createPage' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteApplication' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteComment' : function(commentId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                commentId : JSON.stringify(commentId),
            },
            method: 'deleteComment',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deletePage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deletePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'dropCell' : function(pageId,cellId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'dropCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'flattenMobileLayout' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'flattenMobileLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplications' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getApplications',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationsBasedOnApplicationSettingsId' : function(appSettingsId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
            },
            method: 'getApplicationsBasedOnApplicationSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationsByPageAreaAndSettingsId' : function(appSettingsId,pageArea, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appSettingsId : JSON.stringify(appSettingsId),
                pageArea : JSON.stringify(pageArea),
            },
            method: 'getApplicationsByPageAreaAndSettingsId',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationsByType' : function(type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getApplicationsByType',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getApplicationsForPage' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getApplicationsForPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCell' : function(pageId,cellId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'getCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getComments' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getComments',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLeftSideBarNames' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLeftSideBarNames',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLooseCell' : function(pageId,cellId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'getLooseCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMobileBody' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getMobileBody',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMobileLink' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMobileLink',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getModalNames' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getModalNames',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getModules' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getModules',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPagesForApplication' : function(appId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'getPagesForApplication',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSecuredSettings' : function(applicationInstanceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'getSecuredSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSecuredSettingsInternal' : function(appName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appName : JSON.stringify(appName),
            },
            method: 'getSecuredSettingsInternal',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasAccessToModule' : function(moduleName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                moduleName : JSON.stringify(moduleName),
            },
            method: 'hasAccessToModule',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'linkPageCell' : function(pageId,cellId,link, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                link : JSON.stringify(link),
            },
            method: 'linkPageCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'moveCell' : function(pageId,cellId,up, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                up : JSON.stringify(up),
            },
            method: 'moveCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'moveCellMobile' : function(pageId,cellId,moveUp, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                moveUp : JSON.stringify(moveUp),
            },
            method: 'moveCellMobile',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAppFromCell' : function(pageId,cellid, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellid : JSON.stringify(cellid),
            },
            method: 'removeAppFromCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetMobileLayout' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'resetMobileLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'restoreLayout' : function(pageId,fromTime, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromTime : JSON.stringify(fromTime),
            },
            method: 'restoreLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveApplicationConfiguration' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveApplicationConfiguration',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCell' : function(pageId,cell, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cell : JSON.stringify(cell),
            },
            method: 'saveCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCellPosition' : function(pageId,cellId,data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                data : JSON.stringify(data),
            },
            method: 'saveCellPosition',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveMobileLink' : function(link, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                link : JSON.stringify(link),
            },
            method: 'saveMobileLink',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePage' : function(page, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                page : JSON.stringify(page),
            },
            method: 'savePage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePageCellGroupAccess' : function(pageId,cellId,groupAccess, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                groupAccess : JSON.stringify(groupAccess),
            },
            method: 'savePageCellGroupAccess',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePageCellSettings' : function(pageId,cellId,settings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                settings : JSON.stringify(settings),
            },
            method: 'savePageCellSettings',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCarouselConfig' : function(pageId,cellId,config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                config : JSON.stringify(config),
            },
            method: 'setCarouselConfig',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCellMode' : function(pageId,cellId,mode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                mode : JSON.stringify(mode),
            },
            method: 'setCellMode',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCellName' : function(pageId,cellId,cellName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                cellName : JSON.stringify(cellName),
            },
            method: 'setCellName',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setPageDescription' : function(pageId,description, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                description : JSON.stringify(description),
            },
            method: 'setPageDescription',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setParentPage' : function(pageId,parentPageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                parentPageId : JSON.stringify(parentPageId),
            },
            method: 'setParentPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setStylesOnCell' : function(pageId,cellId,styles,innerStyles,width, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setWidth' : function(pageId,cellId,outerWidth,outerWidthWithMargins, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startLoadPage' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'startLoadPage',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'swapAppWithCell' : function(pageId,fromCellId,toCellId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                fromCellId : JSON.stringify(fromCellId),
                toCellId : JSON.stringify(toCellId),
            },
            method: 'swapAppWithCell',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleHiddenOnMobile' : function(pageId,cellId,hide, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
                hide : JSON.stringify(hide),
            },
            method: 'toggleHiddenOnMobile',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleLeftSideBar' : function(pageId,columnName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                columnName : JSON.stringify(columnName),
            },
            method: 'toggleLeftSideBar',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'togglePinArea' : function(pageId,cellId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'togglePinArea',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'translatePages' : function(pages, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pages : JSON.stringify(pages),
            },
            method: 'translatePages',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateCellLayout' : function(layout,pageId,cellId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                layout : JSON.stringify(layout),
                pageId : JSON.stringify(pageId),
                cellId : JSON.stringify(cellId),
            },
            method: 'updateCellLayout',
            interfaceName: 'core.pagemanager.IPageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PaymentManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PaymentManager.prototype = {
    'getConfig' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getConfig',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getGeneralPaymentConfig' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getGeneralPaymentConfig',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStorePaymentConfiguration' : function(paymentAppId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentAppId : JSON.stringify(paymentAppId),
            },
            method: 'getStorePaymentConfiguration',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStorePaymentConfigurations' : function(paymentAppId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentAppId : JSON.stringify(paymentAppId),
            },
            method: 'getStorePaymentConfigurations',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetAllAccountingConfigurationForUsersAndOrders' : function(password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'resetAllAccountingConfigurationForUsersAndOrders',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveGeneralPaymentConfig' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveGeneralPaymentConfig',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePaymentConfiguration' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'savePaymentConfiguration',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveStorePaymentConfiguration' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveStorePaymentConfiguration',
            interfaceName: 'core.paymentmanager.IPaymentManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PaymentTerminalManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PaymentTerminalManager.prototype = {
    'getSetings' : function(offset, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                offset : JSON.stringify(offset),
            },
            method: 'getSetings',
            interfaceName: 'core.paymentterminalmanager.IPaymentTerminalManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveSettings' : function(settings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                settings : JSON.stringify(settings),
            },
            method: 'saveSettings',
            interfaceName: 'core.paymentterminalmanager.IPaymentTerminalManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.InvoiceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.InvoiceManager.prototype = {
    'createInvoice' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'createInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBase64EncodedInvoice' : function(orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getBase64EncodedInvoice',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendReceiptToCashRegisterPoint' : function(deviceId,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                deviceId : JSON.stringify(deviceId),
                orderId : JSON.stringify(orderId),
            },
            method: 'sendReceiptToCashRegisterPoint',
            interfaceName: 'core.pdf.IInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.LasGruppenPDFGenerator = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.LasGruppenPDFGenerator.prototype = {
    'generatePdf' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'generatePdf',
            interfaceName: 'core.pdf.ILasGruppenPDFGenerator',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PgaManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PgaManager.prototype = {
    'buyExtraCleaning' : function(multilevelname, date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                date : JSON.stringify(date),
            },
            method: 'buyExtraCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'buyLateCheckout' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'buyLateCheckout',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeCheckoutDate' : function(multilevelname, newDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newDate : JSON.stringify(newDate),
            },
            method: 'changeCheckoutDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkLogin' : function(multilevelname, token, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                token : JSON.stringify(token),
            },
            method: 'checkLogin',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyRoom' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSettings' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getSettings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUnpaidCartItems' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUnpaidCartItems',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isLoggedIn' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isLoggedIn',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'loginByItem' : function(multilevelname, bookingItemId,pincode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingItemId : JSON.stringify(bookingItemId),
                pincode : JSON.stringify(pincode),
            },
            method: 'loginByItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logout' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'logout',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveSettings' : function(multilevelname, pgaSettings, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pgaSettings : JSON.stringify(pgaSettings),
            },
            method: 'saveSettings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendPaymentLink' : function(multilevelname, email,prefix,phone, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                email : JSON.stringify(email),
                prefix : JSON.stringify(prefix),
                phone : JSON.stringify(phone),
            },
            method: 'sendPaymentLink',
            multiLevelName: multilevelname,
            interfaceName: 'core.pga.IPgaManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PkkControlManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PkkControlManager.prototype = {
    'getPkkControlData' : function(licensePlate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                licensePlate : JSON.stringify(licensePlate),
            },
            method: 'getPkkControlData',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPkkControls' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPkkControls',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerPkkControl' : function(data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'registerPkkControl',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removePkkControl' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'removePkkControl',
            interfaceName: 'core.pkk.IPkkControlManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsBookingProcess = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsBookingProcess.prototype = {
    'addAddons' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'addAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addTestMessagesToQueue' : function(multilevelname, message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'addTestMessagesToQueue',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'cancelPaymentProcess' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'cancelPaymentProcess',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeDateOnRoom' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'changeDateOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeGuestCountForRoom' : function(multilevelname, roomId,guestCount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                guestCount : JSON.stringify(guestCount),
            },
            method: 'changeGuestCountForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeNumberOnType' : function(multilevelname, change, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                change : JSON.stringify(change),
            },
            method: 'changeNumberOnType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'chargeOrderWithVerifoneTerminal' : function(multilevelname, orderId,terminalId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                terminalId : JSON.stringify(terminalId),
            },
            method: 'chargeOrderWithVerifoneTerminal',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completeBooking' : function(multilevelname, input, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                input : JSON.stringify(input),
            },
            method: 'completeBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completeBookingForTerminal' : function(multilevelname, input, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                input : JSON.stringify(input),
            },
            method: 'completeBookingForTerminal',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAddonsSummary' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'getAddonsSummary',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBooking' : function(multilevelname, pmsBookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingId : JSON.stringify(pmsBookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConfiguration' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTerminalMessages' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTerminalMessages',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logOn' : function(multilevelname, logindata, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                logindata : JSON.stringify(logindata),
            },
            method: 'logOn',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logOut' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'logOut',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printReciept' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'printReciept',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAddons' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'removeAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeGroupedRooms' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'removeGroupedRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeRoom' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'removeRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveGuestInformation' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'saveGuestInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setBookingItemToCurrentBooking' : function(multilevelname, roomId,itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                itemId : JSON.stringify(itemId),
            },
            method: 'setBookingItemToCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setGuestInformation' : function(multilevelname, bookerInfo, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookerInfo : JSON.stringify(bookerInfo),
            },
            method: 'setGuestInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startBooking' : function(multilevelname, arg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'startBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startPaymentProcess' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'startPaymentProcess',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsEventManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsEventManager.prototype = {
    'createEvent' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'createEvent',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteEntry' : function(multilevelname, entryId,day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                day : JSON.stringify(day),
            },
            method: 'deleteEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntry' : function(multilevelname, entryId,day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                day : JSON.stringify(day),
            },
            method: 'getEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEntryShort' : function(multilevelname, shortId,day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                shortId : JSON.stringify(shortId),
                day : JSON.stringify(day),
            },
            method: 'getEntryShort',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventEntries' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getEventEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventList' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEventList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventListWithDeleted' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEventListWithDeleted',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isChecked' : function(multilevelname, pmsBookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingId : JSON.stringify(pmsBookingId),
            },
            method: 'isChecked',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveEntry' : function(multilevelname, entry,day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entry : JSON.stringify(entry),
                day : JSON.stringify(day),
            },
            method: 'saveEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmseventmanager.IPmsEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.CareTakerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.CareTakerManager.prototype = {
    'addRepeatingTask' : function(multilevelname, repeatingData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                repeatingData : JSON.stringify(repeatingData),
            },
            method: 'addRepeatingTask',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignTask' : function(multilevelname, taskId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                userId : JSON.stringify(userId),
            },
            method: 'assignTask',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForTasksToCreate' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForTasksToCreate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completeTask' : function(multilevelname, taskId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
            },
            method: 'completeTask',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteRepeatingTask' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteRepeatingTask',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCareTakerList' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getCareTakerList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCaretakers' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCaretakers',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRepeatingTasks' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getRepeatingTasks',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomOverview' : function(multilevelname, defectsOnly, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                defectsOnly : JSON.stringify(defectsOnly),
            },
            method: 'getRoomOverview',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ICareTakerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsCoverageAndIncomeReportManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsCoverageAndIncomeReportManager.prototype = {
    'getStatistics' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getStatistics',
            interfaceName: 'core.pmsmanager.IPmsCoverageAndIncomeReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsGetShopOverView = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsGetShopOverView.prototype = {
    'getCustomerObject' : function(storeI, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                storeI : JSON.stringify(storeI),
            },
            method: 'getCustomerObject',
            interfaceName: 'core.pmsmanager.IPmsGetShopOverView',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCustomerToSetup' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCustomerToSetup',
            interfaceName: 'core.pmsmanager.IPmsGetShopOverView',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCustomerObject' : function(object, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                object : JSON.stringify(object),
            },
            method: 'saveCustomerObject',
            interfaceName: 'core.pmsmanager.IPmsGetShopOverView',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsInvoiceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsInvoiceManager.prototype = {
    'calculatePriceMatrix' : function(multilevelname, booking,room, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
                room : JSON.stringify(room),
            },
            method: 'calculatePriceMatrix',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearOrder' : function(multilevelname, bookingId,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'clearOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'convertCartToOrders' : function(multilevelname, id,address,paymentId,orderCreationType,overrideDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                address : JSON.stringify(address),
                paymentId : JSON.stringify(paymentId),
                orderCreationType : JSON.stringify(orderCreationType),
                overrideDate : JSON.stringify(overrideDate),
            },
            method: 'convertCartToOrders',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrder' : function(multilevelname, bookingId,filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'createOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrderOnUnsettledAmount' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'createOrderOnUnsettledAmount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createPeriodeInvoice' : function(multilevelname, start,end,amount,roomId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createRegisterCardOrder' : function(multilevelname, item, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                item : JSON.stringify(item),
            },
            method: 'createRegisterCardOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'creditOrder' : function(multilevelname, bookingId,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'creditOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteStatisticsFilter' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteStatisticsFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteYieldPlan' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteYieldPlan',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchDibsOrdersToAutoPay' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'fetchDibsOrdersToAutoPay',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generateStatistics' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'generateStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccountingStatistics' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAccountingStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAdvancePriceYieldPlan' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getAdvancePriceYieldPlan',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAdvancePriceYields' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllAdvancePriceYields',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllStatisticsFilters' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllStatisticsFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUnpaidItemsForRoom' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'getAllUnpaidItemsForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDiscountsForUser' : function(multilevelname, userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getDiscountsForUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersForRoomToPay' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'getOrdersForRoomToPay',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPaymentLinkConfig' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPaymentLinkConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPreferredPaymentMethod' : function(multilevelname, bookingId,filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'getPreferredPaymentMethod',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSubscriptionOverview' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getSubscriptionOverview',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalOnOrdersForRoom' : function(multilevelname, pmsRoomId,inctaxes, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
                inctaxes : JSON.stringify(inctaxes),
            },
            method: 'getTotalOnOrdersForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalOrdersOnBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getTotalOrdersOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalPaidOnRoomOrBooking' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getTotalPaidOnRoomOrBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserDiscountByCouponCode' : function(multilevelname, couponCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                couponCode : JSON.stringify(couponCode),
            },
            method: 'getUserDiscountByCouponCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isRoomPaidFor' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'isRoomPaidFor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markOrderAsPaid' : function(multilevelname, bookingId,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'markOrderAsPaid',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'recalculateAllBookings' : function(multilevelname, password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'recalculateAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeDuplicateOrderLines' : function(multilevelname, order, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                order : JSON.stringify(order),
            },
            method: 'removeDuplicateOrderLines',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeOrderLinesOnOrdersForBooking' : function(multilevelname, id,roomIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                roomIds : JSON.stringify(roomIds),
            },
            method: 'removeOrderLinesOnOrdersForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveAdvancePriceYield' : function(multilevelname, yieldPlan, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                yieldPlan : JSON.stringify(yieldPlan),
            },
            method: 'saveAdvancePriceYield',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveDiscounts' : function(multilevelname, discounts, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                discounts : JSON.stringify(discounts),
            },
            method: 'saveDiscounts',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePaymentLinkConfig' : function(multilevelname, config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'savePaymentLinkConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveStatisticsFilter' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'saveStatisticsFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendRecieptOrInvoice' : function(multilevelname, orderId,email,bookingId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendRecieptOrInvoiceWithMessage' : function(multilevelname, orderId,email,bookingId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                email : JSON.stringify(email),
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'sendRecieptOrInvoiceWithMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'supportsDailyPmsInvoiceing' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'supportsDailyPmsInvoiceing',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'validateAllInvoiceToDates' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'validateAllInvoiceToDates',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsInvoiceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsManager.prototype = {
    'addAddonToCurrentBooking' : function(multilevelname, itemtypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemtypeId : JSON.stringify(itemtypeId),
            },
            method: 'addAddonToCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addAddonToRoom' : function(multilevelname, addon,pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                addon : JSON.stringify(addon),
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'addAddonToRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addAddonsToBooking' : function(multilevelname, type,roomId,remove, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addBookingItem' : function(multilevelname, bookingId,item,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addBookingItemType' : function(multilevelname, bookingId,item,start,end,guestInfoFromRoom, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addCartItemToRoom' : function(multilevelname, item,pmsBookingRoomId,addedBy, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addComment' : function(multilevelname, bookingId,comment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                comment : JSON.stringify(comment),
            },
            method: 'addComment',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addProductToRoom' : function(multilevelname, productId,pmsRoomId,count, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addRepeatingData' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'addRepeatingData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addToWaitingList' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'addToWaitingList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addToWorkSpace' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'addToWorkSpace',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeDates' : function(multilevelname, roomId,bookingId,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeInvoiceDate' : function(multilevelname, roomId,newDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                newDate : JSON.stringify(newDate),
            },
            method: 'changeInvoiceDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkDoorStatusControl' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkDoorStatusControl',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForDeadCodesApac' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForDeadCodesApac',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForRoomsToClose' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForRoomsToClose',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkIfGuestHasArrived' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkIfGuestHasArrived',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkInRoom' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'checkInRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkOutRoom' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'checkOutRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'cleanupOrdersThatDoesNoLongerExists' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'cleanupOrdersThatDoesNoLongerExists',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'closeItem' : function(multilevelname, id,start,end,source, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completeCareTakerJob' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'completeCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completeCurrentBooking' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'completeCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'confirmBooking' : function(multilevelname, bookingId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'confirmBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'convertTextDate' : function(multilevelname, text, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                text : JSON.stringify(text),
            },
            method: 'convertTextDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createAddonsThatCanBeAddedToRoom' : function(multilevelname, productId,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'createAddonsThatCanBeAddedToRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createAllVirtualOrders' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createAllVirtualOrders',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createChannel' : function(multilevelname, channel, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                channel : JSON.stringify(channel),
            },
            method: 'createChannel',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewPricePlan' : function(multilevelname, code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'createNewPricePlan',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewUserOnBooking' : function(multilevelname, bookingId,name,orgId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                name : JSON.stringify(name),
                orgId : JSON.stringify(orgId),
            },
            method: 'createNewUserOnBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrder' : function(multilevelname, bookingId,filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                filter : JSON.stringify(filter),
            },
            method: 'createOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createPrepaymentOrder' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'createPrepaymentOrder',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createUser' : function(multilevelname, newUser, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                newUser : JSON.stringify(newUser),
            },
            method: 'createUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteAllBookings' : function(multilevelname, code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'deleteAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteDeliveryLogEntry' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteDeliveryLogEntry',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deletePmsFilter' : function(multilevelname, name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'deletePmsFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deletePricePlan' : function(multilevelname, code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'deletePricePlan',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'detachOrderFromBooking' : function(multilevelname, bookingId,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                orderId : JSON.stringify(orderId),
            },
            method: 'detachOrderFromBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'doChargeCardFromAutoBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'doChargeCardFromAutoBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'doNotification' : function(multilevelname, key,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'doNotification',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'endRoom' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'endRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'endRoomWithDate' : function(multilevelname, pmsRoomId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
                date : JSON.stringify(date),
            },
            method: 'endRoomWithDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'failedChargeCard' : function(multilevelname, orderId,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'failedChargeCard',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'forceMarkRoomAsCleaned' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'forceMarkRoomAsCleaned',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'freezeSubscription' : function(multilevelname, pmsBookingRoomId,freezeUntil, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
                freezeUntil : JSON.stringify(freezeUntil),
            },
            method: 'freezeSubscription',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generateNewCodeForRoom' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'generateNewCodeForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generatePgaAccess' : function(multilevelname, pmsBookingId,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingId : JSON.stringify(pmsBookingId),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'generatePgaAccess',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generateRepeatDateRanges' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'generateRepeatDateRanges',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccesories' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAccesories',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAdditionalInfo' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'getAdditionalInfo',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAdditionalTypeInformation' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAdditionalTypeInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAdditionalTypeInformationById' : function(multilevelname, typeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                typeId : JSON.stringify(typeId),
            },
            method: 'getAdditionalTypeInformationById',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAddonsAvailable' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAddonsAvailable',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAddonsForRoom' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'getAddonsForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAddonsWithDiscount' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getAddonsWithDiscount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAddonsWithDiscountForBooking' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getAddonsWithDiscountForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllAdditionalInformationOnRooms' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllAdditionalInformationOnRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllBookings' : function(multilevelname, state, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllBookingsForLoggedOnUser' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllBookingsForLoggedOnUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllBookingsUnsecure' : function(multilevelname, state, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getAllBookingsUnsecure',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllCrmUsers' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllCrmUsers',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllPmsFilters' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllPmsFilters',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllRoomTypes' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllRoomTypes',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllRoomsNeedCleaningToday' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllRoomsNeedCleaningToday',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllRoomsThatHasAddonsOfType' : function(multilevelname, type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getAllRoomsThatHasAddonsOfType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsers' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllUsers',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAvailabilityForType' : function(multilevelname, bookingItemId,startTime,endTime,intervalInMinutes, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingFromBookingEngineId' : function(multilevelname, bookingEngineId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingEngineId : JSON.stringify(bookingEngineId),
            },
            method: 'getBookingFromBookingEngineId',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingFromRoom' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getBookingFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingFromRoomIgnoreDeleted' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'getBookingFromRoomIgnoreDeleted',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingWithOrderId' : function(multilevelname, orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'getBookingWithOrderId',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCardsToSave' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCardsToSave',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCareTakerJob' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCareTakerJobs' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCareTakerJobs',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getChannelMatrix' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getChannelMatrix',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCleaningHistoryForItem' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'getCleaningHistoryForItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCleaningStatistics' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getCleaningStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConferenceData' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConfiguration' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getContract' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getContract',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrenctContract' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCurrenctContract',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentBooking' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDefaultDateRange' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getDefaultDateRange',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDefaultMessage' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getDefaultMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDeliveryLog' : function(multilevelname, productIds,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDeliveryLogByView' : function(multilevelname, viewId,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEarliestEndDate' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getEarliestEndDate',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFutureConferenceData' : function(multilevelname, fromDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fromDate : JSON.stringify(fromDate),
            },
            method: 'getFutureConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getGroupedConferenceData' : function(multilevelname, fromDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fromDate : JSON.stringify(fromDate),
            },
            method: 'getGroupedConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getIntervalAvailability' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getIntervalAvailability',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getItemsForView' : function(multilevelname, viewId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
                date : JSON.stringify(date),
            },
            method: 'getItemsForView',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogEntries' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getLogEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMessage' : function(multilevelname, bookingId,key, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                key : JSON.stringify(key),
            },
            method: 'getMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyRooms' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNumberOfAvailable' : function(multilevelname, itemType,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNumberOfCustomers' : function(multilevelname, state, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                state : JSON.stringify(state),
            },
            method: 'getNumberOfCustomers',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPmsBookingFilter' : function(multilevelname, name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'getPmsBookingFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPrecastedRoom' : function(multilevelname, roomId,bookingItemTypeId,from,to, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                bookingItemTypeId : JSON.stringify(bookingItemTypeId),
                from : JSON.stringify(from),
                to : JSON.stringify(to),
            },
            method: 'getPrecastedRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPrices' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getPrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPricesByCode' : function(multilevelname, code,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomForItem' : function(multilevelname, itemId,atTime, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
                atTime : JSON.stringify(atTime),
            },
            method: 'getRoomForItem',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomsNeedingCheckoutCleaning' : function(multilevelname, day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingCheckoutCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomsNeedingIntervalCleaning' : function(multilevelname, day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingIntervalCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomsNeedingIntervalCleaningSimple' : function(multilevelname, day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getRoomsNeedingIntervalCleaningSimple',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomsToSwap' : function(multilevelname, roomId,moveToType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                moveToType : JSON.stringify(moveToType),
            },
            method: 'getRoomsToSwap',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSimpleCleaningOverview' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getSimpleCleaningOverview',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSimpleInventoryList' : function(multilevelname, roomName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomName : JSON.stringify(roomName),
            },
            method: 'getSimpleInventoryList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSimpleRooms' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getSimpleRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSimpleRoomsForGroup' : function(multilevelname, bookingEngineId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingEngineId : JSON.stringify(bookingEngineId),
            },
            method: 'getSimpleRoomsForGroup',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStatistics' : function(multilevelname, filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getWorkSpaceRooms' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getWorkSpaceRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getpriceCodes' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getpriceCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasLockSystemActive' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'hasLockSystemActive',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasNoBookings' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'hasNoBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hourlyProcessor' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'hourlyProcessor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'initBookingRules' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'initBookingRules',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isActive' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isActive',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isClean' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'isClean',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isUsedToday' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'isUsedToday',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logEntry' : function(multilevelname, logText,bookingId,itemId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logEntryObject' : function(multilevelname, log, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                log : JSON.stringify(log),
            },
            method: 'logEntryObject',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAddonDelivered' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'markAddonDelivered',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markIgnoreUnsettledAmount' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'markIgnoreUnsettledAmount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markKeyDeliveredForAllEndedRooms' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'markKeyDeliveredForAllEndedRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markOtaPaymentsAutomaticallyPaidOnCheckin' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'markOtaPaymentsAutomaticallyPaidOnCheckin',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markRoomAsCleaned' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'markRoomAsCleaned',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markRoomAsCleanedWithoutLogging' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'markRoomAsCleanedWithoutLogging',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markRoomDirty' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'markRoomDirty',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'massUpdatePrices' : function(multilevelname, price,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                price : JSON.stringify(price),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'massUpdatePrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'mergeBookingsOnOrders' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'mergeBookingsOnOrders',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'orderChanged' : function(multilevelname, orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'orderChanged',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'orderCreated' : function(multilevelname, orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'orderCreated',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printCode' : function(multilevelname, gdsDeviceId,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                gdsDeviceId : JSON.stringify(gdsDeviceId),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'printCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'processor' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'processor',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAddonFromRoom' : function(multilevelname, id,pmsBookingRooms, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                pmsBookingRooms : JSON.stringify(pmsBookingRooms),
            },
            method: 'removeAddonFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeAddonFromRoomById' : function(multilevelname, addonId,roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                addonId : JSON.stringify(addonId),
                roomId : JSON.stringify(roomId),
            },
            method: 'removeAddonFromRoomById',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeCareTakerJob' : function(multilevelname, jobId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                jobId : JSON.stringify(jobId),
            },
            method: 'removeCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeChannel' : function(multilevelname, channel, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                channel : JSON.stringify(channel),
            },
            method: 'removeChannel',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeFromBooking' : function(multilevelname, bookingId,roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                roomId : JSON.stringify(roomId),
            },
            method: 'removeFromBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeFromCurrentBooking' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'removeFromCurrentBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeFromWaitingList' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'removeFromWaitingList',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removePgaAccess' : function(multilevelname, pmsBookingId,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingId : JSON.stringify(pmsBookingId),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'removePgaAccess',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeProductFromRoom' : function(multilevelname, pmsBookingRoomId,productId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
                productId : JSON.stringify(productId),
            },
            method: 'removeProductFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'reportMissingInventory' : function(multilevelname, inventories,itemId,roomId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetPriceForRoom' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'resetPriceForRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'returnedKey' : function(multilevelname, roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'returnedKey',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveAccessory' : function(multilevelname, accessory, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                accessory : JSON.stringify(accessory),
            },
            method: 'saveAccessory',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveAdditionalTypeInformation' : function(multilevelname, info, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                info : JSON.stringify(info),
            },
            method: 'saveAdditionalTypeInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveBooking' : function(multilevelname, booking, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
            },
            method: 'saveBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCareTakerJob' : function(multilevelname, job, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                job : JSON.stringify(job),
            },
            method: 'saveCareTakerJob',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveConferenceData' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'saveConferenceData',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveConfiguration' : function(multilevelname, notifications, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                notifications : JSON.stringify(notifications),
            },
            method: 'saveConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveFilter' : function(multilevelname, name,filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
                filter : JSON.stringify(filter),
            },
            method: 'saveFilter',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendCode' : function(multilevelname, prefix,phoneNumber,roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                prefix : JSON.stringify(prefix),
                phoneNumber : JSON.stringify(phoneNumber),
                roomId : JSON.stringify(roomId),
            },
            method: 'sendCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendConfirmation' : function(multilevelname, email,bookingId,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                email : JSON.stringify(email),
                bookingId : JSON.stringify(bookingId),
                type : JSON.stringify(type),
            },
            method: 'sendConfirmation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessage' : function(multilevelname, bookingId,email,title,message, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageOnRoom' : function(multilevelname, email,title,message,roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                email : JSON.stringify(email),
                title : JSON.stringify(title),
                message : JSON.stringify(message),
                roomId : JSON.stringify(roomId),
            },
            method: 'sendMessageOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageToAllTodaysGuests' : function(multilevelname, message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                message : JSON.stringify(message),
            },
            method: 'sendMessageToAllTodaysGuests',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMissingPayment' : function(multilevelname, orderId,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'sendMissingPayment',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendPaymentLink' : function(multilevelname, orderId,bookingId,email,prefix,phone, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendPaymentLinkWithText' : function(multilevelname, orderId,bookingId,email,prefix,phone,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                bookingId : JSON.stringify(bookingId),
                email : JSON.stringify(email),
                prefix : JSON.stringify(prefix),
                phone : JSON.stringify(phone),
                message : JSON.stringify(message),
            },
            method: 'sendPaymentLinkWithText',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendSmsOnRoom' : function(multilevelname, prefix,phone,message,roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                prefix : JSON.stringify(prefix),
                phone : JSON.stringify(phone),
                message : JSON.stringify(message),
                roomId : JSON.stringify(roomId),
            },
            method: 'sendSmsOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendSmsToGuest' : function(multilevelname, guestId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                guestId : JSON.stringify(guestId),
                message : JSON.stringify(message),
            },
            method: 'sendSmsToGuest',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendStatistics' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'sendStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setBooking' : function(multilevelname, addons, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                addons : JSON.stringify(addons),
            },
            method: 'setBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setBookingByAdmin' : function(multilevelname, booking,keepRoomPrices, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
                keepRoomPrices : JSON.stringify(keepRoomPrices),
            },
            method: 'setBookingByAdmin',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setBookingItem' : function(multilevelname, roomId,bookingId,itemId,split, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setBookingItemAndDate' : function(multilevelname, roomId,itemId,split,start,end, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setDefaultAddons' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'setDefaultAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setGuestOnRoom' : function(multilevelname, guests,bookingId,roomId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setNewCleaningIntervalOnRoom' : function(multilevelname, roomId,interval, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                interval : JSON.stringify(interval),
            },
            method: 'setNewCleaningIntervalOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setNewRoomType' : function(multilevelname, roomId,bookingId,newType, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setNewStartDateAndAssignToRoom' : function(multilevelname, roomId,newStartDate,bookingItemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                newStartDate : JSON.stringify(newStartDate),
                bookingItemId : JSON.stringify(bookingItemId),
            },
            method: 'setNewStartDateAndAssignToRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setPrices' : function(multilevelname, code,prices, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
                prices : JSON.stringify(prices),
            },
            method: 'setPrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'splitBooking' : function(multilevelname, roomIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomIds : JSON.stringify(roomIds),
            },
            method: 'splitBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'splitStay' : function(multilevelname, roomId,splitDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                splitDate : JSON.stringify(splitDate),
            },
            method: 'splitStay',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startBooking' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'startBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'swapRoom' : function(multilevelname, roomId,roomIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                roomIds : JSON.stringify(roomIds),
            },
            method: 'swapRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleAddon' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'toggleAddon',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferFromOldCodeToNew' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'transferFromOldCodeToNew',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferTicketsAsAddons' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'transferTicketsAsAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'tryAddToEngine' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'tryAddToEngine',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'unConfirmBooking' : function(multilevelname, bookingId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                message : JSON.stringify(message),
            },
            method: 'unConfirmBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'undeleteBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'undeleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'undoCheckOut' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'undoCheckOut',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'undoLastCleaning' : function(multilevelname, itemId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                itemId : JSON.stringify(itemId),
            },
            method: 'undoLastCleaning',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateAdditionalInformationOnRooms' : function(multilevelname, info, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                info : JSON.stringify(info),
            },
            method: 'updateAdditionalInformationOnRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateAddons' : function(multilevelname, items,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                items : JSON.stringify(items),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'updateAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateAddonsBasedOnGuestCount' : function(multilevelname, pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'updateAddonsBasedOnGuestCount',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateAddonsCountToBooking' : function(multilevelname, type,roomId,count, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePriceMatrixOnRoom' : function(multilevelname, pmsBookingRoomId,priceMatrix, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
                priceMatrix : JSON.stringify(priceMatrix),
            },
            method: 'updatePriceMatrixOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateRepeatingDataForBooking' : function(multilevelname, data,bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
                bookingId : JSON.stringify(bookingId),
            },
            method: 'updateRepeatingDataForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateRoomByUser' : function(multilevelname, bookingId,room, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                room : JSON.stringify(room),
            },
            method: 'updateRoomByUser',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'warnFailedBooking' : function(multilevelname, booking, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
            },
            method: 'warnFailedBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'wubookCreditCardIsInvalid' : function(multilevelname, bookingId,reason, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
                reason : JSON.stringify(reason),
            },
            method: 'wubookCreditCardIsInvalid',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsManagerProcessor = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsManagerProcessor.prototype = {
    'doProcessing' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'doProcessing',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsManagerProcessor',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsNotificationManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsNotificationManager.prototype = {
    'deleteMessage' : function(multilevelname, messageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                messageId : JSON.stringify(messageId),
            },
            method: 'deleteMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsNotificationManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllMessages' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllMessages',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsNotificationManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLanguagesForMessage' : function(multilevelname, key,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                type : JSON.stringify(type),
            },
            method: 'getLanguagesForMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsNotificationManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMessage' : function(multilevelname, messageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                messageId : JSON.stringify(messageId),
            },
            method: 'getMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsNotificationManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPrefixesForMessage' : function(multilevelname, key,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                type : JSON.stringify(type),
            },
            method: 'getPrefixesForMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsNotificationManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveMessage' : function(multilevelname, msg, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                msg : JSON.stringify(msg),
            },
            method: 'saveMessage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsNotificationManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsPaymentTerminal = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsPaymentTerminal.prototype = {
    'addProductToRoom' : function(multilevelname, productId,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'addProductToRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeGuestCountOnRoom' : function(multilevelname, pmsBookingRoomId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
                count : JSON.stringify(count),
            },
            method: 'changeGuestCountOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeRoomTypeOnRoom' : function(multilevelname, pmsBookingRoomId,newTypeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
                newTypeId : JSON.stringify(newTypeId),
            },
            method: 'changeRoomTypeOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'findBookings' : function(multilevelname, phoneNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
            },
            method: 'findBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBooking' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMaxNumberOfRooms' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'getMaxNumberOfRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderSummary' : function(multilevelname, bookingId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                bookingId : JSON.stringify(bookingId),
            },
            method: 'getOrderSummary',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomTypesThatRoomCanBeChangedTo' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getRoomTypesThatRoomCanBeChangedTo',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'payIndividualRoom' : function(multilevelname, pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'payIndividualRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printReciept' : function(multilevelname, orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
            },
            method: 'printReciept',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeProductFromRoom' : function(multilevelname, productId,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'removeProductFromRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startBooking' : function(multilevelname, data, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'startBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateBooking' : function(multilevelname, booking,user,company, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                booking : JSON.stringify(booking),
                user : JSON.stringify(user),
                company : JSON.stringify(company),
            },
            method: 'updateBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsPaymentTerminal',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsReportManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsReportManager.prototype = {
    'getCleaningLog' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getCleaningLog',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConferenceStatistics' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getConferenceStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMonthlyStatistics' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMonthlyStatistics',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReport' : function(multilevelname, start,end,compareTo,excludeClosedRooms, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                compareTo : JSON.stringify(compareTo),
                excludeClosedRooms : JSON.stringify(excludeClosedRooms),
            },
            method: 'getReport',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomCoverage' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getRoomCoverage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSubscriptionReport' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getSubscriptionReport',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsage' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getUsage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsReportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsSelfManagement = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsSelfManagement.prototype = {
    'getAddonsWithDiscountForBooking' : function(multilevelname, id,pmsBookingRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                pmsBookingRoomId : JSON.stringify(pmsBookingRoomId),
            },
            method: 'getAddonsWithDiscountForBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsSelfManagement',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBookingById' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getBookingById',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsSelfManagement',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrderById' : function(multilevelname, id,orderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                orderId : JSON.stringify(orderId),
            },
            method: 'getOrderById',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsSelfManagement',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveAddonSetup' : function(multilevelname, id,addons, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                addons : JSON.stringify(addons),
            },
            method: 'saveAddonSetup',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsSelfManagement',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PmsWebBookingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PmsWebBookingManager.prototype = {
    'getAllRooms' : function(multilevelname, start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.IPmsWebBookingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SmsHistoryManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SmsHistoryManager.prototype = {
    'generateSmsUsage' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'generateSmsUsage',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsmanager.ISmsHistoryManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PosManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PosManager.prototype = {
    'addCashWithDrawalToTab' : function(tabId,amount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                amount : JSON.stringify(amount),
            },
            method: 'addCashWithDrawalToTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addGiftCardToTab' : function(tabId,value, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                value : JSON.stringify(value),
            },
            method: 'addGiftCardToTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addOrderIdToZReport' : function(incrementalOrderId,zReportId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                incrementalOrderId : JSON.stringify(incrementalOrderId),
                zReportId : JSON.stringify(zReportId),
                password : JSON.stringify(password),
            },
            method: 'addOrderIdToZReport',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addToTab' : function(tabId,cartItem, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                cartItem : JSON.stringify(cartItem),
            },
            method: 'addToTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeTaxRate' : function(tabId,taxGroupNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                taxGroupNumber : JSON.stringify(taxGroupNumber),
            },
            method: 'changeTaxRate',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completeTransaction' : function(tabId,orderId,cashPointDeviceId,kitchenDeviceId,paymentMetaData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                orderId : JSON.stringify(orderId),
                cashPointDeviceId : JSON.stringify(cashPointDeviceId),
                kitchenDeviceId : JSON.stringify(kitchenDeviceId),
                paymentMetaData : JSON.stringify(paymentMetaData),
            },
            method: 'completeTransaction',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createCashPoint' : function(name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                name : JSON.stringify(name),
            },
            method: 'createCashPoint',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewTab' : function(referenceName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                referenceName : JSON.stringify(referenceName),
            },
            method: 'createNewTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewTable' : function(tableName,tableNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableName : JSON.stringify(tableName),
                tableNumber : JSON.stringify(tableNumber),
            },
            method: 'createNewTable',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createNewView' : function(viewName,viewType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewName : JSON.stringify(viewName),
                viewType : JSON.stringify(viewType),
            },
            method: 'createNewView',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createOrder' : function(cartItems,paymentId,tabId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItems : JSON.stringify(cartItems),
                paymentId : JSON.stringify(paymentId),
                tabId : JSON.stringify(tabId),
            },
            method: 'createOrder',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createZReport' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createZReport',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteTab' : function(tabId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
            },
            method: 'deleteTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteTable' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'deleteTable',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteView' : function(viewId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
            },
            method: 'deleteView',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllTabs' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllTabs',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCashPoint' : function(cashPointId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cashPointId : JSON.stringify(cashPointId),
            },
            method: 'getCashPoint',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCashPoints' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCashPoints',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentTabIdForTableId' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getCurrentTabIdForTableId',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductList' : function(viewId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
            },
            method: 'getProductList',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTab' : function(tabId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
            },
            method: 'getTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTabCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTabCount',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTable' : function(viewId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
            },
            method: 'getTable',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTables' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTables',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotal' : function(tabId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
            },
            method: 'getTotal',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalForCurrentZReport' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTotalForCurrentZReport',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTotalForItems' : function(cartItems, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItems : JSON.stringify(cartItems),
            },
            method: 'getTotalForItems',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getView' : function(viewId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
            },
            method: 'getView',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getViews' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getViews',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getZReport' : function(zReportId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                zReportId : JSON.stringify(zReportId),
            },
            method: 'getZReport',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getZReportsUnfinalized' : function(filterOptions, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getZReportsUnfinalized',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasTables' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'hasTables',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'moveList' : function(viewId,listId,down, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                viewId : JSON.stringify(viewId),
                listId : JSON.stringify(listId),
                down : JSON.stringify(down),
            },
            method: 'moveList',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printKitchen' : function(tabId,gdsDeviceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                gdsDeviceId : JSON.stringify(gdsDeviceId),
            },
            method: 'printKitchen',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printOverview' : function(tabId,cashPointDeviceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                cashPointDeviceId : JSON.stringify(cashPointDeviceId),
            },
            method: 'printOverview',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'printRoomReceipt' : function(gdsDeviceId,roomName,guestName,items, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                gdsDeviceId : JSON.stringify(gdsDeviceId),
                roomName : JSON.stringify(roomName),
                guestName : JSON.stringify(guestName),
                items : JSON.stringify(items),
            },
            method: 'printRoomReceipt',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeFromTab' : function(cartItemId,tabId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItemId : JSON.stringify(cartItemId),
                tabId : JSON.stringify(tabId),
            },
            method: 'removeFromTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeItemsFromTab' : function(tabId,cartItems, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                cartItems : JSON.stringify(cartItems),
            },
            method: 'removeItemsFromTab',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCashPoint' : function(cashPoint, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cashPoint : JSON.stringify(cashPoint),
            },
            method: 'saveCashPoint',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveTable' : function(table, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                table : JSON.stringify(table),
            },
            method: 'saveTable',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveView' : function(view, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                view : JSON.stringify(view),
            },
            method: 'saveView',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setDiscountToCartItem' : function(tabId,cartItemId,newValue, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                cartItemId : JSON.stringify(cartItemId),
                newValue : JSON.stringify(newValue),
            },
            method: 'setDiscountToCartItem',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setNewProductPrice' : function(tabId,cartItemId,newValue, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                cartItemId : JSON.stringify(cartItemId),
                newValue : JSON.stringify(newValue),
            },
            method: 'setNewProductPrice',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setTabDiscount' : function(tabId,discount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tabId : JSON.stringify(tabId),
                discount : JSON.stringify(discount),
            },
            method: 'setTabDiscount',
            interfaceName: 'core.pos.IPosManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PrintManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PrintManager.prototype = {
    'getPrintJobs' : function(printerId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                printerId : JSON.stringify(printerId),
            },
            method: 'getPrintJobs',
            interfaceName: 'core.printmanager.IPrintManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.StorePrintManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StorePrintManager.prototype = {
    'deletePrinter' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deletePrinter',
            interfaceName: 'core.printmanager.IStorePrintManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPrinters' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPrinters',
            interfaceName: 'core.printmanager.IStorePrintManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePrinter' : function(printer, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                printer : JSON.stringify(printer),
            },
            method: 'savePrinter',
            interfaceName: 'core.printmanager.IStorePrintManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ProductManager.prototype = {
    'addAdditionalTaxGroup' : function(productId,taxGroupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                taxGroupId : JSON.stringify(taxGroupId),
            },
            method: 'addAdditionalTaxGroup',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeStockQuantity' : function(productId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'changeStockQuantity',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeTaxCode' : function(product,taxGroupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                product : JSON.stringify(product),
                taxGroupId : JSON.stringify(taxGroupId),
            },
            method: 'changeTaxCode',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'copyProduct' : function(fromProductId,newName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fromProductId : JSON.stringify(fromProductId),
                newName : JSON.stringify(newName),
            },
            method: 'copyProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createProduct' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createProductList' : function(listName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listName : JSON.stringify(listName),
            },
            method: 'createProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteCategory' : function(categoryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                categoryId : JSON.stringify(categoryId),
            },
            method: 'deleteCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteProductList' : function(listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'deleteProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'findProducts' : function(filterOptions, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'findProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAccountingDetail' : function(accountNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                accountNumber : JSON.stringify(accountNumber),
            },
            method: 'getAccountingDetail',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllCategories' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllCategories',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllProducts' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllProductsForRestaurant' : function(filterOptions, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterOptions : JSON.stringify(filterOptions),
            },
            method: 'getAllProductsForRestaurant',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllProductsIncDeleted' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllProductsIncDeleted',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllProductsLight' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllProductsLight',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCategory' : function(categoryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                categoryId : JSON.stringify(categoryId),
            },
            method: 'getCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDeletedProduct' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getDeletedProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLatestProducts' : function(count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPageIdByName' : function(productName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productName : JSON.stringify(productName),
            },
            method: 'getPageIdByName',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPrice' : function(productId,variations, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                variations : JSON.stringify(variations),
            },
            method: 'getPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProduct' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductByPage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProductByPage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductFromApplicationId' : function(app_uuid, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                app_uuid : JSON.stringify(app_uuid),
            },
            method: 'getProductFromApplicationId',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductLight' : function(ids, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ids : JSON.stringify(ids),
            },
            method: 'getProductLight',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductList' : function(listId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listId : JSON.stringify(listId),
            },
            method: 'getProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductLists' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getProductLists',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProducts' : function(productCriteria, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productCriteria : JSON.stringify(productCriteria),
            },
            method: 'getProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRandomProducts' : function(fetchSize,ignoreProductId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                fetchSize : JSON.stringify(fetchSize),
                ignoreProductId : JSON.stringify(ignoreProductId),
            },
            method: 'getRandomProducts',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTaxes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeProduct' : function(productId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
            },
            method: 'removeProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeTaxGroup' : function(productId,taxGroupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                taxGroupId : JSON.stringify(taxGroupId),
            },
            method: 'removeTaxGroup',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveAccountingDetail' : function(detail, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                detail : JSON.stringify(detail),
            },
            method: 'saveAccountingDetail',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveAccountingInformation' : function(productId,infos, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                infos : JSON.stringify(infos),
            },
            method: 'saveAccountingInformation',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCategory' : function(categories, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                categories : JSON.stringify(categories),
            },
            method: 'saveCategory',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveProduct' : function(product, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                product : JSON.stringify(product),
            },
            method: 'saveProduct',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveProductList' : function(productList, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productList : JSON.stringify(productList),
            },
            method: 'saveProductList',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'search' : function(searchWord,pageSize,page, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
                pageSize : JSON.stringify(pageSize),
                page : JSON.stringify(page),
            },
            method: 'search',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setMainImage' : function(productId,imageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                imageId : JSON.stringify(imageId),
            },
            method: 'setMainImage',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setProductDynamicPrice' : function(productId,count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                count : JSON.stringify(count),
            },
            method: 'setProductDynamicPrice',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setTaxes' : function(group, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'setTaxes',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'translateEntries' : function(entryIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryIds : JSON.stringify(entryIds),
            },
            method: 'translateEntries',
            interfaceName: 'core.productmanager.IProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.PullServerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.PullServerManager.prototype = {
    'getPullMessages' : function(keyId,storeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                keyId : JSON.stringify(keyId),
                storeId : JSON.stringify(storeId),
            },
            method: 'getPullMessages',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markMessageAsReceived' : function(messageId,storeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                messageId : JSON.stringify(messageId),
                storeId : JSON.stringify(storeId),
            },
            method: 'markMessageAsReceived',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'savePullMessage' : function(pullMessage, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pullMessage : JSON.stringify(pullMessage),
            },
            method: 'savePullMessage',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'triggerCheckForPullMessage' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'triggerCheckForPullMessage',
            interfaceName: 'core.pullserver.IPullServerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.QuestBackManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.QuestBackManager.prototype = {
    'answerQuestions' : function(testId,applicationId,pageId,answers, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignTestsToUsers' : function(testIds,userids, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testIds : JSON.stringify(testIds),
                userids : JSON.stringify(userids),
            },
            method: 'assignTestsToUsers',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignUserToTest' : function(testId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
            },
            method: 'assignUserToTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createTemplatePageIfNotExists' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createTemplatePageIfNotExists',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createTest' : function(testName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testName : JSON.stringify(testName),
            },
            method: 'createTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteTest' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'deleteTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'exportToExcel' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'exportToExcel',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllTests' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllTests',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getBestCategoryResultForCompany' : function(userId,catId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                catId : JSON.stringify(catId),
            },
            method: 'getBestCategoryResultForCompany',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCategories' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCategories',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCategoriesForTest' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getCategoriesForTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompanyScoreForTestForCurrentUser' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getCompanyScoreForTestForCurrentUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNextQuestionPage' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getNextQuestionPage',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOptionsByPageId' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getOptionsByPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPageId' : function(questionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                questionId : JSON.stringify(questionId),
            },
            method: 'getPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProgress' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getProgress',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProgressForUser' : function(userId,testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getProgressForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getQuestion' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getQuestion',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getQuestionTitle' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getQuestionTitle',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getResult' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getResult',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getResultRequirement' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getResultRequirement',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getResultWithReference' : function(testId,referenceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                referenceId : JSON.stringify(referenceId),
            },
            method: 'getResultWithReference',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getScoreForTest' : function(userId,testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getScoreForTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTest' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTestResult' : function(testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
            },
            method: 'getTestResult',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTestResultForUser' : function(testId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                testId : JSON.stringify(testId),
                userId : JSON.stringify(userId),
            },
            method: 'getTestResultForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTestResults' : function(userId,testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
            },
            method: 'getTestResults',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTests' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTests',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTestsForUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getTestsForUser',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTypeByPageId' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getTypeByPageId',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasAnswered' : function(pageId,testId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                testId : JSON.stringify(testId),
            },
            method: 'hasAnswered',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'importExcel' : function(base64,language, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                base64 : JSON.stringify(base64),
                language : JSON.stringify(language),
            },
            method: 'importExcel',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isQuestBackSent' : function(userId,testId,reference, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                testId : JSON.stringify(testId),
                reference : JSON.stringify(reference),
            },
            method: 'isQuestBackSent',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'questionTreeChanged' : function(applicationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationId : JSON.stringify(applicationId),
            },
            method: 'questionTreeChanged',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveQuestBackAnswerResponse' : function(answerId,answer, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                answerId : JSON.stringify(answerId),
                answer : JSON.stringify(answer),
            },
            method: 'saveQuestBackAnswerResponse',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveQuestBackResultRequirement' : function(requirement, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                requirement : JSON.stringify(requirement),
            },
            method: 'saveQuestBackResultRequirement',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveTest' : function(test, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                test : JSON.stringify(test),
            },
            method: 'saveTest',
            interfaceName: 'core.questback.IQuestBackManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendQuestBack' : function(testId,userId,reference,event, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.BookingComRateManagerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.BookingComRateManagerManager.prototype = {
    'getRateManagerConfig' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getRateManagerConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.ratemanager.IBookingComRateManagerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pushAllBookings' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'pushAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.ratemanager.IBookingComRateManagerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'pushInventoryList' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'pushInventoryList',
            multiLevelName: multilevelname,
            interfaceName: 'core.ratemanager.IBookingComRateManagerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveRateManagerConfig' : function(multilevelname, config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveRateManagerConfig',
            multiLevelName: multilevelname,
            interfaceName: 'core.ratemanager.IBookingComRateManagerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateRate' : function(multilevelname, start,end,roomId,rate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                roomId : JSON.stringify(roomId),
                rate : JSON.stringify(rate),
            },
            method: 'updateRate',
            multiLevelName: multilevelname,
            interfaceName: 'core.ratemanager.IBookingComRateManagerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ReportingManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ReportingManager.prototype = {
    'getAllEventsFromSession' : function(startDate,stopDate,searchSessionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                searchSessionId : JSON.stringify(searchSessionId),
            },
            method: 'getAllEventsFromSession',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getConnectedUsers' : function(startdate,stopDate,filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startdate : JSON.stringify(startdate),
                stopDate : JSON.stringify(stopDate),
                filter : JSON.stringify(filter),
            },
            method: 'getConnectedUsers',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersCreated' : function(startDate,stopDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getOrdersCreated',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPageViews' : function(startDate,stopDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getPageViews',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductViewed' : function(startDate,stopDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getProductViewed',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReport' : function(startDate,stopDate,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
                type : JSON.stringify(type),
            },
            method: 'getReport',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserLoggedOn' : function(startDate,stopDate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                startDate : JSON.stringify(startDate),
                stopDate : JSON.stringify(stopDate),
            },
            method: 'getUserLoggedOn',
            interfaceName: 'core.reportingmanager.IReportingManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ResturantManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ResturantManager.prototype = {
    'addCartItemToCurrentTableSession' : function(tableId,cartItem, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
                cartItem : JSON.stringify(cartItem),
            },
            method: 'addCartItemToCurrentTableSession',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addCartItems' : function(cartItems,tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItems : JSON.stringify(cartItems),
                tableId : JSON.stringify(tableId),
            },
            method: 'addCartItems',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addCartItemsToReservation' : function(cartItems,reservationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cartItems : JSON.stringify(cartItems),
                reservationId : JSON.stringify(reservationId),
            },
            method: 'addCartItemsToReservation',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeToDifferentSession' : function(sessionId,tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                sessionId : JSON.stringify(sessionId),
                tableId : JSON.stringify(tableId),
            },
            method: 'changeToDifferentSession',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkPinCode' : function(pincode,bookingId,pmsRoomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pincode : JSON.stringify(pincode),
                bookingId : JSON.stringify(bookingId),
                pmsRoomId : JSON.stringify(pmsRoomId),
            },
            method: 'checkPinCode',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'completePayment' : function(paymentMethodId,cartItemIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentMethodId : JSON.stringify(paymentMethodId),
                cartItemIds : JSON.stringify(cartItemIds),
            },
            method: 'completePayment',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createCartForReservation' : function(reservationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                reservationId : JSON.stringify(reservationId),
            },
            method: 'createCartForReservation',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createCartForTable' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'createCartForTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createRoom' : function(roomName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomName : JSON.stringify(roomName),
            },
            method: 'createRoom',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createTable' : function(roomId,tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
                tableId : JSON.stringify(tableId),
            },
            method: 'createTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createTableSession' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'createTableSession',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteTable' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'deleteTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllSessions' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllSessions',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllSessionsForTable' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getAllSessionsForTable',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentTableData' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getCurrentTableData',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomById' : function(roomId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'getRoomById',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRooms' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getRooms',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTableById' : function(tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                tableId : JSON.stringify(tableId),
            },
            method: 'getTableById',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTableDataForReservation' : function(reservationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                reservationId : JSON.stringify(reservationId),
            },
            method: 'getTableDataForReservation',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTableDayData' : function(date,tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                date : JSON.stringify(date),
                tableId : JSON.stringify(tableId),
            },
            method: 'getTableDayData',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTableReservation' : function(reservationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                reservationId : JSON.stringify(reservationId),
            },
            method: 'getTableReservation',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTerminalMessages' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTerminalMessages',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isOrderPriceCorrect' : function(paymentMethodId,cartItems,price, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentMethodId : JSON.stringify(paymentMethodId),
                cartItems : JSON.stringify(cartItems),
                price : JSON.stringify(price),
            },
            method: 'isOrderPriceCorrect',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'payOnRoom' : function(room,cartItemsIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                room : JSON.stringify(room),
                cartItemsIds : JSON.stringify(cartItemsIds),
            },
            method: 'payOnRoom',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'prePrint' : function(paymentMethodId,cartItemIds,printerId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                paymentMethodId : JSON.stringify(paymentMethodId),
                cartItemIds : JSON.stringify(cartItemIds),
                printerId : JSON.stringify(printerId),
            },
            method: 'prePrint',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startNewReservation' : function(start,end,name,tableId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                name : JSON.stringify(name),
                tableId : JSON.stringify(tableId),
            },
            method: 'startNewReservation',
            interfaceName: 'core.resturantmanager.IResturantManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SalesManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SalesManager.prototype = {
    'findCustomer' : function(key,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                type : JSON.stringify(type),
            },
            method: 'findCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCustomer' : function(orgId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orgId : JSON.stringify(orgId),
            },
            method: 'getCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEvent' : function(eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsForCustomer' : function(orgId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orgId : JSON.stringify(orgId),
            },
            method: 'getEventsForCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsForDay' : function(day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getEventsForDay',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLatestCustomer' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLatestCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLatestEvent' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLatestEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCustomer' : function(customer, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                customer : JSON.stringify(customer),
            },
            method: 'saveCustomer',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveEvent' : function(event, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                event : JSON.stringify(event),
            },
            method: 'saveEvent',
            interfaceName: 'core.sales.ISalesManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ScormManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ScormManager.prototype = {
    'deleteScormPackage' : function(packageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                packageId : JSON.stringify(packageId),
            },
            method: 'deleteScormPackage',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllPackages' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllPackages',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMandatoryPackages' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getMandatoryPackages',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyScorm' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getMyScorm',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPackage' : function(packageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                packageId : JSON.stringify(packageId),
            },
            method: 'getPackage',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getScormCertificateContent' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getScormCertificateContent',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getScormForCurrentUser' : function(scormId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                scormId : JSON.stringify(scormId),
                userId : JSON.stringify(userId),
            },
            method: 'getScormForCurrentUser',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'needUpdate' : function(username,scormid,isCompleted,isPassed,isFailed, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                scormid : JSON.stringify(scormid),
                isCompleted : JSON.stringify(isCompleted),
                isPassed : JSON.stringify(isPassed),
                isFailed : JSON.stringify(isFailed),
            },
            method: 'needUpdate',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveScormCertificateContent' : function(content, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                content : JSON.stringify(content),
            },
            method: 'saveScormCertificateContent',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveSetup' : function(scormPackage, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                scormPackage : JSON.stringify(scormPackage),
            },
            method: 'saveSetup',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'syncMoodle' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'syncMoodle',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateResult' : function(scorm, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                scorm : JSON.stringify(scorm),
            },
            method: 'updateResult',
            interfaceName: 'core.scormmanager.IScormManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SearchManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SearchManager.prototype = {
    'search' : function(searchWord, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'search',
            interfaceName: 'core.searchmanager.ISearchManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SedoxProductManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SedoxProductManager.prototype = {
    'addCommentToUser' : function(userId,comment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                comment : JSON.stringify(comment),
            },
            method: 'addCommentToUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addCreditToSlave' : function(slaveId,amount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slaveId : JSON.stringify(slaveId),
                amount : JSON.stringify(amount),
            },
            method: 'addCreditToSlave',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addFileToProduct' : function(base64EncodedFile,fileName,fileType,productId,options, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addFileToProductAsync' : function(sedoxBinaryFile,fileType,fileName,productId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addReference' : function(productId,reference, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                reference : JSON.stringify(reference),
            },
            method: 'addReference',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addSlaveToUser' : function(masterUserId,slaveUserId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                masterUserId : JSON.stringify(masterUserId),
                slaveUserId : JSON.stringify(slaveUserId),
            },
            method: 'addSlaveToUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserCredit' : function(id,description,amount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
                description : JSON.stringify(description),
                amount : JSON.stringify(amount),
            },
            method: 'addUserCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeDeveloperStatus' : function(userId,disabled, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                disabled : JSON.stringify(disabled),
            },
            method: 'changeDeveloperStatus',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearManager' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearManager',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createSedoxProduct' : function(sedoxProduct,base64encodedOriginalFile,originalFileName,forSlaveId,origin,comment,useCredit,options,reference, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'finishUpload' : function(forSlaveId,sharedProduct,useCredit,comment,originalFile,cmdEncryptedFile,options,base64EncodeString,originalFileName,origin,fromUserId,referenceId, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsers' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsersAsTreeNodes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllUsersAsTreeNodes',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsersWithNegativeCreditLimit' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllUsersWithNegativeCreditLimit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentUserCreditHistory' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getCurrentUserCreditHistory',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentUserCreditHistoryCount' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getCurrentUserCreditHistoryCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDevelopers' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getDevelopers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getExtraInformationForFile' : function(productId,fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
            },
            method: 'getExtraInformationForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFileNotProcessedToDayCount' : function(daysBack, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                daysBack : JSON.stringify(daysBack),
            },
            method: 'getFileNotProcessedToDayCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLatestProductsList' : function(count, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                count : JSON.stringify(count),
            },
            method: 'getLatestProductsList',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getNextFileId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getNextFileId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrders' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOrders',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOrdersPageCount' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getOrdersPageCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPriceForProduct' : function(productId,files, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'getPriceForProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductById' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getProductById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductIds' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getProductIds',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductsByDaysBack' : function(day, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                day : JSON.stringify(day),
            },
            method: 'getProductsByDaysBack',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductsFirstUploadedByCurrentUser' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getProductsFirstUploadedByCurrentUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getProductsFirstUploadedByCurrentUserTotalPages' : function(filterData, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filterData : JSON.stringify(filterData),
            },
            method: 'getProductsFirstUploadedByCurrentUserTotalPages',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSedoxProductByMd5Sum' : function(md5sum, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                md5sum : JSON.stringify(md5sum),
            },
            method: 'getSedoxProductByMd5Sum',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSedoxUserAccount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getSedoxUserAccount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSedoxUserAccountById' : function(userid, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userid : JSON.stringify(userid),
            },
            method: 'getSedoxUserAccountById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSharedProductById' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getSharedProductById',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSlaves' : function(masterUserId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                masterUserId : JSON.stringify(masterUserId),
            },
            method: 'getSlaves',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStatistic' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getStatistic',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUploadHistory' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUploadHistory',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserFileDownloadCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUserFileDownloadCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserFileUploadCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUserFileUploadCount',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'invokeCreditUpdate' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'invokeCreditUpdate',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'login' : function(emailAddress,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                emailAddress : JSON.stringify(emailAddress),
                password : JSON.stringify(password),
            },
            method: 'login',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsFinished' : function(productId,finished, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                finished : JSON.stringify(finished),
            },
            method: 'markAsFinished',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'notifyForCustomer' : function(productId,extraText, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                extraText : JSON.stringify(extraText),
            },
            method: 'notifyForCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'purchaseOnlyForCustomer' : function(productId,files, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'purchaseOnlyForCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'purchaseProduct' : function(productId,files, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                files : JSON.stringify(files),
            },
            method: 'purchaseProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'refreshEvcCredit' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'refreshEvcCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeBinaryFileFromProduct' : function(productId,fileId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
            },
            method: 'removeBinaryFileFromProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeSlaveFromMaster' : function(slaveId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slaveId : JSON.stringify(slaveId),
            },
            method: 'removeSlaveFromMaster',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'requestSpecialFile' : function(productId,comment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                comment : JSON.stringify(comment),
            },
            method: 'requestSpecialFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'search' : function(search, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'search',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'searchForUsers' : function(searchString, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchString : JSON.stringify(searchString),
            },
            method: 'searchForUsers',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'searchUserFiles' : function(search, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'searchUserFiles',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendProductByMail' : function(productId,extraText,files, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                extraText : JSON.stringify(extraText),
                files : JSON.stringify(files),
            },
            method: 'sendProductByMail',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendProductToDifferentEmail' : function(productId,emailAddress,files,extraText, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setChecksum' : function(productId,checksum, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                checksum : JSON.stringify(checksum),
            },
            method: 'setChecksum',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCreditAllowedLimist' : function(userId,creditlimit, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                creditlimit : JSON.stringify(creditlimit),
            },
            method: 'setCreditAllowedLimist',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setEvcId' : function(userId,evcId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                evcId : JSON.stringify(evcId),
            },
            method: 'setEvcId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setExtraInformationForFile' : function(productId,fileId,text, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                fileId : JSON.stringify(fileId),
                text : JSON.stringify(text),
            },
            method: 'setExtraInformationForFile',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setFixedPrice' : function(userId,price, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                price : JSON.stringify(price),
            },
            method: 'setFixedPrice',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setPushoverId' : function(pushover, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pushover : JSON.stringify(pushover),
            },
            method: 'setPushoverId',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setPushoverIdForUser' : function(pushover,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pushover : JSON.stringify(pushover),
                userId : JSON.stringify(userId),
            },
            method: 'setPushoverIdForUser',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSpecialRequestsForFile' : function(productId,fileId,dpf,egr,decat,vmax,adblue,dtc,flaps, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setType' : function(productId,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                type : JSON.stringify(type),
            },
            method: 'setType',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sync' : function(option, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                option : JSON.stringify(option),
            },
            method: 'sync',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'syncFromMagento' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'syncFromMagento',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleAllowNegativeCredit' : function(userId,allow, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                allow : JSON.stringify(allow),
            },
            method: 'toggleAllowNegativeCredit',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleAllowWindowsApp' : function(userId,allow, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                allow : JSON.stringify(allow),
            },
            method: 'toggleAllowWindowsApp',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleBadCustomer' : function(userId,badCustomer, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                badCustomer : JSON.stringify(badCustomer),
            },
            method: 'toggleBadCustomer',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleIsNorwegian' : function(userId,isNorwegian, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                isNorwegian : JSON.stringify(isNorwegian),
            },
            method: 'toggleIsNorwegian',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'togglePassiveSlaveMode' : function(userId,toggle, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                toggle : JSON.stringify(toggle),
            },
            method: 'togglePassiveSlaveMode',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleSaleableProduct' : function(productId,saleable, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                saleable : JSON.stringify(saleable),
            },
            method: 'toggleSaleableProduct',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleStartStop' : function(productId,toggle, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                productId : JSON.stringify(productId),
                toggle : JSON.stringify(toggle),
            },
            method: 'toggleStartStop',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'transferCreditToSlave' : function(slaveId,amount, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slaveId : JSON.stringify(slaveId),
                amount : JSON.stringify(amount),
            },
            method: 'transferCreditToSlave',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateEvcCreditAccounts' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'updateEvcCreditAccounts',
            interfaceName: 'core.sedox.ISedoxProductManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SendRegningManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SendRegningManager.prototype = {
    'sendOrder' : function(orderId,email, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                email : JSON.stringify(email),
            },
            method: 'sendOrder',
            interfaceName: 'core.sendregning.ISendRegningManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SimpleEventManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SimpleEventManager.prototype = {
    'addUserToEvent' : function(pageId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
                userId : JSON.stringify(userId),
            },
            method: 'addUserToEvent',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteEvent' : function(eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'deleteEvent',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllEvents' : function(listPageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listPageId : JSON.stringify(listPageId),
            },
            method: 'getAllEvents',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventById' : function(eventId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                eventId : JSON.stringify(eventId),
            },
            method: 'getEventById',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventByPageId' : function(pageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                pageId : JSON.stringify(pageId),
            },
            method: 'getEventByPageId',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEventsInFuture' : function(listPageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                listPageId : JSON.stringify(listPageId),
            },
            method: 'getEventsInFuture',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveEvent' : function(simpleEvent, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                simpleEvent : JSON.stringify(simpleEvent),
            },
            method: 'saveEvent',
            interfaceName: 'core.simpleeventmanager.ISimpleEventManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.StoreManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StoreManager.prototype = {
    'acceptGDPR' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'acceptGDPR',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'acceptSlave' : function(slaveStoreId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                slaveStoreId : JSON.stringify(slaveStoreId),
            },
            method: 'acceptSlave',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'autoCreateStore' : function(hostname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                hostname : JSON.stringify(hostname),
            },
            method: 'autoCreateStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeTimeZone' : function(timezone, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                timezone : JSON.stringify(timezone),
            },
            method: 'changeTimeZone',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createStore' : function(hostname,email,password,notify, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'delete' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'delete',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'enableExtendedMode' : function(toggle,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'enableExtendedMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'enableSMSAccess' : function(toggle,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                toggle : JSON.stringify(toggle),
                password : JSON.stringify(password),
            },
            method: 'enableSMSAccess',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'generateStoreId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'generateStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllEnvironments' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllEnvironments',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCriticalMessage' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCriticalMessage',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCurrentSession' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCurrentSession',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getKey' : function(key, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
            },
            method: 'getKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getKeySecure' : function(key,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                password : JSON.stringify(password),
            },
            method: 'getKeySecure',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMultiLevelNames' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMultiLevelNames',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyStore' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSlaves' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getSlaves',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStoreId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'initializeStore' : function(webAddress,initSessionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                webAddress : JSON.stringify(webAddress),
                initSessionId : JSON.stringify(initSessionId),
            },
            method: 'initializeStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'initializeStoreByStoreId' : function(storeId,initSessionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                storeId : JSON.stringify(storeId),
                initSessionId : JSON.stringify(initSessionId),
            },
            method: 'initializeStoreByStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'initializeStoreWithModuleId' : function(webAddress,initSessionId,moduleId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                webAddress : JSON.stringify(webAddress),
                initSessionId : JSON.stringify(initSessionId),
                moduleId : JSON.stringify(moduleId),
            },
            method: 'initializeStoreWithModuleId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isAddressTaken' : function(address, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                address : JSON.stringify(address),
            },
            method: 'isAddressTaken',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isPikStore' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isPikStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isProductMode' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isProductMode',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'receiveSyncData' : function(json, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                json : JSON.stringify(json),
            },
            method: 'receiveSyncData',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeDomainName' : function(domainName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'removeDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeKey' : function(key, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
            },
            method: 'removeKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveKey' : function(key,value,secure, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                value : JSON.stringify(value),
                secure : JSON.stringify(secure),
            },
            method: 'saveKey',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveStore' : function(config, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                config : JSON.stringify(config),
            },
            method: 'saveStore',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'seenCriticalMessage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'seenCriticalMessage',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setDefaultMultilevelName' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                multilevelname : JSON.stringify(multilevelname),
            },
            method: 'setDefaultMultilevelName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setImageIdToFavicon' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setImageIdToFavicon',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setIntroductionRead' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'setIntroductionRead',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setIsTemplate' : function(storeId,isTemplate, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                storeId : JSON.stringify(storeId),
                isTemplate : JSON.stringify(isTemplate),
            },
            method: 'setIsTemplate',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setMasterStoreId' : function(masterStoreId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                masterStoreId : JSON.stringify(masterStoreId),
            },
            method: 'setMasterStoreId',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setPrimaryDomainName' : function(domainName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                domainName : JSON.stringify(domainName),
            },
            method: 'setPrimaryDomainName',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSessionLanguage' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'setSessionLanguage',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setStoreIdentifier' : function(identifier, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                identifier : JSON.stringify(identifier),
            },
            method: 'setStoreIdentifier',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'syncData' : function(environment,username,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                environment : JSON.stringify(environment),
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'syncData',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleIgnoreBookingErrors' : function(password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'toggleIgnoreBookingErrors',
            interfaceName: 'core.storemanager.IStoreManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.StripeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.StripeManager.prototype = {
    'chargeOrder' : function(orderId,cardId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                cardId : JSON.stringify(cardId),
            },
            method: 'chargeOrder',
            interfaceName: 'core.stripe.IStripeManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createAndChargeCustomer' : function(orderId,token, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                token : JSON.stringify(token),
            },
            method: 'createAndChargeCustomer',
            interfaceName: 'core.stripe.IStripeManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.SupportManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.SupportManager.prototype = {
    'addToSupportCase' : function(supportCaseId,history, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                supportCaseId : JSON.stringify(supportCaseId),
                history : JSON.stringify(history),
            },
            method: 'addToSupportCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignCareTakerForCase' : function(caseId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                caseId : JSON.stringify(caseId),
                userId : JSON.stringify(userId),
            },
            method: 'assignCareTakerForCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeModuleForCase' : function(caseId,moduleId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                caseId : JSON.stringify(caseId),
                moduleId : JSON.stringify(moduleId),
            },
            method: 'changeModuleForCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeStateForCase' : function(caseId,stateId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                caseId : JSON.stringify(caseId),
                stateId : JSON.stringify(stateId),
            },
            method: 'changeStateForCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeSupportCaseType' : function(caseId,type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                caseId : JSON.stringify(caseId),
                type : JSON.stringify(type),
            },
            method: 'changeSupportCaseType',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeTitleOnCase' : function(caseId,title, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                caseId : JSON.stringify(caseId),
                title : JSON.stringify(title),
            },
            method: 'changeTitleOnCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createSupportCase' : function(supportCase, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                supportCase : JSON.stringify(supportCase),
            },
            method: 'createSupportCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFeatureListEntry' : function(entryId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
            },
            method: 'getFeatureListEntry',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFeatureThree' : function(moduleId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                moduleId : JSON.stringify(moduleId),
            },
            method: 'getFeatureThree',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getServerStatusList' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getServerStatusList',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSupportCase' : function(supportCaseId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                supportCaseId : JSON.stringify(supportCaseId),
            },
            method: 'getSupportCase',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSupportCases' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getSupportCases',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSupportStatistics' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getSupportStatistics',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'helloWorld' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'helloWorld',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveFeatureThree' : function(moduleId,list, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                moduleId : JSON.stringify(moduleId),
                list : JSON.stringify(list),
            },
            method: 'saveFeatureThree',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateFeatureListEntry' : function(entryId,text,title,language, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                entryId : JSON.stringify(entryId),
                text : JSON.stringify(text),
                title : JSON.stringify(title),
                language : JSON.stringify(language),
            },
            method: 'updateFeatureListEntry',
            interfaceName: 'core.support.ISupportManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.TicketManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.TicketManager.prototype = {
    'deleteTicket' : function(ticketId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ticketId : JSON.stringify(ticketId),
            },
            method: 'deleteTicket',
            interfaceName: 'core.ticket.ITicketManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllTickets' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllTickets',
            interfaceName: 'core.ticket.ITicketManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTicket' : function(ticketId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ticketId : JSON.stringify(ticketId),
            },
            method: 'getTicket',
            interfaceName: 'core.ticket.ITicketManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveTicket' : function(ticket, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ticket : JSON.stringify(ticket),
            },
            method: 'saveTicket',
            interfaceName: 'core.ticket.ITicketManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateEvent' : function(ticketId,event, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                ticketId : JSON.stringify(ticketId),
                event : JSON.stringify(event),
            },
            method: 'updateEvent',
            interfaceName: 'core.ticket.ITicketManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.TimeRegisteringManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.TimeRegisteringManager.prototype = {
    'deleteTimeUnsecure' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteTimeUnsecure',
            interfaceName: 'core.timeregisteringmanager.ITimeRegisteringManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllTimesRegistered' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllTimesRegistered',
            interfaceName: 'core.timeregisteringmanager.ITimeRegisteringManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyHours' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyHours',
            interfaceName: 'core.timeregisteringmanager.ITimeRegisteringManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRegisteredHoursForUser' : function(userId,start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getRegisteredHoursForUser',
            interfaceName: 'core.timeregisteringmanager.ITimeRegisteringManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerTime' : function(start,end,comment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
                comment : JSON.stringify(comment),
            },
            method: 'registerTime',
            interfaceName: 'core.timeregisteringmanager.ITimeRegisteringManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.TrackAndTraceManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.TrackAndTraceManager.prototype = {
    'acceptTodaysInstruction' : function(routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'acceptTodaysInstruction',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'acknowledgeDriverMessage' : function(msgId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                msgId : JSON.stringify(msgId),
            },
            method: 'acknowledgeDriverMessage',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addDeliveryTaskToDestionation' : function(destionatId,task, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destionatId : JSON.stringify(destionatId),
                task : JSON.stringify(task),
            },
            method: 'addDeliveryTaskToDestionation',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addDriverToRoute' : function(userId,routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                routeId : JSON.stringify(routeId),
            },
            method: 'addDriverToRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addPickupOrder' : function(destnationId,order,inTask, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destnationId : JSON.stringify(destnationId),
                order : JSON.stringify(order),
                inTask : JSON.stringify(inTask),
            },
            method: 'addPickupOrder',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeCountedDriverCopies' : function(taskId,orderReference,quantity, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                orderReference : JSON.stringify(orderReference),
                quantity : JSON.stringify(quantity),
            },
            method: 'changeCountedDriverCopies',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeQuantity' : function(taskId,orderReference,parcels,containers, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                orderReference : JSON.stringify(orderReference),
                parcels : JSON.stringify(parcels),
                containers : JSON.stringify(containers),
            },
            method: 'changeQuantity',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkRemovalOfRoutes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkRemovalOfRoutes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteReplyMessage' : function(replyMessageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                replyMessageId : JSON.stringify(replyMessageId),
            },
            method: 'deleteReplyMessage',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteRoute' : function(routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'deleteRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllExportedDataForRoute' : function(routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'getAllExportedDataForRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllRoutes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllRoutes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDestinationById' : function(destinationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
            },
            method: 'getDestinationById',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDriverMessage' : function(msgId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                msgId : JSON.stringify(msgId),
            },
            method: 'getDriverMessage',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getDriverMessages' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getDriverMessages',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getExceptions' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getExceptions',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getExport' : function(routeId,currentState, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                currentState : JSON.stringify(currentState),
            },
            method: 'getExport',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getExportedData' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getExportedData',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLoadStatus' : function(statusId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                statusId : JSON.stringify(statusId),
            },
            method: 'getLoadStatus',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLoadStatuses' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLoadStatuses',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getMyRoutes' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getMyRoutes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPooledDestiontions' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPooledDestiontions',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPooledDestiontionsByUsersDepotId' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPooledDestiontionsByUsersDepotId',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getReplyMessages' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getReplyMessages',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRouteIdsThatHasNotCompleted' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getRouteIdsThatHasNotCompleted',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoutesById' : function(routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
            },
            method: 'getRoutesById',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoutesCompletedPast24Hours' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getRoutesCompletedPast24Hours',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'loadData' : function(base64,fileName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                base64 : JSON.stringify(base64),
                fileName : JSON.stringify(fileName),
            },
            method: 'loadData',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'loadDataBase64' : function(base64,fileName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                base64 : JSON.stringify(base64),
                fileName : JSON.stringify(fileName),
            },
            method: 'loadDataBase64',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsArrived' : function(destinationId,startedTimeStamp,lon,lat, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
                startedTimeStamp : JSON.stringify(startedTimeStamp),
                lon : JSON.stringify(lon),
                lat : JSON.stringify(lat),
            },
            method: 'markAsArrived',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsCompleted' : function(routeId,lat,lon, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                lat : JSON.stringify(lat),
                lon : JSON.stringify(lon),
            },
            method: 'markAsCompleted',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsCompletedWithTimeStamp' : function(routeId,lat,lon,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                lat : JSON.stringify(lat),
                lon : JSON.stringify(lon),
                date : JSON.stringify(date),
            },
            method: 'markAsCompletedWithTimeStamp',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsCompletedWithTimeStampAndPassword' : function(routeId,lat,lon,date,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                lat : JSON.stringify(lat),
                lon : JSON.stringify(lon),
                date : JSON.stringify(date),
                password : JSON.stringify(password),
            },
            method: 'markAsCompletedWithTimeStampAndPassword',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markAsDeliverd' : function(taskId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
            },
            method: 'markAsDeliverd',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markDeparting' : function(destinationId,latitude,longitude,timeStamp,signatureImage,typedSignature, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
                latitude : JSON.stringify(latitude),
                longitude : JSON.stringify(longitude),
                timeStamp : JSON.stringify(timeStamp),
                signatureImage : JSON.stringify(signatureImage),
                typedSignature : JSON.stringify(typedSignature),
            },
            method: 'markDeparting',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markInstructionAsRead' : function(destinationId,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
                date : JSON.stringify(date),
            },
            method: 'markInstructionAsRead',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markOrderWithException' : function(taskId,orderReferenceNumber,exceptionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                orderReferenceNumber : JSON.stringify(orderReferenceNumber),
                exceptionId : JSON.stringify(exceptionId),
            },
            method: 'markOrderWithException',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markRouteAsStarted' : function(routeId,startedTimeStamp,lon,lat, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                startedTimeStamp : JSON.stringify(startedTimeStamp),
                lon : JSON.stringify(lon),
                lat : JSON.stringify(lat),
            },
            method: 'markRouteAsStarted',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markRouteAsStartedWithCheck' : function(routeId,startedTimeStamp,lon,lat, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                startedTimeStamp : JSON.stringify(startedTimeStamp),
                lon : JSON.stringify(lon),
                lat : JSON.stringify(lat),
            },
            method: 'markRouteAsStartedWithCheck',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markTaskWithExceptionDeliverd' : function(taskId,exceptionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                exceptionId : JSON.stringify(exceptionId),
            },
            method: 'markTaskWithExceptionDeliverd',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'moveDesitinationToPool' : function(routeId,destinationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                destinationId : JSON.stringify(destinationId),
            },
            method: 'moveDesitinationToPool',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'moveDestinationFromPoolToRoute' : function(destId,routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destId : JSON.stringify(destId),
                routeId : JSON.stringify(routeId),
            },
            method: 'moveDestinationFromPoolToRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'registerCollectionData' : function(destinationId,collectionTasks, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
                collectionTasks : JSON.stringify(collectionTasks),
            },
            method: 'registerCollectionData',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeDriverToRoute' : function(userId,routeId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                routeId : JSON.stringify(routeId),
            },
            method: 'removeDriverToRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'replyGeneral' : function(routeId,text,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                text : JSON.stringify(text),
                date : JSON.stringify(date),
            },
            method: 'replyGeneral',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'replyMessage' : function(messageId,text,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                messageId : JSON.stringify(messageId),
                text : JSON.stringify(text),
                date : JSON.stringify(date),
            },
            method: 'replyMessage',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'replyMessageForDestionation' : function(destinationId,text,date, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
                text : JSON.stringify(text),
                date : JSON.stringify(date),
            },
            method: 'replyMessageForDestionation',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveDestination' : function(destination, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destination : JSON.stringify(destination),
            },
            method: 'saveDestination',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveException' : function(exception, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                exception : JSON.stringify(exception),
            },
            method: 'saveException',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveRoute' : function(route, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                route : JSON.stringify(route),
            },
            method: 'saveRoute',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendMessageToDriver' : function(driverId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                driverId : JSON.stringify(driverId),
                message : JSON.stringify(message),
            },
            method: 'sendMessageToDriver',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setCagesOrPalletCount' : function(taskId,quantity, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                quantity : JSON.stringify(quantity),
            },
            method: 'setCagesOrPalletCount',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setDesitionationException' : function(destinationId,exceptionId,lon,lat, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setInstructionOnDestination' : function(routeId,destinationId,message, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                routeId : JSON.stringify(routeId),
                destinationId : JSON.stringify(destinationId),
                message : JSON.stringify(message),
            },
            method: 'setInstructionOnDestination',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setScannedBarcodes' : function(taskId,orderReference,barcodes,barcodeEnteredManually, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                taskId : JSON.stringify(taskId),
                orderReference : JSON.stringify(orderReference),
                barcodes : JSON.stringify(barcodes),
                barcodeEnteredManually : JSON.stringify(barcodeEnteredManually),
            },
            method: 'setScannedBarcodes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSequence' : function(exceptionId,sequence, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                exceptionId : JSON.stringify(exceptionId),
                sequence : JSON.stringify(sequence),
            },
            method: 'setSequence',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSortingOfRoutes' : function(sortingName, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                sortingName : JSON.stringify(sortingName),
            },
            method: 'setSortingOfRoutes',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'unsetSkippedReason' : function(destinationId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                destinationId : JSON.stringify(destinationId),
            },
            method: 'unsetSkippedReason',
            interfaceName: 'core.trackandtrace.ITrackAndTraceManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.TrackerManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.TrackerManager.prototype = {
    'getActivities' : function(start,end, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getActivities',
            interfaceName: 'core.trackermanager.ITrackerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logTracking' : function(applicationName,type,value,textDescription, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                applicationName : JSON.stringify(applicationName),
                type : JSON.stringify(type),
                value : JSON.stringify(value),
                textDescription : JSON.stringify(textDescription),
            },
            method: 'logTracking',
            interfaceName: 'core.trackermanager.ITrackerManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.UserManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UserManager.prototype = {
    'addCardToUser' : function(userId,card, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                card : JSON.stringify(card),
            },
            method: 'addCardToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addComment' : function(userId,comment, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                comment : JSON.stringify(comment),
            },
            method: 'addComment',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addGroupToUser' : function(userId,groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'addGroupToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addMetaData' : function(userId,key,value, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                key : JSON.stringify(key),
                value : JSON.stringify(value),
            },
            method: 'addMetaData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addUserPrivilege' : function(userId,managerName,managerFunction, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                managerName : JSON.stringify(managerName),
                managerFunction : JSON.stringify(managerFunction),
            },
            method: 'addUserPrivilege',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignCompanyToGroup' : function(company,groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                company : JSON.stringify(company),
                groupId : JSON.stringify(groupId),
            },
            method: 'assignCompanyToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignCompanyToUser' : function(company,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                company : JSON.stringify(company),
                userId : JSON.stringify(userId),
            },
            method: 'assignCompanyToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignMetaDataToVirtualSessionUser' : function(key,value, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                key : JSON.stringify(key),
                value : JSON.stringify(value),
            },
            method: 'assignMetaDataToVirtualSessionUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'assignReferenceToCompany' : function(companyId,companyReference, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                companyReference : JSON.stringify(companyReference),
            },
            method: 'assignReferenceToCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'canCreateUser' : function(user, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'canCreateUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'cancelImpersonating' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'cancelImpersonating',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'changeUserByUsingPinCode' : function(userId,pinCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                pinCode : JSON.stringify(pinCode),
            },
            method: 'changeUserByUsingPinCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkIfFieldOnUserIsOkey' : function(field,value, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                field : JSON.stringify(field),
                value : JSON.stringify(value),
            },
            method: 'checkIfFieldOnUserIsOkey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkUserNameAndPassword' : function(username,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'checkUserNameAndPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearTokenList' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearTokenList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearUserManagerForAllData' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearUserManagerForAllData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'confirmCompanyOwner' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'confirmCompanyOwner',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'connectCompanyToUser' : function(userId,taxNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                taxNumber : JSON.stringify(taxNumber),
            },
            method: 'connectCompanyToUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createCompany' : function(vatNumber,name, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                vatNumber : JSON.stringify(vatNumber),
                name : JSON.stringify(name),
            },
            method: 'createCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createGoogleTotpForUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'createGoogleTotpForUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createTokenAccess' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'createTokenAccess',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createUser' : function(user, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'createUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'createUserAndCompany' : function(company, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                company : JSON.stringify(company),
            },
            method: 'createUserAndCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteCompany' : function(companyId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'deleteCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteExtraAddressToGroup' : function(groupId,addressId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
                addressId : JSON.stringify(addressId),
            },
            method: 'deleteExtraAddressToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'deleteUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteUserRole' : function(roleId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                roleId : JSON.stringify(roleId),
            },
            method: 'deleteUserRole',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'doEmailExists' : function(email, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'doEmailExists',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'doesUserExistsOnReferenceNumber' : function(number, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                number : JSON.stringify(number),
            },
            method: 'doesUserExistsOnReferenceNumber',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'findUsers' : function(searchCriteria, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'findUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'forceCompanyOwner' : function(userId,isCompanyOwner, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                isCompanyOwner : JSON.stringify(isCompanyOwner),
            },
            method: 'forceCompanyOwner',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAdministratorCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAdministratorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllCompanies' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllCompanies',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllCompaniesForGroup' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getAllCompaniesForGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllCompanyFiltered' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllCompanyFiltered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllGroups' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllGroups',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllGroupsFiletered' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllGroupsFiletered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsers' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsersFiltered' : function(filter, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                filter : JSON.stringify(filter),
            },
            method: 'getAllUsersFiltered',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsersSimple' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllUsersSimple',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllUsersWithCommentToApp' : function(appId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                appId : JSON.stringify(appId),
            },
            method: 'getAllUsersWithCommentToApp',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCard' : function(cardId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                cardId : JSON.stringify(cardId),
            },
            method: 'getCard',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompaniesConnectedToGroupCount' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getCompaniesConnectedToGroupCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompany' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompanyByReference' : function(companyReference, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyReference : JSON.stringify(companyReference),
            },
            method: 'getCompanyByReference',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCustomersCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCustomersCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getEditorCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getEditorCount',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getGroup' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLoggedOnUser' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLoggedOnUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogins' : function(year, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                year : JSON.stringify(year),
            },
            method: 'getLogins',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getPingoutTime' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getPingoutTime',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getSubUsers' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getSubUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTokenList' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTokenList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUnconfirmedCompanyOwners' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUnconfirmedCompanyOwners',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserById' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getUserById',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserList' : function(userIds, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userIds : JSON.stringify(userIds),
            },
            method: 'getUserList',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserRoles' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUserRoles',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUserWithPermissionCheck' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'getUserWithPermissionCheck',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsersBasedOnGroupId' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'getUsersBasedOnGroupId',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsersByCompanyId' : function(companyId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'getUsersByCompanyId',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsersByType' : function(type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'getUsersByType',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getUsersThatHasPinCode' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getUsersThatHasPinCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'impersonateUser' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'impersonateUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isCaptain' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'isCaptain',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isImpersonating' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isImpersonating',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isLoggedIn' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isLoggedIn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logLogout' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'logLogout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logOn' : function(username,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'logOn',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'loginWithPincode' : function(username,password,pinCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
                pinCode : JSON.stringify(pinCode),
            },
            method: 'loginWithPincode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logonUsingKey' : function(logonKey, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                logonKey : JSON.stringify(logonKey),
            },
            method: 'logonUsingKey',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logonUsingRefNumber' : function(refCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                refCode : JSON.stringify(refCode),
            },
            method: 'logonUsingRefNumber',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logonUsingToken' : function(token, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                token : JSON.stringify(token),
            },
            method: 'logonUsingToken',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logonUsingTotp' : function(username,password,oneTimeCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
                oneTimeCode : JSON.stringify(oneTimeCode),
            },
            method: 'logonUsingTotp',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logonUsingTotpAgainstCrm' : function(username,password,oneTimeCode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
                oneTimeCode : JSON.stringify(oneTimeCode),
            },
            method: 'logonUsingTotpAgainstCrm',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'logout' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'logout',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'mergeUsers' : function(userIds,properties, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userIds : JSON.stringify(userIds),
                properties : JSON.stringify(properties),
            },
            method: 'mergeUsers',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeComment' : function(userId,commentId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                commentId : JSON.stringify(commentId),
            },
            method: 'removeComment',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeGroup' : function(groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeGroupFromUser' : function(userId,groupId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                groupId : JSON.stringify(groupId),
            },
            method: 'removeGroupFromUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeMetaData' : function(userId,key, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                key : JSON.stringify(key),
            },
            method: 'removeMetaData',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'removeUserFromCompany' : function(companyId,userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
                userId : JSON.stringify(userId),
            },
            method: 'removeUserFromCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'requestAdminRight' : function(managerName,managerFunction,applicationInstanceId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                managerName : JSON.stringify(managerName),
                managerFunction : JSON.stringify(managerFunction),
                applicationInstanceId : JSON.stringify(applicationInstanceId),
            },
            method: 'requestAdminRight',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'requestNewPincode' : function(username,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                username : JSON.stringify(username),
                password : JSON.stringify(password),
            },
            method: 'requestNewPincode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'resetPassword' : function(resetCode,username,newPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                resetCode : JSON.stringify(resetCode),
                username : JSON.stringify(username),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'resetPassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveCompany' : function(company, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                company : JSON.stringify(company),
            },
            method: 'saveCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveExtraAddressToGroup' : function(group,address, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                group : JSON.stringify(group),
                address : JSON.stringify(address),
            },
            method: 'saveExtraAddressToGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveGroup' : function(group, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                group : JSON.stringify(group),
            },
            method: 'saveGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveUser' : function(user, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                user : JSON.stringify(user),
            },
            method: 'saveUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveUserRole' : function(role, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                role : JSON.stringify(role),
            },
            method: 'saveUserRole',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'searchForCompanies' : function(searchWord, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchWord : JSON.stringify(searchWord),
            },
            method: 'searchForCompanies',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'searchForGroup' : function(searchCriteria, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchCriteria : JSON.stringify(searchCriteria),
            },
            method: 'searchForGroup',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendResetCode' : function(title,text,username, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                title : JSON.stringify(title),
                text : JSON.stringify(text),
                username : JSON.stringify(username),
            },
            method: 'sendResetCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setPasswordDirect' : function(userId,encryptedPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                encryptedPassword : JSON.stringify(encryptedPassword),
            },
            method: 'setPasswordDirect',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setSessionCompany' : function(companyId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyId : JSON.stringify(companyId),
            },
            method: 'setSessionCompany',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleMainContact' : function(userId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
            },
            method: 'toggleMainContact',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'toggleModuleForUser' : function(moduleId,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                moduleId : JSON.stringify(moduleId),
                password : JSON.stringify(password),
            },
            method: 'toggleModuleForUser',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'undoSuspension' : function(userId,suspensionId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                suspensionId : JSON.stringify(suspensionId),
            },
            method: 'undoSuspension',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePassword' : function(userId,oldPassword,newPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                oldPassword : JSON.stringify(oldPassword),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'updatePassword',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePasswordByResetCode' : function(resetCode,newPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                resetCode : JSON.stringify(resetCode),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'updatePasswordByResetCode',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePasswordSecure' : function(userId,newPassword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                userId : JSON.stringify(userId),
                newPassword : JSON.stringify(newPassword),
            },
            method: 'updatePasswordSecure',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateUserCounter' : function(counter,password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                counter : JSON.stringify(counter),
                password : JSON.stringify(password),
            },
            method: 'updateUserCounter',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'upgradeUserToGetShopAdmin' : function(password, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                password : JSON.stringify(password),
            },
            method: 'upgradeUserToGetShopAdmin',
            interfaceName: 'core.usermanager.IUserManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.ImageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.ImageManager.prototype = {
    'getBase64EncodedImageLocally' : function(imageId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                imageId : JSON.stringify(imageId),
            },
            method: 'getBase64EncodedImageLocally',
            interfaceName: 'core.utils.IImageManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.UtilManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UtilManager.prototype = {
    'getBase64EncodedPDFWebPage' : function(urlToPage, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                urlToPage : JSON.stringify(urlToPage),
            },
            method: 'getBase64EncodedPDFWebPage',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompaniesFromBrReg' : function(search, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                search : JSON.stringify(search),
            },
            method: 'getCompaniesFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompanyFree' : function(companyVatNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFree',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCompanyFromBrReg' : function(companyVatNumber, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                companyVatNumber : JSON.stringify(companyVatNumber),
            },
            method: 'getCompanyFromBrReg',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getFile' : function(id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'getFile',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getStartupCount' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getStartupCount',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'isInProductionMode' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'isInProductionMode',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveFile' : function(file, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                file : JSON.stringify(file),
            },
            method: 'saveFile',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'sendPriceOffer' : function(link,email, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                link : JSON.stringify(link),
                email : JSON.stringify(email),
            },
            method: 'sendPriceOffer',
            interfaceName: 'core.utils.IUtilManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.UUIDSecurityManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.UUIDSecurityManager.prototype = {
    'grantAccess' : function(userId,uuid,read,write, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'hasAccess' : function(uuid,read,write, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                uuid : JSON.stringify(uuid),
                read : JSON.stringify(read),
                write : JSON.stringify(write),
            },
            method: 'hasAccess',
            interfaceName: 'core.uuidsecuritymanager.IUUIDSecurityManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.VerifoneManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.VerifoneManager.prototype = {
    'cancelPaymentProcess' : function(terminalId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                terminalId : JSON.stringify(terminalId),
            },
            method: 'cancelPaymentProcess',
            interfaceName: 'core.verifonemanager.IVerifoneManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'chargeOrder' : function(orderId,terminalId,overrideDevMode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                terminalId : JSON.stringify(terminalId),
                overrideDevMode : JSON.stringify(overrideDevMode),
            },
            method: 'chargeOrder',
            interfaceName: 'core.verifonemanager.IVerifoneManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'clearMessages' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'clearMessages',
            interfaceName: 'core.verifonemanager.IVerifoneManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getTerminalMessages' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getTerminalMessages',
            interfaceName: 'core.verifonemanager.IVerifoneManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.VippsManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.VippsManager.prototype = {
    'cancelOrder' : function(orderId,ip, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                ip : JSON.stringify(ip),
            },
            method: 'cancelOrder',
            interfaceName: 'core.vippsmanager.IVippsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForOrdersToCapture' : function(gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForOrdersToCapture',
            interfaceName: 'core.vippsmanager.IVippsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkIfOrderHasBeenCompleted' : function(incOrderId, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                incOrderId : JSON.stringify(incOrderId),
            },
            method: 'checkIfOrderHasBeenCompleted',
            interfaceName: 'core.vippsmanager.IVippsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'startMobileRequest' : function(phoneNumber,orderId,ip, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                phoneNumber : JSON.stringify(phoneNumber),
                orderId : JSON.stringify(orderId),
                ip : JSON.stringify(ip),
            },
            method: 'startMobileRequest',
            interfaceName: 'core.vippsmanager.IVippsManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.WebManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.WebManager.prototype = {
    'htmlGet' : function(url, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                url : JSON.stringify(url),
            },
            method: 'htmlGet',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'htmlGetJson' : function(url, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                url : JSON.stringify(url),
            },
            method: 'htmlGetJson',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'htmlPost' : function(url,data,jsonPost,encoding, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'htmlPostBasicAuth' : function(url,data,jsonPost,encoding,auth, gs_silent, gs_dont_persist) {
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
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'htmlPostJson' : function(url,data,encoding, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                url : JSON.stringify(url),
                data : JSON.stringify(data),
                encoding : JSON.stringify(encoding),
            },
            method: 'htmlPostJson',
            interfaceName: 'core.webmanager.IWebManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.WubookManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.WubookManager.prototype = {
    'activateWubookCallback' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'activateWubookCallback',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addBooking' : function(multilevelname, rcode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'addBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addNewBookingsPastDays' : function(multilevelname, daysback, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                daysback : JSON.stringify(daysback),
            },
            method: 'addNewBookingsPastDays',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'addRestriction' : function(multilevelname, restriction, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                restriction : JSON.stringify(restriction),
            },
            method: 'addRestriction',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'checkForNoShowsAndMark' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'checkForNoShowsAndMark',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteAllRooms' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'deleteAllRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteBooking' : function(multilevelname, rcode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'deleteBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'deleteRestriction' : function(multilevelname, id, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                id : JSON.stringify(id),
            },
            method: 'deleteRestriction',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'doubleCheckDeletedBookings' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'doubleCheckDeletedBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchAllBookings' : function(multilevelname, daysback, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                daysback : JSON.stringify(daysback),
            },
            method: 'fetchAllBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchBooking' : function(multilevelname, rcode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'fetchBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchBookingCodes' : function(multilevelname, daysback, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                daysback : JSON.stringify(daysback),
            },
            method: 'fetchBookingCodes',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchBookingFromCallback' : function(multilevelname, rcode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'fetchBookingFromCallback',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchBookings' : function(multilevelname, daysBack,registrations, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                daysBack : JSON.stringify(daysBack),
                registrations : JSON.stringify(registrations),
            },
            method: 'fetchBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'fetchNewBookings' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'fetchNewBookings',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getAllRestriction' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getAllRestriction',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getCallbackUrl' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getCallbackUrl',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getLogEntries' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getLogEntries',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getOtas' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getOtas',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getRoomRates' : function(multilevelname, channelId,channelType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                channelId : JSON.stringify(channelId),
                channelType : JSON.stringify(channelType),
            },
            method: 'getRoomRates',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'getWubookRoomData' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'getWubookRoomData',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'insertAllRooms' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'insertAllRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markCCInvalid' : function(multilevelname, rcode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'markCCInvalid',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'markNoShow' : function(multilevelname, rcode, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                rcode : JSON.stringify(rcode),
            },
            method: 'markNoShow',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'newOta' : function(multilevelname, type, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                type : JSON.stringify(type),
            },
            method: 'newOta',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'saveWubookRoomData' : function(multilevelname, res, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                res : JSON.stringify(res),
            },
            method: 'saveWubookRoomData',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'setRoomRates' : function(multilevelname, channelId,rates,channelType, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                channelId : JSON.stringify(channelId),
                rates : JSON.stringify(rates),
                channelType : JSON.stringify(channelType),
            },
            method: 'setRoomRates',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'testConnection' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'testConnection',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateAvailabilityFromButton' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'updateAvailabilityFromButton',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateMinStay' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'updateMinStay',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updatePrices' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'updatePrices',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

    'updateShortAvailability' : function(multilevelname, gs_silent, gs_dont_persist) {
        var data = {
            args : {
            },
            method: 'updateShortAvailability',
            multiLevelName: multilevelname,
            interfaceName: 'core.wubook.IWubookManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },

}
GetShopApiWebSocket.YouTubeManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocket.YouTubeManager.prototype = {
    'searchYoutube' : function(searchword, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                searchword : JSON.stringify(searchword),
            },
            method: 'searchYoutube',
            interfaceName: 'core.youtubemanager.IYouTubeManager',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
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
    this.ApacManager = new GetShopApiWebSocket.ApacManager(this);
    this.GetShopApplicationPool = new GetShopApiWebSocket.GetShopApplicationPool(this);
    this.StoreApplicationInstancePool = new GetShopApiWebSocket.StoreApplicationInstancePool(this);
    this.StoreApplicationPool = new GetShopApiWebSocket.StoreApplicationPool(this);
    this.DoorManager = new GetShopApiWebSocket.DoorManager(this);
    this.BackupManager = new GetShopApiWebSocket.BackupManager(this);
    this.BamboraManager = new GetShopApiWebSocket.BamboraManager(this);
    this.BigStock = new GetShopApiWebSocket.BigStock(this);
    this.BookingEngine = new GetShopApiWebSocket.BookingEngine(this);
    this.BrainTreeManager = new GetShopApiWebSocket.BrainTreeManager(this);
    this.C3Manager = new GetShopApiWebSocket.C3Manager(this);
    this.CalendarManager = new GetShopApiWebSocket.CalendarManager(this);
    this.CartManager = new GetShopApiWebSocket.CartManager(this);
    this.CarTuningManager = new GetShopApiWebSocket.CarTuningManager(this);
    this.CertegoManager = new GetShopApiWebSocket.CertegoManager(this);
    this.ChecklistManager = new GetShopApiWebSocket.ChecklistManager(this);
    this.DBBackupManager = new GetShopApiWebSocket.DBBackupManager(this);
    this.DibsManager = new GetShopApiWebSocket.DibsManager(this);
    this.EpayManager = new GetShopApiWebSocket.EpayManager(this);
    this.EventBookingManager = new GetShopApiWebSocket.EventBookingManager(this);
    this.ExcelManager = new GetShopApiWebSocket.ExcelManager(this);
    this.FileManager = new GetShopApiWebSocket.FileManager(this);
    this.FtpManager = new GetShopApiWebSocket.FtpManager(this);
    this.GalleryManager = new GetShopApiWebSocket.GalleryManager(this);
    this.GetShop = new GetShopApiWebSocket.GetShop(this);
    this.GetShopAccountingManager = new GetShopApiWebSocket.GetShopAccountingManager(this);
    this.GetShopLockManager = new GetShopApiWebSocket.GetShopLockManager(this);
    this.GetShopLockSystemManager = new GetShopApiWebSocket.GetShopLockSystemManager(this);
    this.GiftCardManager = new GetShopApiWebSocket.GiftCardManager(this);
    this.GdsManager = new GetShopApiWebSocket.GdsManager(this);
    this.InformationScreenManager = new GetShopApiWebSocket.InformationScreenManager(this);
    this.ListManager = new GetShopApiWebSocket.ListManager(this);
    this.MecaManager = new GetShopApiWebSocket.MecaManager(this);
    this.MekonomenManager = new GetShopApiWebSocket.MekonomenManager(this);
    this.MessageManager = new GetShopApiWebSocket.MessageManager(this);
    this.NewsLetterManager = new GetShopApiWebSocket.NewsLetterManager(this);
    this.MobileManager = new GetShopApiWebSocket.MobileManager(this);
    this.OAuthManager = new GetShopApiWebSocket.OAuthManager(this);
    this.OcrManager = new GetShopApiWebSocket.OcrManager(this);
    this.StoreOcrManager = new GetShopApiWebSocket.StoreOcrManager(this);
    this.EhfXmlGenerator = new GetShopApiWebSocket.EhfXmlGenerator(this);
    this.OrderManager = new GetShopApiWebSocket.OrderManager(this);
    this.PageManager = new GetShopApiWebSocket.PageManager(this);
    this.PaymentManager = new GetShopApiWebSocket.PaymentManager(this);
    this.PaymentTerminalManager = new GetShopApiWebSocket.PaymentTerminalManager(this);
    this.InvoiceManager = new GetShopApiWebSocket.InvoiceManager(this);
    this.LasGruppenPDFGenerator = new GetShopApiWebSocket.LasGruppenPDFGenerator(this);
    this.PgaManager = new GetShopApiWebSocket.PgaManager(this);
    this.PkkControlManager = new GetShopApiWebSocket.PkkControlManager(this);
    this.PmsBookingProcess = new GetShopApiWebSocket.PmsBookingProcess(this);
    this.PmsEventManager = new GetShopApiWebSocket.PmsEventManager(this);
    this.CareTakerManager = new GetShopApiWebSocket.CareTakerManager(this);
    this.PmsCoverageAndIncomeReportManager = new GetShopApiWebSocket.PmsCoverageAndIncomeReportManager(this);
    this.PmsGetShopOverView = new GetShopApiWebSocket.PmsGetShopOverView(this);
    this.PmsInvoiceManager = new GetShopApiWebSocket.PmsInvoiceManager(this);
    this.PmsManager = new GetShopApiWebSocket.PmsManager(this);
    this.PmsManagerProcessor = new GetShopApiWebSocket.PmsManagerProcessor(this);
    this.PmsNotificationManager = new GetShopApiWebSocket.PmsNotificationManager(this);
    this.PmsPaymentTerminal = new GetShopApiWebSocket.PmsPaymentTerminal(this);
    this.PmsReportManager = new GetShopApiWebSocket.PmsReportManager(this);
    this.PmsSelfManagement = new GetShopApiWebSocket.PmsSelfManagement(this);
    this.PmsWebBookingManager = new GetShopApiWebSocket.PmsWebBookingManager(this);
    this.SmsHistoryManager = new GetShopApiWebSocket.SmsHistoryManager(this);
    this.PosManager = new GetShopApiWebSocket.PosManager(this);
    this.PrintManager = new GetShopApiWebSocket.PrintManager(this);
    this.StorePrintManager = new GetShopApiWebSocket.StorePrintManager(this);
    this.ProductManager = new GetShopApiWebSocket.ProductManager(this);
    this.PullServerManager = new GetShopApiWebSocket.PullServerManager(this);
    this.QuestBackManager = new GetShopApiWebSocket.QuestBackManager(this);
    this.BookingComRateManagerManager = new GetShopApiWebSocket.BookingComRateManagerManager(this);
    this.ReportingManager = new GetShopApiWebSocket.ReportingManager(this);
    this.ResturantManager = new GetShopApiWebSocket.ResturantManager(this);
    this.SalesManager = new GetShopApiWebSocket.SalesManager(this);
    this.ScormManager = new GetShopApiWebSocket.ScormManager(this);
    this.SearchManager = new GetShopApiWebSocket.SearchManager(this);
    this.SedoxProductManager = new GetShopApiWebSocket.SedoxProductManager(this);
    this.SendRegningManager = new GetShopApiWebSocket.SendRegningManager(this);
    this.SimpleEventManager = new GetShopApiWebSocket.SimpleEventManager(this);
    this.StoreManager = new GetShopApiWebSocket.StoreManager(this);
    this.StripeManager = new GetShopApiWebSocket.StripeManager(this);
    this.SupportManager = new GetShopApiWebSocket.SupportManager(this);
    this.TicketManager = new GetShopApiWebSocket.TicketManager(this);
    this.TimeRegisteringManager = new GetShopApiWebSocket.TimeRegisteringManager(this);
    this.TrackAndTraceManager = new GetShopApiWebSocket.TrackAndTraceManager(this);
    this.TrackerManager = new GetShopApiWebSocket.TrackerManager(this);
    this.UserManager = new GetShopApiWebSocket.UserManager(this);
    this.ImageManager = new GetShopApiWebSocket.ImageManager(this);
    this.UtilManager = new GetShopApiWebSocket.UtilManager(this);
    this.UUIDSecurityManager = new GetShopApiWebSocket.UUIDSecurityManager(this);
    this.VerifoneManager = new GetShopApiWebSocket.VerifoneManager(this);
    this.VippsManager = new GetShopApiWebSocket.VippsManager(this);
    this.WebManager = new GetShopApiWebSocket.WebManager(this);
    this.WubookManager = new GetShopApiWebSocket.WubookManager(this);
    this.YouTubeManager = new GetShopApiWebSocket.YouTubeManager(this);
}
errorTextMatrix = {
"1":"Created user does not have a unique id. (User was not saved to database)",
"2":"Full name is empty of the user-object that has been tried to be created",
"3":"Lastname is empty of the userobject that has been tried to be created",
"4":"Emailaddress is empty of the userobject that has been tried to be created",
"5":"Storeid is not set on the sending object.",
"6":"The messages sessionId is not set. ",
"7":"The message storeid is not set.",
"8":"User object is null on CreateUser.",
"9":"User not found - UserStoreCollection has not been created yet.",
"10":"User not found - No user with this the userid on this store.",
"11":"Search criteria for find users is not set.",
"12":"UserType is not defined.",
"13":"Login failed, incorrect username or password.",
"14":"Skeleton does not exists.",
"15":"The skeleton you are trying to create is invalid.",
"16":"Atleast one area has to be specified while creating application.",
"17":"Application name is missing.",
"18":"Application does not exists.",
"19":"Invalid arguments.",
"20":"Tried to get an element from the session but the sessionId is blank.",
"21":"Initialize message does not have a valid webaddress.",
"22":"This session does not have a user logged in.",
"23":"Failed to retreive the store from session.",
"24":"Product manager tried to save / retreive product from a session that is not valid.",
"25":"Failed to send dataobject to database saver, it does not have a valid storeid.",
"26":"Access denied, please logon to gain access to this feature.",
"27":"The product you try to get does not exists. ( Only thrown internally )",
"28":"No products found by the specified ids.",
"29":"Invalid appconfiguration object.",
"30":"Requested page does not exists.",
"31":"Can not add application to this page area, was not able to find the page.",
"32":"Could not add application to app area, the specified area was not found.",
"33":"Was not able to find the pagearea where to remove the application from.",
"34":"Was not able to find the page that the application should be removed from.",
"35":"Tried to create a menu entry with a page, but was not able to create a new page.",
"36":"Can not change stickyness on a application that does not exists.",
"37":"Invalid image to save.",
"38":"An error occured while saving the image.",
"39":"Invalid height set when fetching an image. (a positive integer, or 0 for default)",
"40":"Invalid width set when fetching an image. (a positive integer, or 0 for default)",
"41":"The image you tried to fetch, does not exists.",
"42":"Invalid arguments.",
"42":"Error when loading image from file.",
"44":"The product with the specified pageid does not exists.",
"45":"Invalid configuration object.",
"46":"Was not able to find the store.",
"47":"Failed to send email.",
"48":"Invalid input when sending an email.",
"49":"Invalid input when trying do add additional image.",
"50":"Product in order does not exists.",
"51":"Product is not null on createOrder message",
"52":"Address is not added to createOrder.",
"53":"Address is not valid when creating a order.",
"54":"Invalid userlist when trying to fetch the userlist.",
"55":"Invalid fileid.",
"56":"Unable to load file.",
"57":"A shop has already been created with an email adress.",
"58":"Trying to update settings for a none existing application.",
"59":"Its not allowed to update a order to status created with this message.",
"60":"Its not allowed to update a order to status payment failed with this message.",
"61":"Order does not exists",
"62":"Could not find the product to update the product quanity for.",
"63":"Arguments are invalid when trying to add an image to a product",
"64":"Fatal error, failed to save object to database, please contact getshop support",
"64":"Tried to get a shop without id",
"66":"Cannot create user, user with same email address already exists.",
"67":"You are already signed up for this event",
"68":"Its not defined what number sms should be sent from",
"69":"Access denied when trying to send sms.",
"70":"Invalid password when trying to toggle sms system.",
"71":"This event is full, please contact for more information",
"72":"Could not send mail, subject not configured",
"73":"Could not send mail, email text not configured",
"74":"Phone number can not be empty when sending an sms.",
"75":"Message can not be empty when sending an sms.",
"76":"I am sorry, your email does not exists.",
"77":"Ooops, an error occurred when sending confirmation code.",
"78":"Failed to reset password, confirmation code is not valid",
"79":"Was not able to decode base64 image",
"80":"Login failed, your account has expired.",
"81":"Object for the execution is not found",
"82":"The method you are trying to invoke does not exists",
"83":"Invoke java security exception",
"84":"Illigal access",
"85":"Illigal arguments, check your input arguments",
"86":"Unkown error, please check additional information",
"87":"The entity you try to save does not exists.",
"88":"The password could not be encrypted.",
"89":"The old password does not match.",
"90":"The page you tried to create already is created",
"91":"Please check your email account for more information.",
"92":"Multiple theme applications has been added, please contact support",
"93":"You have added application that requires payment.",
"94":"The web address is already taken",
"95":"The store configuration can not be null",
"96":"Can not rename attribute to an attribute which already exists.",
"97":"The users you try to modify is not in your group.",
"98":"Incorrect deepfreeze password.",
"99":"The coupon code is invalid.",
"100":"Invalid input parameters.",
"101":"Was not able to find the user in the waitinglist.",
"102":"Insufficient credits.",
"103":"Test has been deleted.",
"104":"No code available for the lock.",
"1000":"Left menu entry already exists.",
"1001":"Menu entry does not exists.",
"1002":"App configuration does not exists.",
"1003":"Parent does not exists.",
"1004":"Menu entry can not be empty",
"1005":"Can not move menu entry to itself.",
"1006":"Invalid order id.",
"1007":"Can not move a menu entry into one of it children.",
"1008":"Invalid auto expand option has to be 0 or 1.",
"1009":"Home page cannot be deleted",
"1010":"Could not find the image to attach the product to",
"1011":"Product does not exists",
"1012":"The entry you are trying to update does not exists",
"1013":"Product can have only one main image",
"1014":"Product must have one main image",
"1015":"Could not find the application.",
"1016":"Unable to calculate price for product.",
"1017":"Unable to find product attribute value.",
"1018":"Failed searching youtube.",
"1019":"The email can not be empty, and you need atleast one user",
"1020":"Can not delete room type since rooms still exists with this type",
"1021":"Start date can not be selected before todays date",
"1022":"End date is starting before start date",
"1023":"An error occured, support has been notified of this problem.",
"1024":"Failed to generate md5sum for file.",
"1025":"Booking reference does not exists.",
"1026":"Failed to create zipfile.",
"1027":"Not enough credit.",
"1028":"Page area does not exists",
"1029":"Did not find the binary file.",
"1030":"No mode set.",
"1031":"Tried to retreive a singelton application that is not singleton.",
"1032":"Not enough rooms available",
"1033":"Klarte ikke å genere PDF.",
"1034":"New orderstatus invalid, order is already marked as paid.",
"1035":"Can not add user to event, the event does not exists",
"1036":"Cant delete location, its in use in some events",
"1037":"User can not be created, already exists one with same cellphone",
"1038":"The time periode is already closed, cannot reclose it",
"1039":"Can not save order, its within a closed periode",
"1040":"Destination can not be pooled, it has been marked as arrived or serviced.",
"1041":"Can not setup this, lock does not have enough userslots for this group. Check log for more information",
"1042":"The LockGroup does not exists",
"1043":"Its not specified a forskningsperiode for the given periode of time, please check the start and end date that it has periodes.",
"1044":"Invalid webadress",
"1045":"The system does not support that a user has multiple periodes within an active periode.",
"1046":"Generating a report with users that are not connected to a company",
"1047":"No accountingsystem selected",
"1048":"No product conencted to the ticket",
"1049":"This accountingsystem does not support direct transfer",
"1050":"Was not able to validate EHF. GetShop has been notified and will contact you once we have checked what the problem is",
"1051":"Order is manually closed, please open it before modifying",
"1052":"This payment method is not allowed to mark as paid manually",
"1053":"You can not do this as it will change financial in a periode that has been closed.",
"1054":"You can not close this periode without closing the previouse one.",
"1055":"Vat number can not be blank.",
"1056":"Company already exists.",
"1057":"There already exists a user with maincompany and has connection to the newly created company.",
"1000001":"The name attribute is invalid",
"1000002":"The entry you are trying to fecth does not exists.",
"1000003":"Could not find list to sort on.",
"1000004":"The item you are trying to delete does not exists",
"1000005":"The item you are trying to reorder does not exists.",
"1000006":"The logo you are trying to remove does not exists.",
"1000007":"The entry you are trying to update does not exists",
"1000008":"The list which held this entry does not exists.",
"1000009":"Invalid start or end date",
"1000010":"Store handler not found",
"1000011":"Could not find the list to decouple",
"1000012":"Invalid application list.",
"1000013":"Page list is invalid.",
"1000014":"This list does not exists",
"1000015":"Booking does not exists",
"1000016":"Can not add a destination to a route that should not have it.",
"1000017":"Det finnes allerede en annen bil med samme mobilnr.",
"2000001":"You must selected the original encrypted file + the tuningfile you wish to send.",
"2000002":"Failed to decrypt file, please check the windows computer where the CMD program is running if it has a error or something.",
"2000003":"At least one file needs to be selected when purchasing a product.",
}
