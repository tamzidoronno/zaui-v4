app.EcommerceOrderView = {
    init: function() {
        $(document).on('click', '.EcommerceOrderView .menuarea .menuentry', this.menuClicked);
        $(document).on('click', '.EcommerceOrderView .show_payment_methods', this.showPaymentMethods);
        $(document).on('click', '.EcommerceOrderView .removecartitemtfromorder', app.EcommerceOrderView.removeItem);
        $(document).on('click', '.EcommerceOrderView .specialedit', app.EcommerceOrderView.specialEditRow);
        $(document).on('click', '.EcommerceOrderView .savespecialcartitem', app.EcommerceOrderView.saveSpecialItem);
    },
    
    markPaidCompleted : function() {
        $('.markaspaidarea').hide();
        thundashop.framework.reprintPage();
    },
    saveSpecialItem : function() {
        var form = $(this).closest('[gstype="form"]');
        var data = thundashop.framework.createGsArgs(form);
        
        var event = thundashop.Ajax.createEvent('','saveSpecialCartItem',form, data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            res = JSON.parse(res);
            console.log(res);
            form.closest('.cartitem').find(".count").val(res.count);
            form.closest('.cartitem').find(".price").val(res.price);
            $('.specialeditview').hide();
        });
    },
    specialEditRow : function() {
        var btn = $(this);
        var data = {
            "itemid" : $(this).attr('itemid'),
            "orderid" : $(this).attr('orderid')
        };
        
        var event = thundashop.Ajax.createEvent('','displaySpecialDataOnItem',$(this), data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            var view = btn.closest('.cartitem').find('.specialeditview');
            view.html(res);
            view.slideDown();
        });
    },
    saveSpecialCartItem : function(res) {
        
    },
    removeItem : function() {
        var confirmed = confirm("Are you sure you want to remove this item?");
        var btn = $(this);
        if(!confirmed) {
            return;
        }
        var itemid = btn.attr('itemid');
        var orderid = btn.attr('orderid');
        var event = thundashop.Ajax.createEvent('','removeCartItem',$(this), {
            "itemid" : itemid,
            "orderid" : orderid
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            btn.closest('.cartitem').remove();
        });
    },
    showPaymentMethods: function() {
        var app = $(this).closest('.app');
        var paymentoptions = app.find('.paymentmethods');
        if (paymentoptions.is(':visible')) {
            paymentoptions.slideUp();
        } else {
            paymentoptions.slideDown();
        }
    },
    
    menuClicked: function() {
        var tab = $(this).attr('tab');
        var needAllSaved = $(this).attr('needAllSaved');
        
        if (needAllSaved === "true" && !$('.EcommerceOrderView .datarow.active [gstype="submit"]').hasClass('disabled')) {
            alert(__f("Please save your changes before you go to payment"));
            return;
        }
    
        $('.EcommerceOrderView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.EcommerceOrderView .workarea div[tab]').removeClass('active');
        $('.EcommerceOrderView .workarea div[tab="'+tab+'"]').addClass('active');
        
        if ($(this).attr('clearTabContent') == "true") {
            var text = __f("Loading");
            $('.EcommerceOrderView .workarea div[tab="'+tab+'"]').html("<div class='loaderspinner'><i class='fa fa-spin fa-spinner'></i><br/>"+text+"</div>");
        }
        
        var data = {
            selectedTab : tab
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.EcommerceOrderView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.EcommerceOrderView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    }
}

app.EcommerceOrderView.init();