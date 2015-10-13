app.QuestBackUserOverview = {
    init: function() {
        $(document).on('click', '.QuestBackUserOverview .test', app.QuestBackUserOverview.startTest);
    },
    
    startTest: function() {
        var data = {
            testid : $(this).attr('testid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "startTest", this, data);
        thundashop.Ajax.postWithCallBack(event, function(pageId) {
            console.log(pageId);
            if (pageId === "done") {
                thundashop.common.goToPage('questback_result_page');
            } else {
                thundashop.common.goToPage(pageId);                
            }
        });
    }
}

app.QuestBackUserOverview.init();