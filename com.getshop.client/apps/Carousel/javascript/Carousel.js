app.Carousel = {
    init: function() {
        $(document).on('click', '.Carousel .gss_slider label', this.colorSlider);
        $(document).on('click', '.Carousel .save_carousel_settings', this.saveSettings);
        $(document).on('change', '.Carousel input[name="slideimage"]', this.uploadSlideImage);
        $(document).on('click', '.Carousel .change_slide_number', this.changeSlideNumber);
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
            $(this).parent().find(".textchanging").text("L");
        } else if(status == "state_two") {
            $(this).parent().find(".colorchanging").css("background-color", "green");
            $(this).parent().find(".textchanging").text("R");
        }
    },
    
    saveSettings: function() {
        var carouselheight = $(".Carousel input[name='carouselheight']").val();
        var autoslide = $(".Carousel input[name='autoslide']:checked").val();
        var slidedirection = $(".Carousel input[name='slidedirection']:checked").val();
        var slidespeed = $(".Carousel input[name='slidespeed']").val();
        var showbullets = $(".Carousel input[name='showbullets']:checked").val();
        var showarrows = $(".Carousel input[name='showarrows']:checked").val();
        var arrowvertical = $(".Carousel input[name='arrowvertical']").val();
        var arrowhorizontal = $(".Carousel input[name='arrowhorizontal']").val();
        
        var data = {
            carouselheight: carouselheight,
            autoslide: autoslide,
            slidedirection: slidedirection,
            slidespeed: slidespeed,
            showbullets: showbullets,
            showarrows: showarrows,
            arrowvertical: arrowvertical,
            arrowhorizontal: arrowhorizontal
        }
        
        thundashop.Ajax.simplePost(this, "saveCarouselSettings", data);
    },
    
    uploadSlideImage: function() {
        var element = this;
        var file = $(this)[0].files[0];
        var slideNumber = $(this).parent().parent().attr("slidenumber");
        
        console.log(file);
        
        if(file) {
            var fileName = file.name;
            var reader = new FileReader();

            reader.onload = function(event) {
                var dataUri = event.target.result;

                var data = {
                    fileBase64: dataUri,
                    slideNumber: slideNumber
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
    
    changeSlideNumber: function() {
        var data = {
            slideNum: $(this).attr("num_change")
        }
        
        var event = thundashop.Ajax.createEvent(null, "setSlideNumber", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var toDo = res.split("_");
            if(toDo[0] != "removeSlider") {
                $(".slides_container").append(res);
            } else if (toDo[0] == "removeSlider"){
                $(".slide_image[slidenumber='" +  + toDo[1] + "']").remove();
            }
            
        });
    }
}

app.Carousel.init();