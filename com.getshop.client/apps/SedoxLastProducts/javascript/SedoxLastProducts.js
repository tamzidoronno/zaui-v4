app.SedoxLastProducts = {
    switchTime: 5000,
    currenTimer: null,
    
    init: function() {
        
    },
    
    startTimer: function() {
        if (app.SedoxLastProducts.currenTimer) {
            clearTimeout(app.SedoxLastProducts.currenTimer);
        }
        
        app.SedoxLastProducts.currenTimer = setTimeout(app.SedoxLastProducts.slideToNext, app.SedoxLastProducts.switchTime);
    },
    
    slideToNext: function() {
        $('.SedoxLastProducts').each(function() {
            var next = false;
            var current = false;
            var nextNumber = 0;
            
            $(this).find('.sedox_internal_view').each(function() {
                if (!next) {
                    nextNumber++;
                }
                
                if (current && !next) {
                    next = $(this);            
                }

                if ($(this).is(':visible')) {
                    current = $(this);
                }
                
                
            });
            
            if (current && !next) {
                nextNumber = 1;
                next = $($(this).find('.sedox_internal_view')[0]);
            }
            
            
            current.fadeOut(300, function() {
                next.fadeIn(300);
            });
            
            $('.productcounter span').html(nextNumber);
//            
//            next.css('display', 'block');
//            next.animate({width: 'toggle'});

        });
        
        app.SedoxLastProducts.currenTimer = setTimeout(app.SedoxLastProducts.slideToNext, app.SedoxLastProducts.switchTime);
    }
};

app.SedoxLastProducts.init();