app.CertegoPreviouseOrders = {
    init: function() {
        $(document).on('click', '.CertegoPreviouseOrders .header', app.CertegoPreviouseOrders.toggleOrderLine)
        $(document).on('keyup', '.CertegoPreviouseOrders .filterlist', app.CertegoPreviouseOrders.filterList);
    },
    
    filterList: function() {
        var searchval = $(this).val();
        $('.CertegoPreviouseOrders .order_row').each(function() {
            var string = $(this).html();
            
            if (string.toLowerCase().indexOf(searchval) != -1 || !searchval){
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    },
    
    toggleOrderLine: function() {
        var container = $(this).closest('.order_row').find('.order_content');
        if (container.is(':visible')) {
            container.hide();
        } else {
            container.show();
        }
        
    }
}

app.CertegoPreviouseOrders.init();