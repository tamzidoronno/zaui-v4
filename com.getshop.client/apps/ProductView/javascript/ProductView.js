app.ProductView = {
    init: function() {
        $(document).on('click', '.ProductView .menuarea .menuentry', this.menuClicked);
    },
    
     menuClicked: function() {
        var tab = $(this).attr('tab');
        var needAllSaved = $(this).attr('needAllSaved');
        
        if (needAllSaved === "true" && !$('.subMenuChanged .datarow.active [gstype="submit"]').hasClass('disabled')) {
            alert(__f("Please save your changes before you go to payment"));
            return;
        }
    
        $('.ProductView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.ProductView .workarea div[tab]').removeClass('active');
        $('.ProductView .workarea div[tab="'+tab+'"]').addClass('active');
        
        if ($(this).attr('clearTabContent') == "true") {
            var text = __f("Loading");
            $('.ProductView .workarea div[tab="'+tab+'"]').html("<div class='loaderspinner'><i class='fa fa-spin fa-spinner'></i><br/>"+text+"</div>");
        }
        
        var data = {
            selectedTab : tab
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.ProductView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.ProductView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    }
    
};

app.ProductView.init();