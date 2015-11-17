thundashop.Namespace.Register('thundashop.dndlayout');

thundashop.dndlayout = {
    dragType : null,
    dragElement : null,
    
    init: function () {

        $(".gsaddrowcontentdnd").draggable({
            cursor: "move",
            cursorAt: {top: -12, left: -20},
            helper: function (event) {
                return $("<div class='ui-widget-header'>Drag to location where you want to add content.</div>");
            },
            start : function() {
                thundashop.dndlayout.dragType = "row";
                $('.gsuicell').on('mouseover',thundashop.dndlayout.drawlayout);
            },
            stop: function() {
                thundashop.dndlayout.removeGsSpacing();
                thundashop.dndlayout.removeGsSpacing();
                $('.gsuicell').off('mouseover',thundashop.dndlayout.drawlayout);
                thundashop.dndlayout.dragElement = null;
            },
            drag : thundashop.dndlayout.doMagic
        });
        $(".gsaddcolumncontentdnd").draggable({
            cursor: "move",
            cursorAt: {top: -12, left: -20},
            helper: function (event) {
                return $("<div class='ui-widget-header'>Drag to location where you want to add content.</div>");
            },
            start : function() {
                thundashop.dndlayout.dragType = "column";
                $('.gsuicell').on('mouseover',thundashop.dndlayout.drawlayout);
            },
            stop: function() {
                thundashop.dndlayout.removeGsSpacing();
                thundashop.dndlayout.removeGsSpacing();
                $('.gsuicell').off('mouseover',thundashop.dndlayout.drawlayout);
                thundashop.dndlayout.dragElement = null;
            },
            drag : thundashop.dndlayout.doMagic
        });
    },
    doMagic : function(event, ui) {
        var edge = thundashop.dndlayout.getEdge(ui);
        thundashop.dndlayout.drawDroppingArea(edge);
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
            if(percentageToLeft < 50) {
                return "left";
            }
            return "right";
        }
        
    },
    removeGsSpacing : function() {
        $('.gsaddcontentlayoutbox').removeClass('gsaddcontentlayoutbox');
        $('.gsdroppingarea').remove();
        $('.gsaddlayoutspacerleft').removeClass('gsaddlayoutspacerleft');
        $('.gsaddlayoutspacerright').removeClass('gsaddlayoutspacerright');
        $('.gsaddlayoutspacertop').removeClass('gsaddlayoutspacertop');
        $('.gsaddlayoutspacerbottom').removeClass('gsaddlayoutspacerbottom');
    },
    
    drawDroppingArea : function(edge) {
        var onElement = thundashop.dndlayout.dragElement;
        
        thundashop.dndlayout.removeGsSpacing();
        
        $('.gsdroppingarea').remove();
        var area = $('<div style="background-color:#000;z-index:3;" class="gsdroppingarea"></div>');
        
        var topOffset = onElement.offset().top - $(window).scrollTop();
        area.css('position','fixed');
        if(edge === "left") {
            area.css('left',onElement.offset().left);
            area.css('top',topOffset);
            area.css('width','20px');
            area.css('height',onElement.height());
            onElement.addClass('gsaddlayoutspacerleft');
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
            onElement.addClass('gsaddlayoutspacertop');
        } else if(edge === "right") {
            area.css('left',onElement.offset().left+onElement.width()-20);
            area.css('top',topOffset);
            area.css('width','20px');
            area.css('height',onElement.height());
            onElement.addClass('gsaddlayoutspacerright');
        }
        
        $('body').append(area);
    },
    
    removelayout : function() {
    },
    drawlayout : function() {
        console.log("Entered:" + $(this).width());
        var element = $(this);
        if(element.find('.gsuicell').length > 0) {
            return;
        }
        thundashop.dndlayout.dragElement = $(this);
    }
};

$(function() {
   thundashop.dndlayout.init(); 
});
