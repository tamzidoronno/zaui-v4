app.PkkControl = {
    init: function() {
        $(document).on('change', '#pkk_search_license_plate', app.PkkControl.search);
        $(document).on('click', '.pkk_search_button.pkk_do_search', app.PkkControl.search);
        $(document).on('click', '.pkk_search_button.followup', app.PkkControl.followUp);
        $(document).on('click', '.PkkControl .removepkk', app.PkkControl.removePkk);
    },
    removePkk: function() {
        if (!confirm("Er du sikker på at du ønsker å fjerne denne oppføringen?")) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent(null, "removePkk", this, {
            id : $(this).attr('id')
        });
        thundashop.Ajax.post(event);
    },
    
    search: function() {
        var data = {
            licensePlate : $('#pkk_search_license_plate').val()
        };
        
        if (data.licensePlate.length != 7) {
            alert('Feil format på regnr, eksempel AB12345');
            return;
        }
        
        
        if(isNaN(+data.licensePlate.substring(2, 7))) {
            alert('Feil format på regnr, eksempel AB12345');
            return;
        }
        
        var event = thundashop.Ajax.createEvent(null, "searchForControl", this, data);
        event['synchron'] = 'true';
        thundashop.Ajax.post(event, function(response) {
            $('.PkkControl .responsePrinter').html(response);
        });
    },
    
    followUp: function() {
        var data = {
            brand: $('.PkkControl .brand').html(),
            licenseplate: $('.PkkControl .licenseplate').html(),
            vinenumber: $('.PkkControl .vinenumber').html(),
            registredyear: $('.PkkControl .registredyear').html(),
            lastcontrol: $('.PkkControl .lastcontrol').html(),
            nextcontrol: $('.PkkControl .nextcontrol').html(),
            pkk_name_of_user: $('.PkkControl #pkk_name_of_user').val(),
            pkk_cellphone_number: $('.PkkControl #pkk_cellphone_number').val(),
            pkk_email_address: $('.PkkControl #pkk_email_address').val(),
        }
        
        console.log(data);
        var event = thundashop.Ajax.createEvent(null, "signUp", this, data);
        event['synchron'] = 'true';
        thundashop.Ajax.post(event, function(response) {
            $('.PkkControl .responsePrinter').html("<div class='successmessage_pkk_controll'><i class='fa fa-check'></i>Takk, du vil høre fra oss når det nærmer seg!</div>");
        });
    }
}

app.PkkControl.init();