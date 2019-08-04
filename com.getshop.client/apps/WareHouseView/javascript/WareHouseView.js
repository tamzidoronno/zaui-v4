app.WareHouseView = {
    init: function() {
        $(document).on('change', '.WareHouseView .locationrow input', app.WareHouseView.updateWareHouseLocationRow)
    },
    
    updateWareHouseLocationRow: function() {
        var row = $(this).closest('.locationrow');
        var data = {
            locationid : $(row).attr('locationid'),
            locationName : $(row).find('[gsname="locationName"]').val(),
            row : $(row).find('[gsname="row"]').val(),
            column : $(row).find('[gsname="column"]').val(),
            barcode : $(row).find('[gsname="barcode"]').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "updateLocationRow", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            
        }, null, true);
    }
}

app.WareHouseView.init();