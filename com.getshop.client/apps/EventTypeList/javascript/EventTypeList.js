app.EventTypeList = {
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Go To Template page"),
                    click: app.EventTypeList.goToTemplatePage
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    goToTemplatePage: function() {
        var bookingeginename = $(this).closest('.app').attr('bookingeginename');
        thundashop.common.goToPage(bookingeginename+"_bookingegine_type_template"); 
    }
    
    
}