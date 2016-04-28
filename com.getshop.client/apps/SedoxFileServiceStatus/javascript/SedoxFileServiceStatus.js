app.SedoxFileServiceStatus = {
    init: function() {
        $(document).on('click', '.gss_slider label', this.colorSlider);
    },
    
    colorSlider: function() {
        var status = $(this).attr("for");
        if(status == "state_one") {
            $(".gss_slider .slider_button").css("background-color", "red");
        } else if(status == "state_two") {
            $(".gss_slider .slider_button").css("background-color", "yellow");
        } else if (status == "state_three") {
            $(".gss_slider .slider_button").css("background-color", "green");
        }
    }
}

app.SedoxFileServiceStatus.init();