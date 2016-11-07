app.MecaFleetAgreeService = {
    init: function() {
        $(document).on('click', '.MecaFleetAgreeService .sendquestion', app.MecaFleetAgreeService.requestDate);
    },
    
    requestDate: function() {
        
        
        var data = {
            type : $(this).attr('type'),
            carid : $(this).attr('carid'),
            date: $('.MecaFleetAgreeService #timepicker').val()
        }
        
        if (!data.date) {
            alert("Du må velge en dato");
            return;
        }
        var event = thundashop.Ajax.createEvent(null, "requestDate", this, data);
        
        thundashop.Ajax.post(event, function(res) {
            alert("Din forespørsel er nå sendt");
            thundashop.common.closeModal();
        });
    }
}

app.MecaFleetAgreeService.init();