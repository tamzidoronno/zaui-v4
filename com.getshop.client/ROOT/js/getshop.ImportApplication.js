GetShop.ImportApplication = {
    apps : {},
    currentApp : -1,
    
    init : function() {
        var me = this;
        $('#ImportNextApp:not(.disabled)').live('click', function() { me.next(); });
        $('#ImportPrevApp:not(.disabled)').live('click', function() { me.prev(); });
        $('#ImportSave').live('click', function() { me.save(); });
        $('#ImportCancel').live('click', function() { me.cancel(); });
    },
            
    cancel: function() {
        var event = thundashop.Ajax.createEvent("ApplicationManager", "ping", null, {});
        thundashop.Ajax.post(event);
        this.unmaskIt();
    },
            
    unmaskIt: function() {
        $('#ImportMenu').hide();
        thundashop.common.unlockMask();
        thundashop.common.unmask();
    },
            
    save: function()  {
        var container = $('.applicationarea[area='+this.area+']');
        var app = container.find('.previewapp .app');
        var appId = app.attr('appid');
        
        var data = {
            list : [ appId ],
            apparea : this.area
        };
        
        var event = thundashop.Ajax.createEvent("ApplicationManager", "importApplication", null, data);
        thundashop.Ajax.post(event);
        this.unmaskIt();
    },
            
    setApps: function(iapps, area) {
        this.currentApp = -1;
        this.apps = iapps;
        this.area = area;
        this.showMenu();
        thundashop.common.lockMask();
        thundashop.common.mask();
    },
            
    showMenu: function() {
        var container = $('.applicationarea[area='+this.area+']');
        var menu = $('#ImportMenu');
        menu.show();
        var left = container.position().left + container.width() + 10;
        var top = container.position().top;
        menu.position().left = left;
        menu.css('left', left);
        menu.css('top', top);
    },
        
    clean : function() {
        var container = $('.applicationarea[area='+this.area+']');
        container.find('.previewapp').remove();
    },
    
    next : function() {
        this.currentApp = this.currentApp + 1;
        var container = $('.applicationarea[area='+this.area+']');
        this.clean();
        container.prepend(this.apps[this.currentApp]);
        this.updateStateOfPrevNext();
    },
    
    prev : function() {
        this.currentApp = this.currentApp - 1;
        var container = $('.applicationarea[area='+this.area+']');
        this.clean();
        container.prepend(this.apps[this.currentApp]);
        this.updateStateOfPrevNext();
    },
            
    updateStateOfPrevNext : function() {
        $('#ImportNextApp').removeClass('disabled');
        $('#ImportPrevApp').removeClass('disabled');
        
        if (this.currentApp === 0) {
            $('#ImportPrevApp').addClass('disabled');
        }
        if (this.currentApp === this.apps.length - 1) {
            $('#ImportNextApp').addClass('disabled');
        }
    }
};

GetShop.ImportApplication.init();