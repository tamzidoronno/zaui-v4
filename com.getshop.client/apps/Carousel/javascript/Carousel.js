app.Carousel = {
    init: function() {
        $(document).on('click', '.Carousel .gss_slider label', this.colorSlider);
        $(document).on('click', '.Carousel .save_carousel_settings', this.saveSettings);
        $(document).on('change', '.Carousel input[name="slideimage"]', this.uploadSlideImage);
        $(document).on('click', '.Carousel .add_slide', this.addSlide);
        $(document).on('click', '.Carousel .delete_slide', this.deleteSlide);
    },
    
    loadSettings: function(element, application) {
        var config = { 
            showSettings : true,
            application: application,
            draggable: true, 
            title: "Settings", 
            items: [
                
            ]
        }
        
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    colorSlider: function() {
        var status = $(this).attr("for").split("_");
        status = status[1] + "_" + status[2];
        
        if(status == "state_one") {
            $(this).parent().find(".colorchanging").css("background-color", "red");
            $(this).parent().find(".directionchanging").text("L");
            $(this).parent().find(".transitionchanging").text("S");
        } else if(status == "state_two") {
            $(this).parent().find(".colorchanging").css("background-color", "green");
            $(this).parent().find(".directionchanging").text("R");
            $(this).parent().find(".transitionchanging").text("F");
        }
    },
    
    saveSettings: function() {
        var carouselheight = $(".Carousel input[name='carouselheight']").val();
        var heighttype = $(".Carousel select[name='heighttype']").val();
        var autoslide = $(".Carousel input[name='autoslide']:checked").val();
        var transition = $(".Carousel input[name='transition']:checked").val();
        var slidedirection = $(".Carousel input[name='slidedirection']:checked").val();
        var slidespeed = $(".Carousel input[name='slidespeed']").val();
        var animationspeed = $(".Carousel input[name='animationspeed']").val();
        var showbullets = $(".Carousel input[name='showbullets']:checked").val();
        var showarrows = $(".Carousel input[name='showarrows']:checked").val();
        var arrowvertical = $(".Carousel input[name='arrowvertical']").val();
        var arrowhorizontal = $(".Carousel input[name='arrowhorizontal']").val();
        
        var data = {
            carouselheight: carouselheight,
            heighttype: heighttype,
            transition: transition,
            autoslide: autoslide,
            slidedirection: slidedirection,
            slidespeed: slidespeed,
            animationspeed: animationspeed,
            showbullets: showbullets,
            showarrows: showarrows,
            arrowvertical: arrowvertical,
            arrowhorizontal: arrowhorizontal
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveCarouselSettings", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.framework.reprintPage();
            thundashop.common.hideInformationBox();
        });
    },
    
    uploadSlideImage: function() {
        var element = this;
        var file = $(this)[0].files[0];
        var slideId = $(this).parent().parent().attr("slide_id");
        
        console.log(file);
        
        if(file) {
            var fileName = file.name;
            var reader = new FileReader();

            reader.onload = function(event) {
                var dataUri = event.target.result;

                var data = {
                    fileBase64: dataUri,
                    slideId: slideId
                };
               
                var event = thundashop.Ajax.createEvent(null, "saveSlideImage", element, data);
                
                thundashop.Ajax.post(event);
            };

            reader.onerror = function(event) {
                console.error("File could not be read! Code " + event.target.error.code);
            };
            
            reader.readAsDataURL(file); 
        }
    },
    
    addSlide: function() {        
        var event = thundashop.Ajax.createEvent(null, "addSlide", this, null);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $(".slides_container").append(res);
        });
    },
    
    deleteSlide: function() {
        var slideId = $(this).parent().attr("slide_id");
        
        var data = {
            slideId: slideId
        }
        
        var event = thundashop.Ajax.createEvent(null, "deleteSlide", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
            $(".slide_image[slide_id='" + slideId + "']").remove();
        });
    },
    
    alignCarousel: function(carouselId, ratio) {
        var currentWidth = $(".Carousel[appid='" + carouselId + "'] #slider1").css("width").slice(0, -2);
        $(".Carousel[appid='" + carouselId + "'] #slider1").css("height", currentWidth / ratio + "px");
    },
    
    setTransitionType: function(transitionType, carouselId) {
        var cssAnimation = document.createElement('style');
        cssAnimation.type = 'text/css';

        if(transitionType == "slide") {
            var cssString = "" + 
                    ".Carousel[appid='" + carouselId +"'] .csslider > ul > li {" +
                    "position: relative;" +
                    "}";

            for(var i = 1; i <= 10; i++) {
                cssString += "" +
                    ".Carousel[appid='" + carouselId +"'] .csslider > input:nth-of-type(" + i + "):checked ~ ul li:first-of-type {" +
                    "margin-left: -" + (i-1) * 100 + "%;" +
                    "}";
            }
        } else if(transitionType == "fade") {
            var cssString = "" + 
                    ".Carousel[appid='" + carouselId + "'] .csslider > ul > li {" +
                    "position: absolute;" +
                    "opacity: 0;" +
                    "}";

            for(var i = 1; i <= 10; i++) {
                cssString += "" +
                    ".Carousel[appid='" + carouselId + "'] .csslider > input:nth-of-type(" + i + "):checked ~ ul li:nth-of-type(" + i + ") {" +
                    "opacity: 1;" +
                    "}";
            }
        }

        var rules = document.createTextNode(cssString);
        cssAnimation.appendChild(rules);
        $("head")[0].appendChild(cssAnimation);
    },
    
    setSlideNumber: function(carouselId) {
        if(typeof(Storage) !== "undefined") {
            var activeSlide = localStorage.getItem(carouselId + "_currentSlide");
            $(".Carousel[appid='" + carouselId +"'] #" + activeSlide).prop("checked", true);
        }


        $(".Carousel[appid='" + carouselId + "'] input[type=radio][name=slides]").change(function() {
            localStorage.setItem(carouselId + "_currentSlide", $(this).attr("id"));
        });
    },
    
    carouselIntervals: {},
    
    initAutoClicker: function(carouselId, direction, speed) {
        if(app.Carousel.carouselIntervals[carouselId] == null) {
            var forwarder = function() {
                $(".Carousel[appid='" + carouselId + "'] .arrows label").each(function() {
                   if($(this).css(direction) === "0px") {
                       $(this).click();
                       return false;
                   } 
                });
            };

            var slideInterval = speed;
            var slideInterval = slideInterval != "" ? slideInterval : 3000;
            var forwarderInterval = setInterval(forwarder, slideInterval);
            app.Carousel.carouselIntervals[carouselId] = true;

            $(".Carousel[appid='" + carouselId + "'] .arrows label").on('click', function() {
                clearInterval(forwarderInterval);
                forwarderInterval = setInterval(forwarder, slideInterval);
            });
        }
    },
    
    setCarouselPercentageHeight: function(carouselId, heightPercentage) {
        $(".Carousel[appid='" + carouselId + "']").find("#slider1").css("height", $(window).height() * (heightPercentage / 100) + "px");
    }
}

app.Carousel.init();