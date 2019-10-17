app.PmsMonthlyBilling = {
    init : function() {
        $(document).on('click','.PmsMonthlyBilling .togglechecklistbox', app.PmsMonthlyBilling.toggleCheckBoxes);
    },
    toggleCheckBoxes : function() {
        if($(this).is(':checked')) {
            $('.sendpaymentlinkcheckbox').attr('checked',"checked");
        } else {
            $('.sendpaymentlinkcheckbox').attr('checked',null);
        }
    }
};

app.PmsMonthlyBilling.init();