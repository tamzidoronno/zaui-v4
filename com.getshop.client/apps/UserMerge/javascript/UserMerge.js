app.UserMerge = {
    init: function() {
        $(document).on('click', '[app="UserMerge"] .add_user', app.UserMerge.addUserToMergeStack)
        $(document).on('click', '[app="UserMerge"] .merge_users', app.UserMerge.mergeUsers)
    },
    
    initUserDatalist: function() {
        $('.gs_datalist_input').each(function() {
            $(this).autocomplete({
                source: $("#" + $(this).attr('datalist')).children().map(function() {
                    return $(this).text();
                }).get()
            });
        });
    },
    
    addUserToMergeStack: function() {
        var user = $(".gs_datalist_input").val();
        var userId = $("option:contains('" + user + "')").val();
        
        $(".property_select").each(function() {            
            $(this).append("<option value='" + userId + "'>" + user + "</option>");
        });
        
        $(".gs_datalist_input").val("");
        $("datalist[id='users'] option[value='" + userId + "'").remove();
        
        app.UserMerge.initUserDatalist();
    },
    
    mergeUsers: function() {
        var data = {};
        var userIds = [];
        var properties = {};
        
        $(".property_select").each(function() {            
            properties[$(this).attr("id")] = $(this).val();
        });
        
        data["properties"] = properties;
        
        $(".property_select:first option").each(function() {
            userIds.push($(this).val());
        });
        
        data["userIds"] = userIds;
        
        var event = thundashop.Ajax.createEvent(null, "mergeUsers", "ns_1c48b89f_2279_40af_8bc1_470c8360fef8\\UserMerge", data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $(".property_select").each(function() {            
                $(this).empty();
            });
        });
    }
}

app.UserMerge.init();