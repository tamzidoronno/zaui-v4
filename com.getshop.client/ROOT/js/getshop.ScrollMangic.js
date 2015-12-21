/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


getshopScrollMagic = {
    controller : new ScrollMagic.Controller(),

    rowLoadeded : function() {
        $('.gsucell').each(function() {
            var start = function() {
                getshopScrollMagic.rowLoaded(this);
            }
            
            var exec = $.proxy(start, $(this));
            $(document).ready(exec);
        })
    },
    
    rowLoaded: function(cell) {
        var cellId = cell.attr('cellid');
        
        if (!cell.attr('data-settings')) {
            return;
        }
        
        var cellSettings = JSON.parse(cell.attr('data-settings'));
        
        var trigger = cell.closest('.gsdepth_0.gsucell').find('.getshopScrollMagicTriggerRow');
        var triggerId = trigger.attr('id');

        var sceneFadeIn = false;
        
        if (cellSettings.scrollFadeIn) {
            cell.css('opacity', cellSettings.scrollFadeInStartOpacity);

            cell.css('right', cellSettings.slideLeft+'px');
            cell.css('top', (cellSettings.slideTop*-1)+'px');


            var scrollSettings = {
                opacity: cellSettings.scrollFadeInEndOpacity,
                translateX: cellSettings.slideLeft,
                translateY: cellSettings.slideTop
            }

            sceneFadeIn = new ScrollMagic.Scene({triggerElement: '#'+triggerId})
                .setVelocity('.gscell[cellid="'+cellId+'"]', scrollSettings, {
                    duration: cellSettings.scrollFadeInDuration
                });
        }

        
        if (cellSettings.paralexxRow) {
            
            if ($('.gseditormode').length > 0) {
                return;
            }
            
            var parent = $('.gs_cell_outer[cellid="'+cellId+'"]');
            var inner = $('.gsucell[cellid="'+cellId+'"]');

            if (parent.attr('par_done')) {
                return;
            }

            var rowHeight = inner.outerHeight(true);
            
            parent.attr('par_done', 'yes');
            parent.css('height', rowHeight+"px");
            parent.css('overflow', "hidden");
            parent.css('position', "relative");

            var fromTop = 0;
            var duration = (rowHeight*2);

            console.log(fromTop);
            // Inner
            inner.css('height', (rowHeight*2)+"px");
            inner.css('top', "0px");

            new ScrollMagic.Scene({triggerElement: $('#parallax_'+triggerId), duration: duration, offset: cellSettings.parallaxoffset})
                .setTween(inner, {y: cellSettings.easey+"%", ease: Linear.easeNone})
//                .addIndicators()
                .addTo(getshopScrollMagic.controller);

            
        }

        if (sceneFadeIn) {

            if (sceneFadeIn)
                sceneFadeIn.addTo(getshopScrollMagic.controller);

        }


    }

}