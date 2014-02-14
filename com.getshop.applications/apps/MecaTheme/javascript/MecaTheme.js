var BlueEnergyTheme = {
    init: function() {
        var me = this;
        PubSub.subscribe('NAVIGATION_COMPLETED', me.pushLeft, me);
        PubSub.subscribe('CKEDITOR_SAVED', me.pushLeft, me);
    },
            
    getPaddingHeight: function(dom) {
        var paddingCss = dom.css('padding');
        
        if (!paddingCss) {
            return 0;
        }
        
        padding = parseInt(paddingCss.replace('px', ''))*2;
        return padding;
    },

    changeRowHeights: function(rowNumber) {
        $('.gs_row_'+rowNumber+' .applicationarea').css('min-height','');
        
        var maxHeight = 0;
        var me = this;
        $('.gs_row_'+rowNumber).each(function() {
            var dom = $(this).find('.applicationarea');
            var height = dom.height();
            if (height > maxHeight) {
                maxHeight = height;
            }
        });
        $('.gs_row_'+rowNumber+' .applicationarea').css('min-height',maxHeight + "px");
    },
            
    pushLeft: function() {

        var width = $(document).width();
        if(width > 800) {
            var height = $('[area=left_1]').closest('table').height();
            var padding = this.getPaddingHeight($('[area=left_1]'));
            height -= padding;
            $('[area=left_1]').css('min-height', height +"px");

            var rows = [];
            $('.gs_row_cell').each(function() {
                var row = $(this).attr('row');
                row = parseInt(row);
                if ($.inArray(row, rows) < 0) {
                    rows.push(row);
                }
            });

            for (var i in rows) {
                var row = rows[i];
                this.changeRowHeights(row);
            }
        }
    }
}

$(document).ready(function() {
    BlueEnergyTheme.init();
});
