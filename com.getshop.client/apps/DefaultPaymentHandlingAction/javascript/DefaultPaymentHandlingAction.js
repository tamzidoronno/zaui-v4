app.DefaultPaymentHandlingAction = {
    init: function() {
        $(document).on('click', '.DefaultPaymentHandlingAction .showChangePaymentMenu', app.DefaultPaymentHandlingAction.showChangePaymentMenu);
        $(document).on('click', '.DefaultPaymentHandlingAction .updateOrderNote', app.DefaultPaymentHandlingAction.updateOrderNote);
        $(document).on('click', '.DefaultPaymentHandlingAction .updateDueDate', app.DefaultPaymentHandlingAction.updateDueDate);
        $(document).on('click', '.DefaultPaymentHandlingAction .sendReceiptWithMessage', app.DefaultPaymentHandlingAction.sendReceiptWithMessage);
    },
    sendReceiptWithMessage : function() {
        var form = $('.sendReceiptWithMessageForm');
        var data = thundashop.framework.createGsArgs(form);
        var event = thundashop.Ajax.createEvent('','sendReceiptWithMessage', form, data);
        thundashop.Ajax.post(event, function(res) {
            app.DefaultPaymentHandlingAction.refresh(res);
        });
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
    updateDueDate : function() {
        var days = prompt("Number of days");
        if(!days) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','updateDueDate',$(this), { 
            "days" : days,
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
        app.PmsPaymentProcess.refresh();
    },
    
    creditCompleted: function(res) {
        thundashop.Ajax.closeOverLay3();
    }
};

if (typeof(DefaultPaymentHandlingActionInitted) == "undefined") {
    app.DefaultPaymentHandlingAction.init();
    DefaultPaymentHandlingActionInitted = true;
}