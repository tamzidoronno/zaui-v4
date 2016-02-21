app.CreateEvent = {
    init: function() {
        $(document).on('click', '.CreateEvent .addMoreDays', app.CreateEvent.addMoreDays)
        $(document).on('click', '.CreateEvent .removeMoreDays', app.CreateEvent.removeMoreDays)
        $(document).on('click', '.CreateEvent .createbutton', app.CreateEvent.createEvent)
    },
    
    addFirstDay: function(days) {
        app.CreateEvent.addMoreDays(days);
    },
    
    addMoreDays: function(days) {        
        if (days.length) {
            for (var i in days) {
                var day = days[i];
                var cloned = $('.CreateEvent .listrow.template').clone();
                cloned.removeClass("template");
                var count = $('.CreateEvent .dayrow:not(.template)').length + 1;
                cloned.find('span').html(count);
                if (count > 1) {
                    cloned.find('.addMoreDays').remove();
                    cloned.find('.removeMoreDays').remove();
                }
                cloned.find('.startdate').val(day[0]);
                cloned.find('.enddate').val(day[1]);
                $(cloned).insertAfter($('.CreateEvent .dayrow').last());
                $(cloned).find('.daydate').datetimepicker({ dateFormat: "dd.mm.yy"});
            }
        } else {
            var cloned = $('.CreateEvent .listrow.template').clone();
            cloned.removeClass("template");
            var count = $('.CreateEvent .dayrow:not(.template)').length + 1;
            cloned.find('span').html(count);
            if (count > 1) {
                cloned.find('.addMoreDays').remove();
                cloned.find('.removeMoreDays').remove();
            }

            $(cloned).insertAfter($('.CreateEvent .dayrow').last());
            $(cloned).find('.daydate').datetimepicker({ dateFormat: "dd.mm.yy"});
        }
    },
    
    removeMoreDays: function() {
        if ($('.CreateEvent .dayrow:not(.template)').length < 2) {
            return;
        }
        
        $('.CreateEvent .dayrow').last().remove();
    },
    
    createEvent: function() {
        var days = [];
        var document = $(this).closest('.app');
        
        document.find('.listrow.dayrow:not(.template)').each(function() {
            var day = {
                start : $(this).find('.startdate').val(),
                end : $(this).find('.enddate').val()
            }
            
            days.push(day);
        });
        
        var data = {
            eventType : document.find('.selectedttype').val(),
            days : days,
            spots : document.find('.availablespots').val(),
            subLocationId : document.find('.selectedSubLocation').val(),
            entryId : $(this).attr('entryId'),
            selectedEventHelder : document.find('.selectedEventHelder').val()
        }
     
        thundashop.Ajax.simplePost(this, "createEvent", data);
    }
}

app.CreateEvent.init();