app.SedoxMasterSlave = {
    init: function() {
        $(document).on('click', '.SedoxMasterSlave .jstree-node', app.SedoxMasterSlave.getInformationOnUser);
        $(document).on('click', '.SedoxMasterSlave .save_information', app.SedoxMasterSlave.saveInformation);
        $(document).on('click', '.SedoxMasterSlave .delete_slave i', app.SedoxMasterSlave.removeSlaveFromMaster);
        $(document).on('click', '.SedoxMasterSlave .add_slave', app.SedoxMasterSlave.addSlaveToMaster);
        $(document).on('click', '.SedoxMasterSlave .slave_name', app.SedoxMasterSlave.navigateToSlave);
        $(document).on('click', '.SedoxMasterSlave .master_name', app.SedoxMasterSlave.navigateToMaster);
    },
    
    populateTree: function() {        
        var event = thundashop.Ajax.createEvent(null, "listUserHierarchy", $(".SedoxMasterSlave"), {});
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $("#jstree").on("redraw.jstree", function(e, data) {
                resizeLeftBar(); 
            });
            
            $('#jstree').jstree({ 'types' : { 'default' : { icon : 'fa fa-user icon-state-warning icon-lg' }, 'master' : { icon : 'fa fa-users' } },
                                  'search' : {"show_only_matches" : true, "show_only_matches_children" : true},
                                  'plugins' : [ "sort", "search", "types", "wholerow" ]
                                });
                                
            $('#jstree').jstree(true).settings.core.data = eval(res);
            $('#jstree').jstree(true).refresh();

            var timeOut = false;
            $('#jstreesearch').keyup(function () {
                if(timeOut) { 
                    clearTimeout(timeOut);
                }
                timeOut = setTimeout(function () {
                    $('#jstree').jstree('search', $('#jstreesearch').val());
                    resizeLeftBar(); 
                }, 500);
            });
        });
    },
    
    getInformationOnUser: function(e) {
        if($(e.target).attr("id") === $(this).attr("aria-labelledby")) {
            var data = {
                userid: $(this).attr("id")
            };
            var event = thundashop.Ajax.createEvent(null, "getUserFromTree", this, data);

            thundashop.Ajax.postWithCallBack(event, function(res) {
                $(".SedoxMasterSlave .information_wrapper").html(res);
                
                $('.SedoxMasterSlave .gs_datalist_input').each(function() {
                    var elem = $(this);
                    var list = $(document.getElementById(elem.attr('datalist')));
                    elem.autocomplete({
                        source: list.children().map(function() {
                                    return $(this).text();
                                }).get()
                    });
                });
            });
        }
    },
    
    saveInformation: function() {
        var slaveDatas = [];
        
        $(".SedoxMasterSlave .slave_options").each(function() {
            var slaveData = {
                slaveid: $(this).attr("slave_id"),
                income: $(this).find(".income_input").val(),
                passiveslave: $(this).find(".passsive_slave_input").is(":checked")
            }
            
            slaveDatas.push(slaveData);
        });
        
        var data = {
            userid: $(this).attr("user_id"),
            comment: $(".SedoxMasterSlave .comment_input").val(),
            slavedatas: slaveDatas
        };
        
        var event = thundashop.Ajax.createEvent(null, "saveInformation", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
           app.SedoxMasterSlave.populateTree();
        });
    },
    
    addSlaveToMaster: function() {
        var element = $(this);
        
        var data = {
            masterid: $(this).attr("master_id"),
            slaveid: $("option:contains('" + $(".SedoxMasterSlave .gs_datalist_input").val() + "')").val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "addSlaveToMaster", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) { 
            app.SedoxMasterSlave.populateTree();
            element.parent().after(res);
        });
        
    },
    
    removeSlaveFromMaster: function() {
        var element = $(this);
        
        var data = {
            slaveid: element.attr("slave_id")
        }
        
        var event = thundashop.Ajax.createEvent(null, "removeSlaveFromMaster", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
           element.parents(".SedoxMasterSlave .slave_information").remove();
           app.SedoxMasterSlave.populateTree();
        });
    },
    
    navigateToSlave: function() {
        if(!$(".jstree-node[id='" + $(this).attr("master_id") + "']").hasClass("jstree-open")) {
          $(".jstree-node[id='" + $(this).attr("master_id") + "'] > div ~ i").click();  
        }
        $(".jstree-node[id='" + $(this).attr("slave_id") + "'] > div").click();
    },
    
    navigateToMaster: function() {
        $(".jstree-node[id='" + $(this).attr("master_id") + "'] > div").click();
    }
};

app.SedoxMasterSlave.init();