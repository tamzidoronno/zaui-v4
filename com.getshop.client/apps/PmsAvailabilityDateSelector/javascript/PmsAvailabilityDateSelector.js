app.PmsAvailabilityDateSelector = {
    init: function() {
        $(document).on('click', '.PmsAvailabilityDateSelector .gs_headerbox i', app.PmsAvailabilityDateSelector.focusDate);
        $(document).on('click', '.PmsAvailabilityDateSelector .updateavailability', app.PmsAvailabilityDateSelector.changed);
    },
    
    changed: function() {
        var cats = [];
        $('.typesinput').each(function() {
            if($(this).is(':checked')) {
                cats.push($(this).attr('typeid'));
            }
        });
        var data = {
            from: $('.PmsAvailabilityDateSelector #startdate').val(),
            to: $('.PmsAvailabilityDateSelector #enddate').val(),
            sorting: $('.PmsAvailabilityDateSelector #sorting').val(),
            categories : cats
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