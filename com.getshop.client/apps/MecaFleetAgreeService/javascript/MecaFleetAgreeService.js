app.MecaFleetAgreeService = {
    init: function() {
        $(document).on('click', '.MecaFleetAgreeService .sendquestion', app.MecaFleetAgreeService.requestDate);
        $(document).on('click', '.MecaFleetAgreeService .manualsetdate', app.MecaFleetAgreeService.setDate);
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
    },
    
    setDate: function() {
        var data = {
            type : $(this).attr('type'),
            carid : $(this).attr('carid'),
            date: $('.MecaFleetAgreeService #timepicker').val()
        }
        
        if (!data.date) {
            alert("Du må velge en dato");
            return;
        }
        var event = thundashop.Ajax.createEvent(null, "setDate", this, data);
        thundashop.Ajax.post(event, function(res) {
            thundashop.common.closeModal();
        });
    }
}

app.MecaFleetAgreeService.init();