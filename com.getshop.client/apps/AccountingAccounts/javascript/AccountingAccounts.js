app.AccountingAccounts = {
    init: function() {
        $(document).on('click', '.AccountingAccounts .changeaccountingname', app.AccountingAccounts.changeAccountingName);
    },
    
    changeAccountingName: function() {
        var newNumber = prompt('New number?');
        
        var data = {
            oldAccountNumber: $(this).attr('oldnumber'),
            accountNumber: newNumber
        };
        
        thundashop.Ajax.simplePost(this, "changeAccountingNumber", data);
    }
}

app.AccountingAccounts.init();