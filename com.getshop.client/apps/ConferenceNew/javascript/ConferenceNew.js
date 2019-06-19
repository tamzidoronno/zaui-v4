app.ConferenceNew = {
    created : function(res) {
        window.document.location = "/pmsconference.php?page=conference&confid=" + res;
    }
}