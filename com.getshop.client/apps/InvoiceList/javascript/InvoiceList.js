app.InvoiceList = {
    init: function() {
        $(document).on('change', '.InvoiceList .timeperiode[gsname="year"]', app.InvoiceList.changePeriodeOptions);
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
};

app.InvoiceList.init();