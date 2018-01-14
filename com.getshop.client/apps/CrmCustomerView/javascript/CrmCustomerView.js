app.CrmCustomerView = {
    init: function() {
        $(document).on('click', '.CrmCustomerView .menuarea .menuentry', this.menuClicked);
    },
    
    menuClicked: function() {
        var tab = $(this).attr('tab');
        var needAllSaved = $(this).attr('needAllSaved');
        
        if (needAllSaved === "true" && !$('.CrmCustomerView .datarow.active [gstype="submit"]').hasClass('disabled')) {
            alert(__f("Please save your changes before you go to payment"));
            return;
        }
    
        $('.CrmCustomerView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.CrmCustomerView .workarea div[tab]').removeClass('active');
        $('.CrmCustomerView .workarea div[tab="'+tab+'"]').addClass('active');
        
        if ($(this).attr('clearTabContent') == "true") {
            var text = __f("Loading");
            $('.CrmCustomerView .workarea div[tab="'+tab+'"]').html("<div class='loaderspinner'><i class='fa fa-spin fa-spinner'></i><br/>"+text+"</div>");
        }
        
        var data = {
            selectedTab : tab
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.CrmCustomerView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.CrmCustomerView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    }
}

app.CrmCustomerView.init();