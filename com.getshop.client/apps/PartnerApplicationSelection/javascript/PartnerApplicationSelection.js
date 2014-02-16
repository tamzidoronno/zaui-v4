app.PartnerApplicationSelection = function(config) {
    this.config = config;
};

app.PartnerApplicationSelection.prototype = {
    saveList : function() {
        var ids = [];
        $('.PartnerApplicationSelection .appentry').each(function() {
            if($(this).find('.selection').hasClass('on')) {
                ids.push($(this).attr('id'));
            }
        });
        var event = thundashop.Ajax.createEvent('','savePartnerList',$(this),{ "ids": ids });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.Alert('',__f('List has been saved'));
        });
    },
    toggleApp : function() {
        var selection = $(this).find(".selection");
        if(selection.hasClass('on')) {
            selection.removeClass('on');
            selection.addClass('off');
        } else {
            selection.removeClass('off');
            selection.addClass('on');
        }
    },
    initEvents : function() {
        $(document).on('click','.PartnerApplicationSelection .appentry', this.toggleApp);
        $(document).on('click','.PartnerApplicationSelection #saveapplicationlist', this.saveList);
    }
};

var partnerapp = new app.PartnerApplicationSelection();
partnerapp.initEvents();

