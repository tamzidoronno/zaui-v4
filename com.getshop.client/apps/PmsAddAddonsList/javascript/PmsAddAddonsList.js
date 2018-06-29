app.PmsAddAddonsList = {
    init : function() {
        $(document).on('click', '.PmsAddAddonsList .addAddonsToRoom', app.PmsAddAddonsList.addAddonsToRoom);
        $(document).on('click', '.PmsAddAddonsList .goBackToStart', app.PmsAddAddonsList.showFirstStep);
        $(document).on('click', '.PmsAddAddonsList .completeAddAddon', app.PmsAddAddonsList.completeAddAddon);
    },
    completeAddAddon : function() {
        var panel = $(this).closest('[gstype="form"]');
        var args = thundashop.framework.createGsArgs(panel);
        
        var event = thundashop.Ajax.createEvent('','addAdvancedAddons', $(this), args);
        if(args.quickadd === "true") {
            thundashop.Ajax.postWithCallBack(event, function() {
                thundashop.framework.reprintPage();
            });
        } else {
            thundashop.Ajax.postWithCallBack(event, function(res){
                app.PmsBookingRoomView.refresh();
            });
        }
    },
    showFirstStep : function() {
        $('.addaddonrows.step1').show();
        $('.addaddonrows.step2').hide();
    },
    addAddonsToRoom : function() {
        $('.addaddonrows.step1').hide();
        $('.addaddonrows.step2').show();
        var event = thundashop.Ajax.createEvent('','loadSecondAddAddonsStep',$(this), {
            "productId" : $(this).attr('productid'),
            "quickadd" : $(this).attr('quickadd')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.addaddonrows.step2').html(res);
        });
    },
    loadAddonsToBeAddedList : function() {
        var panel = $('.PmsAddAddonsList .addaddonsstep2');
        var args = thundashop.framework.createGsArgs(panel);
        var event = thundashop.Ajax.createEvent('','loadAddonsToBeAddedPreview',panel, args);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.addonpreview').html(res);
        });
    }
};

app.PmsAddAddonsList.init();