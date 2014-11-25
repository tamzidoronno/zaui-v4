app.QuestBack = {
    
    init: function() {
       $(document).on('click', '.QuestBack .save_questions', this.saveQuestion); 
       $(document).on('click', '.QuestBack .deletequestions', this.deleteQuestion); 
       $(document).on('click', '.QuestBack .next_question_button', this.answerQuestion); 
    },
    answerQuestion: function() {
        var data = {
            id : $(this).attr('qustionid'),
            answer :  $('.QuestBack .question input[name=selected_question]:checked').val()
        }
        
        if (!data.answer) {
            alert(__w('Please select a option'));
            return
        }
        
        var event = thundashop.Ajax.createEvent(null, "answerQuestion", this, data);
        thundashop.Ajax.post(event);
   },
    deleteQuestion: function() {
       var data = {
           id : $(this).attr('questionid')
       } 
       var event = thundashop.Ajax.createEvent(null, "removeQuestion", this, data);
       thundashop.Ajax.post(event);
    },
    saveQuestion: function() {
       var data = {
           question : $('.QuestBack #question').val(),
           ans1 : $('.QuestBack #select_1 input').val(),
           ans2 : $('.QuestBack #select_2 input').val(),
           ans3 : $('.QuestBack #select_3 input').val(),
           ans4 : $('.QuestBack #select_4 input').val(),
           ans5 : $('.QuestBack #select_5 input').val(),
           ans6 : $('.QuestBack #select_6 input').val()
       }
       
       var event = thundashop.Ajax.createEvent(null, "saveQuestion", this, data);
       thundashop.Ajax.post(event);
    },
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-trash-o",
                    iconsize : "30",
                    title: "Remove form",
                    click: app.Contact.remove,
                    extraArgs: {}
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}
 
 
app.QuestBack.init();
