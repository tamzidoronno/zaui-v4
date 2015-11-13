app.GetShopAnimationFront = {
    start : function() {
              
            var center = $(window).width() / 2;
            var alignLeft = 260;
            
            $(function () { // wait for document ready
               var flightpath2 = {
                    entry: {
                        curviness: 1.2,
                        autoRotate: true,
                        values: [
                            {x: -100, y: 400},
                            {x: -200, y: 200}
                        ]
                    },
                    entry2: {
                        curviness: 1.7,
                        autoRotate: true,
                        values: [
                            {x: 13000, y: 200},
                            {x: -$(window).width()+center-alignLeft, y: 0}
                        ]
                    },
                    entry3: {
                        curviness: 1.2,
                        autoRotate: true,
                        values: [
                            {x: -$(window).width()+600, y: -300},
                            {x: -$(window).width()+center-alignLeft, y: 0}
                        ]
                    },
                    entry4: {
                        curviness: 1.2,
                        autoRotate: true,
                        values: [
                            {x: -$(window).width()-600, y: -200},
                            {x: -$(window).width()+center-alignLeft, y: 0}
                        ]
                    },
                    entry5: {
                        curviness: 1.2,
                        autoRotate: true,
                        values: [
                            {x: -$(window).width(), y: -100},
                            {x: -$(window).width()+center-alignLeft, y: 0}
                        ]
                    },
                    entry6: {
                        curviness: 1.2,
                        autoRotate: true,
                        values: [
                            {x: -$(window).width()+200, y: -100},
                            {x: -$(window).width()+center-alignLeft, y: 0}
                        ]
                    },
                };
                
                 var flightpath = {
                    entry: {
                        curviness: 1.25,
                        autoRotate: true,
                        values: [
                            {x: 00, y: -20},
                            {x: 300, y: 10}
                        ]
                    },
                    looping: {
                        curviness: 1.25,
                        autoRotate: true,
                        values: [
                            {x: 510-alignLeft+200, y: 60},
                            {x: 620-alignLeft+200, y: -60},
                            {x: 500-alignLeft+200, y: -100},
                            {x: 380-alignLeft+200, y: 20},
                            {x: 500-alignLeft+200, y: 60},
                            {x: 580-alignLeft+200, y: 20},
                            {x: center-alignLeft+200, y: 25}
                        ]
                    }
                };
                // init controller
                var controller = new ScrollMagic.Controller();

                // create tween
                var tw1 = TweenMax.to($("#plane2"), 1, {css: {bezier: flightpath.entry}, ease: Power1.easeInOut});
                var tw2 = TweenMax.to($("#plane2"), 1, {css: {bezier: flightpath.looping}, ease: Power1.easeInOut});
                var tw3 = TweenMax.to($("#animation_l1"), 1, {css: {bezier: flightpath2.entry}, ease: Power1.easeInOut});
                var tw4 = TweenMax.to($("#animation_l2"), 1, {css: {bezier: flightpath2.entry2}, ease: Power1.easeInOut});
                var tw5 = TweenMax.to($("#animation_l3"), 1, {css: {bezier: flightpath2.entry3}, ease: Power1.easeInOut});
                var tw6 = TweenMax.to($("#animation_l4"), 1, {css: {bezier: flightpath2.entry4}, ease: Power1.easeInOut});
                var tw7 = TweenMax.to($("#animation_l5"), 1, {css: {bezier: flightpath2.entry5}, ease: Power1.easeInOut}); 
                var tw8 = TweenMax.to($("#animation_l6"), 1, {css: {bezier: flightpath2.entry}, ease: Power1.easeInOut});  
                var tw9 = TweenMax.to($("#animation_l7"), 1, {css: {bezier: flightpath2.entry}, ease: Power1.easeInOut});
                
                var tl = new TimelineMax().add([tw1,tw2,tw3,tw4,tw5,tw6,tw7,tw8, tw9], 'sequence');

                // build scene
                var scene = new ScrollMagic.Scene({triggerElement: "#trigger", duration: 1000, offset: 700});
                scene = scene.setPin("#target");
                scene = scene.setTween(tl);
                scene = scene.addTo(controller);
//                scene = scene.addIndicators() // add indicators (requires plugin)
            });
            
    }}

