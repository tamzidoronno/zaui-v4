/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

app.QuestBack = {
    init: function() {
        $(document).on('click', '.QuestBack .quest_back_type_selected', app.QuestBack.selectType)
        $(document).on('click', '.QuestBack .edit_heading_button', app.QuestBack.showEditHeading)
        $(document).on('click', '.QuestBack .gs_button.add_option', app.QuestBack.addOption)
        $(document).on('click', '.QuestBack .admin_delete_option', app.QuestBack.deleteOption)
        $(document).on('click', '.QuestBack .admin_correct_option', app.QuestBack.markAsCorrectOption)
        $(document).on('click', '.QuestBack .answer_question', app.QuestBack.answerQuestion)
        $(document).on('click', '.QuestBack .go_to_next_question', app.QuestBack.goToNextQuestion)
        $(document).on('click', '.QuestBack .options_area_options div', app.QuestBack.toggleBoxIfPossible)
        $(document).on('change', '.QuestBack .admin_option_text', app.QuestBack.optionTextChanged)
        $(document).on('focusout', '.QuestBack .headingtext', app.QuestBack.saveHeading)
    },
    
    toggleBoxIfPossible: function() {
        $(this).find('input').attr('checked','checked');
    },
    
    goToNextQuestion: function() {
        var event = thundashop.Ajax.createEvent(null, "nextQuestion", this, {});
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "done") {
                thundashop.common.goToPage('questback_result_page');
            } else {
                thundashop.common.goToPage(result);
            }
        });
    },
    
    answerQuestion: function() {
        if (app.QuestBack.inProgress) {
            return;
        }
        
        app.QuestBack.inProgress = true;
        app.QuestBack.oldButtonText = $(this).html();
        $(this).html('<i class="fa fa-spinner fa-spin"></i> '+ __f('Checking')+"...");
        
        
        var selectedAnswers = $('.answer_option_box').map(function() {
            if ($(this).is(':checked')) {
                return $(this).val();
            }
        });
        
        var data = {
            answers : $.makeArray( selectedAnswers )
        }
        var me = this;
        var event = thundashop.Ajax.createEvent(null, "checkAnswer", this, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            app.QuestBack.inProgress = false;
            $(me).html(app.QuestBack.oldButtonText);
            
            if (result === "wrong") {
                thundashop.common.Alert(__f("Wrong answer"), __f("You have answered incorrectly, please check your answers and try again"), true, 2000);
            } else {
                if (result === "done") {
                    thundashop.common.goToPage('questback_result_page');
                } else {
                    thundashop.common.goToPage(result);
                }
            }
        });
    },
    
    optionTextChanged: function() {
        var data = {
            text : $(this).val(),
            optionId : $(this).parent().attr('optionid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "optionTextChanged", this, data);
        // Silent
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    
    deleteOption: function() {
        var data = {
            optionId : $(this).parent().attr('optionid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "deleteOption", this, data);
        thundashop.Ajax.post(event);
    },
    
    markAsCorrectOption: function() {
        var data = {
            optionId : $(this).parent().attr('optionid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "markAsCorrectOption", this, data);
        thundashop.Ajax.post(event);
    },
    
    addOption: function() {
        var event = thundashop.Ajax.createEvent(null, "addOption", this, {});
        thundashop.Ajax.post(event);        
    },
    
    showEditHeading: function() {
        $('.QuestBack .heading_text_showing').hide();
        $('.QuestBack .edit_text_header').show();
    },
    
    saveHeading: function() {
        var data = {
            headingText: $('.QuestBack .headingtext').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveHeaderText", $('.QuestBack .headingtext'), data);
        thundashop.Ajax.post(event);        
    },
    
    selectType: function() {
        var data = {
            type: $(this).attr('type')
        }

        if (data.type == 3 ) {
            alert('Not yet implemented');
            return;
        }
        var event = thundashop.Ajax.createEvent(null, "setType", this, data);
        thundashop.Ajax.post(event);
    }
}

app.QuestBack.init();

