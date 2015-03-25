thundashop.Namespace.Register('thundashop.app.LeftMenu');

thundashop.app.LeftMenu = {
    init: function() {
        PubSub.subscribe('IMAGE_UPLOADED', this.imageUploaded, this);
        $(document).on('click', '.LeftMenu .menu_0', thundashop.app.LeftMenu.topLevelClicked);
    },
    
    imageUploaded: function(msg, data) {
        if(data.appname == "LeftMenu") {
            thundashop.framework.reprintPage();
        }
    },
    
    topLevelClicked: function() {
        var toplevel = $(this).attr('toplevel');
        var subs = $('[toplevel="'+toplevel+'"]:not(.menu_0)');
        
        if (subs.length === 0) {
            return;
        }
        
        var data = {
            topLevel :toplevel
        };
        
        if ($(subs[0]).is(':visible')) {
            subs.slideUp();
            data.show = false;
        } else {
            subs.slideDown();
            data.show = true;
        }
        
        var event = thundashop.Ajax.createEvent("", "setShowHideEntries", this, data);
        thundashop.Ajax.postWithCallBack(event, function() {
            
        }, true);
    }
};

thundashop.app.LeftMenu.init();

thundashop.app.LeftMenu.activeDragId = null;
thundashop.app.LeftMenu.activateOrdering = function() {
    $(".LeftMenu .entry").draggable({
        start: function() {
            thundashop.app.LeftMenu.activeDragId = $(this).attr('parentid');
            $(this).closest('.LeftMenu').find('.droparea_split').show();
        },
        stop: function() {
            $(this).closest('.LeftMenu').find('.droparea_split').hide();
        },
        helper: "clone",
        axis: "y",
        distance: 20
    });

    $("ul, li").disableSelection();

    $(".LeftMenu .droparea").droppable({
        hoverClass: "droparea_hover",
        tolerance: "pointer",
        drop: function(event, ui) {
            var data = {
                "id": thundashop.app.LeftMenu.activeDragId,
                "parentid": $(this).attr('parentid'),
                "after" : $(this).attr('after')
            }
            var event = thundashop.Ajax.createEvent('', 'MoveEntry', $(this), data);
            thundashop.Ajax.post(event);
        }
    });
}

$('.LeftMenu .edit, .TopMenu .edit, .CategoryLister .edit').live('click', function(e) {
    $('.LeftMenu .menu').hide();
    $('.TopMenu .menu').hide();
    $('.CategoryLister .menu').hide();
    $(this).closest('.entry').find('.menu').slideDown();
});

$(document).live('click', function(e) {
    var target = $(e.target);
    if (target.closest('.menu').length > 0 || target.hasClass('edit')) {
        return;
    }
    $('.LeftMenu .menu').hide();
    $('.TopMenu .menu').hide();
    $('.CategoryLister .menu').hide();
});

$('.LeftMenu .addmenuentrybutton').live('click', function(e) {
    $(this).hide();
    $(this).closest('.LeftMenu').find('.addmenuentryform').show();
    $(this).closest('.LeftMenu').find('*[gsname="newentry"]').focus();
})