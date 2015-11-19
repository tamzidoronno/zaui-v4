thundashop.Namespace.Register('thundashop.dndlayout');

thundashop.dndlayout = {
    dragType : null,
    dragElement : null,
    
    stopDragging : function() {
        var droppedOn = $('.gsdroppingareahover');
        if(droppedOn.length > 0) {
            console.log('Dropped on: ' + droppedOn.attr('cellId') + " egde: " + droppedOn.attr('edge') + " type: " + thundashop.dndlayout.dragType);
            var data = {
                "cellid" : droppedOn.attr('cellId'),
                "edge" : droppedOn.attr('edge'),
                "type" : thundashop.dndlayout.dragType
            }
            var event = thundashop.Ajax.createEvent('','dndAddCell',$(this), data);
            thundashop.Ajax.post(event);
        }

        thundashop.dndlayout.removeGsSpacing();
        $('.gsuicell').off('mouseover',thundashop.dndlayout.drawlayout);
        $('.gsdroppingarea').remove();
        $('.gsaddlayoutspacertoptop').removeClass('gsaddlayoutspacertoptop');
        thundashop.dndlayout.dragElement = null;
    },
    dragHelper : function() {
        return $("<div class='ui-widget-header'>Drag to location where you want to add content.</div>");
    },
    
    init: function () {

        $(".gsaddrowcontentdnd").draggable({
            cursor: "move",
            cursorAt: {top: -12, left: -20},
            start : function() {
                thundashop.dndlayout.dragType = "row";
                $('.gsuicell').on('mouseover',thundashop.dndlayout.drawlayout);
            },
            helper: thundashop.dndlayout.dragHelper,
            stop: thundashop.dndlayout.stopDragging,
            drag : thundashop.dndlayout.doMagic
        });
        $(".gsaddcolumncontentdnd").draggable({
            cursor: "move",
            cursorAt: {top: -12, left: -20},
            start : function() {
                thundashop.dndlayout.dragType = "column";
                $('.gsuicell').on('mouseover',thundashop.dndlayout.drawlayout);
            },
            helper: thundashop.dndlayout.dragHelper,
            stop: thundashop.dndlayout.stopDragging,
            drag : thundashop.dndlayout.doMagic
        });
    },
    doMagic : function(event, ui) {
//        console.log(ui);
        var edge = thundashop.dndlayout.getEdge(ui);
        var onElement = thundashop.dndlayout.dragElement;
        thundashop.dndlayout.drawDroppingArea(edge, onElement, true);
//        console.log(edge);
    },
    getEdge : function(ui) {
        if(!thundashop.dndlayout.dragElement) {
            return "";
        }
        var onElement = thundashop.dndlayout.dragElement;
        var offset = onElement.offset();
        var leftDiff = ui.offset.left - offset.left -20;
        var topDiff = ui.offset.top - offset.top - 10;
        var percentageToLeft = (leftDiff / onElement.width()) * 100;
        var percentageToTop = (topDiff / onElement.height()) * 100;
        if(thundashop.dndlayout.dragType === "row") {
            if(percentageToTop < 50) {
                return "top";
            }
            return "bottom";
        } else {
            if(percentageToLeft < 25) {
                return "left";
            }
            if(percentageToLeft < 50) {
                return "center-left";
            }
            if(percentageToLeft < 75) {
                return "center-right";
            }
            return "right";
        }
        
    },
    removeGsSpacing : function() {
        $('.gsaddcontentlayoutbox').removeClass('gsaddcontentlayoutbox');
        $('.gsaddlayoutspacerleft').removeClass('gsaddlayoutspacerleft');
        $('.gsaddlayoutspacerright').removeClass('gsaddlayoutspacerright');
        $('.gsaddlayoutspacertop').removeClass('gsaddlayoutspacertop');
        $('.gsaddlayoutspacerbottom').removeClass('gsaddlayoutspacerbottom');
    },
    
    drawDroppingArea : function(edge, onElement, drawRowOnTop) {
        if(!onElement) {
            return;
        }
        var cellid = onElement.closest('.gscell').attr('cellid');
        if($('.gsdroppingarea[cellid="'+cellid+'"][edge="'+edge+'"]').length > 0) {
           return; 
        }
        if(drawRowOnTop) {
            thundashop.dndlayout.removeGsSpacing();
            $('.gsdroppingarea').remove();
        }
        
        if(!onElement.closest('.gsucell').hasClass('gscolumn')) {
            if(edge === "center-left") {
                edge = "left";
            }
            if(edge === "center-right") {
                edge = "right";
            }
        }
        
        var area = $('<div class="gsdroppingarea" style="background-color:#000;" ></div>');
//        console.log(onElement.attr('class'));
        area.attr('cellid',cellid);
        area.attr('edge',edge);
        var topOffset = onElement.offset().top - $(window).scrollTop();
        area.css('position','fixed');
        if(edge === "left") {
            area.css('left',onElement.offset().left);
            area.css('top',topOffset);
            area.css('width','20px');
            area.css('height',onElement.height());
            onElement.addClass('gsaddlayoutspacerleft');
        } else if(edge === "center-left") {
            area.css('left',onElement.offset().left);
            area.css('top',topOffset);
            area.css('width',onElement.width()/2);
            area.css('height',onElement.height());
        } else if(edge === "center-right") {
            area.css('left',onElement.offset().left+onElement.width()/2);
            area.css('top',topOffset);
            area.css('width',onElement.width()/2);
            area.css('height',onElement.height());
        } else if(edge === "bottom") {
            area.css('left',onElement.offset().left);
            area.css('top',topOffset+onElement.height());
            area.css('height','20px');
            area.css('width',onElement.width());
            onElement.addClass('gsaddlayoutspacerbottom');
        } else if(edge === "top") {
            area.css('left',onElement.offset().left);
            area.css('top',topOffset);
            area.css('height','20px');
            area.css('width',onElement.width());
            if(drawRowOnTop) {
                onElement.addClass('gsaddlayoutspacertop');
            } else {
                onElement.addClass('gsaddlayoutspacertoptop');
            }
        } else if(edge === "right") {
            area.css('left',onElement.offset().left+onElement.width()-20);
            area.css('top',topOffset);
            area.css('width','20px');
            area.css('height',onElement.height());
            onElement.addClass('gsaddlayoutspacerright');
        }
        
        var row = onElement.closest('.gsrow').find('.gsinner').first();
        if(drawRowOnTop && row.children('.gscolumn').length > 0 && thundashop.dndlayout.dragType === "row") {
            thundashop.dndlayout.drawDroppingArea("top",row, false);
        }
        area.on('mouseenter', function() {
            $(this).addClass('gsdroppingareahover');
        });
        area.on('mouseleave', function() {
            $(this).removeClass('gsdroppingareahover');
        });
        $('body').append(area);
    },
    
    drawlayout : function() {
        var element = $(this);
        if(element.find('.gsuicell').length > 0) {
            return;
        }
        thundashop.dndlayout.dragElement = $(this);
        
        var row = $(this).closest('.gsrow').find('.gsinner').first();
        if(row.children('.gscolumn').length === 0) {
            $('.gsaddlayoutspacertoptop').removeClass('gsaddlayoutspacertoptop');
        }
    }
};

$(function() {
   thundashop.dndlayout.init(); 
});
