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
        $(current).parent().find('.text_over_image').hide();
        $(current).hide();
        
        $(next).attr('visible', "1");
        
        $(next).show();
         PubSub.publish('BANNERMANAGER_IMAGECHANGE', { "id" : id });
        
        setTimeout(function() {
            $(next).parent().find('.text_over_image').fadeIn(500);
        }, 300);
        
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
    },
          
    addTextToImage: function(imageContainer, config) {
        var textHolder = $("<span>");
        textHolder.addClass('text_over_image');
        textHolder.css('position', 'absolute');
        textHolder.css('display', 'none');
        textHolder.css('left', config.x);
        textHolder.css('font-size', config.fontSize);
        textHolder.css('z-index', 1);
        textHolder.css('color', "#"+config.color);
        textHolder.css('top', config.y);
        textHolder.html(config.text);
        imageContainer.append(textHolder);
        textHolder.fadeIn(400);
    },
            
    showText: function(appId, texts) {

        var bannerSlider = $('.bannerslider#'+appId);
        bannerSlider.find('.text_over_image').remove();
        for (var imageId in texts) {
            
            var textEntires = texts[imageId];
            
            var newImage = $("<img/>");
            var imageUrl = bannerSlider.find('.banner[imageid='+imageId+']').find('img').attr('src');
            newImage.attr("src", imageUrl);
            newImage.attr('imageId', imageId);
            newImage.attr('texts', JSON.stringify(textEntires));
            newImage.load(function() {
                var originalWidth = this.width;
                
                var texts = JSON.parse($(this).attr('texts'));
                var imageId = $(this).attr('imageId');
                var imageContainer = $(bannerSlider.find('.banner[imageid='+imageId+"]")[0])
                var compressionRate = bannerSlider.width() / originalWidth;

                for (var key in texts) {
                    var config = texts[key];
                    config.x = config.x * compressionRate;
                    config.y = config.y * compressionRate;
                    config.fontSize = config.size * compressionRate;
                    config.color = config.colour;
                    app.Banner.Slider.addTextToImage(imageContainer, config);
                }
            });
        }
        
    }
};
