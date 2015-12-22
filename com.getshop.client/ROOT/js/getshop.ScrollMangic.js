/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


getshopScrollMagic = {
    controller : new ScrollMagic.Controller(),

    rowLoaded: function(cellId) {
        var data = {
            cellId : cellId
        }
        
        var execFuntion = $.proxy(function() {
            getshopScrollMagic.rowLoadedInner(data.cellId);
        }, data);

        $(document).ready(function() {
            execFuntion();
        });
    },
    
    rowLoadedInner: function(cellId) {
        var cell = $('.gscell[cellid="'+cellId+'"]');
        var cellSettings = cell.find('.gsCellSettings_attrs');

        var trigger = cell.closest('.gsdepth_0.gsucell').find('.getshopScrollMagicTriggerRow');
        
        if ($('.gseditormode').length > 0) {
            return;
        }
        
        if (cellSettings.attr('scrollFadeIn')) {
           getshopScrollMagic.addFadeScene(cell, cellSettings, trigger);
        }

        if (cellSettings.attr('paralexxRow')) {
            getshopScrollMagic.addParalaxxScene(cell, cellSettings, trigger);
        }
    },
    
    addParalaxxScene: function(cell, cellSettings, trigger) {
        var inner =  $('.gscell[cellid="'+cell.attr("cellid")+'"] > div.gsinner');
        var height = cell.outerHeight(true);
        var doubleHeight = height*2;
        var duration = height*4;
        
        inner.css('height',doubleHeight+"px");
        inner.css('top',"-"+(height*0.75)+"px");

        cell.css('height',height+"px");
        cell.css('overflow',"hidden");
        
        new ScrollMagic.Scene({triggerElement: '#'+trigger.attr("id"), triggerHook: "onEnter", duration: duration})
            .setTween('.gscell[cellid="'+cell.attr("cellid")+'"] > div', {y: "100%", ease: Linear.easeNone})
            .addTo(getshopScrollMagic.controller);
    },
    
    addFadeScene: function(cell, cellSettings, trigger) {
        cell.css('opacity', cellSettings.attr('scrollFadeInStartOpacity'));
        cell.css('right', cellSettings.attr('slideLeft')+'px');
        cell.css('top', (cellSettings.attr('slideTop')*-1)+'px');

        var scrollSettings = {
            opacity: cellSettings.attr('scrollFadeInEndOpacity'),
            translateX: cellSettings.attr('slideLeft'),
            translateY: cellSettings.attr('slideTop')
        }

        new ScrollMagic.Scene({triggerElement: '#'+trigger.attr("id")})
            .setVelocity('.gscell[cellid="'+cell.attr("cellid")+'"]', scrollSettings,
                {
                    duration: cellSettings.attr('scrollFadeInDuration')
                })
            .addTo(getshopScrollMagic.controller);
    }

}