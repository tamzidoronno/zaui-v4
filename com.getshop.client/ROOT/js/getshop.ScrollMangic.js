/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


getshopScrollMagic = {
    controllers : {},

        rowLoaded: function(cellId) {
        var cell = $('.gscell[cellid="'+cellId+'"]');
        var cellSettings = cell.find('.gsCellSettings_attrs');

        var trigger = cell.closest('.gsdepth_0.gsucell').find('.getshopScrollMagicTriggerRow');
        var triggerId = trigger.attr('id');

        var sceneFadeIn = false;

        if (cellSettings.attr('scrollFadeIn')) {
            cell.css('opacity', cellSettings.attr('scrollFadeInStartOpacity'));

            cell.css('right', cellSettings.attr('slideLeft')+'px');
            cell.css('top', (cellSettings.attr('slideTop')*-1)+'px');


            var scrollSettings = {
                opacity: cellSettings.attr('scrollFadeInEndOpacity'),
                translateX: cellSettings.attr('slideLeft'),
                translateY: cellSettings.attr('slideTop')
            }

            sceneFadeIn = new ScrollMagic.Scene({triggerElement: '#'+triggerId})
                .setVelocity('.gscell[cellid="'+cellId+'"]', scrollSettings,
                    {
                        duration: cellSettings.attr('scrollFadeInDuration')
                    });
        }


        if (sceneFadeIn) {
            var controller = new ScrollMagic.Controller();

            if (sceneFadeIn)
                sceneFadeIn.addTo(controller);

        }



    }

}