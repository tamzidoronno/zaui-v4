thundashop.common.applicationPicker = {
    selectApps: new Array(),
    selectAppsName: {},
    selectAppsContent: {},
    options: {},

    init: function() {
        $('.jqueryApplicationPicker .entry_container').live('click', this.displayAddedApplications);
        $('.jqueryApplicationPicker .selectApp').live('click', this.selectApplication);
        $('.jqueryApplicationPicker .remove').live('click', this.removeSelectedApp);
        $('.jqueryApplicationPicker #importApplications').live('click', this.importApplications);
        $('.jqueryApplicationPicker .previewApp').live('click', this.previewApplication);
        $('.jqueryApplicationPicker .previewArea .close').live('click', this.closePreview);
    },
            
    load: function(options) {
        this.selectApps = new Array();
        
        if(options === undefined) {
            options = {};
        }
        thundashop.common.applicationPicker.options = options;
    
        this.loadApplications();
        this.printSelectedApplications();
    },
    
    closePreview : function() {
        $(this).closest('.previewArea').hide();
    },
    
    previewApplication : function() {
        var data = {
            "appId" : $(this).attr('appId')
        }
        var event = thundashop.Ajax.createEvent("", "previewApplication", "", data);
        thundashop.Ajax.postWithCallBack(event, thundashop.common.applicationPicker.preview);
    },
   
    preview: function(data) {
        $('.jqueryApplicationPicker .previewArea .previewArea-inner').html('');
        $('.jqueryApplicationPicker .previewArea').show();
        $('.jqueryApplicationPicker .previewArea .previewArea-inner').html(data);
    },
   
    selectDefaultApplication : function() {
        $('.jqueryApplicationPicker .entry_container')[0].click();
    },
        
    removeSelectedApp : function() {
        var box = $(this).closest('.selectedApplication');
        var appId = box.attr('appId');
        for(var key in thundashop.common.applicationPicker.selectApps) {
            if(thundashop.common.applicationPicker.selectApps[key]== appId) {
                thundashop.common.applicationPicker.selectApps.splice(key, 1);
            }
        }
        thundashop.common.applicationPicker.printSelectedApplications();
    },
        
    loadApplications: function(list) {
        var event = thundashop.Ajax.createEvent("", "loadApplicationPicker", $(this), {
            "list" : thundashop.common.applicationPicker.options.list,
            "area" : thundashop.common.applicationPicker.options.area
        });
        thundashop.common.showInformationBox(event, "Import applications");
    },
    displayAddedApplications: function() {
        var appName = $(this).attr('appName');
        var event = thundashop.Ajax.createEvent("", "loadApplicationPicker", $(this), {
            "appName": appName,
            "list" : thundashop.common.applicationPicker.options.list,
            "area" : thundashop.common.applicationPicker.options.area
        });
        thundashop.common.showInformationBox(event, "Import applications");
        thundashop.common.applicationPicker.printSelectedApplications();
    },
    printSelectedApplications: function() {
        var selectArea = $('.jqueryApplicationPicker .selectionArea .selectedAppArea');
        selectArea.html('');
        if(this.selectApps.length == 0) {
            $('.jqueryApplicationPicker .selectionArea .emptySelectionArea').show();
            $('.jqueryApplicationPicker .selectionArea .continue').hide();
            return;
        }
        $('.jqueryApplicationPicker .selectionArea .emptySelectionArea').hide();
        $('.jqueryApplicationPicker .selectionArea .continue').show();
        for(var key in this.selectApps) {
            var appId = this.selectApps[key];
            var applicationSelected = $("<div class='selectedApplication' appId='"+appId+"'></div>");
            var appName = this.selectAppsName[appId];
            applicationSelected.append("<div class='selectedAppName'>" + appName + "<span class='remove'></span></div>");
            var pages = thundashop.common.applicationPicker.selectAppsContent[appId];
            for(var key in pages) {
                applicationSelected.append("<div class='onPage'>" + pages[key] + "</div>");
            }
        selectArea.append(applicationSelected);
        }
    },
        
    selectApplication: function() {
        var appId = $(this).attr('appId');
        var appName = $(this).closest('.jqueryApplicationPicker').find('.selectedAppName').attr('appName');
        found = false;
        for (var key in thundashop.common.applicationPicker.selectApps) {
            if (thundashop.common.applicationPicker.selectApps[key] == appId) {
                found = true;
            }
        }
        if (!found) {
            thundashop.common.applicationPicker.selectApps.push(appId);
            thundashop.common.applicationPicker.selectAppsName[appId] = appName;
            
            //Add content.
            i = 0;
            var result = [];
            $(this).closest('.jqueryApplicationPicker').find('.applicationList[appId="'+appId+'"]').find('.pageName').each(function(e) {
                var name = $(this).attr('pageName');
                result.push(name);
            });
            thundashop.common.applicationPicker.selectAppsContent[appId] = result;
            
            thundashop.common.applicationPicker.printSelectedApplications();
        }
    },
        
    importApplications : function() {

        var entries = [];
        $('.jqueryApplicationPicker .selectionArea .selectedApplication').each(function(e) {
           entries.push($(this).attr('appId')); 
        });

        thundashop.common.applicationPicker.options.callback(entries);
        thundashop.common.hideInformationBox();
    }
};

thundashop.common.applicationPicker.init();