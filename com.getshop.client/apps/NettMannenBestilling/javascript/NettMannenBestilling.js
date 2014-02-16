app.NettMannenBestilling = {
    submitForm : function() {
        var form =  $(this).closest('.app').find('.datainputfield');
        var thanks = $(this).closest('.app').find('.thanks');
        var data = {
            name : form.find('.name').val(),
            phone : form.find('.phone').val(),
            email : form.find('.email').val(),
            type :  form.find('#whatisitabout').val()
        }
        
        if(!data.name || !data.phone || !data.email) {
            alert('Alle felt må fylles ut');
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','submitForm',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            form.fadeOut('slow', function() {
                thanks.fadeIn();
            });
        });
    },
    init : function() {
        $(document).on('click', '.NettMannenBestilling .submit_contact', app.NettMannenBestilling.submitForm);
    }
}
app.NettMannenBestilling.init();