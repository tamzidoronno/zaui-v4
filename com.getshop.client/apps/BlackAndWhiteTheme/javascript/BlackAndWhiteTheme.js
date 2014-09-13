app.RoughTheme = {};

app.RoughTheme = {
    init: function() {
        if (this.isCurrentTheme()) {
            var me = this;
//            $('.mainarea').bind('resize', me.setCellsToRightHeight);
            PubSub.subscribe('NAVIGATION_COMPLETED', me.setCellsToRightHeight, me);
            PubSub.subscribe('CKEDITOR_SAVED', me.setCellsToRightHeight, me);
            this.setCellsToRightHeight();
        }
    },
    setCellsToRightHeight: function() {
            $('.mainarea').find('.gs_row').each(function() {
                var lineHeight = $(this).height();
                $(this).find('.gs_col').each(function() {
                    if ($(this).hasClass('.BottomButton')) {
                        debugger;
                        return;
                    }

                    if ($(this).height() < lineHeight) {
                        $(this).find('.applicationarea').each (function() {
                            $(this).outerHeight(lineHeight);
                            var height = $(this).height();
                            $(this).css('min-height', height+"px");
                            $(this).css("height", "");    
                        });
                    }
                });
            });

    },
    isCurrentTheme: function() {
        var currentThemeId = $('[theme]').attr('theme');
        return currentThemeId === "bcd06c3e-283b-4862-aafc-4d4b8209c9b8";
    }
};


$(window).load(function() {
    app.RoughTheme.init();
});
