app.CompanyView = {
    init: function() {
        $(document).on('click', '.CompanyView .menu .entry', app.CompanyView.changeMenu);
        $(document).on('click', '.CompanyView .topmenu .entry', app.CompanyView.changeMenuSub);
    },
    
    userSaved: function(res) {
        thundashop.Ajax.reloadApp('2f62f832-5adb-407f-a88e-208248117017', false);
    },
    companyCreated : function() {
        window.location.reload();
    },
    
    changeMenu: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        thundashop.Ajax.simplePost(this, 'changeMenu', data);
    },
    
    changeMenuSub: function() {
        var data = {
            tab : $(this).attr('tab')
        };
        
        thundashop.Ajax.simplePost(this, 'changeMenuSub', data);
    },
};

app.CompanyView.init();