app.PmsDayOverview = {
    init : function() {
        $(document).on('click', '.PmsDayOverview .keyreturnedroom', app.PmsDayOverview.markKeyAsReturned);
    },
    markKeyAsReturned : function() {
        var confirmed = confirm("Are you sure the key is returned for room: " + $(this).text());
        if(confirmed) {
            thundashop.Ajax.simplePost($(this), 'keyIsReturned', {
                "roomid" : $(this).attr('roomid')
            });
        }
    }
};

app.PmsDayOverview.init();