app.SupportNew = {
    data : {},
    
    init: function() {
        $(document).on('click', '.SupportNew .sendbutton', app.SupportNew.showQuestion);
        $(document).on('click', '.SupportNew .urgency', app.SupportNew.sendTicket);
        $(document).on('click', '.SupportNew .questionbox .closebutton', app.SupportNew.closeQuestion);
        
        document.onpaste = function(event){
            var items = (event.clipboardData || event.originalEvent.clipboardData).items;
            for (index in items) {
              var item = items[index];
              if (item.kind === 'file') {
                // adds the file to your dropzone instance
                gsDropZone.addFile(item.getAsFile())
              }
            }

        }
    },
 
    closeQuestion: function() {
        $('.SupportNew .questionbox').hide();
    },
    
    showQuestion: function() {
        var title = $('.SupportNew .subject').val();
        var content = $('.SupportNew .content').val();
        
        if (!title) {
            alert("Message body cant be empty");
            return;
        }
        
        app.SupportNew.data.title = title;
        app.SupportNew.data.content = content;
        app.SupportNew.data.attachemnts = [];
        
        var acceptedFiles = gsDropZone.getAcceptedFiles();
        for (var i in acceptedFiles){ 
            var acceptedFile = acceptedFiles[i];
            if (acceptedFile.status != "success") {
                alert("Please wait until all files has been completed uploading");
                return;
            }
            
            app.SupportNew.data.attachemnts.push(acceptedFile.upload.uuid);
        };
        
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
        
        app.SupportNew.data.emailtonotify = $('.SupportNew .emailtonotify').val();
        app.SupportNew.data.phoneprefix = $('.SupportNew .phoneprefix').val();
        app.SupportNew.data.phonenumber = $('.SupportNew .phonenumber').val();
        
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