app.DefaultPaymentHandlingAction = {
    init: function() {
        $(document).on('click', '.DefaultPaymentHandlingAction .showChangePaymentMenu', app.DefaultPaymentHandlingAction.showChangePaymentMenu);
    },
    
    showChangePaymentMenu: function(e) {
        var menu = $(this).find('.changePaymentMenu');
        if (menu.is(':visible')) {
            menu.hide();
        } else {
            menu.show();
        }
    },
    
    refresh: function(res) {
        $('.DefaultPaymentHandlingAction .applicationinner').html(res.content);
    },
    
    markAsPaidCompleted: function(res) {
        thundashop.Ajax.closeOverLay3();
    }
};

if (typeof(DefaultPaymentHandlingActionInitted) == "undefined") {
    app.DefaultPaymentHandlingAction.init();
    DefaultPaymentHandlingActionInitted = true;
}