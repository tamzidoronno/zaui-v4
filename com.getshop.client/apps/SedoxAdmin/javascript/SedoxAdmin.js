SedoxWebSocketListener = function(address) {
    this.sentMessages =  [];
    this.address = address;
};

SedoxWebSocketListener.prototype = {
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
    },
            
    handleMessage: function(msg) {
//        console.log(msg);
        var dataObject = JSON.parse(msg.data);
        if (dataObject) {
            if (dataObject.userId === $('body').attr('currentuserid')) {
                return;
            }
        }
        
        if (dataObject.action === "startstoptoggled" && $('#pageid').val() === "83a088e9-9770-4433-b091-1df3afce8d6d") {
            thundashop.common.goToPage("83a088e9-9770-4433-b091-1df3afce8d6d");
        }
        
        if (dataObject.action === "startstoptoggled" && $('#pageid').val() === "productview") {
            if (window.location.href.indexOf(dataObject.productId) > 0) {
                thundashop.common.goToPage("productview&productId="+dataObject.productId);
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
        if (this.socket.OPEN) {
            try {
                this.socket.send(messageJson);
            } catch (e) {
                this.getMessage(deferred.messageId);
                deferred.resolve(null);
                if (this.sentMessages.length === 0 && this.transferCompleted) {
                   this.transferCompleted();
                }
            }
        }
           
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
};

app.SedoxAdmin = {
    init: function() {
        $(document).on('click', '.SedoxAdmin .sedox_admin_topmenu .entry', app.SedoxAdmin.topMenuClicked);
        $(document).on('change', '.SedoxAdmin #searchfield', app.SedoxAdmin.searchUsers);
        $(document).on('click', '.SedoxAdmin .userentrysearch', app.SedoxAdmin.showUserInformation);
        $(document).on('click', '.showUserInformationSedox', app.SedoxAdmin.showUserInformation);
        $(document).on('click', '.SedoxAdmin .savecredit', app.SedoxAdmin.updateUserCredit);
        $(document).on('click', '.SedoxAdmin .savedevelopers', app.SedoxAdmin.saveDevelopers);
        $(document).on('click', '.SedoxAdmin .saveusersettings', app.SedoxAdmin.saveUserSettings);
        $(document).on('change', '.SedoxAdmin #slavesearchtextfield', app.SedoxAdmin.searchForSlavesToAdd);
        $(document).on('change', '.SedoxAdmin .extraincometext', app.SedoxAdmin.addCreditForSlave);
        $(document).on('click', '.SedoxAdmin .addusertomaster', app.SedoxAdmin.addUserToMaster);
        $(document).on('click', '.SedoxAdmin i.removeuser', app.SedoxAdmin.removeUserFromMaster);
        $(document).on('click', '.SedoxAdmin .showuser', app.SedoxAdmin.changeToUser);
        $(document).on('change', '.SedoxAdmin #togglepassiveslave', app.SedoxAdmin.togglePassiveChanged);
        
        $(document).ready(function() {
            if ($('body').attr('editormode') === "true") {
                app.SedoxAdmin.webSocketListener = new SedoxWebSocketListener(window.location.host);
                app.SedoxAdmin.webSocketListener.connect();
            }    
        });
        
    },
            
    togglePassiveChanged: function() {
        var masterId = $(this).closest('.useroverview').attr('userid');

        var data = {
            isPassiveSlave : $(this).is(":checked"),
            userId : masterId
        }
        
        var event = thundashop.Ajax.createEvent("", "toggleSlave", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            thundashop.common.Alert("Success", "Saved");
        });
    },
            
    changeToUser: function() {
        var userId = $(this).attr('userid');
        app.SedoxAdmin.updateInfoBox(userId);
    },
            
    addCreditForSlave: function() {
        var slaveId = $(this).attr('userid');
      
        var data = {
            amount: $(this).val(),
            slave: slaveId
        };

        var event = thundashop.Ajax.createEvent("", "addCreditToSlave", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            thundashop.common.Alert("Success", "Saved");
            app.SedoxAdmin.updateInfoBox(masterId);
        }, true);
    },
            
    removeUserFromMaster: function() {
        var masterId = $(this).closest('.useroverview').attr('userid');
        var slaveId = $(this).attr('userid');
      
        var data = {
            master: masterId,
            slave: slaveId
        };

        var event = thundashop.Ajax.createEvent("", "removeSlaveToMaster", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            app.SedoxAdmin.updateInfoBox(masterId);
        });
    },
            
    addUserToMaster: function() { 
        var masterId = $(this).closest('.useroverview').attr('userid');
        var slaveId = $(this).attr('userid');
        
        var data = {
            master: masterId,
            slave: slaveId
        };

        var event = thundashop.Ajax.createEvent("", "addSlaveToMaster", this, data);

        thundashop.Ajax.postWithCallBack(event, function(result) {
            app.SedoxAdmin.updateInfoBox(masterId);
        });
    },
            
    searchForSlavesToAdd: function() {
        var data = {
            text : $('.SedoxAdmin #slavesearchtextfield').val()
        }
        
        var event = thundashop.Ajax.createEvent("", "searchForSlaves", $('.SedoxAdmin'), data);
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.SedoxAdmin .add_slave_result').html(result);
        });
    },
    saveUserSettings: function() {
        var userId = $(this).attr('userId');
        
        var data = {
            userId : userId,
            allowNegativeCredit : $('.SedoxAdmin #allownegativecredit').is(':checked'),
            allowWindowsApplication : $('.SedoxAdmin #allowwindowsapp').is(':checked'),
            isNorwegian : $('.SedoxAdmin #norwegiancustomer').is(':checked'),
            badCustomer : $('.SedoxAdmin #badCustomer').is(':checked'),
            fixedPrice : $('.SedoxAdmin #fixedPrice').val()
        };
        
        var event = thundashop.Ajax.createEvent("", "saveUserInfo", this, data);
        
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert("Success", "Saved");
        });
    },
    saveDevelopers: function() {
        ids = [];
        $('.sedoxsettings input:checked').each(function() {
            var id = $(this).attr('id');
            ids.push(id);
        });
        
        var data = {
            activeDevelopers : ids
        };
        
        var event = thundashop.Ajax.createEvent("", "toggleDevelopers", this, data);
        
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert("Success", "Saved");
        });
    },
    updateUserCredit: function() {
        var userId = $(this).attr('userId');
        var data = {
            userId : userId,
            desc : $('.SedoxAdmin #sedox_credit_description').val(),
            amount : $('.SedoxAdmin #amount').val()
        }
        
        var event = thundashop.Ajax.createEvent("", "updateCredit", this, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Succes", "Credit updated");
            $('.SedoxAdmin #sedox_credit_description').val("");
            $('.SedoxAdmin #amount').val("");
            app.SedoxAdmin.updateInfoBox(userId);
        })
    },
            
    updateInfoBox: function(userId, isapp) {
        var data = {
            userId: userId
        }
        
        sapp = $('.SedoxAdmin:not("#informationbox") .applicationinner');
       
        if ($('.SedoxAdmin:not("#informationbox")').length === 0) {
            sapp = isapp;
        }
       
        var event = thundashop.Ajax.createEvent("", "showUserInformation", sapp, data);
        thundashop.common.showInformationBox(event, "User information");
    },
            
    showUserInformation: function() {
        app.SedoxAdmin.updateInfoBox($(this).attr('userId'), this);
    },
    searchUsers: function() {
        var data = {
            searchString : $(this).val()
        };
        
        var event = thundashop.Ajax.createEvent("", "searchForUsers", this, data);
        thundashop.Ajax.post(event);
    },
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
            
    topMenuClicked: function() {
        var changeTo = $(this).attr('changeto');
        var menu = $(this).parent().attr('menu');
        var data = { 
            menu : menu,
            changeTo : changeTo
        };
        
        var event = thundashop.Ajax.createEvent(null, "changeToSubMenu", this, data);
        thundashop.Ajax.post(event);
    }
};

app.SedoxAdmin.init();