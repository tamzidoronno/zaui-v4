/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

app.ProMeisterLogin = {
    addressOnSuccess: "",
    
    init : function() {
        $(document).on('click', '.promeisterloginform .loginbutton', app.ProMeisterLogin.login);
        $(document).on('click', '.promeisterloginform .groupselection', app.ProMeisterLogin.groupSelected);
        $(document).on('click', '.promeisterloginform .signupform_close', app.ProMeisterLogin.close);
        $(document).on('click', '.promeisterloginform .login_searchCompany', app.ProMeisterLogin.searchForCompany);
        $(document).on('click', '.promeisterloginform .login_create_new_account', app.ProMeisterLogin.showStep1)
        $(document).on('click', '.promeisterloginform .showstep3', app.ProMeisterLogin.showStep3)
        $(document).on('click', '.promeisterloginform .select_searched_company', app.ProMeisterLogin.createUser)
        $(document).on('click', '.promeisterloginform .show_forgot_password', app.ProMeisterLogin.showForgottenPassword)
        $(document).on('click', '.promeisterloginform .do_reset_password', app.ProMeisterLogin.doResetPassword)
    },
    
    showForgottenPassword: function() {
        app.ProMeisterLogin.hideSteps();
        $('.forgot_password').show();
    },
    
    createUser: function() {
        var data = {
            groupId : $('.promeisterloginform .groupselection.selected').attr('groupId'),
            name : $('.promeisterloginform #name').val(),
            email : $('.promeisterloginform #email').val(),
            invoiceemail : $('.promeisterloginform #invoiceemail').val(),
            cellphone : $('.promeisterloginform #cellphone').val()
        };
        
        data.orgnr = $(this).attr('orgnr');
        
        var event = thundashop.Ajax.createEvent(null, "proMeisterCreateUser", null, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "success") {
                window.location = app.ProMeisterLogin.addressOnSuccess;
                app.ProMeisterLogin.close();
            } else {
                alert(__f("Failed to create user"));
            }
            
        });
    },
    
    groupSelected: function() {
        $('.groupselection.selected').removeClass('selected');
        $(this).addClass('selected');
        app.ProMeisterLogin.showStep2();
    },
    
    hideSteps: function() {
        $('.signupform_content_signin').hide();
        $('.forgot_password').hide();
        $('.signup_step1').hide();
        $('.signup_step2').hide();
        $('.signup_step3').hide();
    },
    
    showStep0: function() {
        app.ProMeisterLogin.hideSteps();
        $('.signupform_content_signin').show();
    },
    
    showStep1: function() {
        app.ProMeisterLogin.hideSteps();
        $('.signup_step1').show();
    },
    
    showStep2: function() {
        app.ProMeisterLogin.hideSteps();
        $('.signup_step2').show();
        
    },
    
    showStep3: function() {
        var success = function() {
            app.ProMeisterLogin.hideSteps();
            $('.signup_step3').show();
        };
        
        var storeId = $('input[name="storeid"]').val();
        
        var failure = function() {
            alert(__f('User you are trying to create already exists, please try to recover password'));
            return;
        };
        
        var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
        
        var email = $('.promeisterloginform #email').val();
        var invoiceemail = $('.promeisterloginform #invoiceemail').val();
        
        if ($('.promeisterloginform #name').val().length === 0) {
            alert(__f('Your name can not be empty'));
            return;
        }
        
        if (invoiceemail.length === 0) {
            alert(__f('Invoicemail can not be empty'));
            return;
        }
        
        if (!re.test(email)) {
            alert(__f('Invalid email address, please check it'));
            return;
        }
        
        if (!re.test(invoiceemail)) {
            alert(__f('Invalid invoice email address, please check it'));
            return;
        }
        
        //Norge
        if (storeId === "2fac0e57-de1d-4fdf-b7e4-5f93e3225445") {
            if ($('.promeisterloginform #cellphone').val().length !== 8) {
                alert(__f('Telefonnr må være 8 siffer'));
                return;
            }    
        }
        
        // Sverige
        if (storeId === "d27d81b9-52e9-4508-8f4c-afffa2458488") {
            if ($('.promeisterloginform #cellphone').val().length !== 9) {
                alert(__f('Telefonnr må være 9 siffer'));
                return;
            }
        }
        
        
        app.ProMeisterLogin.validateStep2(success, failure);
    },
    
    validateStep2: function(success, failure) {
        var data = {
            username : $('.promeisterloginform #email').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "doesEmailExists", null, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "ok") {
                success();
            } else {
                failure();
            }
        });
    },
    
    searchForCompany: function() {
        var data = {
            companyname : $('#login_serach_company').val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "proSearchCompany", null, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.company_search_result').html(result);
        });
    },
    
    close: function() {
        $('.signupform_outer').hide();
    },
    
    show: function(addressOnSuccess) {
        
        if (addressOnSuccess === true) {
            addressOnSuccess = app.ProMeisterLogin.addressOnSuccess;
        }
        
        $(window).scrollTop(0);
        app.ProMeisterLogin.showStep0();
        app.ProMeisterLogin.addressOnSuccess = addressOnSuccess;
        $('.signupform_outer').show();
    },
    
    login: function() {
        var login = {
            username : $('.promeisterloginform #login_username').val(),
            password : $('.promeisterloginform #login_password').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "proLogin", null, login);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "success") {
                window.location = app.ProMeisterLogin.addressOnSuccess;
                app.ProMeisterLogin.close();
            } else {
                alert(__f("Wrong username or password"));
            }
            
        });
    },
    
    doResetPassword: function() {
        var data = {
            email : $('.promeisterloginform #reset_email_address').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "proResetPassword", null, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (result === "1") {
                alert(__w('A new password has been sent to your email account'));
                app.ProMeisterLogin.show(true);
            } else {
                alert(__w('No account created with the given email address'));
            }
            
        });
    }
};

app.ProMeisterLogin.init();
