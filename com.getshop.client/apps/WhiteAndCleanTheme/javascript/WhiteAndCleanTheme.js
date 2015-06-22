//$(function() {
//  $('a[href*=#]:not([href=#])').click(function() {
//    if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
//      var target = $(this.hash);
//      target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
//      if (target.length) {
//        $('html,body').animate({
//          scrollTop: target.offset().top - 140
//        }, 500);
//        return false;
//      }
//    }
//  });
//});
//

//var isTop = true;
//$(window).scroll(function (event) {
//    var scroll = $(window).scrollTop();
//    if (scroll !== 0) {
//        if (isTop) {
//            isTop = false;
//            $('.gsdepth_0.gscount_0.gsrow img').animate({
//                height: 80
//            });
//            $('.gsarea[area="header"]').animate({
//                height: 80
//            });
//        }
//    } else {
//        $('.gsdepth_0.gscount_0.gsrow img').animate({
//            height: 150
//        });
//        $('.gsarea[area="header"]').animate({
//            height: 110
//        }, 20);
//        isTop = true;
//        
//    }
//});