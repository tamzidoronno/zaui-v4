thundashop.Namespace.Register('thundashop.app.LeftMenu');

thundashop.app.LeftMenu = {
    init: function() {
        PubSub.subscribe('IMAGE_UPLOADED', this.imageUploaded, this);
    },
    
    imageUploaded: function(msg, data) {
        if(data.appname == "LeftMenu") {
            thundashop.framework.reprintPage();
        }
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
