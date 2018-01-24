app.PmsSearchBox = {
    init : function() {
        $(document).on('click','.PmsSearchBox .advancesearchtoggle',app.PmsSearchBox.toggleAdvanceSearch);
    },
    toggleAdvanceSearch : function() {
        if($('.PmsSearchBox .advancesearch').is(':visible')) {
            $('.PmsSearchBox .simplesearch').show();
            $('.PmsSearchBox .advancesearch').hide();
        } else {
            $('.PmsSearchBox .simplesearch').hide();
            $('.PmsSearchBox .advancesearch').show();
        }
    }
};
app.PmsSearchBox.init();