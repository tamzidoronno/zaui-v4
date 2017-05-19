app.QuestBackResultOverview = {
    init: function(){
        $(document).on('click','.QuestBackResultOverview .subanswer.withReply', app.QuestBackResultOverview.openReplyField);
        $(document).on('click','.QuestBackResultOverview .subanswerArea .fa-minus', app.QuestBackResultOverview.closeReplyField);
        $(document).on('click','.QuestBackResultOverview .replyfield', app.QuestBackResultOverview.preventDefault); 
        $(document).on('click','.QuestBackResultOverview .replybutton', app.QuestBackResultOverview.sendReply);
        $(document).on('click','.QuestBackResultOverview .archivebutton', app.QuestBackResultOverview.archive);
        $(document).on('keyup','.QuestBackResultOverview .replyfield textarea', app.QuestBackResultOverview.checkAreaforContent);
        $(document).on('click','.QuestBackResultOverview .replyArchive .fa-plus', app.QuestBackResultOverview.expandArchive);
    },
    archive: function(){
        /*Nesten ferdig*/
        var answerId = $(this).parent().find('.subanswerId').val();
        var box = $(this).parents('.subanswer');
        var data = {
            "answerId" : answerId
        };
        var event = thundashop.Ajax.createEvent('','archiveReply',$(this),data);
            thundashop.Ajax.postWithCallBack(event, function() {
                box.slideUp("slow", function(){$(this).remove();});
                $('.replyfield').slideUp("slow");
                $('.fa-minus').slideUp("slow");
                box.hide();
            });
    },
    expandArchive: function(){
        $(this).parents().find('.archivefield').slideToggle("slow");
    },
    checkAreaforContent: function(){
        var value=$.trim($(this).val());
        if(value.length>0){
            $(this).parent().find('.replybutton').text('Reply').addClass('replyClass');
        }
        else{
            $(this).parent().find('.replybutton').text('Archive').removeClass('replyClass');
        }
    },
    sendReply: function(){
        var email = $(this).parent().find('.subanswerEmail').val();
        var answerId = $(this).parent().find('.subanswerId').val();
        var message = $(this).parent().find('textarea').val();
        var box = $(this).parents('.subanswer');
        var data = {
            "email" : email,
            "answerId" : answerId,
            "message" : message
        };
        if($(this).parent().find('.replybutton').hasClass('replyClass')){
            var event = thundashop.Ajax.createEvent('','sendReply',$(this),data);
            thundashop.Ajax.postWithCallBack(event, function() {
                box.slideUp("slow", function(){$(this).remove();});
                $('.replyfield').slideUp("slow");
                $('.fa-minus').slideUp("slow");
                box.hide();
            });
        }else{
            var event = thundashop.Ajax.createEvent('','archiveReply',$(this),data);
            thundashop.Ajax.postWithCallBack(event, function() {
                box.slideUp("slow", function(){$(this).remove();});
                $('.replyfield').slideUp("slow");
                $('.fa-minus').slideUp("slow");
                box.hide();
            });
        } 
        
    },
    preventDefault :function(e){
        e.preventDefault();
        e.stopPropagation();
    },
    closeReplyField: function(e){
        $('.replyfield').slideUp("slow");
        $('.fa-minus').slideUp("slow");
        $('.subanswer').removeClass('answerActive');
        e.preventDefault();
        e.stopPropagation();
    },
    openReplyField: function(){
        $('.replyfield').slideUp("slow");
        $('.fa-minus').slideUp("slow");
        $('.subanswer').removeClass('answerActive');
        
        var field =  $(this);
        field.find('.replyfield').slideDown("slow");
        field.find('.fa-minus').slideDown("slow");
        field.find('textarea').focus();
        field.addClass('answerActive');
    }
}
app.QuestBackResultOverview.init();