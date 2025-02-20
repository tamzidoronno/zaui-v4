/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

    
    
app.QuestBackManagement = {
    init: function() {
        $(document).on('click', '.QuestBackManagement .showTemplatePage', app.QuestBackManagement.showTemplatePage);
        $(document).on('click', '.QuestBackManagement .menuelement', app.QuestBackManagement.changeMenu);
        $(document).on('click', '.QuestBackManagement .delete_test_button', app.QuestBackManagement.deleteTest);
        $(document).on('click', '.QuestBackManagement .modify_questionbase_button', app.QuestBackManagement.modifyTest);
        $(document).on('click', '.QuestBackManagement .saveTestSettings', app.QuestBackManagement.saveTestSettings);
        $(document).on('click', '.QuestBackManagement .assignTestsToUser', app.QuestBackManagement.assignTestsToUser);
        $(document).on('click', '.QuestBackManagement .createTest', app.QuestBackManagement.createTest);
        $(document).on('change', '.QuestBackManagement #testToSeeResultFor', app.QuestBackManagement.testToSeeResultFor);
        $(document).on('change', '.QuestBackManagement #usermanagementInput', app.QuestBackManagement.filterUsers);
        $(document).on('click', '.QuestBackManagement .dropdown dt a', app.QuestBackManagement.slideToggle);
        $(document).on('click', '.QuestBackManagement .dropdown dd ul li a', app.QuestBackManagement.somethinghide);
        $(document).on('click', '.QuestBackManagement .multiSelect input[type="checkbox"]', app.QuestBackManagement.something);
        $(document).on('change', '.QuestBackManagement .user_to_send', app.QuestBackManagement.toggleUserSelected);
        
        $(document).bind('click', function(e) {
          var $clicked = $(e.target);
          if (!$clicked.parents().hasClass("dropdown")) $(".dropdown dd ul").hide();
        });
    
    },
    
    toggleUserSelected: function() {
        var data = {
            userid : $(this).val(),
            checked: $(this).is(':checked')
        }
        
        var event = thundashop.Ajax.createEvent(null, "toggleCheckBox", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.QuestBackManagement .summary').html(res);
        }, [], true, true);
    },
    
    slideToggle : function() {
        $(".QuestBackManagement .dropdown dd ul").slideToggle('fast');
    },
    somethinghide : function(){
        $(".QuestBackManagement .dropdown dd ul").hide();
    },
    something : function() {
        //var exisitngtitle = $('.multiSel').text();
        var title = $(this).closest('.mutliSelect').find('input[type="checkbox"]').val(),
        title = $(this).val() + ",";

        if ($(this).is(':checked')){
            var html = '<span title="' + title +'">' + title + " " + '</span>';
            $('.multiSel').append(html);
            $(".hida").hide();
        } else {
            $('span[title="' + title + '"]').remove();
            var ret = $(".hida");
            $('.dropdown dt a').append(ret);
        }
    },
    getSelectedValue: function(id){
        return $("#" + id).find("dt a span.value").html();
    },
    
    
    filterUsers: function()  {
        var data = {
            searchWord : $(this).val()
        }
        var event = thundashop.Ajax.createEvent(null, "searchForUsers", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $('.QuestBackManagement .testUserSearchResult').html(res);
        },[], true);
    },
    
    testToSeeResultFor: function() {
        var data = {
            testId : $('#testToSeeResultFor').val(),
        }
        
        var event = thundashop.Ajax.createEvent(null, "showTestResults", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.test_result_view').html(result);
        });
    },
    
    assignTestsToUser: function() {
        var ids = [];
        $('.usertest').each(function() {
            if($(this).is(':checked')) {
                ids.push($(this).attr('testid'));
            }
        });
        var data = {
            testIds : ids
        }
        
        data.usersIds = $('.QuestBackManagement .user_to_send:checked').map(function () {
            return this.value;
        }).get()
        
        var event = thundashop.Ajax.createEvent(null, "assignTestToUsers", this, data);
        thundashop.Ajax.post(event, function() {
            alert('Test has been sent');
        });
    },
    
    saveTestSettings: function() {
        var data = {
            testid : $(this).attr('testid'),
            testtype : $('.QuestBackManagement #testtype').val(),
            forceCorrectAnswer : $('.QuestBackManagement .force_correct_answers').is(':checked'),
            name : $('.QuestBackManagement .testsettings .test_name').val(),
            
            redFrom : $('.QuestBackManagement .testsettings #red_from').val(),
            redTo : $('.QuestBackManagement .testsettings #red_to').val(),
            redText : $('.QuestBackManagement .testsettings #red_text').val(),
            
            greenFrom : $('.QuestBackManagement .testsettings #green_from').val(),
            greenTo : $('.QuestBackManagement .testsettings #green_to').val(),
            greenText : $('.QuestBackManagement .testsettings #green_text').val(),
            
            yellowFrom : $('.QuestBackManagement .testsettings #yellow_from').val(),
            yellowTo : $('.QuestBackManagement .testsettings #yellow_to').val(),
            yellowText : $('.QuestBackManagement .testsettings #yellow_text').val(),
            
        };
        
        
        var nodeIds = [];
        $('.test_qustions_added').each(function() {
            if ($(this).is(':checked')) {
                nodeIds.push($(this).val());
            }
        });
        
        data.nodeIds = nodeIds;
        
        var event = thundashop.Ajax.createEvent(null, "saveTestSettings", this, data);
        thundashop.Ajax.post(event, function() {
            thundashop.common.unmask();
        });
    },
    
    modifyTest: function() {
        var data = { testid : $(this).closest('tr').attr('testid') };
        var event = thundashop.Ajax.createEvent(null, "showTestSettings", this, data);
        thundashop.common.showInformationBox(event, "Edit test");
    },
    
    deleteTest: function() {
        var ret = confirm("Are you sure you want to delete this test?");
        
        if (ret) {
            var data = { testid : $(this).closest('tr').attr('testid') };
            var event = thundashop.Ajax.createEvent(null, "deleteTest", this, data);
            thundashop.Ajax.post(event);
        }
    },
    
    createTest: function() {
        var data = { testname : $('.QuestBackManagement .test_field_name').val() };
        var event = thundashop.Ajax.createEvent(null, "createTest", this, data);
        thundashop.Ajax.post(event);
    },
    
    changeMenu: function() {
        var toshow = $(this).attr('toshow');
        $('.QuestBackManagement .menuelement.active').removeClass('active');
        $(this).addClass('active');
        $('.QuestBackManagement .parts .part').hide();
        $('.QuestBackManagement .parts .part[toshow="'+toshow+'"]').show();
        var data = {
            toshow : $(this).attr('toshow')
        }
        
        var event = thundashop.Ajax.createEvent(null, "setToShow", this, data);
        thundashop.Ajax.postWithCallBack(event, function() {}, true);
    },
    
    showTemplatePage: function() {
        var event = thundashop.Ajax.createEvent(null, "showTemplatePage", this, {});
        thundashop.Ajax.postWithCallBack(event, function() {
            alert('done');
        });
    },
    
    checkLevel: function(obj) {
        return !obj.reference.parent().hasClass('qb_question');
    },
    
    saveTree: function(tree) {
        var listInJson = JSON.stringify($(tree).jstree().get_json());
        var data = {
            list : listInJson
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveList", tree, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            
        });
    },
    
    guid: function () {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
    },
    
    navigateToQuestion: function(e, data) {
        var data = {
            entryId : $(e.target).closest('.qb_question').attr('nodeid')
        };

        var event = thundashop.Ajax.createEvent(null, "getPageIdForQuestion", $(e.target), data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            thundashop.common.goToPage(result);
        })
    },
    
    createTree: function(idOfTree) {
        $('#'+idOfTree).jstree({
        "core" : {
            "check_callback" : true            
        },
        "contextmenu": {  
            "items": function($node) {
                var tree = $('#'+idOfTree).jstree(true);
                return {
                    "Create": {
                        "separator_before": false,
                        "separator_after": false,
                        "label": "Create",
                        "action": function (obj) { 
                            var validLevel = app.QuestBackManagement.checkLevel(obj);
                            if (!validLevel) {
                                alert(__f('You can not create a question under a question'));
                                return;
                            }
                            
                            $node = tree.create_node($node);
                            tree.edit($node);
                        }
                    },
                    "Rename": {
                        "separator_before": false,
                        "separator_after": false,
                        "label": "Rename",
                        "action": function (obj) { 
                            if (obj.reference.parent().attr('aria-level') === "1") {
                                alert('The toplevel can not be renamed');
                                return;
                            }
                            tree.edit($node);
                        }
                    },                         
                    "Remove": {
                        "separator_before": false,
                        "separator_after": false,
                        "label": "Remove",
                        "action": function (obj) {
                            if (obj.reference.parent().attr('aria-level') === "1") {
                                alert('The toplevel can not be deleted');
                                return;
                            }
                            tree.delete_node($node);
                        }
                    },                         
                    "GoToPage": {
                        "separator_before": false,
                        "separator_after": false,
                        "label": "GoToPage",
                        "action": function (obj) {
                            if (!obj.reference.parent().hasClass('qb_question')) {
                                alert('Can not go to this page, only for questions');
                                return;
                            }
                            
                            var data = {
                                entryId : obj.reference.parent().attr('nodeid')
                            };
                            
                            
                            var event = thundashop.Ajax.createEvent(null, "getPageIdForQuestion", tree.element, data);
                            thundashop.Ajax.postWithCallBack(event, function(result) {
                                thundashop.common.goToPage(result);
                            })
                        }
                    }
                };
            }
        },
        "state" : {"key" : "CheckState"},
        "plugins" : [
                "contextmenu", 
                "dnd",
                "state",
                "wholerow"
            ]
        }).bind("create_node.jstree", function(e, data) {
            data.node.li_attr.nodeid = app.QuestBackManagement.guid();
            
            if ($('#'+data.parent).hasClass('qb_category')) {
                data.node.li_attr.class = 'qb_question';    
            } else {
                data.node.li_attr.class = 'qb_category';
                
            }
            app.QuestBackManagement.saveTree(this);
        })
        .bind("set_text.jstree", function(e, data) { app.QuestBackManagement.saveTree(this); })
        .bind("rename_node.jstree", function(e, data) { app.QuestBackManagement.saveTree(this); })
        .bind("delete_node.jstree", function(e, data) { app.QuestBackManagement.saveTree(this); })
        .bind("move_node.jstree", function(e, data) { app.QuestBackManagement.saveTree(this); })
        .bind("dblclick.jstree", app.QuestBackManagement.navigateToQuestion )
        .bind('ready.jstree', function() {$(this).jstree("open_node", $("#rootnode")); })
    }
};

app.QuestBackManagement.init();