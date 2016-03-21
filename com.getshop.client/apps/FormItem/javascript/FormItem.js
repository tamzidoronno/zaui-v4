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
            var additional = field.attr('additionalmsg');
            
            if(!field.is("input") && !field.is('textarea') && !field.is('select')) {
                value = field.html();
            }
            
            if(field.is(':radio') && !field.is(':checked')) {
                return;
            }
            
            if(field.is(':checkbox') && !field.is(':checked')) {
                if(required) {
                    valid = false;
                }
                return;
            } 
            
            if(field.is(':checkbox') && field.is(':checked')) {
                value = "yes";
            }
            
            if(required && !value && required !== "false") {
                valid = false;
            }
            
            var obj = {
                "name" : $(this).attr("name"),
                "val" : value,
                "required" : required,
                "additionalmsg" : additional,
                "type" : formtype
            };
            result.push(obj);
        });
        
        if($('#g-recaptcha-response').length > 0) {
            if(!$('#g-recaptcha-response').val()) {
                alert('You need to verify the captcha first');
                return;
            }
        }
        
        data.captcha = $('#g-recaptcha-response').val();
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
    
    keyUpOnField: function(e) {
        var key = e.keyCode || e.which;
        var submitkey = $(this).attr('submitonenter');
        
        if (key === 13 && submitkey) {
            $('input[type="submit"][name="'+submitkey+'"]').click();
        }
    },
    
    initEvents : function() {
        $(document).on('click','.FormItem .submitForm', app.FormItem.submitForm);
        $(document).on('keyup','.FormItem .inputfield', app.FormItem.keyUpOnField);
    }
    
};

app.FormItem.initEvents();