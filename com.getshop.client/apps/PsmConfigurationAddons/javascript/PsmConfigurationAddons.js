app.PsmConfigurationAddons = {
    init : function() {
        $(document).on('click', '.PsmConfigurationAddons .doSaveSettings', app.PsmConfigurationAddons.saveSettings);
        $(document).on('click', '.PsmConfigurationAddons .onlyavailableforitem', app.PsmConfigurationAddons.selectOnlyForItem);
        $(document).on('click', '.PsmConfigurationAddons .readdaddon', app.PsmConfigurationAddons.readdaddon);
        $(document).on('click', '.PsmConfigurationAddons .loadExtendedProductInformation', app.PsmConfigurationAddons.loadExtendedProductInfo);
        $(document).on('click', '.PsmConfigurationAddons .saveExtendedInformation', app.PsmConfigurationAddons.saveExtendedInformation);
        $(document).on('click', '.PsmConfigurationAddons .addNewDateRange', app.PsmConfigurationAddons.addNewDateRange);
        $(document).on('click', '.PsmConfigurationAddons .removeRestrictionRange', app.PsmConfigurationAddons.removeRestrictionRange);
        $(document).on('click', '.PsmConfigurationAddons .changeproductname', app.PsmConfigurationAddons.doChangeName);
        $(document).on('click', '.PsmConfigurationAddons .editgroupaddon', app.PsmConfigurationAddons.editGroupAddon);
        $(document).on('click', '.PsmConfigurationAddons .saveproductsingroupaddon', app.PsmConfigurationAddons.saveProductsInGroupAddon);
    },
    saveProductsInGroupAddon: function() {
        var row = $(this).closest('tr');
        
        var productIds = [];
        
        $('.PsmConfigurationAddons .group_addon_item:checked').each(function() {
            productIds.push($(this).val());
        });
        
        var data = {
            productId : $(this).attr('mainproductid'),
            productIds : productIds
        };
        
        var event = thundashop.Ajax.createEvent(null, "saveGroupAddonProducts", this, data);
        thundashop.Ajax.post(event, function(res) {
            row.find('.extendedinformation').fadeOut();
        });
    },
    
    editGroupAddon: function() {
        var row = $(this).closest('tr');
        
        var event = thundashop.Ajax.createEvent('',"loadGroupProductInfo", row, {
            "productId" : row.attr('productid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            row.find('.extendedinformation').html(res);
            row.find('.extendedinformation').show();
        });
    },
    
    doChangeName : function() {
        var td = $(this).closest('td');
        var curname = td.find('.productnamecontainer').text();
        var newname = prompt("New name", curname);
        if(!newname) {
            return;
        }
        var productid = $(this).attr('productid');
        
        var event = thundashop.Ajax.createEvent('','changeProductName',$(this), {
            "productid" : productid,
            "name" : newname
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            td.find('.productnamecontainer').html(newname);
        });
    },
    removeRestrictionRange : function() {
        var row = $(this).closest('tr');
        var id = $(this).attr('rangeid');
        var data = {
            "rangeid" : id,
            "id" : row.attr('productid')
        }
        var event = thundashop.Ajax.createEvent('','removeValidTimeRange', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PsmConfigurationAddons.loadExtendedProductInfoFromRow(row);
        });

    },
    addNewDateRange : function() {
        var data = thundashop.framework.createGsArgs($(this).closest('.saveExtendedInformationForm'));
        var form = $(this).closest('.addDateRangeForm');
        var row = $(this).closest('tr');
        var data = thundashop.framework.createGsArgs(form);
        var event = thundashop.Ajax.createEvent('','addDateRangeForm', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            app.PsmConfigurationAddons.loadExtendedProductInfoFromRow(row);
        });
    },
    saveExtendedInformation : function() {
        var row = $(this).closest('tr');
        var data = thundashop.framework.createGsArgs(row.find('.saveExtendedInformationForm'));
        data['onlyForItems'] =  [];
        $('.selectedItem').each(function() {
            data['onlyForItems'].push(row.find('.loadExtendedProductInformation').attr('itemid'));
        });
        var event = thundashop.Ajax.createEvent('','saveExtendedProductInfo', row, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            row.find('.extendedinformation').fadeOut();
        });
    },
    loadExtendedProductInfo : function() {
        var row = $(this).closest('tr');
        app.PsmConfigurationAddons.loadExtendedProductInfoFromRow(row);
    },
    loadExtendedProductInfoFromRow : function(row) {
        var event = thundashop.Ajax.createEvent('',"loadExtendedProductInfo", row, {
            "productId" : row.attr('productid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            row.find('.extendedinformation').html(res);
            row.find('.extendedinformation').show();
        });
    },
    readdaddon : function() {
        var event = thundashop.Ajax.createEvent('','readAddons',$(this), {
            "productId" : $(this).closest('.settingsForProduct').attr('productid')
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert('Updated','Update completed');
        });
    },
    selectOnlyForItem : function() {
        if($(this).hasClass('selectedItem')) {
            $(this).removeClass('selectedItem');
        } else {
            $(this).addClass('selectedItem');
        }
    },
    saveSettings : function() {
        var data = {};
        var btn = $(this);
        $('.PsmConfigurationAddons .settingsForProduct').each(function() {
            var form = $(this);
            var onlyForItems = [];
            form.find('.onlyavailableforitem').each(function() {
                if($(this).hasClass('selectedItem')) {
                    onlyForItems.push($(this).attr('itemid'));
                }
            });
            var dataToSave = thundashop.framework.createGsArgs(form);
            dataToSave.onlyForItems = onlyForItems;
            console.log(dataToSave);
            data[form.attr('productid')] = dataToSave;
        });
        var toPost = {};
        toPost['products'] = data;
        var event = thundashop.Ajax.createEvent('','saveProductConfig',btn,toPost);
        thundashop.Ajax.post(event);
    }
};

app.PsmConfigurationAddons.init();