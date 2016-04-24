thundashop.Namespace.Register('thundashop.dndlayout');

thundashop.dndlayout = {
    dragType : null,
    dragElement : null,
    landedOnCellid : null,
    direction : null,
    
    dragHelper : function() {
        return $("<div class='gsdndhelpertext'>Drag to a cell you would like to modify.</div>");
    },
    
    init: function () {
        $(".gsaddrowcontentdnd").draggable({
            cursor: "move",
            cursorAt: {top: -12, left: -20},
            start : function() {
                thundashop.dndlayout.dragType = "row";
                thundashop.dndlayout.startDragging();
            },
            helper: thundashop.dndlayout.dragHelper,
            stop: thundashop.dndlayout.stopDragging
        });
        $(".gsaddcolumncontentdnd").draggable({
            cursor: "move",
            cursorAt: {top: -12, left: -20},
            start : function() {
                thundashop.dndlayout.dragType = "column";
                thundashop.dndlayout.startDragging();
            },
            helper: thundashop.dndlayout.dragHelper,
            stop: thundashop.dndlayout.stopDragging
        });
    },
    startDragging : function() {
        $('.gsfinalstepdnd').remove();
        $('.gsinner').each(function() {
            if($(this).find('.gsinner').length === 0) {
                var cellid = $(this).closest('.gsucell').attr('cellid');
                var spot = $('<span class="gsdragspot" cellid="'+cellid+'"><span class="positioner"><span class="droprowindicator_top"></span><span class="droprowindicator_bottom"></span></span></span>');
                
                $(this).on('mouseover', function() {
                    $('.gsdragspot').hide();
                    spot.show();
                    thundashop.dndlayout.landedOnCellid = cellid;
                });
                spot.hide();
                $(this).append(spot);
            }
        });

        $('.gsuicell').on('mouseover', function() {
        });
    },
    
    stopDragging : function() {
        var cellid = thundashop.dndlayout.landedOnCellid;
        $('.gsdragspot').remove();
        $('.gscellbox').remove();
        thundashop.dndlayout.activateCell(cellid);
    },
    stepup : function() {
        var cell = $('.gsucell[cellid="'+thundashop.dndlayout.landedOnCellid+'"]');
        cell = cell.parents('.gsucell').first();
        var cellid = cell.attr('cellid');
        thundashop.dndlayout.landedOnCellid = cellid;
        thundashop.dndlayout.activateCell(cellid);
    },
    
    activateCell : function(cellid) {
        $('.gsmodifycellindicator').remove();
        var cell = $('.gsucell[cellid="'+cellid+'"]');
        var offset = cell.offset();
        var dndpanel = $('.gsdndlayoutpanelouter');
        dndpanel.hide();
        
        $('.gsdndlayoutpanel').hide();
        if(cell.hasClass('gscolumn')) {
            $('.gscolumndndlayoutpanel').show();
        } else {
            $('.gsrowdndlayoutpanel').show();
        }
        dndpanel.find('.gsoperatecell').attr('cellid', cellid);
        dndpanel.css('position','absolute');
        dndpanel.css('top',offset.top + cell.outerHeight());
        dndpanel.css('left',offset.left);
        dndpanel.fadeIn();
        
        
        var spot = $('<span class="gsmodifycellindicator"></span>');
        cell.append(spot);
    }
};

$(function() {
    $('.gsdndpanelstepup').on('click', thundashop.dndlayout.stepup);
   thundashop.dndlayout.init(); 
});
