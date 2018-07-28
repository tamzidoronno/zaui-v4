app.PmsCleaning = {
    init : function() {
        $(document).on('click', '.PmsCleaning .roomNotReady', app.PmsCleaning.confirmCleaning);
        $(document).on('click', '.PmsCleaning .posponeuntiltomorrow', app.PmsCleaning.posponeuntiltomorrow);
   },
   posponeuntiltomorrow: function() {
       var confirmed = confirm("Are you sure you want to pospone the cleaning until tomorrow? This will make the room reappera tomorrow in the cleaning table.");
       if(!confirmed) {
           return;
       }
       thundashop.Ajax.simplePost($(this),'pospone', {
           "roomid" : $(this).closest('tr').attr('roomid')
       });
   },
   confirmCleaning: function() {
       var confirmed = confirm("Confirm that room " + $(this).text() + " has been cleaned");
       if(confirmed) {
           var data = {
                "id" : $(this).attr('itemid')
            }
            thundashop.Ajax.simplePost($(this), 'markCleaned', data);
        }
   },
   
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Show settings"),
                    click: app.PmsCleaning.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsCleaning.init();