app.ApacAccessGroups = {
    init: function() {
        $(document).on('click', '.ApacAccessGroups .saveGroup', app.ApacAccessGroups.saveGroup);
        $(document).on('click', '.ApacAccessGroups .changePinCode', app.ApacAccessGroups.changePinCode);
    },
    
    changePinCode: function() {
        var newCode = prompt('New code');
        
        var data = {
            code : newCode,
            slotid : $(this).attr('slotid')
        };
        
        thundashop.Ajax.simplePost(this, 'changeSlotCode', data);
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
        
        thundashop.Ajax.simplePost(this, "saveGroup", data);
    }
}

app.ApacAccessGroups.init();