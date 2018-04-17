app.SrsTables = {
    init: function() {
        $(document).on('click', '.SrsTables .showselectroom', app.SrsTables.showRooms)
        $(document).on('click', '.SrsTables .halfhourarea', app.SrsTables.showAddRestaurant)
        $(document).on('click', '.SrsTables .closecreation', app.SrsTables.closeCreation)
    },
    
    closeCreation: function() {
        $('.SrsTables .createnewtablesession').hide();
    },
    
    showAddRestaurant: function(event) {
        var hour = $(this).attr('hour');
        var minutes = $(this).attr('minutes');
        var date = $(this).closest('.daycontainer').attr('date');
        
        var pad = "00";
        hour = pad.substring(0, pad.length - hour.length) + hour;
        minutes = pad.substring(0, pad.length - minutes.length) + minutes;
        
        var nextHour = ""+(parseInt(hour) + 1);
        nextHour = pad.substring(0, pad.length - nextHour.length) + nextHour;
        
        var startDate = date+" "+hour+":"+minutes;
        var endDate = date+" "+nextHour+":"+minutes;
        
        $('.SrsTables .createnewtablesession').show();
        $('.SrsTables .createnewtablesession').find('[gsname="start"]').val(startDate);
        $('.SrsTables .createnewtablesession').find('[gsname="end"]').val(endDate);
        
        
        var calid = $(this).closest('.daycontainer').attr('calendarid');
        $('.SrsTables .createnewtablesession').find('[gsname="tableid"] option').removeAttr('selected', '');
        $('.SrsTables .createnewtablesession').find('[gsname="tableid"] option[value="'+calid+'"]').attr('selected', 'true');
        
        var top = $(this).offset().top - ($('.SrsTables .createnewtablesession').outerHeight(true) / 2);
        var left = $(this).offset().left - ($('.SrsTables .createnewtablesession').outerWidth(true) / 2);
        if (left < 0) {
            left = 0;
        }
        
        var bottom = $('.SrsTables .tables').outerHeight(true) + $('.SrsTables .tables').offset().top;
        var bottomOfBox = top + $('.SrsTables .createnewtablesession').outerHeight(true) + $('[area="body"]').offset().top;
        if (bottomOfBox > bottom) {
            top = bottom - $('.SrsTables .createnewtablesession').outerHeight(true);
        }

        $('.SrsTables .createnewtablesession').css('top', top+"px");
        $('.SrsTables .createnewtablesession').css('left', left+"px");
        
        
        
    },
    
    showRooms: function() {
        var div = $('.SrsTables .selectroom');
        if (div.is(':visible')) {
            div.hide();
        } else {
            div.show();
        }
    }
}

app.SrsTables.init();