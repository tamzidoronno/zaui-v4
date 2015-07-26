app.FormItem = {
    editFormItem : function() {
        var event = thundashop.Ajax.createEvent('','loadConfig',$(this), {});
        thundashop.common.showInformationBox(event, 'Configure your item');
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
                    title: __f("Manage products in this list"),
                    click: app.FormItem.editFormItem
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    submitForm : function() {
        var data = {};
        var result = [];
        var valid = true;
        $('.formiteminput').each(function() {
            var field = $(this);
            var value = field.val();
            var required = field.attr("needed");
            var formtype = field.attr("formtype");
            
            if(!field.is("input") && !field.is('textarea') && !field.is('select')) {
                value = field.html();
            }
            
            if(field.is(':radio') && !field.is(':checked')) {
                return;
            }
            if(required && !value) {
                valid = false;
            }
            
            var obj = {
                "name" : $(this).attr("name"),
                "val" : value,
                "required" : required,
                "type" : formtype
            };
            result.push(obj);
        });
        data.result = result;
        data.valid = valid;
        
        if(!valid) {
            alert('Please fill inn the required fields before submitting the form.');
        } else {
            var event = thundashop.Ajax.createEvent('','submitForm',$(this), data);
            thundashop.Ajax.post(event);
        }
        
       
        console.log(data);
    },
    
    initEvents : function() {
        $(document).on('click','.FormItem .submitForm', app.FormItem.submitForm);
    }
    
};

app.FormItem.initEvents();