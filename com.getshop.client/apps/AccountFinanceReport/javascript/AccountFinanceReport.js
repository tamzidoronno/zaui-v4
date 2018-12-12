app.AccountFinanceReport = {
    init: function() {
        $(document).on('click', '.AccountFinanceReport .showresultbutton.deactivated', app.AccountFinanceReport.displayWarning);
        $(document).on('click', '.AccountFinanceReport .showresultbutton.closeperiode', app.AccountFinanceReport.closePeriode);
        $(document).on('click', '.AccountFinanceReport .showresultbutton.recalc', app.AccountFinanceReport.resetLastMonth);
        $(document).on('click', '.AccountFinanceReport .showresultbutton.vismaloginlink', app.AccountFinanceReport.doVismaLogin);
        $(document).on('change', '.AccountFinanceReport .timeperiode[gsname="year"]', app.AccountFinanceReport.changePeriodeOptions);
    },
    
    doVismaLogin: function() {
        var event = thundashop.Ajax.createEvent(null, "createVismaNETLoginLink", $('.AccountFinanceReport'), {});
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            var win = window.open(res, '_blank');
            var timer = setInterval(function() { 
                if(win.closed) {
                    clearInterval(timer);
                    $('html').html('reloading, please wait');
                    document.location = document.location;
                }
            }, 1000);

        });
    },
    
    resetLastMonth: function(password) {
        var data = {
            password: password
        }
        
        thundashop.Ajax.simplePost($('.AccountFinanceReport'), "resetLastMonth", data);
    },
    
    changePeriodeOptions: function() {
        var selectedYear = $('.AccountFinanceReport .timeperiode[gsname="year"]').val();
        
        $('.AccountFinanceReport .timeperiode[gsname="month"] option').show();
        $('.AccountFinanceReport .timeperiode[gsname="month"] option').removeClass('hiddenelement');
        
        if (selectedYear == storeCreatedYear) {
            $('.AccountFinanceReport .timeperiode[gsname="month"] option').each(function() {
                var val = $(this).val();
                if (val < storeCreatedMonth) {
                    $(this).hide();
                    $(this).addClass('hiddenelement');
                }
            });
            
            var elementHidden = $('.AccountFinanceReport .timeperiode[gsname="month"]').find(':selected').hasClass('hiddenelement');
            if (elementHidden) {
                var visibleItems = $('.AccountFinanceReport .timeperiode[gsname="month"] option:not(.hiddenelement)');
                $(visibleItems[0]).attr('selected', 'selected');
            }
        }
    },
    
    closePeriode: function() {
        var res = confirm('Are you sure you want to close this periode?');
        if (!res) {
            return;
        }
        
        thundashop.Ajax.simplePost(this, "closeCurrentPeriode", {});
    },
    
    displayWarning: function() {
        alert('You can not close a periode that is not finished, if you want to close days, please select hower the row and close it from there');
    }
};

app.AccountFinanceReport.init();