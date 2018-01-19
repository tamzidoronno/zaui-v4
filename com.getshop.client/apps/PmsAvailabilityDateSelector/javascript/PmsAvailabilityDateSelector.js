app.PmsAvailabilityDateSelector = {
    init: function() {
        $(document).on('click', '.PmsAvailabilityDateSelector .gs_headerbox i', app.PmsAvailabilityDateSelector.focusDate);
        $(document).on('change', '.PmsAvailabilityDateSelector .gs_headerbox input', app.PmsAvailabilityDateSelector.changed);
        $(document).on('change', '.PmsAvailabilityDateSelector .gs_headerbox select', app.PmsAvailabilityDateSelector.changed);
    },
    
    changed: function() {
        var data = {
            from: $('.PmsAvailabilityDateSelector #startdate').val(),
            to: $('.PmsAvailabilityDateSelector #enddate').val(),
            category: $('.PmsAvailabilityDateSelector select').val()
        };
        
        if (data.from && data.to) {
            thundashop.Ajax.simplePost($('.PmsAvailabilityDateSelector'), "update", data);
        }
    },
    
    focusDate: function() {
        $(this).closest('.gs_headerbox').find('input').focus();
    }
}

app.PmsAvailabilityDateSelector.init();