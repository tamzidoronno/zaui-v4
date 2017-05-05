app.ScormManager = {
    init: function() {
        $(document).on('click', '.ScormManager .showgroups', app.ScormManager.showIt);
    },
    
    showIt: function() {
        var div = $(this).closest('.scormrow').find('.scormgroups');
        if (div.is(':visible')) {
            div.hide();
        } else {
            div.show();
        }
    }
};

app.ScormManager.init();