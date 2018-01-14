app.EcommerceOrderView = {
    init: function() {
        $(document).on('click', '.EcommerceOrderView .menuarea .menuentry', this.menuClicked);
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
            selectedTab : tab,
            roomId : $(this).closest('.menuarea').attr('roomId'),
            id :  $(this).closest('.menuarea').attr('bookingEngineId'),    
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.EcommerceOrderView.tabChanged, data, true, true);
    }
}

app.EcommerceOrderView.init();