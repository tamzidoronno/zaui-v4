app.PmsSearchBooking = {
    init: function() {
        $(document).on('click', '.PmsSearchBooking .searchbox .tab', this.changeTab);
        $(document).on('click', '.PmsSearchBooking .orderpreview .close', this.closePreview);
        $(document).on('click', '.PmsSearchBooking .orderpreview .closebutton', this.closePreview);
        $(document).on('click', '.PmsSearchBooking .orderpreview .continue', this.continueToBooking);
        $(document).on('click', '.PmsSearchBooking .menuarea .menuentry', this.menuClicked);
    },
    
    menuClicked: function() {
        $('.PmsSearchBooking .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        var tab = $(this).attr('tab');
        
        $('.PmsSearchBooking .workarea div[tab]').removeClass('active');
        $('.PmsSearchBooking .workarea div[tab="'+tab+'"]').addClass('active');
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, { selectedTab: tab });
        event['synchron'] = true;
        thundashop.Ajax.post(event, null, null, true, true);
    },
    
    continueToBooking: function() {
        thundashop.common.goToPage("checkout&changeGetShopModule=salespoint");
    },
    
    closePreview: function() {
        $('.PmsSearchBooking .orderpreview').fadeOut();
    },
    
    changeTab: function() {
        var tabName = $(this).attr('tab');
        $('.PmsSearchBooking .searchbox .tab').removeClass('active');
        $('.PmsSearchBooking .searchbox .tab_content').removeClass('active');
        
        $('.PmsSearchBooking .searchbox .tab[tab="'+tabName+'"]').addClass('active');
        $('.PmsSearchBooking .searchbox .tab_content[tab="'+tabName+'"]').addClass('active');
    },
    
    goToSalesPoint: function(res) {
        $('.PmsSearchBooking .orderpreview').fadeIn();
        $('.PmsSearchBooking .orderpreview .content').html(res.previewhtml);
//        console.log(res);
//        ;
    }
}

app.PmsSearchBooking.init();