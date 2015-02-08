thundashop.app.contact = {};

app.Contact = {
    initEvents : function() {
      $(document).on('change','.Contact select[name="numberOfFields"]', app.Contact.updateFormFields);
      $(document).on('click','.Contact .saveContactConfiguration', app.Contact.saveConfiguration);
    },
    loadConfiguration : function() {
        var event = thundashop.Ajax.createEvent('','loadConfiguration',$(this));
        thundashop.common.showInformationBox(event, 'Contact configuration');
    },
    updateFormFields : function() {
        var numberOfFields = parseInt($('#informationbox.Contact select[name="numberOfFields"]').val());
        $('#informationbox.Contact .contactfield').hide();
        for(var i = 0; i <= numberOfFields; i++) {
            $('#informationbox.Contact .fieldName_'+i).show();
        }
    },
    saveConfiguration : function() {
        var data = {};
        
        $('.contactConfigTable input').each(function() {
            data[$(this).attr('name')] = $(this).val();
        });
        $('.contactConfigTable select').each(function() {
            data[$(this).attr('name')] = $(this).val();
        });
        
        var event = thundashop.Ajax.createEvent('','saveContactConfig',$(this),data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
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
                    title: __f("Edit contact form"),
                    click: app.Contact.loadConfiguration
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}

app.Contact.initEvents();

thundashop.app.contact.sendMessage = function(target) {
    var data = target.closest('.contact_form');
    var fields = {};
    var dataToSend = {};
    data.find('.contactfield').each(function() {
        if($(this).attr('id')) {
            fields[$(this).attr('id')] = $(this).val();
        }
    });

    fields['content'] = data.find('.content').val();

    dataToSend['field'] = fields;

    var event = thundashop.Ajax.createEvent('Contact', 'sendMessage', target, dataToSend);
    var callback = function(data) {
        if (data === "Required") {
            thundashop.common.Alert(__w('Sorry'), __w('All fields are required.'), true);
        } else if (data === "email") {
            thundashop.common.Alert(__w('Sorry'), __w('The email address is invalid.'), true);
        } else {
            thundashop.common.Alert(__w('Success'), __w('Your message has been delivered'), false);

            var data = target.closest('.app');
            data.find('input.tobecleared').each(function() { $(this).val('') });
            data.find('textarea.tobecleared').each(function() { $(this).val('') });
            $('.Contact .selected').removeClass('selected');

        }
    };
    thundashop.Ajax.postWithCallBack(event, callback);
}

$('.Contact').live('click', function(e) {
    var target = $(e.target);
    if (target.hasClass('send_email')) {
        thundashop.app.contact.sendMessage(target);
    }
})
thundashop.app.contact.editContent = function(target) {
    var event = thundashop.Ajax.createEvent('Contact', 'editContact', target, {
    });
    thundashop.Ajax.post(event, 'Contact');
}

thundashop.app.contact.saveContent = function(target) {
    var container = target.closest('.contact_form');
    var email = container.find("#email").val();
    var phone = container.find("#phone").val();
    var address = container.find("#address").val();
    var postalcode = container.find("#postalcode").val();
    var header = container.find('iframe').contents().find('body').html();
    var event = thundashop.Ajax.createEvent('Contact', 'saveContact', target, {
        "email": email,
        "phone": phone,
        "address": address,
        "postalcode": postalcode,
        "header": header
    });
    thundashop.Ajax.post(event, 'Contact');
}

$('.Contact').live('click', function(e) {
    var target = $(e.target);
    if (target.hasClass('edit_contact_button')) {
        thundashop.app.contact.editContent(target);
    }
    if (target.hasClass('save_changes')) {
        thundashop.app.contact.saveContent(target);
    }
});

$('.Contact .group').live('click', function() {
    $('.Contact .group').removeClass('selected');
    $(this).addClass('selected');
});
