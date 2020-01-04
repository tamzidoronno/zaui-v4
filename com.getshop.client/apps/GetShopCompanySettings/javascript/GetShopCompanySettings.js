app.GetShopCompanySettings = {
    init: function() {
        $(document).on('click', '.GetShopCompanySettings .taboperation', app.GetShopCompanySettings.selectMenu);
        $(document).on('keyup', '.GetShopCompanySettings .searchForCustomer', app.GetShopCompanySettings.searchForCustomer);
    },

    searchForCustomer: function() {
        var text = $(this).val();
        var systemId = $(this).closest('.workarea').find('input[gsname="systemid"]').val();
        var data = {
            value : text,
            systemId : systemId
        };
        
        var event = thundashop.Ajax.createEvent(null, "searchForCustomerToMoveSystemTo", $(this), data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.movetoresult').html(res);
        })
    },
    
    selectMenu: function() {
        var data = {
            tab : $(this).attr('tab'),
            value : $(this).attr('value')
        };

        thundashop.Ajax.simplePost(this, 'setTab', data);
    }

}

app.GetShopCompanySettings.init();