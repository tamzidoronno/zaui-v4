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
        $('.gsfinalstepdnd').remove();
        $('.gsinner').each(function() {
            if($(this).find('.gsinner').length === 0) {
                thundashop.dndlayout.landedOnCellid = null;
                
                var cellid = $(this).closest('.gsucell').attr('cellid');
                var spot = $('<span class="gsdragspot" cellid="'+cellid+'"><span class="positioner"><span class="droprowindicator_top"></span><span class="droprowindicator_bottom"></span></span></span>');
                if(thundashop.dndlayout.dragType == "row") {
                    var topSpot = $('<span class="gsdropspot_top"><i class="fa fa-arrow-up"></i> Drop</span>');
                    var bottomSpot = $('<span class="gsdropspot_bottom"><i class="fa fa-arrow-down"></i> Drop</span>');
                } else {
                    var topSpot = $('<span class="gsdropspot_left"><i class="fa fa-arrow-left"></i> Drop</span>');
                    var bottomSpot = $('<span class="gsdropspot_right"><i class="fa fa-arrow-right"></i> Drop</span>');
                }
                
                spot.find('.positioner').prepend(topSpot);
                spot.find('.positioner').prepend(bottomSpot);
               
                topSpot.on('mouseenter', function() {
                    var cellid = $(this).closest('.gsdragspot').attr('cellid');
                    thundashop.dndlayout.landedOnCellid = cellid;
                    if(thundashop.dndlayout.dragType == "row") {
                        thundashop.dndlayout.direction = "top";
                    } else {
                        thundashop.dndlayout.direction = "left";
                    }
                });
                bottomSpot.on('mouseenter', function() {
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
                
                $(this).on('mouseover', function() {
                    $('.gsdragspot').hide();
                    spot.show();
                });
                spot.hide();
                $(this).append(spot);
            }
        });

        $('.gsuicell').on('mouseover', function() {
        });
    },
    createSelectBox : function(cell, offset, original) {
        var cellid = cell.attr('cellid');
        if(!cellid) {
            cellid = cell.closest('.gsucell').attr('cellid');
        }
        var direction = thundashop.dndlayout.direction;
        var box = $('<span class="gsfinalstepdnd gspositioner gs'+direction+'" direction="'+direction+'" cellid="'+cellid+'"><div class="gsselectionbox '+direction+'">select</div></span>');
        
        if(direction === "top") {
            box.find('.gsselectionbox').css('width', cell.width() + "px");
            box.find('.gsselectionbox').css('top',(offset*-1)+"px");
        }
        if(direction === "bottom") {
            box.find('.gsselectionbox').css('width', cell.width() + "px");
            box.find('.gsselectionbox').css('top',cell.height()+(offset)+"px");
        }
        if(direction === "left") {
            box.find('.gsselectionbox').css('width', "17px");
            box.find('.gsselectionbox').css('height', cell.height()+"px");
            box.find('.gsselectionbox').css('left',(offset*-1)+"px");
        }
        if(direction === "right") {
            box.find('.gsselectionbox').css('width', "17px");
            box.find('.gsselectionbox').css('height', original.height()+"px");
            box.find('.gsselectionbox').css('left',(original.width()+offset)+"px");
        }
        box.on('click', function() {
            var type = "addrow";
            
            if(direction == "left" || direction == "right") {
                type = "addcolumn";
            }

            var before = "";
            if(direction === "top" || direction == "left") {
                before = cellid;
            }
            
            if(direction === "bottom" || direction == "right") {
                before = "";
            }
            
             var data = {
                "cellid" : $(this).attr('cellid'),
                "before" : before,
                "type" : type
            };
            var event = thundashop.Ajax.createEvent('','operateCell',$(this), data);
            thundashop.Ajax.post(event);
        })
        
        return box;
    },
    
    stopDragging : function() {
        if(thundashop.dndlayout.landedOnCellid) {
            var direction = thundashop.dndlayout.direction;
            var cellid = thundashop.dndlayout.landedOnCellid;
            var cell = $('.gsucell[cellid="'+cellid+'"]');
            var select = thundashop.dndlayout.createSelectBox(cell, 0, cell);
            cell.prepend(select);
            var counter = 17;
            cell.parents('.gsinner').each(function() {
                if((direction === "top" || direction === "bottom") && $(this).closest('.gsucell').hasClass('gsrow')) {
                    var select = thundashop.dndlayout.createSelectBox($(this), counter, cell);
                    $(this).prepend(select);
                    counter += 17;
                }
                if((direction === "left" || direction === "right") && $(this).closest('.gsucell').hasClass('gscolumn')) {
                    var select = thundashop.dndlayout.createSelectBox($(this), counter, cell);
                    $(this).prepend(select);
                    counter += 17;
                }
            });
        }
        $('.gsdragspot').remove();
        $('.gscellbox').remove();
    },    
};

$(function() {
   thundashop.dndlayout.init(); 
});
