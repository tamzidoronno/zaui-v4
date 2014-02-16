var MtbTheme = {
    init: function() {
        var me = this;
        PubSub.subscribe('NAVIGATION_COMPLETED', me.pushFooter, me);
        PubSub.subscribe('CKEDITOR_SAVED', me.pushFooter, me);
    },
            
    pushFooter: function() {
        var theme = $('#skeleton').find('.skelholder').attr('theme');
        if (theme  === "1fd7f850-d5c4-11e2-8b8b-0800200c9a66") {
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
    }
}

$(document).ready(function() {
    MtbTheme.init();
});