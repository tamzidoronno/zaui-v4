app.SalesPointCashPoints = {
    init: function() {
        $(document).on('click', '.saveproductconfig', app.SalesPointCashPoints.saveConfig)
        $(document).on('click', '.activateExternal', app.SalesPointCashPoints.activateExternal)
    },
    
    activateExternal: function() {
        var data = {
            id: $('.config_sales_point').attr('cashpointid')
        }
        
        thundashop.Ajax.simplePost($('.SalesPointCashPoints'), "activateExternal", data);
    },
    
    saveConfig: function() {
        var data = {
            id: $('.config_sales_point').attr('cashpointid'),
            productlistsids: []
        }
        
        $(".selectedposlist:checked").each(function() {
            data.productlistsids.push($(this).val());
        });
        
        data.receiptprinter = $('.config_sales_point select[gsname="receiptprinter"]').val();
        data.kitchenprinter = $('.config_sales_point select[gsname="kitchenprinter"]').val();
        data.departmentId = $('.config_sales_point select[gsname="departmentid"]').val();
        data.warehouseid = $('.config_sales_point select[gsname="warehouseid"]').val();
        data.isMaster = $('.config_sales_point input[gsname="isMaster"]').is(':checked');
        data.ignoreHotelErrors = $('.config_sales_point input[gsname="ignoreHotelErrors"]').is(':checked');
        data.cashPointName = $('.config_sales_point input[gsname="cashPointName"]').val();
        
        thundashop.Ajax.simplePost($('.SalesPointCashPoints'), "saveConfig", data);
        $('.gsoverlay2.active').click();
    }
}

app.SalesPointCashPoints.init();