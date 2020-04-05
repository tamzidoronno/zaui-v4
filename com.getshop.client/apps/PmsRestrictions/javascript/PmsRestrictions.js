app.PmsRestrictions = {
    init : function() {
        $(document).on('change', '.PmsRestrictions .restrictiontype', app.PmsRestrictions.changeBookingType);
        $(document).on('change', '.PmsRestrictions .repeat_type', app.PmsRestrictions.changeRepeatType);
        $(document).on('click', '.PmsRestrictions .checkallclosingtypes', app.PmsRestrictions.checkallclosingtypes);
    },
    checkallclosingtypes : function() {
        var checked = $(this).is(':checked');
        $('.closeforota').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    changeRepeatType : function() {
        var option = $(this).val();
        $('.repeatoption').hide();
        $('.repeatoption.repeat_'+option).show();
    },
    changeBookingType : function() {
        var val = $(this).val();
        
        $('.starteventtime').hide();
        $('.endeventtime').hide();
        $('.whenbooking').show();
        $('.whenstaying').hide();
        $('.mindays').hide();
        
        if(val === "4" || val === "6") {
            $('.starteventtime').show();
            $('.endeventtime').show();
        }
        
        if(val === "1" || val === "2" || val === "3") {
            $('.whenbooking').hide();
            $('.whenstaying').show();
        }
        
        if(val === "2" || val === "3" || val === "9") {
            $('.mindays').show();
        }
        if(val === "9") {
            $('.mindays').attr('placeholder','guests');
        }
        
    }
}

app.PmsRestrictions.init();