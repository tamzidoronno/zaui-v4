Ext.define('Booking.view.Main', {
    extend: 'Ext.Container',
    
    config:  {
        fullscreen: true,
        layout: 'vbox',
        
        items: [
            {
                xtype: 'panel',
                items: [{
                    xtype: 'image',
                    src: '/displayImage.php?id='+Config.logoid+'&width=100&height=54&ignoreprecache=true',
                    height: 55,
                    style: 'margin-left: 10px; margin-top: 10px',
                    width: 100
                }],

                height: 55
            },
            {
                xtype: 'booking',
                flex: 1
            },
            {
                xtype: 'button',
                action: 'signon',
                text: 'Meld meg på',
                height: 50
            }
        ]
    }
})