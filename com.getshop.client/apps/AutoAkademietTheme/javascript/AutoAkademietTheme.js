var AutoAkademietTheme = {
    init: function() {
        var me = this;
        PubSub.subscribe('NAVIGATION_COMPLETED', me.pushFooter, me);
        PubSub.subscribe('CKEDITOR_SAVED', me.pushFooter, me);
    },
            
    pushFooter: function() {
        var theme = $('#skeleton').find('.skelholder').attr('theme');
        if (theme  === "b9cc5940-edef-11e2-91e2-0800200c9a66") {
            var headerHeight = $('.gs_outer.header').outerHeight(true);
            var outerHeight = $('.gs_outer_mainarea').outerHeight(true);
            var breadCrumbHeight = $('.gs_outer.breadcrumb').outerHeight(true);
            var total = headerHeight + outerHeight + breadCrumbHeight;
            var docHeight = $(document).height();
            var footerHeight = $('.gs_outer.footer').outerHeight(true);
            
            if ((total+footerHeight) < docHeight) {
                var addon = docHeight - total - footerHeight + 20;
                $('.gs_outer_mainarea').css('min-height', $('.gs_outer_mainarea').height()+addon);
            }
        }
    }
};

$(document).ready(function() {
    AutoAkademietTheme.init();
});