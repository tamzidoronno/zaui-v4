app.PmsSubscriptionOverview = {
    init: function () {
        $(document).on('click', '.PmsSubscriptionOverview #onlyunpaid', app.PmsSubscriptionOverview.showUnpaid);
    },
    showUnpaid : function() {
        if($(this).is(':checked')) {
            $('.overviewrow').hide();
            $('.overviewrow.No_Dibs').show();
        } else {
            $('.overviewrow').show();
        }
    },
    showDownloaded : function() {
    }
};
app.PmsSubscriptionOverview.init();