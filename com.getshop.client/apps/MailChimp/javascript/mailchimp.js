app.MailChimp = {
    init : function() {
        alert('test');
    },
    addEmail : function(event) {
        if(event.type === "keyup" && event.keyCode !== 13) {
            return;
        }
        
        $('.MailChimp .incorrectinput').hide();
        $('.MailChimp .emailAdded').hide();
        
        var email = $('.MailChimp .mailinputfield').val();
        if(email.indexOf("@") <= 0) {
            $('.MailChimp .incorrectinput').show();
            return;
        } 
        
        var data = {
            "email" : email
        }
        
        var event = thundashop.Ajax.createEvent('','addEmail',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            $('.MailChimp .mailinputfield').val('');
            $('.MailChimp .emailAdded').show();
        });
    },
    initEvents : function() {
        $(document).on('keyup', '.MailChimp .mailinputfield', app.MailChimp.addEmail);
        $(document).on('click', '.MailChimp .mailchimpaddbutton', app.MailChimp.addEmail);
    }
}

app.MailChimp.initEvents();