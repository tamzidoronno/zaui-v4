app.ProMeisterCreateAccount = {
    init: function() {
        $(document).on("keyup",'.ProMeisterCreateAccount #searchforcompany', app.ProMeisterCreateAccount.companySearch)
        $(document).on("click",'.ProMeisterCreateAccount .groupselect', app.ProMeisterCreateAccount.groupSelected)
        $(document).on("click",'.ProMeisterCreateAccount .userinformationnext', app.ProMeisterCreateAccount.addUserInformation)
        $(document).on("click",'.ProMeisterCreateAccount .companyselected', app.ProMeisterCreateAccount.companySelected)
    },
    
    companySelected: function() {
        var selected = $('input[name="selectedCompany"]:checked').val();
        
        if (!selected) {
            alert(__w("Please select a company"));
            return;
        }
        
        app.ProMeisterCreateAccount.data.company = selected;
        
        var event = thundashop.Ajax.createEvent(null, "createAccount", this, app.ProMeisterCreateAccount.data);
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, app.ProMeisterCreateAccount.loginResult);
    },
    
    loginResult: function(res) {
        thundashop.common.closeModal();
    },
    
    validatePassword: function() {
        var password1 = $('.ProMeisterCreateAccount input[name="password"]').val();
        var password2 = $('.ProMeisterCreateAccount input[name="password_repeated"]').val();
        
        if (password1 !== password2) {
            $('.ProMeisterCreateAccount input[name="password"]').addClass('missing');
            $('.ProMeisterCreateAccount input[name="password_repeated"]').addClass('missing');
            return false;
        }
        
        return true;
    },
    
    validateUserInformation: function() {
        var allGood = true;
        $('.ProMeisterCreateAccount input[mandatory="yes"]').each(function() {
            $(this).removeClass("missing");
            
            if (!$(this).is(':visible')) {
                return;
            }
            
            if (!$(this).val()) {
                allGood = false;
                $(this).addClass("missing");
            }
        });
        
        return allGood;
    },
    
    addUserInformation: function() {
        if (!app.ProMeisterCreateAccount.validateUserInformation()) {
            return;
        }
        
        if (!app.ProMeisterCreateAccount.validatePassword()) {
            alert(__f("Please make sure your repeated password is the same as original"));
            return;
        }
        
        app.ProMeisterCreateAccount.collectUserInformation();
        
        $('.ProMeisterCreateAccount .registration_step').hide();
        $('.ProMeisterCreateAccount .registration_step.step3').show();
    },
    
    collectUserInformation: function() {
        app.ProMeisterCreateAccount.data.fullname = $('.ProMeisterCreateAccount input[name="fullname"]').val();
        app.ProMeisterCreateAccount.data.email = $('.ProMeisterCreateAccount input[name="email"]').val();
        app.ProMeisterCreateAccount.data.invoicemail = $('.ProMeisterCreateAccount input[name="invoicemail"]').val();
        app.ProMeisterCreateAccount.data.cellphone = $('.ProMeisterCreateAccount input[name="cellphone"]').val();
        app.ProMeisterCreateAccount.data.isgarageleader = $('.ProMeisterCreateAccount input[name="isgarageleader"]:checked').length;
        app.ProMeisterCreateAccount.data.password = $('.ProMeisterCreateAccount input[name="password"]').val();
        
        if ($('.ProMeisterCreateAccount input[name="groupreference"]').val()) {
            app.ProMeisterCreateAccount.data.groupreference = $('.ProMeisterCreateAccount input[name="groupreference"]').val();    
        }
        
    },
    
    groupSelected: function() {
        app.ProMeisterCreateAccount.data = {
            groupId : $(this).attr('value')
        }
        
        app.ProMeisterCreateAccount.hideShowGroupFields();
        $('.ProMeisterCreateAccount .registration_step').hide();
        $('.ProMeisterCreateAccount .registration_step.step2').show();
    },
    
    hideShowGroupFields: function() {
         $('.ProMeisterCreateAccount .groupfield').each(function() {
             var activatedForGroups = $(this).attr('avtivatedForGroups');
             if (activatedForGroups.indexOf(app.ProMeisterCreateAccount.data.groupId) > -1) {
                 $(this).show();
             } else {
                 $(this).hide();
             }
         })
    },
    
    companySearch: function() {
        var theapp = $(this).closest(".app");
        var text = $(this).val();
        if (text.length < 4) {
            theapp.find(".searchresult").html(__w("Please enter minimum three characters"));
        } else {
            var event = thundashop.Ajax.createEvent(null, "searchForCompanies", this, {
                text: text
            });
            event['synchron'] = true;
            thundashop.Ajax.postWithCallBack(event, app.ProMeisterCreateAccount.showCompanies);
        }
    },
    
    showCompanies: function(res) {
        $('.ProMeisterCreateAccount .searchresult').html(res);
    }
};

app.ProMeisterCreateAccount.init();