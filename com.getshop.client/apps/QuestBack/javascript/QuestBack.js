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
        $(document).on('change', '.QuestBack .admin_option_text', app.QuestBack.optionTextChanged)
        $(document).on('focusout', '.QuestBack .headingtext', app.QuestBack.saveHeading)
    },
    
    optionTextChanged: function() {
        var data = {
            text : $(this).val(),
            optionId : $(this).parent().attr('optionid')
        }
        
        var event = thundashop.Ajax.createEvent(null, "optionTextChanged", this, data);
        thundashop.Ajax.post(event);
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
        
        if (data.type == 2 || data.type == 3 ) {
            alert('Not yet implemented');
            return;
        }
        var event = thundashop.Ajax.createEvent(null, "setType", this, data);
        thundashop.Ajax.post(event);
    }
}

app.QuestBack.init();

