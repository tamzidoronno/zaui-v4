app.SalesPointCashPoints = {
    init: function() {
        $(document).on('click', '.saveproductconfig', app.SalesPointCashPoints.saveConfig)
    },
    
    saveConfig: function() {
        var data = {
            id: $('.config_sales_point').attr('cashpointid'),
            productlistsids: []
        }
        
        $(".selectedposlist:checked").each(function() {
            data.productlistsids.push($(this).val());
        })
        
        thundashop.Ajax.simplePost($('.SalesPointCashPoints'), "saveConfig", data);
        $('.gsoverlay2.active').click();
    }
}

app.SalesPointCashPoints.init();