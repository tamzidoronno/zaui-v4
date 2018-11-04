app.InvoicePayment = {
    init: function() {
        $(document).on('change', '.InvoicePayment .invoicenote' , app.InvoicePayment.noteChanged);
    },
    noteChanged: function() {
        var data = {};
        
        $(this).closest('[gstype="form"]').find('input[type="hidden"]').each(function() {
            data[$(this).attr('gsname')] = $(this).val();
        });
        
        data.invoicenote = $(this).val();
        
        var event = thundashop.Ajax.createEvent(null, "invoiceNoteChanged", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, null, null, true, true);
    }
}

app.InvoicePayment.init();
