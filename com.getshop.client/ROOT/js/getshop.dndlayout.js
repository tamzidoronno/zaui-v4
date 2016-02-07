thundashop.Namespace.Register('thundashop.dndlayout');

thundashop.dndlayout = {
    dragType : null,
    dragElement : null,
    landedOnCellid : null,
    direction : null,
    

    dragHelper : function() {
        return $("<div class='gsdndhelpertext'>Drag to location where you want to create a new row or column.</div>");
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
        var zindex = 100;
        $('.gsinner').each(function() {
                thundashop.dndlayout.landedOnCellid = null;
                var dot = $('<span class="gsdragspot"></span>');
                var topSpot = $('<span class="gsdot"><i class="fa fa-circle"></i></span>');
                var bottomSpot = $('<span class="gsdot"><i class="fa fa-circle"></i></span>');
                
                dot.addClass(thundashop.dndlayout.dragType);
                topSpot.addClass(thundashop.dndlayout.dragType);
                bottomSpot.addClass(thundashop.dndlayout.dragType);
               
                dot.attr('cellid', $(this).closest('.gsucell').attr('cellid'));
                dot.append(bottomSpot);
//                dot.append(topSpot);
                var children = $(this).children('.gsucell').length;
                var top = (children*5) * -1;
                var bottom = top * -1;
                console.log(children + " - " + top);
                if(thundashop.dndlayout.dragType === "row") {
                    bottomSpot.css('bottom',top+"px");
                    bottomSpot.css('left',$(this).width()/2);
                    topSpot.css('left',$(this).width()/2);
                    topSpot.css('top',top+'px');
                } else {
                    bottomSpot.css('top',$(this).height()/2);
                    bottomSpot.css('left',top+"px");
                    topSpot.css('left',$(this).width() + (top*-1));
                    topSpot.css('top',$(this).height()/2);
                }
               
                topSpot.on('mouseenter', function() {
                    console.log('enter2');
                    var cellid = $(this).closest('.gsdragspot').attr('cellid');
                    thundashop.dndlayout.landedOnCellid = cellid;
                    if(thundashop.dndlayout.dragType == "row") {
                        thundashop.dndlayout.direction = "top";
                    } else {
                        thundashop.dndlayout.direction = "left";
                    }
                });
                bottomSpot.on('mouseenter', function() {
                    console.log('enter');
                    var cellid = $(this).closest('.gsdragspot').attr('cellid');
                    thundashop.dndlayout.landedOnCellid = cellid;
                    if(thundashop.dndlayout.dragType == "row") {
                        thundashop.dndlayout.direction = "bottom";
                    } else {
                        thundashop.dndlayout.direction = "right";
                    }
                });
                
                topSpot.on('mouseleave', function() {
                    thundashop.dndlayout.landedOnCellid = null;
                });
                bottomSpot.on('mouseleave', function() {
                    thundashop.dndlayout.landedOnCellid = null;
                });
                
                var parents = $(this).parents('.gsucell').length;
                if(parents === 1) {
                    $(this).on('mouseover', function() {
                        $('.gsdragspot').hide();
                        $(this).find('.gsdragspot').show();
                    });
                }
                dot.hide();
                $(this).prepend(dot);
        });

        $('.gsuicell').on('mouseover', function() {
        });
    },
    stopDragging : function() {
        if(thundashop.dndlayout.landedOnCellid) {
            var cellid = thundashop.dndlayout.landedOnCellid;
            var cell = $('.gsucell[cellid="'+cellid+'"]');
            var parent = "";
            var direction = thundashop.dndlayout.direction;
            var before = "";
            var type = "addrow";
            
            if(direction == "left" || direction == "right") {
                type = "addcolumn";
            }
                inside = true;
            
            if(direction === "top" || direction == "left") {
                before = cellid;
            }
//            if(!inside) {
//                before = cell.parent().closest('.gsrow').attr('cellid');
//                cellid = cell.parent().closest('.gsrow').parent().closest('.gsrow').attr('cellid');
//            }
            if(direction === "bottom" || direction == "right") {
                before = "";
            }
            
             var data = {
                "cellid" : cellid,
                "before" : before,
                "type" : type
            };
            var event = thundashop.Ajax.createEvent('','operateCell',$(this), data);
            thundashop.Ajax.post(event);
            
        } else {
            console.log('not landed');
        }
        $('.gsdragspot').remove();
    },    
};

$(function() {
   thundashop.dndlayout.init(); 
});
