/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.QuestBackManagement = {
    init: function() {
        $(document).on('click', '.QuestBackManagement .showTemplatePage', app.QuestBackManagement.showTemplatePage)
    },
    
    showTemplatePage: function() {
        var event = thundashop.Ajax.createEvent(null, "showTemplatePage", this, {});
        thundashop.Ajax.postWithCallBack(event, function() {
            alert('done');
        });
    }
};

app.QuestBackManagement.init();