app.Booking = {
    init: function() {
        $(document).on('change', '.Booking .select_persons', this.personsChanged);
        $(document).on('change', '.Booking .children', this.personsChanged);
        $(document).on('click', '.removeuserfromevent', this.removeUserFromEvent);
        $(document).on('click', '.Booking .calendar_go_to_payment', this.goToPayment);
    },
    
    removeUserFromEvent: function() {
        var data = {
            entryId: $(this).attr('entryId'),
            userId: $(this).attr('userId')
        };
        
        var confirmRes = confirm(__f('Are you sure you want to remove this user?'));
        if (!confirmRes)
            return;
        
        var me = this;
        var event = thundashop.Ajax.createEvent(null, "removeUserFromEvent", this, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $(me).closest('tr').remove();
        });
    },
    
    goToPayment: function() {
        var data = {
            entryId: $('#entry_id_to_order').val(),
            persons: []
        };
        
        var stop = false;
        $('.Booking .personrow').each(function() {
            if ($(this).find('.name').is(':visible') && !$(this).find('.name').val()) {
                alert('Alle feltene er påkrevd');
                stop = true;
            }
            if ($(this).find('.email').is(':visible') && !$(this).find('.email').val()) {
                alert('Alle feltene er påkrevd');
                stop = true;
            }
            if ($(this).find('.cellphone').is(':visible') && !$(this).find('.cellphone').val()) {
                alert('Alle feltene er påkrevd');
                stop = true;
            }
            
            if ($(this).is(':visible')) {
                var person = {
                    name : $(this).find('.name').val(),
                    email : $(this).find('.email').val(),
                    cellphone : $(this).find('.cellphone').val(),
                    children : $(this).find('.children').is(':checked')
                }
                
                data.persons.push(person);
            }
        });
        
        if (stop) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent(null, 'completeOrder', $('.Booking'), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.Booking .order_summary_booking').html(result);
        })
        
    },
    
    personsChanged: function() {
        var select = $('.Booking .select_persons');
        $('.Booking .personrow').hide();
        
        for (var i=0; i<=select.val(); i++) {
            $('.Booking .personrow[person="'+i+'"]').show();
        }

        app.Booking.updateSummary();
    },
    
    updateSummary: function() {
        var data = {
            entryId: $('#entry_id_to_order').val(),
            persons: []
        };
        
        $('.Booking .personrow').each(function() {
            if ($(this).is(':visible')) {
                var person = {
                    name : $(this).find('.name').val(),
                    email : $(this).find('.email').val(),
                    cellphone : $(this).find('.cellphone').val(),
                    children : $(this).find('.children').is(':checked')
                }
                
                data.persons.push(person);
            }
        });
        
        var event = thundashop.Ajax.createEvent(null, 'getSummary', $('.Booking'), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.Booking .order_summary_booking').html(result);
        });
    },
    
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: __f("Settings"),
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-gears",
                    iconsize : "30",
                    title: __f("Settings"),
                    click: app.Booking.showSettings
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
    
    
    
};

app.Booking.init();