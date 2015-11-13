thundashop.app.users = {
    deleteUser: function(userId, domElement) {
        var result = thundashop.common.confirm(__f("Are you sure you want to delete the account?"));
        if (result) {
            var data = {
                userid : userId
            };
            event = thundashop.Ajax.createEvent('Users', 'deleteUser', domElement, data);
            thundashop.Ajax.post(event);
        }
    }
};

thundashop.app.users.load_email = function(target) {
    var email = target.attr('email_id');
    document.location.href=document.location.href + "&email_id="+email+"&folder="+target.attr('folder');
};

thundashop.app.users.sendemail = function(target) {
    target = target.closest('.Users');
    var email = target.find('.email_invite').val();
    var title = target.find('.title_invite').val();
    var msg = target.find('.msg_invite').val();
    
    var event = thundashop.Ajax.createEvent('Users', 'sendEmail', target, {
        'email' : email,
        'title' : title,
        'msg' : msg
    });
    thundashop.Ajax.post(event); 
};

thundashop.app.users.create_user = function(target) {
    var email = target.closest('.Users').find('.create_user_input').val();
    var data = {
        "email" : email
    }
    var event = thundashop.Ajax.createEvent("Users","createUser", target, data);
    thundashop.Ajax.post(event);
};

thundashop.app.users.check_email = function(target) {
    var email = target.val();
    var event = thundashop.Ajax.createEvent('Users', 'checkEmail', target, {
        'email' : email
    });
    var data = thundashop.Ajax.postSynchron(event); 
    $('.Users .check_email_field').html(data);
};

$('.Users .send_message_button').live('click', function(e) {
    thundashop.app.users.sendemail($(this));
});

$('.Users .email_row').live('click', function(e) { 
    thundashop.app.users.load_email($(this));
});

$('.Users .email_invite').live('blur', function(e) {
    thundashop.app.users.check_email($(this));
});

$('.Users .editCompany').live('click', function(e) {
    var data = {
        userId : $(this).attr('userid')
    }
    
    var event = thundashop.Ajax.createEvent(null, "showEditCompany", this, data);
    thundashop.common.showInformationBox(event, __f("Edit company information"));
});

$('.Users .createButton_subuser_admin').live('click', function(e) {
    var data = {
        leaderId : $(this).attr('leaderId'),
        name : $('#new_subuser_name').val(),
        phone : $('#new_subuser_cellphone').val()
    }
    
    var event = thundashop.Ajax.createEvent(null, "createSubAccountAdmin", this, data);
    thundashop.Ajax.post(event, function() {
        document.location = '/index.php?page=users_all_users&userid=' + data.leaderId;
    });
});

$('.Users .admin_add_sub_account').live('click', function(e) {
    var data = {
        leaderId : $(this).attr('leaderId')
    }
    
    var event = thundashop.Ajax.createEvent(null, "showAdminAddSubUserAccount", this, data);
    thundashop.common.showInformationBox(event, __f("Edit company information"));
});

$('.Users .saveCompanyInformation').live('click', function(e) {
    var data = {
        userId : $(this).attr('userid'),
        companyNameToSave : $('.Users #companyNameToSave').val(),
        streetaddress : $('.Users #streetaddress').val(),
        postnumber : $('.Users #postnumber').val(),
        city : $('.Users #city').val(),
        country : $('.Users #country').val()
    }
    
    var event = thundashop.Ajax.createEvent(null, "saveCompanyInformation", this, data);
    thundashop.Ajax.post(event, function() {
        document.location = '/index.php?page=users_all_users&userid=' + data.userId;
    });
});

$('.Users .deleteuser').live('click', function(e) {
    var userId = $(this).attr('userid');
    thundashop.app.users.deleteUser(userId, $(this));
});

$('.Users #show_statistic').live('click', function(e) {
    var data = {
        startDate : $('.Users #start_date').val(),
        endDate : $('.Users #end_date').val()
    }
    
    var event = thundashop.Ajax.createEvent(null, "showGroupStatistic", this, data);
    thundashop.Ajax.postWithCallBack(event, function(result) {
        $('.Users .statisic_result').html(result);
    });
});

$('.Users .create_group').live('click', function(e) {
    var groupName = $('#groupnamecreate').val();
    var data = {
        title : groupName
    }
    var event = thundashop.Ajax.createEvent('Users', 'createGroup', $('#groupnamecreate'), data);
    thundashop.Ajax.post(event);
});


$('.Users .create_user').live('click', function(e) {
    thundashop.app.users.create_user($(this));
});
$('.Users .create_user_input').live('keyup', function(e) {
    if(e.keyCode === 13) {
        thundashop.app.users.create_user($(this));
    }
});

$('.Users #users_search').live('keyup', function(e) {
    var text= $('.Users #users_search').val();
    if (text.length < 3) {
        return;
    }
    
    var data = {
        text : text
    }
    var event = thundashop.Ajax.createEvent("Users", 'searchForUser', $('.Users #users_search'), data);
    event['synchron'] = true;
    
    var sendSearch = function() {
        var callback = function(result) {
            $('#result').html(result);
        };
        thundashop.Ajax.post(event, callback, {}, true);
    }
    
    if (thundashop.app.users.searchTimer) {
        clearTimeout(thundashop.app.users.searchTimer);
    }
    
    thundashop.app.users.searchTimer = setTimeout(sendSearch, 400);
});

$('.Users #addgroup').live('click', function() {
    var userId = $(this).attr('userid');
    var groupId = $(this).attr('groupid');
    var data = {
        userid : userId,
        groupId : groupId
    }
    
    var event = thundashop.Ajax.createEvent("Users", "addGroup", $(this), data);
    thundashop.Ajax.post(event);
});

$('.Users #removegroup').live('click', function() {
    var userId = $(this).attr('userid');
    var groupId = $(this).attr('groupid');
    var data = {
        userid : userId,
        groupId : groupId
    }
    
    var event = thundashop.Ajax.createEvent("Users", "removeUserFromGroup", $(this), data);
    thundashop.Ajax.post(event);
});

$(document).on('click', '.Users .save_score_settings', function() {
    var me = this;
    var data = {
        userId : $(this).attr('userid')
    };

    $('.Users .score_settings_table').find('input').each(function() {
        data[$(this).attr('id')] = $(this).val()
    });
    
    var event = thundashop.Ajax.createEvent(null, "saveProMeisterSettings", this, data);
    thundashop.Ajax.postWithCallBack(event, function() {
        alert('saved');
    });
});

$(document).on('click','.Users #activate_permissionfilter', function() {
    $(this).parent().hide();
    $('.Users .application_filter').show();
});

$(document).on('click','.Users #save_user_filter', function() {
    var toSave = {};
    $('.Users .application_filter .gs_on').each(function() {
        toSave[$(this).attr('id')] = $(this).parent().find('.pm_rights').attr('type');
    });
    if(toSave.length === 0) {
        thundashop.common.Alert(__f('Invalid option'),__f("Atleast one application need to be selected."), true);
        return;
    }
    var data = {
        "toSave" : toSave,
        "userid" : $('.Users .application_filter').attr('userid')
    };
    
    var event = thundashop.Ajax.createEvent('','updateApplicationPermissions', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.Alert('Saved','settings has been saved');
    });
    
});
$(document).on('click','.Users #reset_user_filter', function() {
    var toSave = [];
    
    var data = {
        "toSave" : toSave,
        "userid" : $('.Users .application_filter').attr('userid')
    };
    
    var event = thundashop.Ajax.createEvent('','updateApplicationPermissions', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.Alert('Saved','settings has been saved');
    });
    
});

$(document).on('click','.Users .application_filter .pm_rights', function() {
    var type = $(this).attr('type');
    if(type === "0") {
        $(this).html(' (r)');
        $(this).attr('type',1);
    }
    if(type === "1") {
        $(this).html(' (w)');
        $(this).attr('type',2);
    }
    if(type === "2") {
        $(this).html(' (rw)');
        $(this).attr('type',0);
    }
});

$(document).on('click','.Users .application_filter .gs_onoff', function() {
    if($(this).hasClass('gs_on')) {
        $(this).parent().find('.pm_rights').html(' (rw)');
        $(this).parent().find('.pm_rights').attr('type',0);
    } else {
        $(this).parent().find('.pm_rights').html('');
    }
});
