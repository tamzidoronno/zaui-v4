app.OcrReder = {
    init : function() {
        $(document).on('click', '.OcrReader .dosetaccountingid' , app.OcrReder.dosetaccountingid);
        $(document).on('click', '.OcrReader .createtranasctionfile', app.OcrReder.createTransactionFile);
    },
    
    createTransactionFile: function() {
        var event = thundashop.Ajax.createEvent(null, "createTransactionFile", this, {});
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(fileid) {
            window.open('/scripts/accountingFileDownload.php?fileid=' + fileid);
            window.location = window.location;
        });
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