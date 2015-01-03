app.Button = {
    init: function() {
        $(document).on('click', '.Button .shop_button_saveNewText', app.Button.saveText);
    },
    
    saveText: function() {
        var data = {
            text: $(this).closest('.Button').find('.button_text_content').html()
        };
        var event = thundashop.Ajax.createEvent(null, "saveText", this, data);
        thundashop.Ajax.post(event);
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
                    title: __f("Change text of button"),
                    click: app.Button.changeText
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    changeText: function(extra, application) {
        $(application).find('.button_text_content').attr('contenteditable', 'true');
        $(application).find('.shop_button_saveNewText').fadeIn();
        
        var el = $(application).find('.button_text_content')[0];
        app.Button.selectElementContents(el);
    },
    
    selectElementContents: function(el) {
        var range = document.createRange();
        range.selectNodeContents(el);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    }

    
};

app.Button.init();