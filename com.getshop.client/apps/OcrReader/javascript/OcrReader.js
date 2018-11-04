app.OcrReder = {
    init : function() {
        $(document).on('click', '.OcrReader .dosetaccountingid' , app.OcrReder.dosetaccountingid);
    },
    dosetaccountingid : function() {
        var password = prompt("Password");
        var id = prompt("Avtaleid");
        if(!id || !password) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','setAccountingId', $(this), {
            "id" : id,
            "password" : password
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            location.reload();
        });
    }
}
app.OcrReder.init();