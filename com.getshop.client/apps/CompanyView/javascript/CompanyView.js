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

function selectCompanyId(companyId)
{
    var data = {
        companyid : companyId,
        gsclick : 'selectCompany',
        newpagesize: 15,
        newpagenumber: 1,
        tab: 'companylist'
    };

    var event = thundashop.Ajax.createEvent(null, 'selectCompany', this, data);
    event.core.appname = 'CompanyView';
    event.core.instanceid = '2f62f832-5adb-407f-a88e-208248117017';
    event.core.pageid = 'customers';
    event.core.appid = '';
    console.log('EVENT',event);
    thundashop.Ajax.post(event);
}

function showCompany(companyId)
{
    // not pretty, but does the jobb...
    app.CompanyView.selectedCompanyId = companyId;
    app.CompanyView.changeMenu('companylist');
    window.setTimeout(selectCompanyId, 2000, companyId);
    return;
}
app.CompanyView.init();