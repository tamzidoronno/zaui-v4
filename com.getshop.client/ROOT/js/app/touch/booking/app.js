

Ext.application({
    name: 'Booking',
    appFolder: '/js/app/touch/booking',
    
    views: ['Main', 'Bookingpanel'],
    controllers: ['Main'],
    
    viewport : {
        autoMaximize : true
    },
    
    launch: function() {
        Ext.create('Booking.view.Main');
    }
});