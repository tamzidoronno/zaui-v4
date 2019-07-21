app.SupportNew = {
    data : {},
    
    init: function() {
        $(document).on('click', '.SupportNew .sendbutton', app.SupportNew.showQuestion);
        $(document).on('click', '.SupportNew .urgency', app.SupportNew.sendTicket);
        $(document).on('click', '.SupportNew .questionbox .closebutton', app.SupportNew.closeQuestion);
    },
    
    closeQuestion: function() {
        $('.SupportNew .questionbox').hide();
    },
    
    showQuestion: function() {
        var title = $('.SupportNew .subject').val();
        if (!myEditor) {
            alert("Something went wrong, please reload page and try again.");
            return;
        }
        
        var content = myEditor.getData();
        
        if (!title) {
            alert("Message body cant be empty");
            return;
        }
        
        app.SupportNew.data.title = title;
        app.SupportNew.data.content = content;
        
        $('.SupportNew .questionbox').show();
        $('.SupportNew .questionbox .askquestion').show();
        $('.SupportNew .questionbox .ticketcreated').hide();
        
        window.scrollTo(0,0);
    },
    
    sendTicket: function() {
        var email = $('.SupportNew .emailtonotify').val();
        
        if (!email) {
            alert("Please specify an email address");
        }
        
        app.SupportNew.data.email = email;
        app.SupportNew.data.urgency = $(this).attr('type');
        
        var event = thundashop.Ajax.createEvent(null, "createTicket", this, app.SupportNew.data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.SupportNew .questionbox .ticketcreated .ticketnumber').html(res);
            $('.SupportNew .questionbox .askquestion').hide();
            $('.SupportNew .questionbox .ticketcreated').show();
        });
    }
}

app.SupportNew.init();