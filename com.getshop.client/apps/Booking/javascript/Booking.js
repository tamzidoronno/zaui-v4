app.Booking = {
    init: function() {
        $(document).on('change', '.Booking .select_persons', this.personsChanged);
        $(document).on('change', '.Booking .children', this.personsChanged);
        $(document).on('click', '.Booking .calendar_go_to_payment', this.goToPayment);
    },
    
    goToPayment: function() {
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
        })
        console.log(data);
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