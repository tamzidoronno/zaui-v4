app.EcommerceTaxes = {
    init: function() {
        $(document).on('click', '.EcommerceTaxes .addtaxgroup', app.EcommerceTaxes.showAddOverride);
    },
    
    showAddOverride: function() {
        var div = $(this).closest('.textfield').find('.addoverridetaxbox');
        
        if ($(div).is(':visible')) {
            $('.addoverridetaxbox').hide();
        } else {
            $('.addoverridetaxbox').hide();
            $(this).closest('.textfield').find('.addoverridetaxbox').show();    
        }
        
    }
}

app.EcommerceTaxes.init();