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