app.EcommerceOrderView = {
    init: function() {
        $(document).on('click', '.EcommerceOrderView .menuarea .menuentry', this.menuClicked);
        $(document).on('click', '.EcommerceOrderView .show_payment_methods', this.showPaymentMethods);
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