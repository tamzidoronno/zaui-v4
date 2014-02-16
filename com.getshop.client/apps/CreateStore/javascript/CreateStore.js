if (typeof(com) == "undefined")
    com = {};
if (typeof(com.getshop) == "undefined")
    com.getshop = {};
if (typeof(com.getshop.app) == "undefined")
    com.getshop.app = {};

com.getshop.app.CreateStore = {
    domainName : "getshop.com",
    data: {},
    createStoreDom: null,
    lastValidUrl: "",
    init: function()  {
        var scope = this;
        $('#start_store').live('click', function() {
            scope.start();
        });
        $('.new_logon #shopname').live('keyup', this.checkUrl);
        $('#getshopmovieplaybox').live('click', this.playGetshopMovie);
        $('.CreateStore [gsname="shopname"]').live('blur', this.checkIfExists);
    },
    checkIfExists: function() {
        var val = $(this).val();
        var target = $(this);
        var error = false;
        if (val === "."+com.getshop.app.CreateStore.domainName) {
            error = true;
        }
        var data = {
            "address": val
        };
        var event = thundashop.Ajax.createEvent("CreateStore", "checkExists", $(this), data);

        thundashop.Ajax.postWithCallBack(event, function(data) {
            if (data === "1") {
                error = true;
            }
            if (error) {
                target.closest('.new_logon').find('.error_shopname').slideDown();
            } else {
                target.closest('.new_logon').find('.error_shopname').slideUp();
            }
        });

    },
    playGetshopMovie: function() {
        var event = thundashop.Ajax.createEvent('CreateStore', 'playMovie', $(this));
        thundashop.common.showInformationBox(event, 'Getshop introduction movie');
    },
    checkUrl: function() {
        var entered = $(this).val();

        if (com.getshop.app.CreateStore.lastValidUrl + "." +com.getshop.app.CreateStore.domainName == entered)
            return;

        if (entered.indexOf(com.getshop.app.CreateStore.domainName) < 0 && com.getshop.app.CreateStore.lastValidUrl != "") {
            entered = com.getshop.app.CreateStore.lastValidUrl;
            thundashop.common.Alert("Invalid address", "You can not change the subaddress now, can be set to your domain when you have setup your webshop.", true);
        }

        if (entered.indexOf(" ") > -1)
            entered = com.getshop.app.CreateStore.lastValidUrl;

        entered = entered.replace("."+com.getshop.app.CreateStore.domainName, "");

        if (entered.indexOf(".") > -1)
            entered = com.getshop.app.CreateStore.lastValidUrl;

        $(this).val(entered + "."+com.getshop.app.CreateStore.domainName);
        this.setSelectionRange(entered.length, entered.length);
        com.getshop.app.CreateStore.lastValidUrl = entered;
    },
    start: function() {

        $('.new_logon #shopname').css('border-color', '#DDD');
        $('.new_logon #username').css('border-color', '#DDD');
        $('.new_logon #password').css('border-color', '#DDD');
        $('.new_logon #email').css('border-color', '#DDD');

        this.registerVariables();

        if ($('.error_shopname').is(":visible")) {
            thundashop.common.Alert("Errors", "The shop name you are trying to register does already exists", true);
            return;
        }

        if (this.isThereErrors()) {
            thundashop.common.Alert("Errors", "please correct the fields in red, they can not be empty", true);
            return;
        }

        this.createStoreDom = $('.new_logon');
        this.clearSchema();
        this.createShop();
    },
    validateEmail: function(email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    },
    isThereErrors: function()  {
        var retval = false;

        if (this.data.shopname == "" || this.data.shopname == "myshop."+com.getshop.app.CreateStore.domainName) {
            $('.new_logon #shopname').css('border-color', 'red');
            retval = true;
        }

        if (this.data.username == "" || this.data.username == "John doe") {
            $('.new_logon #username').css('border-color', 'red');
            retval = true;
        }

        if (this.data.email == "" || this.data.email == "your@email.com" || !this.validateEmail(this.data.email)) {
            $('.new_logon #email').css('border-color', 'red');
            retval = true;
        }

        if (this.data.password == "" || this.data.password == "supersecretpassword") {
            $('.new_logon #password').css('border-color', 'red');
            retval = true;
        }

        return retval;
    },
    clearSchema: function() {
        var header = $('<div/>');
        header.html('Setting up your webshop, please wait.');
        header.addClass('title');
        this.createStoreDom.html(header);
        this.printConfirm("Create store", 'progress-createstore');
        this.printConfirm("Initialize new store", 'progress-initstore');
        this.printConfirm("Adduser user to start", 'progress-adduser');
        this.printConfirm("Add default pages", 'progress-addpages');
        this.printConfirm("Send confirm email", 'progress-sendmail');
        this.printConfirm("Create autologin link", 'progress-createlink');
    },
    createShop: function() {
        var scope = this;
        var event = thundashop.Ajax.createEvent(null, "startCreateStore", $('.CreateStore'), this.data);
        thundashop.Ajax.postWithCallBack(event, function(response) {
            scope.createStoreDone(response)
        });
    },
    createStoreDone: function(response) {
        this.setDone('progress-createstore');
        var scope = this;
        var event = thundashop.Ajax.createEvent(null, "initStore", $('.CreateStore'), this.data);
        thundashop.Ajax.postWithCallBack(event, function() {
            scope.initStoreDone()
        });
    },
    initStoreDone: function() {
        this.setDone('progress-initstore');
        var scope = this;
        var event = thundashop.Ajax.createEvent(null, "addUser", $('.CreateStore'), this.data);
        thundashop.Ajax.postWithCallBack(event, function() {
            scope.addUserDone()
        });
    },
    addUserDone: function() {
        this.setDone('progress-adduser');
        var scope = this;
        var event = thundashop.Ajax.createEvent(null, "addPages", $('.CreateStore'), this.data);
        thundashop.Ajax.postWithCallBack(event, function() {
            scope.addPagesDone()
        });
    },
    addPagesDone: function() {
        this.setDone('progress-addpages');
        var scope = this;
        var event = thundashop.Ajax.createEvent(null, "sendMail", $('.CreateStore'), this.data);
        thundashop.Ajax.postWithCallBack(event, function() {
            scope.sendMailDone()
        });
    },
    sendMailDone: function() {
        this.setDone('progress-sendmail');
        var scope = this;
        var event = thundashop.Ajax.createEvent(null, "createLink", $('.CreateStore'), this.data);
        thundashop.Ajax.postWithCallBack(event, function(link) {
            scope.createLinkDone(link)
        });
    },
    createLinkDone: function(link) {
        this.setDone('progress-createlink');
        this.showDoneMessage(link);
    },
    showDoneMessage: function(link) {

        /* <![CDATA[ */
        goog_snippet_vars = function() {
            var w = window;
            w.google_conversion_id = 1030741358;
            w.google_conversion_label = "UokhCObb2wQQ7rq_6wM";
            w.google_conversion_value = 0;
        }
        // DO NOT CHANGE THE CODE BELOW.
        goog_report_conversion = function(url) {
            goog_snippet_vars();
            window.google_conversion_format = "3";
            window.google_is_call = true;
            var opt = new Object();
            opt.onload_callback = function() {
                if (typeof(url) != 'undefined') {
                    window.location = url;
                }
            }
            var conv_handler = window['google_trackConversion'];
            if (typeof(conv_handler) == 'function') {
                conv_handler(opt);
            }
        }
        goog_report_conversion(link);
    },
    setDone: function(id) {
        $('#' + id).removeClass('disabled');
    },
    printConfirm: function(message, id) {
        var div = $('<div/>');
        div.attr('id', id);
        div.addClass('progress');
        div.addClass('disabled');
        div.html(message);
        this.createStoreDom.append(div);
    },
    registerVariables: function() {
        var data = {};

        data.shopname = $('.new_logon #shopname').val().toLowerCase();
        data.username = $('.new_logon #username').val();
        data.password = $('.new_logon #password').val();
        data.email = $('.new_logon #email').val();
        this.data = data;
    }
}

com.getshop.app.CreateStore.init();