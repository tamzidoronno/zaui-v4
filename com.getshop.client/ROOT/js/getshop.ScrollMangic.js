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
        var cell = $('.gsucell[cellid="'+cellId+'"]');
        var cellSettings = cell.attr('data-settings');

        if (!cellSettings) {
            return;
        }
        
        cellSettings = JSON.parse(cellSettings);
        
        var trigger = cell.closest('.gsucell_extra_outer').find('.getshopScrollMagicTriggerRow[type="normal"]');
        var triggerOutside = cell.closest('.gsucell_extra_outer').find('.getshopScrollMagicTriggerRow[type="outside"]');
        
        if ($('.gseditormode').length > 0) {
            return;
        }
        
        if (cellSettings.scrollFadeIn) {
           getshopScrollMagic.addFadeScene(cell, cellSettings, trigger);
        }

        if (cellSettings.paralexxRow) {
            if (cellSettings.parallaxEffectType === "parallax1") {
                getshopScrollMagic.addParalaxxScene2(cell, cellSettings, triggerOutside);                
            }
            
            if (cellSettings.parallaxEffectType === "parallax2") {
                getshopScrollMagic.addParalaxxScene(cell, cellSettings, trigger);
            }

        }
    },
    
    addParalaxxScene2: function(cell, cellSettings, trigger) {;
        var cell = cell.closest('.gsucell_outer');
        var inner =  $('.gsucell[cellid="'+cell.attr("cellid")+'"]');
        var extraInner =  inner.find('div.gsinner').first();
        var height = cell.outerHeight(true);
        
        var doubleHeight = height*2;
        var duration = doubleHeight;
        var offset = cellSettings.parallaxoffset;
        
        if (cell.position().top < $(window).height()) {
            offset += $(window).height()-cell.position().top;
        }
        
        var fromTop = 0;

        var ease = cellSettings.easey;
        
        inner.css('height',doubleHeight+"px");
        inner.css('top',"-"+fromTop+"px");
        extraInner.css('top',fromTop+"px");
        inner.css('position',"relative");

        cell.css('height',(height)+"px");
        cell.css('overflow',"hidden");
        
        new ScrollMagic.Scene({triggerElement: '#'+trigger.attr("id"), triggerHook: "onEnter", duration: duration, offset: offset})
            .setTween(inner, {y: ease+"%", ease: Linear.easeNone})
            .addTo(getshopScrollMagic.controller);
    
        // Sticks the applications so they move with the page.
        new ScrollMagic.Scene({triggerElement: '#'+trigger.attr("id"), triggerHook: "onEnter", duration: duration, offset: offset})
            .setTween(extraInner, {y: "-"+ease+"%", ease: Linear.easeNone})
            .addTo(getshopScrollMagic.controller);
    },
    

    
//    This one works for the intended usage ( a row becomes parallaxed.
    addParalaxxScene: function(cell, cellSettings, trigger) {
        var inner =  $('.gsucell[cellid="'+cell.attr("cellid")+'"] div.gsinner').first();
        var height = cell.outerHeight(true);
        var doubleHeight = $(window).height();
        var duration = doubleHeight*2;
        var fromTop = ""+doubleHeight*0.25;
        
        inner.css('height',doubleHeight+"px");
        inner.css('top',"-"+fromTop+"px");

        cell.css('height',height+"px");
        cell.css('overflow',"hidden");
        
        console.log("========");
        console.log(doubleHeight);
        console.log(Math.floor((doubleHeight*0.25)+height));
        console.log(duration);
        
        new ScrollMagic.Scene({triggerElement: '#'+trigger.attr("id"), triggerHook: "onEnter", duration: duration, offset: cellSettings.parallaxoffset})
            .setTween(inner, {y: cellSettings.easey+"%", ease: Linear.easeNone})
            .addTo(getshopScrollMagic.controller);
    },
    
    addFadeScene: function(cell, cellSettings, trigger) {
        cell.css('opacity', cellSettings.scrollFadeInStartOpacity);
        cell.css('right', cellSettings.slideLeft+'px');
        cell.css('top', (cellSettings.slideTop*-1)+'px');

        var scrollSettings = {
            opacity: cellSettings.scrollFadeInEndOpacity,
            translateX: cellSettings.slideLeft,
            translateY: cellSettings.slideTop
        }

        new ScrollMagic.Scene({triggerElement: '#'+trigger.attr("id")})
            .setVelocity('.gscell[cellid="'+cell.attr("cellid")+'"]', scrollSettings,
                {
                    duration: cellSettings.scrollFadeInDuration
                })
            .addTo(getshopScrollMagic.controller);
    }

}