app.TicketViewCustomer = {
    init: function() {
        $(document).on('click', '.TicketViewCustomer .imagethumbnail', app.TicketViewCustomer.showFullSizeImage)
        $(document).on('click', '.TicketViewCustomer .imageviewer', app.TicketViewCustomer.hideFullSizeImage)
        $(document).on('click', '.TicketViewCustomer #fileuploadedsuccessfully', app.TicketViewCustomer.fileUploaded);
    },
    
    fileUploaded: function() {
        console.log("OK");
        thundashop.Ajax.simplePost(this, "fileUploaded", {
            ticketToken: $(this).attr('tickettoken'),
            uuid : $(this).attr('uuid')
        });
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