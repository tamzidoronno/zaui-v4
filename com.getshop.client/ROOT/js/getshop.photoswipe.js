getshop.photoswipe = {
    init: function () {
        $(document).on('click','.gsgallery',getshop.photoswipe.loadGallery);
    },
    loadGallery: function () {
        if(!$('.pswp').is(':visible')) {
            $('.pswp').show();
        }
        var pswpElement = document.querySelectorAll('.pswp')[0];
        var items = [];
        var thumbnail = $(this);
        $(this).closest('.gsgalleryroot').find('.gsgallery').each(function() {
            items.push({
                "src" : $(this).attr('img'),
                "w" : $(this).attr('width'),
                "h" : $(this).attr('height')
            });
        });
        var options = {
            index: 0,
            getThumbBoundsFn: function(index) {
                // See Options -> getThumbBoundsFn section of documentation for more info
                var pageYScroll = window.pageYOffset || document.documentElement.scrollTop,
                    rect = thumbnail[ 0 ].getBoundingClientRect();

                return {x:rect.left, y:rect.top + pageYScroll, w:rect.width};
            }
        };
        var gallery = new PhotoSwipe(pswpElement, PhotoSwipeUI_Default, items, options);
        gallery.init();
    }
};

getshop.photoswipe.init();