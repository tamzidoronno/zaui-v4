app.InvoicePayment = {
    init: function() {
        $(document).on('change', '.InvoicePayment .invoicenote' , app.InvoicePayment.noteChanged);
        $(document).on('click', '.InvoicePayment .shop_button.dosendehf', app.InvoicePayment.sendEhf);
        $(document).on('click', '.InvoicePayment .shop_button.sendByEmail', app.InvoicePayment.sendByEmail);
        $(document).on('change', '.InvoicePayment .gsniceselect1.selectguest', app.InvoicePayment.guestChanged);
    },
    
    guestChanged: function() {
        $(this).closest('.sendByEhf').find('[gsname="bookerEmail"]').val($(this).val());
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
    },
    
    sendByEmail: function() {
        var data = {};
        data.emailaddress = $(this).closest('.sendByEhf').find('[gsname="emailaddress"]').val();
        
        var event = thundashop.Ajax.createEvent(null, "sendByEmail", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.InvoicePayment .sendByEhf .emailSent').show();
            setTimeout(function() {
                $('.InvoicePayment .sendByEhf .emailSent').hide();
            }, 5000);
        });
    },
    
    sendEhf: function() {
        var data = {};
        data.vatNumber = $(this).closest('.sendehfbox').find('[gsname="vatNumber"]').val();
        data.orderid = $(this).closest('.sendehfbox').attr('orderid');
        
        var event = thundashop.Ajax.createEvent(null, "sendEhf", this, data);
        event['synchron'] = true;
        $('.InvoicePayment .sendehfbox').hide();
        $('.InvoicePayment .sendingehf').show();
        $('.InvoicePayment .sendehfbox .ehfresult').html("");
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            $('.InvoicePayment .sendehfbox').show();
            $('.InvoicePayment .sendingehf').hide();
            $('.InvoicePayment .sendehfbox .ehfresult').html(res);
            setTimeout(function() {
                $('.InvoicePayment .sendehfbox .ehfresult').html("");
            }, 5000);
        });
    }
}

app.InvoicePayment.init();
