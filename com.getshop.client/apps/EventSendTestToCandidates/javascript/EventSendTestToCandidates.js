app.EventSendTestToCandidates = {
    init: function() {
        $(document).on('change', '.EventSendTestToCandidates .selectQuestBackTest', app.EventSendTestToCandidates.changed)
    },
    
    changed: function() {
        
        var event = thundashop.Ajax.createEvent(null, "showCandidates", this, {
            testId : $(this).val()
        })
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.userInformationlist').html(res);
        })
    }
}

app.EventSendTestToCandidates.init();