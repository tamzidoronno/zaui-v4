// VERSION: 2.3 LAST UPDATE: 11.07.2013
/* 
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 * 
 * Made by Wilq32, wilq32@gmail.com, Wroclaw, Poland, 01.2009
 * Website: http://code.google.com/p/jqueryrotate/ 
 */

(function($) {
    jQuery.fn.extend({
        rotate: function()
        {
            var sourceimage = $(this);

            function drawRotatedImage(angle) {
                var img = new Image;
                img.src = sourceimage.attr('src');

                var canvas = document.createElement("canvas");
                var ctx = canvas.getContext("2d");
                
                canvas.width = img.height;
                canvas.height = img.width;
                ctx.width = img.height;
                ctx.height = img.width;
                ctx.rotate(angle * Math.PI / 180);
                ctx.drawImage(img, 0, ctx.width*-1);
                var data = canvas.toDataURL('image/png');
                return data;
            }
            return drawRotatedImage(90);
        }
    });
})(jQuery);