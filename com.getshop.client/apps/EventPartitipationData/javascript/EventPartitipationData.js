app.EventPartitipationData = {
    save : function() {
        var theapp = $(this).closest('.app');
        var data = {
            "title" : theapp.find('.eventtitle').val(), 
            "title2" : theapp.find('.eventtitle2').val(), 
            "text" : theapp.find('.eventtext').val(),
            "heading" : theapp.find('.heading').val(),
        }
        
        var event = thundashop.Ajax.createEvent("", "saveData", $(this), data);
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert("Saved", "Text has been saved");
        });
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: []
        };
        
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    init : function() {
        $(document).on('click','.EventPartitipationData .save_event_partition_data', app.EventPartitipationData.save);
    }
};
app.EventPartitipationData.init();