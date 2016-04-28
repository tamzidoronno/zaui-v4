app.SedoxFileServiceStatus = {
    init: function() {
        $(document).on('click', '.gss_slider label', this.colorSlider);
    },
    
    colorSlider: function() {
        var status = $(this).attr("for");
        var attr = $(this).parent().attr("gs_model_attr");
        
        if(status == attr + "_state_one") {
            $(this).parent().find(".slider_button").css("background-color", "red");
        } else if(status == attr + "_state_two") {
            $(this).parent().find(".slider_button").css("background-color", "yellow");
        } else if (status == attr + "_state_three") {
            $(this).parent().find(".slider_button").css("background-color", "green");
        }
    }
}

app.SedoxFileServiceStatus.init();