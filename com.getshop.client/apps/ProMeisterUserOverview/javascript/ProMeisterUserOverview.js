/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.ProMeisterUserOverview = {
    init: function() {
        $(document).on('click', '.ProMeisterUserOverview .showUser', app.ProMeisterUserOverview.showUserOverview)
        $(document).on('click', '.ProMeisterUserOverview .user_to_select', app.ProMeisterUserOverview.userToSelect)
        $(document).on('click', '.ProMeisterUserOverview .createSubUser .createButton_subuser', app.ProMeisterUserOverview.createSubUser)
    },
    
    createSubUser: function() {
        var storeId = $('input[name="storeid"]').val();
        
        var data = {
            fullName : $('.ProMeisterUserOverview #new_subuser_name').val(),
            cellPhone: $('.ProMeisterUserOverview #new_subuser_cellphone').val(),
        };
        
        
        if (!data.fullName) {
            alert(__f('Your name can not be empty'));
            return;
        }
        
        if (storeId === "2fac0e57-de1d-4fdf-b7e4-5f93e3225445") {
            if (data.cellPhone.length !== 8) {
                alert(__f('Telefonnr må være 8 siffer'));
                return;
            }    
        }
        
        if (storeId === "d27d81b9-52e9-4508-8f4c-afffa2458488") {
            if (data.cellPhone.length !== 10) {
                alert(__f('Telefonnr må være 10 siffer'));
                return;
            }
        }
        
        var event = thundashop.Ajax.createEvent("", "createSubUserLoggedIn", this, data);
        thundashop.Ajax.post(event, function() {
            thundashop.common.Alert(__f('User created'), __f('User created successfully'));
        });
    },
    
    userToSelect: function() {
        var data = {
            userId : $(this).attr('userid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "setUserId", this, data);
        thundashop.Ajax.post(event);
    },
    
    showUserOverview: function() {
        var dom = $('.ProMeisterUserOverview .createSubUser');
        if (dom.is(':visible')) {
            dom.hide();
        } else {
            dom.show();
        }
    }
};

app.ProMeisterUserOverview.init();