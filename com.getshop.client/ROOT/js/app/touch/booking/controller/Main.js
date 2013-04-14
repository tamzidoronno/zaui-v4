Ext.define('Booking.controller.Main', {
    extend: 'Ext.app.Controller',
    
    config: {
        refs: {
            schema:'booking'
        },
        
        control: {
            'button[action=signon]': {
                tap: 'SignOn'
            }, 
            
            'booking': {
                initialize: function(selected, form) {
                    var value = this.getSchema().down('selectfield').getValue();
                    this.setFreeSlots(value);
                }
            },
            
            'booking selectfield': {
                change: function(selected, form) {
                    var value = selected.getValue();
                    this.setFreeSlots(value);
                }
            }
        }
    },

    init: function() {
        
    },
    
    getEntry : function(entryId) {
        var entry = null;
        Ext.each(Config.entries, function(item) {
            if (item.entryId == entryId)
                entry = item;
        });
        
        return entry;
    },
    
    setFreeSlots: function(value) {
        var entry = this.getEntry(value);
        if (entry) {
            var attendees = (entry.attendees != null) ? entry.attendees.length : 0;
            var free = entry.maxAttendees - attendees;
            this.getSchema().down('[name=availability]').setHtml("Ledig plasser: " + free);    
        }
    },
    
    SignOn: function(button) {
        var blank = false;
        data = {};
        Ext.Array.each(this.getSchema().items.items, 
            function(item) {
                if (!item.getValue)
                    return;
                
                var value = item.getValue();
                if (!value || value == "") {
                    blank = true;
                }
                data[item.getName()] = value;
            }
        );
        
        if (blank) {
            Ext.Msg.alert('STOP!','Alle felt er påkrevd.');
            return;
        }
        
        var entry = this.getEntry(data.eventid);
        if (!entry) {
            return;
        }
        
        var attendees = (entry.attendees != null) ? entry.attendees.length : 0;
        var free = entry.maxAttendees - attendees;
        
        if (free <= 0) {
            Ext.Msg.alert('Fult!','Kurset er fullt, ta kontakt for mer informasjon.');
            return;
        }
        
        Ext.Ajax.request({
            url: 'booking.php?register=true',
            params: data,
            scope: this,
            success: function(response){
                Ext.Msg.alert('Takk', 'Kandidaten er nå påmeldt.', function() {
                    window.location.reload();
                });
            }
        });
    }
})