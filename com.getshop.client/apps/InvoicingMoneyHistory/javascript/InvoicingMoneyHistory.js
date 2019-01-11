app.InvoicingMoneyHistory = {
    init: function() {
        $(document).on('change', '.InvoicingMoneyHistory .timeperiode[gsname="year"]', app.InvoicingMoneyHistory.changePeriodeOptions);
    },
    
    changePeriodeOptions: function() {
        var selectedYear = $('.InvoicingMoneyHistory .timeperiode[gsname="year"]').val();
        
        $('.InvoicingMoneyHistory .timeperiode[gsname="month"] option').show();
        $('.InvoicingMoneyHistory .timeperiode[gsname="month"] option').removeClass('hiddenelement');
        
        if (selectedYear == storeCreatedYear) {
            $('.InvoicingMoneyHistory .timeperiode[gsname="month"] option').each(function() {
                var val = $(this).val();
                if (val < storeCreatedMonth) {
                    $(this).hide();
                    $(this).addClass('hiddenelement');
                }
            });
            
            var elementHidden = $('.InvoicingMoneyHistory .timeperiode[gsname="month"]').find(':selected').hasClass('hiddenelement');
            if (elementHidden) {
                var visibleItems = $('.InvoicingMoneyHistory .timeperiode[gsname="month"] option:not(.hiddenelement)');
                $(visibleItems[0]).attr('selected', 'selected');
            }
        }
    },
};

app.InvoicingMoneyHistory.init();