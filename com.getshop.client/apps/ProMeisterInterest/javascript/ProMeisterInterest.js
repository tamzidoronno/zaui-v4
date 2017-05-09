app.ProMeisterInterest = {
    init: function() {
        $(document).on('click', '.ProMeisterInterest .toggleSelectedUserId', app.ProMeisterInterest.toggleUserId);
    },
    
    toggleUserId: function() {
        var data = {
            userid : $(this).val(),
            locationid: $(this).attr('location'),
            checked: $(this).is(':checked')
        }
        
        var event = thundashop.Ajax.createEvent(null, "toggleCheckBox", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.ProMeisterInterest .usersselected').html(res);
        }, [], true, true);
    }
}

app.ProMeisterInterest.init();