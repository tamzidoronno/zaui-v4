app.AccountFinanceReport = {
    init: function() {
        $(document).on('click', '.AccountFinanceReport .showresultbutton.deactivated', app.AccountFinanceReport.displayWarning);
        $(document).on('click', '.AccountFinanceReport .showresultbutton.closeperiode', app.AccountFinanceReport.closePeriode);
    },
    
    closePeriode: function() {
        var res = confirm('Are you sure you want to close this periode?');
        if (!res) {
            return;
        }
        
        thundashop.Ajax.simplePost(this, "closeCurrentPeriode", {});
    },
    
    displayWarning: function() {
        alert('You can not close a periode that is not finished, if you want to close days, please select hower the row and close it from there');
    }
};

app.AccountFinanceReport.init();