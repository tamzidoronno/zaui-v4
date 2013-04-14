function zeroFill( number, width )
{
    width -= number.toString().length;
    if ( width > 0 )
    {
        return new Array( width + (/\./.test( number ) ? 2 : 1) ).join( '0' ) + number;
    }
    return number + ""; // always return a string
}

Ext.define('Booking.view.Bookingpanel', {
    extend: 'Ext.form.Panel',
    xtype: 'booking',
    config: {
        items: [
        {
            xtype: 'textfield',
            name: 'name',
            label: 'Navn'
        },
        {
            xtype: 'textfield',
            name: 'cellphone',
            label: 'Mob.Tlf.'
        },
        {
            xtype: 'emailfield',
            name: 'email',
            label: 'E-post'
        },
        {
            xtype: 'emailfield',
            name: 'birthday',
            label: 'Org.nr.'
        },
        {
            xtype: 'textfield',
            name: 'company',
            label: 'Firma'
        },
        {
            xtype: 'selectfield',
            label: 'Kurs',
            name: 'eventid',
            options: Ext.Array.map(Config.entries, function(entry) { 
                var text = zeroFill(entry.day, 2)+"/"+zeroFill(entry.month,2)+"/"+entry.year+": "+entry.title;
                return {
                    text: text,  
                    value: entry.entryId
                }
            })
        },
        {
            xtype: 'panel',
            name: 'availability',
            style: 'border: solid 2px #BBB; margin-top: 10px; padding: 5px;',
            html: '',
            disabled: true
        }
        ]
    }
})