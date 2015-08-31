/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.ProMeisterUserOverview = {
    init: function() {
        $(document).on('click', '.ProMeisterUserOverview .showUser', app.ProMeisterUserOverview.showUserOverview)
        $(document).on('click', '.ProMeisterUserOverview .user_to_select', app.ProMeisterUserOverview.userToSelect)
    },
    
    userToSelect: function() {
        var data = {
            userId : $(this).attr('userid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "setUserId", this, data);
        thundashop.Ajax.post(event);
    },
    
    showUserOverview: function() {
        if ($('.ProMeisterUserOverview #eventsoverview_outer').is(':visible')) {
            $('.ProMeisterUserOverview #eventsoverview_outer').slideUp(function() {
                $('.ProMeisterUserOverview #useroverview_outer').slideDown();
            });
        } else {
            $('.ProMeisterUserOverview #useroverview_outer').slideUp(function() {
                $('.ProMeisterUserOverview #eventsoverview_outer').slideDown();
            });
        }
    }
};

app.ProMeisterUserOverview.init();