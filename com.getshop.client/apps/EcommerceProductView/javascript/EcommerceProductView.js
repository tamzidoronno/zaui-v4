app.EcommerceProductView = {
    init: function() {
        $(document).on('click', '.EcommerceProductView .menuentry', app.EcommerceProductView.menuClicked);
        $(document).on('click', '.EcommerceProductView .showeditlist', app.EcommerceProductView.showEditList);
        $(document).on('click', '.EcommerceProductView .closeedit', app.EcommerceProductView.closeEditList);
    },
    
    showEditList: function() {
        var listid = $(this).attr('listid');
        $('.editlist[listid="'+listid+'"]').show();
    },
    
    closeEditList: function() {
        $(this).closest('.editlist').hide();
    },
    
    menuClicked: function() {
        var tab = $(this).attr('tab');
        $('.EcommerceProductView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.EcommerceProductView .workarea div[tab]').removeClass('active');
        $('.EcommerceProductView .workarea div[tab="'+tab+'"]').addClass('active');
        
        var data = {
            selectedTab : tab
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.EcommerceProductView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.EcommerceProductView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    }
};

app.EcommerceProductView.init();