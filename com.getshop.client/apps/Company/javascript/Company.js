app.Company = {
    init: function() {
        $(document).on('click', '.gss_show_company', app.Company.showSelectedCompany);
        $(document).on('keyup', '.gss_search_user_company', app.Company.startSearch);
    },
    
    startSearch: function(e) {
        if (e.keyCode == 13) {
            $('#gss_startSearchForCompany').click();
        }
    },
    
    showSelectedCompany: function() {
        var companyId = $(this).closest('.gss_companyrow').find('i').attr('gss_value_2')
        
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
