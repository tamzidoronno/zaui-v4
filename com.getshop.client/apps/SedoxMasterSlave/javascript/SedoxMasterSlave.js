app.SedoxMasterSlave = {
    init: function() {
        $(document).on('click', '.SedoxMasterSlave .jstree-node', app.SedoxMasterSlave.getInformationOnUser);
        $(document).on('click', '.SedoxMasterSlave .save_information', app.SedoxMasterSlave.saveInformation);
    },
    
    populateTree: function() {        
        var event = thundashop.Ajax.createEvent(null, "listUserHierarchy", $(".SedoxMasterSlave"), {});
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('#jstree').jstree({ 'types' : { 'default' : { icon : 'fa fa-user icon-state-warning icon-lg' } },
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
            slaveid: $(this).attr("slave_id"),
            income: $(".SedoxMasterSlave .income_input").val(),
            passiveslave: $(".SedoxMasterSlave .checkbox #passiveslavebox").is(":checked"),
            masterid: $("option:contains('" + $(".SedoxMasterSlave .gs_datalist_input").val() + "')").val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "saveInformation", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function() {
           app.SedoxMasterSlave.populateTree();
        });
    }
};

app.SedoxMasterSlave.init();