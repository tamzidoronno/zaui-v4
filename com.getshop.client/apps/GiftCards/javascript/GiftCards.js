app.GiftCards = {
    init : function() {
        $(document).on('click','.GiftCards .editgiftcard', app.GiftCards.loadEditPanel);
    },
    loadEditPanel : function() {
        $(this).find('.editgiftcardpanel').show();
    }
}
app.GiftCards.init();