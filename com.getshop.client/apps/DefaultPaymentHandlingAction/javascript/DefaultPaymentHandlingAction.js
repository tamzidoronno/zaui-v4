app.DefaultPaymentHandlingAction = {
    init: function() {
        $(document).on('click', '.DefaultPaymentHandlingAction .showChangePaymentMenu', app.DefaultPaymentHandlingAction.showChangePaymentMenu);
        $(document).on('click', '.DefaultPaymentHandlingAction .updateOrderNote', app.DefaultPaymentHandlingAction.updateOrderNote);
    },
    updateOrderNote : function() {
        var note = prompt("Note to set on order");
        var event = thundashop.Ajax.createEvent('','updateOrderNote',$(this), {
            "note" : note,
            "text" : $(this).val(),
            "orderid" : $(this).attr('orderid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.PmsPaymentProcess.refresh();
        });

    },
    showChangePaymentMenu: function(e) {
        var menu = $('.changePaymentMenu');
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