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
            showbullets: showbullets,
            showarrows: showarrows,
            arrowvertical: arrowvertical,
            arrowhorizontal: arrowhorizontal
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveCarouselSettings", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
            location.reload();
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
    }
}

app.Carousel.init();