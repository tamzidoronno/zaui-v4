app.SedoxMasterSlave = {
    init: function() {
        $(document).on('click', '.SedoxMasterSlave .jstree-node', app.SedoxMasterSlave.getInformationOnUser);
        $(document).on('click', '.SedoxMasterSlave .save_information', app.SedoxMasterSlave.saveInformation);
        $(document).on('click', '.SedoxMasterSlave .delete_slave i', app.SedoxMasterSlave.removeSlaveFromMaster);
        $(document).on('click', '.SedoxMasterSlave .add_slave', app.SedoxMasterSlave.addSlaveToMaster);
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
        var data = {
            slaveid: $(this).attr("master_id"),
            income: $(".SedoxMasterSlave .income_input").val()
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
    }
};

app.SedoxMasterSlave.init();