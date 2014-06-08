thundashop.Namespace.Register('thundashop.app.TopMenu');

thundashop.app.TopMenu.currentDragId = null;

thundashop.app.TopMenu.activateOrdering = function() {
    $( "#topmenuline li" ).draggable({
        start: function() {
             $('#topmenuline .droparea').show();
             thundashop.app.TopMenu.currentDragId = $(this).attr('menuid');
        },
        stop: function() {
             $('#topmenuline .droparea').hide();
        },
        distance: 5,
        helper: "clone",
        axis: "x",
        distance: 20
    });

    $( "ul, li" ).disableSelection();

    $( "#topmenuline .droparea" ).droppable({
        hoverClass: "droparea_hover",
        drop: function( event, ui ) {
            var data = {
                "id" : thundashop.app.TopMenu.currentDragId,
                "after" : $(this).closest('li').attr('menuid')
            }
            var event = thundashop.Ajax.createEvent('','MoveEntry',$(this), data);
        }
    });
}

thundashop.app.TopMenu.mouseOver = function() {
    var hasSubEntries = $(this).find('.subentries').length;
    if (hasSubEntries) {
        $($(this).find('.subentries')[0]).show();
    }
};

thundashop.app.TopMenu.mouseOut = function() {
    if($(window).width() > 800) {
        $(this).find('.subentries').hide();
    }
}

thundashop.app.TopMenu.showMobileMenu = function() {
    if( $('.TopMenu li:not(.mobile)').is(':visible')) {
        $('.TopMenu li:not(.mobile)').hide();
    } else {
        $('.TopMenu li:not(.mobile)').show();
    }
}

$('.TopMenu .mobile').live('click', function() {
    $('.TopMenu li:not(.mobile)').each(function() {
        if ($(this).is(":visible")) {
            $(this).hide();
        } else {
            $(this).show();
        }
    });
})

$('.TopMenu .addnew').live('click', function() {
    $(this).hide();
    $('#add_top_menu_entry').show().focus();
//    $('#add_top_menu_entry').focus();
});



$(document).on('mouseover', '.TopMenu li', thundashop.app.TopMenu.mouseOver);
$(document).on('mouseout', '.TopMenu li', thundashop.app.TopMenu.mouseOut);
$(document).on('click','.TopMenu .mobile_button', thundashop.app.TopMenu.showMobileMenu);