app.SedoxFileServiceStatus = {
    init: function() {
        $(document).on('click', '#backsidesettings div[app="SedoxFileServiceStatus"] .gss_slider label', this.colorSlider);
    },
    
    colorSlider: function() {
        var status = $(this).attr("for").split("_");
        status = status[1] + "_" + status[2];
        
        console.log(status);
        
        if(status == "state_one") {
            $(this).parent().find(".slider_button").css("background-color", "red");
        } else if(status == "state_two") {
            $(this).parent().find(".slider_button").css("background-color", "yellow");
        } else if (status == "state_three") {
            $(this).parent().find(".slider_button").css("background-color", "green");
        }
    }
}

app.SedoxFileServiceStatus.init();