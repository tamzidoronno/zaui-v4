app.ProMeisterCreateAccount = {
    init: function() {
        $(document).on("keyup",'.ProMeisterCreateAccount #searchforcompany', app.ProMeisterCreateAccount.companySearch)
        $(document).on("click",'.ProMeisterCreateAccount .searchforcompanyicon', app.ProMeisterCreateAccount.companySearch)
        $(document).on("click",'.ProMeisterCreateAccount .userGroupNumberEntered', app.ProMeisterCreateAccount.userGroupNumberEntered)
        $(document).on("click",'.ProMeisterCreateAccount .createAccount', app.ProMeisterCreateAccount.createAccount)
        $(document).on("change",'.ProMeisterCreateAccount input[name="isgarageleader"]', app.ProMeisterCreateAccount.garageLeaderChanged)
    },
    
    garageLeaderChanged: function() {
        if ($(this).is(':checked')) {
            $('.ProMeisterCreateAccount .othercompanies').slideDown();
        } else {
            $('.ProMeisterCreateAccount .othercompanies').slideUp();
        }
    },
    
    userGroupNumberEntered: function() {
        var number = $('.ProMeisterCreateAccount .customerNumber').val();
        var max = $('.ProMeisterCreateAccount .customerNumber').attr('max');
        var min = $('.ProMeisterCreateAccount .customerNumber').attr('min');
        
        if (max) {
            max = parseInt(max);
        }
        
        if (min) {
            min = parseInt(min);
        }
        
        if (max && min) {
            if (number.length < min || number.length > max) {
                var text = __w("Please enter correct number of chars, min: {min}, max: {max}");
                text = text.replace("{min}", min);
                text = text.replace("{max}", max);
                alert(text);
                return;
            }
        }
        
        thundashop.Ajax.simplePost(this, "setCustomerReference", { ref: number });
    },
    
    validateEmail: function(email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    },
    
    createAccount: function() {
        var data = {};
        data.fullname = $('.ProMeisterCreateAccount input[name="fullname"]').val();
        data.email = $('.ProMeisterCreateAccount input[name="email"]').val();
        data.invoicemail = $('.ProMeisterCreateAccount input[name="invoicemail"]').val();
        data.cellphone = $('.ProMeisterCreateAccount input[name="cellphone"]').val();
        data.isgarageleader = $('.ProMeisterCreateAccount input[name="isgarageleader"]:checked').length;
        data.otherCompanies = $('.ProMeisterCreateAccount input[name="othercompanies"]').val();
        data.password = $('.ProMeisterCreateAccount input[name="password"]').val();
        
        if (!app.ProMeisterCreateAccount.validateEmail(data.invoicemail)) {
            alert(__w("Please check your the invoice email address"));
            return;
        }
        
        if ($('input[name="storeid"]').val() === "6524eb45-fa17-4e8c-95a5-7387d602a69b") {
            data.cellphone = data.cellphone.replace(" ", "");
            data.cellphone = data.cellphone.replace("-", "");
            
            if (data.cellphone.length !== 10) {
                alert(__w("The cellphone must be 10 digit"));
                return;
            }
        }
        
        
        var event = thundashop.Ajax.createEvent(null, "createAccount", this, data);
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, app.ProMeisterCreateAccount.loginResult);
    },
    
    loginResult: function(res) {
        thundashop.common.closeModal(function() {
            document.location = document.location;
        });
        
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
    
    companySearch: function(e) {
        if (e.keyCode !== 13 && e.type !== "click") {
            return;
        }
        
        var theapp = $(this).closest(".app");
        var text = $('.ProMeisterCreateAccount input').val();
        
        var event = thundashop.Ajax.createEvent(null, "searchForCompanies", this, {
            text: text
        });
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, app.ProMeisterCreateAccount.showCompanies);
    },
    
    showCompanies: function(res) {
        $('.ProMeisterCreateAccount .searchresult').html(res);
    }
};

app.ProMeisterCreateAccount.init();