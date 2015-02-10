app.EmbedCode = {
    initEvents : function() {
        $(document).on('click','.EmbedCode .addtextbutton', app.EmbedCode.updateCode);
    },
    
    updateCode : function() {
        var text = $('.EmbedCode .codetextarea').val();
        var text_startup = $('.EmbedCode .codetextarea_startup').val();
        var event = thundashop.Ajax.createEvent('','saveCode',$(this), {
            "code" : text,
            "code_startup" : text_startup
        });
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    
    loadConfiguration : function() {
        var event = thundashop.Ajax.createEvent('','loadConfigBox',$(this),{});
        thundashop.common.showInformationBox(event, 'Embed code');
    },
    
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
                    title: __f("Edit code"),
                    click: app.EmbedCode.loadConfiguration
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.EmbedCode.initEvents();