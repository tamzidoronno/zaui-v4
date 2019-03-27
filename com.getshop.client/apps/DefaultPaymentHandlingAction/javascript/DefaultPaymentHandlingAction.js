app.DefaultPaymentHandlingAction = {
    refresh: function(res) {
        $('.DefaultPaymentHandlingAction .applicationinner').html(res.content);
    },
    
    markAsPaidCompleted: function(res) {
        thundashop.Ajax.closeOverLay3();
    }
};