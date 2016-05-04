app.SedoxPerformanceEvcCredit = {
    init: function() {
        $(document).on('click', '.SedoxPerformanceEvcCredit .refresh_evc_credit', app.SedoxPerformanceEvcCredit.refreshCredit);
    },
    
    refreshCredit: function() {
        var spinner = $('<span class="indicator"><i class="fa fa-spin fa-spinner"></i> </span>');
        $(this).prepend(spinner);
        var me = this;
        var event = thundashop.Ajax.createEvent(null, "refreshCredit", this, {});
        thundashop.Ajax.post(event, function() {
            me.find('.indicator').remove();
        });
    },
}

app.SedoxPerformanceEvcCredit.init();