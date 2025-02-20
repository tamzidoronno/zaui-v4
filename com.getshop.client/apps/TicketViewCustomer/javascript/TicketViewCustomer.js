app.TicketViewCustomer = {
    init: function() {
        $(document).on('click', '.TicketViewCustomer .imagethumbnail', app.TicketViewCustomer.showFullSizeImage)
        $(document).on('click', '.TicketViewCustomer .imageviewer', app.TicketViewCustomer.hideFullSizeImage)
        $(document).on('click', '.TicketViewCustomer #fileuploadedsuccessfully', app.TicketViewCustomer.fileUploaded);
        $(document).on('click', '.TicketViewCustomer .savereplycontent', app.TicketViewCustomer.saveTicketContent);
        $(document).on('click', '.TicketViewCustomer .editocontentinarea', app.TicketViewCustomer.editocontentinarea);
        $(document).on('keyup', '.TicketViewCustomer .updateTimeOnTicket', app.TicketViewCustomer.updateTimeOnTicket);
        $(document).on('click', '.TicketViewCustomer .updateTimeOnTicket', app.TicketViewCustomer.markAll);
        $(document).on('click', '.TicketViewCustomer .saveticketheading', app.TicketViewCustomer.saveTicketHeading);
        $(document).on('keydown', '.TicketViewCustomer .heading', app.TicketViewCustomer.ticketHeading);
        
        document.onpaste = function(event) {
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
    
    ticketHeading : function(event) {
        if(event.keyCode === 13) {
            app.TicketViewCustomer.saveTicketHeading();
        }
    },
    
    saveTicketHeading : function() {
        var title = $('.ticketheading').text();
        var ticketid = $('.ticketheading').attr('ticketid');
        var event = thundashop.Ajax.createEvent('','saveTicketHeading',$('.TicketViewCustomer'), {
            "title" : title,
            "tickedId" : ticketid
        });
        thundashop.Ajax.post(event);
    },
    
    markAll : function() {
        $(this).select();
    },
    updateTimeOnTicket : function() {
        var event = thundashop.Ajax.createEvent('','updateTimeOnTicket', $(this), {
            "ticketId" : $(this).attr('ticketid'),
            "timetype" : $(this).attr('timetype'),
            "time" : $(this).attr('time'),
            "value" : $(this).val()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            
        });
    },
    
    startTime : function(timer) {
        var time = $('#startTime').val();
        var timeCounter = $('#startTime').val();
        if(typeof(counterTimer) !== "undefined") {
            clearTimeout(counterTimer);
        }
        counterTimer = setInterval(function() {
            timeCounter++;
            var secondsUsed = (timeCounter - time)+(60*timer);
            var minutes = parseInt(secondsUsed / 60);
            var seconds = secondsUsed-(minutes * 60);
            $('#secondsused').val(secondsUsed);
            $('.cleartexttime').html(minutes + " minutes, " + seconds + " spent on this case");
        }, "1000");
    },
    editocontentinarea : function() {
        var area = $(this).closest('.contentbox').find('.editcontentarea');
        $(this).closest('.contentbox').find('.savecontent').show();
        var random = parseInt((Math.random()* 1000));
        var id = 'fasfdasfase24234_'+random;
        $(this).closest('.contentbox').find('.savecontent .savereplycontent').attr('saveid',id);
        area.attr('id',id);
        ClassicEditor
            .create( document.querySelector( "#" + id ), {
                toolbar : ["undo", "redo", "bold", "italic", "blockQuote", "ckfinder", "imageTextAlternative", "imageUpload", "heading", "imageStyle:full", "imageStyle:side", "link", "numberedList", "bulletedList", "mediaEmbed", "insertTable", "tableColumn", "tableRow", "mergeTableCells"]
            })
            .then( editor => {
                myEditor = editor;
            })
            .catch( error => {
                console.error( error );
            } );
    },
    saveTicketContent : function () {
        var content = myEditor.getData();
        var event = thundashop.Ajax.createEvent('','updateResponseText',$(this), {
            "content" : content,
            "contentid" : $(this).closest('.contentbox').find('.contentid').val(),
            "ticketid" : $('#ticketid').val()
        });
        thundashop.Ajax.post(event);
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