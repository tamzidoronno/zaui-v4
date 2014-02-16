thundashop.app.contact = {};

app.Contact = {
    remove: function(element, application) {
        var appid = application.attr('appid');
        thundashop.Skeleton.removeApplication(appid, 'middle');
    },
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-trash-o",
                    iconsize : "30",
                    title: "Remove form",
                    click: app.Contact.remove,
                    extraArgs: {}
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}

thundashop.app.contact.sendMessage = function(target) {
    var data = target.closest('.contact_form');
    var name = data.find('#name').val();
    var phone = data.find('#phone').val();
    var email = data.find('#email').val();
    var company = data.find('#company').val();
    var subject = data.find('#content').val();

    var groupName = "";

    if ($('.Contact .group').length !== 0) {
        if ($('.Contact .group.selected').length !== 0) {
            groupName = $('.Contact .group.selected').attr('groupname');
        }
    }

    data = {
        "email": email,
        "phone": phone,
        "name": name,
        "company": company,
        "content": subject,
        "groupname": groupName
    };

    if ($('.Contact #invoiceemail').length > 0) {
        data.invoiceemail = $('.Contact #invoiceemail').val();
    }

    var event = thundashop.Ajax.createEvent('Contact', 'sendMessage', target, data);
    var callback = function(data) {
        if (data === "Required") {
            thundashop.common.Alert(__w('Sorry'), __w('All fields are required.'), true);
        } else if (data === "email") {
            thundashop.common.Alert(__w('Sorry'), __w('The email address is invalid.'), true);
        } else {
            thundashop.common.Alert(__w('Success'), __w('Your message has been delivered'), false);

            var data = target.closest('.app');
            data.find('#name').val('');
            data.find('#phone').val('');
            data.find('#email').val('');
            data.find('#content').val('');
            data.find('#company').val('');
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
