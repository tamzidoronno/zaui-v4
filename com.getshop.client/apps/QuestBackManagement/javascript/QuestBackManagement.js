/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.QuestBackManagement = {
    init: function() {
        $(document).on('click', '.QuestBackManagement .showTemplatePage', app.QuestBackManagement.showTemplatePage)
        $(document).on('click', '.QuestBackManagement .menuelement', app.QuestBackManagement.changeMenu)
        $(document).on('click', '.QuestBackManagement .delete_test_button', app.QuestBackManagement.deleteTest)
        $(document).on('click', '.QuestBackManagement .modify_questionbase_button', app.QuestBackManagement.modifyTest)
        $(document).on('click', '.QuestBackManagement .saveTestSettings', app.QuestBackManagement.saveTestSettings)
        $(document).on('click', '.QuestBackManagement .createTest', app.QuestBackManagement.createTest)
    },
    
    saveTestSettings: function() {
        var data = {
            testid : $(this).attr('testid'),
            forceCorrectAnswer : $('.QuestBackManagement .force_correct_answers').is(':checked'),
            name : $('.QuestBackManagement .testsettings .test_name').val()
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
            entryId : $(e.target).parent().attr('nodeid')
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
        
        "plugins" : [
                "contextmenu", 
                "dnd",
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
        .bind('ready.jstree', function() {
            $(this).jstree("open_all"); 
        });
    }
};

app.QuestBackManagement.init();