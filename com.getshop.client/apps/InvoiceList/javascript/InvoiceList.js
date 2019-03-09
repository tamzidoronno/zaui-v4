app.InvoiceList = {
    init: function() {
        $(document).on('change', '.InvoiceList .timeperiode[gsname="year"]', app.InvoiceList.changePeriodeOptions);
        $(document).on('change', '.InvoiceList .duerow.header .incrementalOrderId input', app.InvoiceList.toggleCheckboxes);
        $(document).on('click', '.InvoiceList .topmenu .dropdownmenu', app.InvoiceList.toggleTopMenu);
        $(document).on('click', '.InvoiceList .topmenu .showsendmenu', app.InvoiceList.showSendMenu);
        $(document).on('click', '.InvoiceList .closesendinvoices', app.InvoiceList.closeSendBox);
        $(document).on('click', '.InvoiceList .send_invoices', app.InvoiceList.sendInvoices);
    },
    
    showSendMenu: function() {
        $('.InvoiceList .sendboxouter').show();
        var orderIds = [];
        
        var checkBoxes = $('.InvoiceList .duerow.realrow input');
        $(checkBoxes).each(function() {
            if ($(this).is(':checked')) {
                orderIds.push($(this).val());
            }
        });
        
        var data = {
            orderIds : orderIds
        };
        
        var event = thundashop.Ajax.createEvent(null, "showOrderToSendList", this, data);
        event['synchron'] = true;
        
        $('.InvoiceList .sendboxouter .boxcontent').html("Loading....");
        
        thundashop.Ajax.post(event, function(res) {
            $('.InvoiceList .sendboxouter .boxcontent').html(res);
        });        
    },
    
    closeSendBox: function() {
        $('.InvoiceList .sendboxouter').hide();
    },
    
    toggleTopMenu: function() {
        var menu = $('.InvoiceList .topmenu .innermenu');
        if (menu.is(':visible')) {
            menu.slideUp();
        } else {
            menu.slideDown();
        }
    },
    
    toggleCheckboxes: function() {
        var checkBoxes = $('.InvoiceList .duerow.realrow input');
        checkBoxes.prop("checked", $(this).prop("checked"));
    },
    
    changePeriodeOptions: function() {
        var selectedYear = $('.InvoiceList .timeperiode[gsname="year"]').val();
        
        $('.InvoiceList .timeperiode[gsname="month"] option').show();
        $('.InvoiceList .timeperiode[gsname="month"] option').removeClass('hiddenelement');
        
        if (selectedYear == storeCreatedYear) {
            $('.InvoiceList .timeperiode[gsname="month"] option').each(function() {
                var val = $(this).val();
                if (val < storeCreatedMonth) {
                    $(this).hide();
                    $(this).addClass('hiddenelement');
                }
            });
            
            var elementHidden = $('.InvoiceList .timeperiode[gsname="month"]').find(':selected').hasClass('hiddenelement');
            if (elementHidden) {
                var visibleItems = $('.InvoiceList .timeperiode[gsname="month"] option:not(.hiddenelement)');
                $(visibleItems[0]).attr('selected', 'selected');
            }
        }
    },

    sendInvoices: function() {
        var rows = $('.InvoiceList .boxcontent .orderrow');
        
        for (var i=0; i<rows.length; i++) {
            var row = rows[i];
            
            if ($(row).hasClass('done') || !$(row).hasClass('orderrow')) {
                continue;
            }
            
            $(row).addClass('done');
            var data = {
                orderid: $(row).attr('orderid'),
                mail: $(row).find('input').val()
            }
            
            var event = thundashop.Ajax.createEvent(null, "quickSendInvoice", row, data);
            event['synchron'] = true;
            
            $(row).find('.col1').prepend("<i class='spinner fa fa-spin fa-spinner'></i>");
            
            thundashop.Ajax.post(event, function(res) {
                $(row).find('.col1 .spinner').remove();
                if (res == "ok") {
                    $(row).find('.col1').prepend('<i class="fa fa-check"></i>');
                } else {
                    $(row).find('.col1').prepend('<i class="fa fa-warning" title="'+res+'"></i>');
                }
                app.InvoiceList.sendInvoices();
            })
            console.log(data);
            
            
            return;
        }
        
        alert("All invoices sent");
    }
};

app.InvoiceList.init();