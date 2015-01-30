thundashop.app.account = {}

thundashop.app.account.registerUser = function(target) {
    var container = target.closest('.app');
    
    var fullName = container.find('#full_name').val();
    var email = container.find('#email').val();
    var password = container.find('#password').val();
    var streetAddress = container.find('#street').val();
    var postalCode = container.find('#postal_code').val();
    var userId = container.find('#user_id').val();
    var city = container.find('#city').val();
    var userlevel = container.find('#userlevel').val();
    var expireDate = container.find('#expireDate').val();
    
    var birthDay = container.find('#birthDay').val();
    var companyName = container.find('#companyName').val();
    var cellPhone = container.find('#cellPhone').val();
    var invoiceemail = container.find('#invoiceemail').val();
    
    var event = thundashop.Ajax.createEvent('Account', 'registerUser', target, {
        "set_update_user" : "1",
        name : fullName,
        email : email,
        user_id : userId,
        password : password,
        streetAddress : streetAddress,
        invoiceemail : invoiceemail,
        postalCode : postalCode,
        city : city,
        expireDate : expireDate,
        userlevel : userlevel,
        birthDay : birthDay,
        companyName : companyName,
        cellPhone : cellPhone
        
    });
    thundashop.Ajax.postSynchronWithReprint(event);
    thundashop.common.Alert(__w("Changes has been saved!"));
}


thundashop.app.account.updatePassword = function(target) {
    var container = target.closest('.app');
    var oldpassword = container.find('#old_password').val();
    var newpassword = container.find('#new_password').val();
    var repeatNewPassword = container.find('#repeat_new_password').val();
    var userId = container.find('#user_id').val();
    
    var event = thundashop.Ajax.createEvent('Account', 'updatePassword', container, {
        "set_new_password" : "1",
        "old" : oldpassword,
        "new" : newpassword,
        "user_id" : userId,
        "new_repeat" : repeatNewPassword,
        "account_page" : "password"
    });
    thundashop.Ajax.post(event);
}

$('.personal_information').live('click', function(e) {
    var target = $(e.target);
    if(target.hasClass('register_user')) {
        thundashop.app.account.registerUser(target);
    }
});

$('.change_passord').live('click', function(e) {
    var target = $(e.target);
    if(target.attr('id') == "set_new_password") {
        thundashop.app.account.updatePassword(target);
    }
});


