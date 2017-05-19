app.ScormList = {
    init: function() {
        $(document).on('click', '.ScormList .fa-plus', app.ScormList.toggleView);
    },
    
    toggleView: function() {
        var view = $(this).closest('.outerrow').find('.groupedinformation');
        if (view.is(':visible')) {
            view.hide();
        } else {
            view.show();
        }
    }
}

app.ScormList.init();