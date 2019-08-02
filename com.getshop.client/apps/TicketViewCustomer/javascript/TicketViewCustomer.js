app.TicketViewCustomer = {
    init: function() {
        $(document).on('click', '.TicketViewCustomer .imagethumbnail', app.TicketViewCustomer.showFullSizeImage)
        $(document).on('click', '.TicketViewCustomer .imageviewer', app.TicketViewCustomer.hideFullSizeImage)
        $(document).on('click', '.TicketViewCustomer #fileuploadedsuccessfully', app.TicketViewCustomer.fileUploaded);
        
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
    
    fileUploaded: function() {
        var event = thundashop.Ajax.createEvent(null, "fileUploaded", this, {
            ticketToken: $(this).attr('tickettoken'),
            uuid : $(this).attr('uuid')
        });
        
        thundashop.Ajax.post(event, function(res) {
            $('.TicketViewCustomer .ticketinfo').html($("<div>"+res.content+"</div>").find('.ticketinfo').html());
        }, null, true);
    },
    
    hideFullSizeImage: function() {
        $(this).hide();
    },
    
    showFullSizeImage: function() {
        var data = $(this).attr('src');
        $('.TicketViewCustomer .imageviewer img').attr('src', data);
        $('.TicketViewCustomer .imageviewer').show();
    }
};

app.TicketViewCustomer.init();