if(typeof(gs_app) === "undefined") {
    gs_app = {};
}

app.Facebook = {
    loadSettings : function(element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}

gs_app.Facebook = function(appid, address) {
    this.appId = appid;
    this.application = $('.app[appid="' + appid + '"]');
    this.address = address;
    this.isSaved = false;
    this.init();
    console.log(this.address);
    console.log(address.length);
    if (address.length > 0) {
        this.isSaved = true;
        this.displayNews();
    }
}

gs_app.Facebook.prototype = {
    fetchAccessToken: "https://graph.facebook.com/oauth/access_token?client_id=390097657728360&client_secret=8110698be3e063aa6247a8a1807fedf2&grant_type=client_credentials&redirect_uri=http://www.getshop.com/",
    accessToken: "",
    appId: "",
    address: "",
    application: null,
    activate: function() {
        this.address = this.application.find('.activate_page').val();
        if (this.address.indexOf('www.facebook') <= 0) {
            alert(__f("The facebook page is not valid"));
            return;
        }
        this.address = this.application.find('.activate_page').val();
        this.displayNews();

    },
    validateAddress: function() {
        this.address = this.address.replace("www.facebook", "graph.facebook");
        this.address = this.address.replace("http://", "https://", this.address);
        if (this.address.indexOf("https:") < 0) {
            this.address = "https://" + this.address;
        }
    },
    getAddress: function(part) {
        return this.address + "/" + part + "?" + this.accessToken;
    },
    displayNews: function() {
        var scope = this;
        $.get(this.fetchAccessToken, function(data) {
            scope.accessToken = data;
            scope.loadNews();
        });
    },
    loadNews: function() {
        var scope = this;
        this.validateAddress();
        var found = false;
        $.ajax({
            "url": this.getAddress("posts") + "&limit=15",
            "success": function(data) {
                scope.application.find('.news_area').html('');
                for (var key in data.data) {
                    var newsentry = data.data[key];
                    if (newsentry.message !== undefined) {
                        var htmlEntry = scope.buildNewsEntry(newsentry);
                        scope.application.find('.news_area').append(htmlEntry);
                        found = true;
                    }
                }
                if (found && !scope.isSaved) {
                    scope.saveAddress();
                }
            },
            error : function(a,b,c) {
                alert('Failed to load the requested url, it has to be a valid facebook page address.');
            }
        });
    },
    saveAddress: function() {
        var event = thundashop.Ajax.createEvent('', 'SaveAddress', this.application, {"address": this.address});
        thundashop.Ajax.postWithCallBack(event, function() {

        });

    },
    buildNewsEntry: function(newsentry) {
        console.log(newsentry);
        var date = new Date(newsentry.created_time);
        var month = date.getMonth()+1;
        var minutes = date.getMinutes();
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        if (month < 10) {
            month = "0" + month;
        }
        var message = newsentry.message;
        message = message.replace(/\n/g, '<br />');

        var when = date.getDate() + "-" + month + "-" + date.getFullYear() + " " + date.getHours() + ":" + minutes;
        var entry = $("<div class='newsentry'></div>");
        entry.append($("<div class='created_time'>" + when + "</div>"));
        if (newsentry.picture !== undefined) {
            entry.append("<img class='picture' src='" + newsentry.picture + "'>");
        }
        entry.append($("<div class='message'>" + message + "</div>"));
        entry.append($('<div class="newsbottom"></div>"'));
        return entry;
    },
    init: function() {
        var scope = this;
        scope.application.on('click', '.activatePageFeed', function() {
            scope.activate.call(scope);
        });
    }
}

