app.PmsConference = {
    init : function() {
        $(document).on('keyup','.PmsConference .updaterowkeyup', app.PmsConference.updateEventRow);
        $(document).on('change','.PmsConference .updaterowselect', app.PmsConference.updateEventRow);
    },
    loadEditItem : function(res) {
        $('.edititembox').html(res);
        $('.edititembox').show();
    },
    eventDeleted: function(res) {
        $('.eventrow[eventid="'+res+'"]').hide();
    },
    userSavedUpdated : function() {
        $('.PmsConference .GetShopQuickUser .change_user_form, .PmsConference .GetShopQuickUser .edit_details_of_user').show();
    },
    continueToConferenceView : function(res) {
    },
    updateEventRow : function() {
        var row = $(this).closest('.eventrow');
        var data = thundashop.framework.createGsArgs(row);
        var event = thundashop.Ajax.createEvent('','updateEventRow',$('.PmsConference'),data);
        thundashop.Ajax.postWithCallBack(event, function() {});
    }
};
app.PmsConference.init();