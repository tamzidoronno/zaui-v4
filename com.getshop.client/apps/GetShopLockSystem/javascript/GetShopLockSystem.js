app.GetShopLockSystem = {
    init: function() {
        $(document).on('click', '.GetShopLockSystem .saveGroup', app.GetShopLockSystem.saveGroup);
        $(document).on('click', '.GetShopLockSystem .changecodeonslot', app.GetShopLockSystem.changeCodeOnSlot);
    },
    
    changeCodeOnSlot: function() {
        var res = prompt('Code');
        if (res) {
            var data = {
                code : res,
                slotid: $(this).attr('slotid')
            };
            
            thundashop.Ajax.simplePost(this, "changeCode", data);
        }
    },
    
    saveGroup: function() {
        var servers = {};
        
        $('.servergroup').each(function() {
            var serverid = $(this).attr('serverid');
            
            var lockIds = [];
            $(this).find('.lockingroup:checkbox:checked').each(function() {
                lockIds.push($(this).val());
            });
            
            servers[serverid] = lockIds;
        });
        
        var data = {
            servers : servers
        };
        
        console.log(data);
        thundashop.Ajax.simplePost(this, "saveGroup", data);
    }
};

app.GetShopLockSystem.init();