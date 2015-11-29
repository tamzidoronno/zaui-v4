thundashop.Namespace.Register('thundashop.dndlayout');

thundashop.dndlayout = {
    dragType : null,
    dragElement : null,
    
    stopDragging : function() {
        var droppedOn = $('.gsborderpointerhover');
        if(droppedOn.length > 0) {
            var cellid = droppedOn.attr('cellid');
            var edge = "";
            if(thundashop.dndlayout.dragType === "row") {
                edge = "top";
                if(droppedOn.hasClass('gsborderpointer_column_bottom')) {
                    edge = "bottom";
                }
            } else {
                edge = "left";
                if(droppedOn.hasClass('gsborderpointer_column_bottom')) {
                    edge = "right";
                }
            }
            console.log('Dropped on: ' + cellid + " egde: "  + edge + " type: " + thundashop.dndlayout.dragType);
            var data = {
                "cellid" : cellid,
                "edge" : edge,
                "type" : thundashop.dndlayout.dragType
            }
            var event = thundashop.Ajax.createEvent('','dndAddCell',$(this), data);
            thundashop.Ajax.post(event);
        }

        thundashop.dndlayout.removeGsSpacing();
        $('.gsuicell').off('mouseover',thundashop.dndlayout.drawlayout);
        $('.gsdroppingarea').remove();
        thundashop.dndlayout.removeDndBorders();
        thundashop.dndlayout.dragElement = null;
    },
    dragHelper : function() {
        return $("<div class='gsdndhelpertext'>Drag to location where you want to create a new row or column.</div>");
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
            drag : thundashop.dndlayout.addDndBorders
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
            drag : thundashop.dndlayout.addDndBorders
        });
    },
    removeDndBorders : function() {
        $('.gsborderpointer').remove();
    },
    addDndBorders : function(event, ui) {
        if(!thundashop.dndlayout.dragElement) {
            return;
        }
        var type = thundashop.dndlayout.dragType;
        var onElement = thundashop.dndlayout.dragElement;
        if(onElement.hasClass('gsdndbordersadded')) {
            return;
        }
        thundashop.dndlayout.removeDndBorders();
        $('.gsdndbordersadded').removeClass('gsdndbordersadded');
        var toAddOn = onElement;
        var index = 1;
        var size = toAddOn.parents('.gsuicell').length+1;
        thundashop.dndlayout.drawDndBorder(toAddOn, 0,size);
        toAddOn.parents('.gsuicell').each(function() {
            thundashop.dndlayout.drawDndBorder($(this), index,size);
            index++;
        });
    },
    hideDndBorders : function() {
        var type = thundashop.dndlayout.dragType;
        $('.gsborderpointer').hide();
    },
    showDndBorders : function() {
        var type = thundashop.dndlayout.dragType;
        $('.gsborderpointer').show();
    },
    drawDndBorder : function(onElement, index, size) {
        onElement.addClass('gsdndbordersadded');
        var cellid = onElement.closest('.gsucell').attr('cellid');
        
        var type = thundashop.dndlayout.dragType;
        
        var border = $('<span></span>');
        var border2 = $('<span></span>');
        border.addClass('gsborderpointer_'+type+'_top');
        border.addClass('gsborderpointer');
        border2.addClass('gsborderpointer_'+type+'_bottom');
        border2.addClass('gsborderpointer');
        
        border.attr('cellid',cellid);
        border2.attr('cellid',cellid);
        var size = 10 + ((size - index) * 5);
        
        var top = onElement.offset().top - $(window).scrollTop();
        
        if(type === "row") {
            border.css('width',onElement.outerWidth());
            border.css('top',top-index);
            border.css('left',onElement.offset().left);
            border.height(size);
            
            border2.css('width',onElement.outerWidth());
            border2.css('left',onElement.offset().left);
            border2.css('top',top+onElement.outerHeight()-size-index);
            border2.height(size);
        }
        if(type === "column") {
            border.css('height',onElement.outerHeight());
            border.css('top',top);
            border.css('left',onElement.offset().left-index);
            border.width(size);
            
            border2.css('height',onElement.outerHeight());
            border2.css('left',onElement.offset().left+onElement.outerWidth()+index-size);
            border2.css('top',top);
            border2.width(size);
        }
        
        border.mouseenter(thundashop.dndlayout.dndBorderMouseOver);
        border2.mouseenter(thundashop.dndlayout.dndBorderMouseOver);
        border.mouseleave(thundashop.dndlayout.dndBorderMouseLeave);
        border2.mouseleave(thundashop.dndlayout.dndBorderMouseLeave);
        
        thundashop.dndlayout.addArrows(border, type, size);
        thundashop.dndlayout.addArrows(border2, type, size);
        
        $('body').append(border);
        $('body').append(border2);
    },
    addArrows : function(element, type, size) {
        element.css('font-size', (size-5)+"px");
        if(type === "row") {
            element.append("<i class='fa fa-arrow-up'></i>");
            element.append("<i class='fa fa-arrow-down'></i>");
        } else {
            element.append("<i class='fa fa-arrow-right'></i>");
            element.append("<i class='fa fa-arrow-left'></i>");
        }
    },
    dndBorderMouseOver : function(event) {
        var target = $(event.currentTarget);
        var cellid = target.attr('cellid');
        var cellToWorkOn = $('.gsucell[cellid="'+cellid+'"]').children('.gsinner');
        target.addClass('gsborderpointerhover');        
        var dragtype = thundashop.dndlayout.dragType;
        var type = "top";
        if(target.hasClass('gsborderpointer_'+dragtype+'_bottom')) {
            type = "bottom";
        }

        cellToWorkOn.addClass('gsborderpointerhover_'+dragtype+'_'+type);
        target.show();
    },
    dndBorderMouseLeave : function() {
        var dragtype = thundashop.dndlayout.dragType;
        $('.gsborderpointerhover_'+dragtype+'_top').removeClass('gsborderpointerhover_'+dragtype+'_top');
        $('.gsborderpointerhover_'+dragtype+'_bottom').removeClass('gsborderpointerhover_'+dragtype+'_bottom');
        $(event.currentTarget).removeClass('gsborderpointerhover');
        thundashop.dndlayout.showDndBorders();
    },
    removeGsSpacing : function() {
        $('.gsaddcontentlayoutbox').removeClass('gsaddcontentlayoutbox');
        $('.gsaddlayoutspacerleft').removeClass('gsaddlayoutspacerleft');
        $('.gsaddlayoutspacerright').removeClass('gsaddlayoutspacerright');
        $('.gsaddlayoutspacertop').removeClass('gsaddlayoutspacertop');
        $('.gsaddlayoutspacerbottom').removeClass('gsaddlayoutspacerbottom');
    },
    
    drawDroppingArea : function(edge, onElement, drawSpecials) {
        if(!onElement) {
            return;
        }
        var cellid = onElement.closest('.gscell').attr('cellid');
        if($('.gsdroppingarea[cellid="'+cellid+'"][edge="'+edge+'"]').length > 0) {
           return; 
        }
        if(drawSpecials) {
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
            area.css('height','20px');
            area.css('width',onElement.width());
            if(drawSpecials) {
                area.css('top',topOffset+onElement.height());
                onElement.addClass('gsaddlayoutspacerbottom');
            } else {
                area.css('top',topOffset+onElement.height()+20);
                onElement.addClass('gsaddlayoutspacerbottombottom');
            }
            onElement.addClass('gsaddlayoutspacerbottom');
        } else if(edge === "top") {
            area.css('left',onElement.offset().left);
            area.css('top',topOffset);
            area.css('height','20px');
            area.css('width',onElement.width());
            if(drawSpecials) {
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
