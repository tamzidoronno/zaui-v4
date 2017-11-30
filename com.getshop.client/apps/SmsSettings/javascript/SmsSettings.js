app.SmsSettings = {
    init: function() {
        $(document).on('click', '.SmsSettings .searchbox .tab', this.changeTab);
    },
    
    changeTab: function() {
        var tabName = $(this).attr('tab');
        $('.SmsSettings .searchbox .tab').removeClass('active');
        $('.SmsSettings .searchbox .tab_content').removeClass('active');
        
        $('.SmsSettings .searchbox .tab[tab="'+tabName+'"]').addClass('active');
        $('.SmsSettings .searchbox .tab_content[tab="'+tabName+'"]').addClass('active');
    }
};

app.SmsSettings.init();