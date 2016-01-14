app.Company = {
    
}

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
