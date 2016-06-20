app.ProMeisterUserTestList = {
    init: function() {
        $(document).on('click', '.ProMeisterUserTestList .test', app.ProMeisterUserTestList.startTest);
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

app.ProMeisterUserTestList.init();