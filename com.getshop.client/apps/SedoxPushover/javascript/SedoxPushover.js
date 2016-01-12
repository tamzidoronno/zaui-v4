app.SedoxPushOver = {
    init: function() {
        $(document).on('click', '.SedoxPushover .savepushoverid', app.SedoxPushOver.saveId);
    },
    
    saveId: function() {
        thundashop.Ajax.simplePost(this, "saveId", {
            id : $('.SedoxPushover .pushoverid').val()
        });
    }
}

app.SedoxPushOver.init();