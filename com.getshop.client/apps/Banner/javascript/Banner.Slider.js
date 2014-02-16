if (typeof(app.Banner) === "undefined") {
    app.Banner = {}
}

app.Banner.Slider = {
    banners: {},
    
    init: function() {
    },
            
    
    startTimeout: function(id) {
        var timeout = function() {
            app.Banner.Slider.runner(id);
        }

        if (typeof(app.Banner.Slider.banners[id].timeOutVar) != "undefined") {
            clearInterval(app.Banner.Slider.banners[id].timeOutVar);
        }

        app.Banner.Slider.banners[id].timeOutVar = setTimeout(timeout, app.Banner.Slider.banners[id].interval);
    },
    
    goToImage: function(id, counter) {
        app.Banner.Slider.banners[id].nextImageCounter = counter;
        app.Banner.Slider.runner(id);
    },
  
    start: function(id) {
        app.Banner.Slider.banners[id] = {};
        app.Banner.Slider.banners[id].id = id;
        app.Banner.Slider.banners[id].nextImageCounter = 0;
        app.Banner.Slider.banners[id].currentImageCounter = 0;
        app.Banner.Slider.banners[id].banners = $("#" + id).find('.banner');
        app.Banner.Slider.banners[id].interval = $("#" + id).attr("interval");
        if (app.Banner.Slider.banners[id].banners.length === 1) {
            app.Banner.Slider.banners[id].interval = 1000000000;
        }
        app.Banner.Slider.runner(id);
    },

    runner: function(id) {
        if (!$('#' + id).length) {
            return;
        }

        if (!app.Banner.Slider.banners[id]) {
            return;
        }

        var nextImageCounter = app.Banner.Slider.banners[id].nextImageCounter;
        var banners = app.Banner.Slider.banners[id].banners;
        var current = banners[app.Banner.Slider.banners[id].currentImageCounter];

        var next = banners[nextImageCounter];
        var imageId = $(next).attr('imageid');
        $('.Banner .banner_text').hide();

        $(current).attr('visible', "0");
        $(current).hide();
        
        $(next).attr('visible', "1");
        $(next).show();

        $('.dots .dot').each(function() {
            $(this).removeClass('selected');
        });

        $('.dots .dot[imageId=' + imageId + ']').addClass('selected');

        app.Banner.Slider.banners[id].currentImageCounter = nextImageCounter;
        nextImageCounter++;

        if (nextImageCounter >= banners.length) {
            app.Banner.Slider.banners[id].nextImageCounter = 0;
        } else {
            app.Banner.Slider.banners[id].nextImageCounter = nextImageCounter;
        }

        this.startTimeout(id);
    }
};