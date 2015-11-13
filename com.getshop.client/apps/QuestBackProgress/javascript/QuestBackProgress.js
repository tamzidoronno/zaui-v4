app.QuestBackProgress = {
    oldDegree : 0,
    
    init: function() {
        
    },

    updateProgressBar: function(percent) {
        angle = 275 * (percent/100);
        
        $elem = $('.QuestBackProgress .speedgauge_outer .speedpin');
        $({deg: app.QuestBackProgress.oldDegree}).animate({deg: angle}, {
            duration: 1000,
            step: function(now) {
                // in the step-callback (that is fired each step of the animation),
                // you can use the `now` paramter which contains the current
                // animation-position (`0` up to `angle`)
                $elem.css({
                    transform: 'rotate(' + now + 'deg)'
                });
            }
        });
        
        app.QuestBackProgress.oldDegree = angle;
    }
}

app.QuestBackProgress.init();