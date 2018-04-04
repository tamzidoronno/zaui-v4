app.MecaFleetServiceList = {
    init: function() {
        $(document).on('click', '.MecaFleetServiceList .show_comment_field', app.MecaFleetServiceList.showCommentField)
    },
    
    showCommentField: function() {
        var curdiv = $(this).closest('.outercommentfield').find('.commentfield');
        if (curdiv.is(':visible')) {
            $('.MecaFleetServiceList .commentfield').hide();
        } else {
            $('.MecaFleetServiceList .commentfield').hide();
            curdiv.show();
        }
    }
};

app.MecaFleetServiceList.init();