app.GetShopSales = {
    init : function() {
        $(document).on('keyup', '.GetShopSales .editcell', app.GetShopSales.markModified);
        $(document).on('change', '.GetShopSales .gsniceselect1', app.GetShopSales.markModified);
        $(document).on('click', '.GetShopSales .savetablebutton', app.GetShopSales.saveRow);
    },
    markModified : function() {
        $(this).addClass('modified');
        $('.savetablebutton').fadeIn();
    },
    saveRow : function() {
        $('.leadrow').each(function() {
            var data = {};
            if($(this).find('.modified').length > 0) {
                $(this).find('.modified').removeClass('modified');
                $(this).find('[gsname]').each(function() {
                    if($(this).is('select')) {
                        data[$(this).attr('gsname')] = $(this).val();
                    } else if($(this).is('input')) {
                        data[$(this).attr('gsname')] = $(this).val();
                    } else {
                        data[$(this).attr('gsname')] = $(this).text();
                    }
                });
                data.id = $(this).attr('leadid');
                
                var event = thundashop.Ajax.createEvent('','saveLead',$(this),data);
                thundashop.Ajax.postWithCallBack(event, function(res) {
                    
                });
            }
        });
        $(this).fadeOut();
    }
}

app.GetShopSales.init();