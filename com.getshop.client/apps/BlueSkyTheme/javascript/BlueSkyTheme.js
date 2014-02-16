GetShop.BlueSkyTheme = {
    ie : function(){
        var undef,
            v = 3,
            div = document.createElement('div'),
            all = div.getElementsByTagName('i');

        while (
            div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->',
            all[0]
        );

        return v > 4 ? v : undef;

    },
            
    init : function() {
        var me = this;
        this.updateProductSize();
        PubSub.subscribe('NAVIGATION_COMPLETED', me.updateProductSize, me);
        PubSub.subscribe('CKEDITOR_SAVED', me.updateProductSize, me);
    },
            
    updateProductSize: function(msg, data) {
        if ($('.ProductManager .left-wrapper').size() === 0) {
            return;
        }
        
        if ($('[theme=929e10e0-af3b-11e2-9e96-0800200c9a66]').size() === 0) {
            return;
        }
        
        $('.ProductManager .left-wrapper').height('auto');
        $('.ProductManager .right-wrapper').height('auto');
        
        var height = $('.ProductManager .left-wrapper').height();
        var height2 = $('.ProductManager .right-wrapper').height();
        
        if (height > height2) {
            $('.ProductManager .right-wrapper').height(height);
        } else if (height2 > height) {
            $('.ProductManager .left-wrapper').height(height2);
        }
    }
};

$(document).ready(function() {
    GetShop.BlueSkyTheme.init();
});
