app.GetShopCompanySettings = {
    init: function() {
        $(document).on('click', '.GetShopCompanySettings .rightmenu .taboperation', app.GetShopCompanySettings.selectMenu);
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