app.MagicText = {
    initEvents : function() {
        
    },
    
    doScrollText : function(direction, appid, config) {
        var curScroll = $(window).scrollTop();
        console.log(curScroll);
        if(direction) {
            if(config.scrollstart > curScroll) {
                console.log('need to add');
                $('.MagicText[appid="'+appid+'"] .line').removeClass('isvisible');
            }
        } else {
            if(config.scrollstart < curScroll) {
                console.log('need to add');
                app.MagicText.addVisibility(appid, config.timer, 50);
            }
        }
    },
    
    addVisibility : function(appid, starttimeout, linetimer) {
        var counter = 1;
        for(var i = 1; i <= 10; i++) {
            setTimeout(function() {
                console.log('adding: ' + counter + "(" + appid + ")");
                $('.MagicText[appid="'+appid+'"]').find('.line_'+counter).addClass('transition');
                $('.MagicText[appid="'+appid+'"]').find('.line_'+counter).addClass('isvisible');
                counter++;
            }, starttimeout);
            starttimeout += linetimer;
        }
    },
    
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            showSettings: true,
            application: application,
            title: "Settings",
            items: []
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.MagicText.initEvents();