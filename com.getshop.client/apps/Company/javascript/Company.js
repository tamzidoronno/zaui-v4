app.Company = {
    init: function() {
        $(document).on('click', '.gss_show_company', app.Company.showSelectedCompany);
    },
    
    showSelectedCompany: function() {
        var companyId = $('.gsschangeusercompany').val();
        if (!companyId) {
            alert("No company selected");
            return;
        }
        
        app.Company.gssinterface.showCompany(companyId);
    }
}

app.Company.init();

app.Company.gssinterface =  {
    showCompany: function(companyId) {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('a6d68820-a8e3-4eac-b2b6-b05043c28d78', function () {
            var data = {
                gss_fragment: 'company',
                gss_view: 'gs_company_workarea',
                gss_value: companyId
            }

            getshop.Settings.post({}, "gs_show_fragment", data);
        });
    }
};
