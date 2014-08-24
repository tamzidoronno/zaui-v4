var SedoxTheme = {
    init: function() {
        var me = this;
        PubSub.subscribe('NAVIGATION_COMPLETED', me.pushFooter, me);
        PubSub.subscribe('CKEDITOR_SAVED', me.pushFooter, me);
    },
            
    pushFooter: function() {
        var theme = $('#skeleton').find('.skelholder').attr('theme');
        if (theme  === "4e2598a8-47d9-4400-ba72-a18b2b45bcb5") {
            var headerHeight = $('.gs_outer.header').outerHeight(true);
            var outerHeight = $('.gs_outer_mainarea').outerHeight(true);
            var breadCrumbHeight = $('.gs_outer.breadcrumb').outerHeight(true);
            var total = headerHeight + outerHeight + breadCrumbHeight;
            var docHeight = $(document).height();
            var footerHeight = $('.gs_outer.footer').outerHeight(true);

            if ((total+footerHeight) < docHeight) {
                var addon = docHeight - total - footerHeight;
                $('.gs_outer_mainarea').css('min-height', $('.gs_outer_mainarea').height()+addon);
            }
        }
    },
            
    loadJS: function(src, callback) {
        var s = document.createElement('script');
        s.src = src;
        s.async = true;
        s.onreadystatechange = s.onload = function() {
            var state = s.readyState;
            if (!callback.done && (!state || /loaded|complete/.test(state))) {
                callback.done = true;
                callback();
            }
        };
        document.getElementsByTagName('head')[0].appendChild(s);
    }
}

SedoxTheme.loadJS('//use.typekit.net/spw7lup.js', function() {
    try { Typekit.load(); } catch(e) {}
});

