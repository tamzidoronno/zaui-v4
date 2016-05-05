app.MagicText = {
    initEvents : function() {
        
    },
    
    doScrollText : function(direction, appid, config) {
        console.log(config);
        var curScroll = $(window).scrollTop();
        var curScroll = ($('.MagicText[appid="'+appid+'"] .line').offset().top - curScroll)  * -1;
        if(config.scrollstart > curScroll) {
            $('.MagicText[appid="'+appid+'"] .line').removeClass('isvisible');
        } else {
            app.MagicText.addVisibility(appid, config.timer, 100);
        }
    },
    
    addVisibility : function(appid, starttimeout, linetimer) {
        var counter = 1;
        for(var i = 1; i <= 10; i++) {
            setTimeout(function() {
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