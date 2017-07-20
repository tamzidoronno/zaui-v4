app.BookingSelfManagment = {
    init: function() {
        $(document).on('click', '.BookingSelfManagment .dayline', app.BookingSelfManagment.toggleLine);
        $(document).on('click', '.BookingSelfManagment .saveAndReturnFromRoom', app.BookingSelfManagment.saveAndReturnFromRoom);
    },
    
    saveAndReturnFromRoom: function() {
        var data = {};
        data.addons = app.BookingSelfManagment.getAddons();
        thundashop.Ajax.simplePost(this, "saveAndReturnFromRoom", data);
    },
    
    getAddons: function() {
        var addons = [];
        $('.addon').each(function() {
            var addon = {};
            addon.id = $(this).attr('addonId');
            addon.productId = $(this).attr('productid');
            addon.days = [];
            $(this).find('.toggleswitch').each(function() {
                var day = {};
                day.date = $(this).attr('date');
                day.state = $(this).find('i').hasClass('fa-toggle-on');
                addon.days.push(day);
            });
            addons.push(addon);
        })
        return addons;
    },
    
    toggleLine: function() {
        var toggleI = $(this).find('.toggleswitch i');
        if (toggleI.hasClass('fa-toggle-on')) {
            toggleI.removeClass('fa-toggle-on');
            toggleI.addClass('fa-toggle-off');
        } else {
            toggleI.removeClass('fa-toggle-off');
            toggleI.addClass('fa-toggle-on');
        }
    }
};

app.BookingSelfManagment.init();