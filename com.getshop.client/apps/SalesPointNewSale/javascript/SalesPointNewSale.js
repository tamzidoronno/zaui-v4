app.SalesPointNewSale = {
    init: function() {
        $(document).on('click', '.SalesPointNewSale .entry.all', app.SalesPointNewSale.leftMenuClicked);
        $(document).on('click', '.SalesPointNewSale .cancelcreatenew', app.SalesPointNewSale.cancelCreateNewTab);
        $(document).on('click', '.SalesPointNewSale .addProductToCurrentTab', app.SalesPointNewSale.addProductToCurrentTab);
        $(document).on('mouseover', '.SalesPointNewSale .cartitemline', app.SalesPointNewSale.showDetails);
        $(document).on('change', '.SalesPointNewSale .cartitemline .changecount', app.SalesPointNewSale.countChange);
        $(document).on('change', '.SalesPointNewSale .cartitemline .changeprice', app.SalesPointNewSale.priceChange);
        $(document).on('click', '.SalesPointNewSale .cartitemline .removefromtab', app.SalesPointNewSale.removeItemFromTab);
        $(document).on('click', '.SalesPointNewSale .deletetab', app.SalesPointNewSale.deleteTab);
        $(document).on('click', '.SalesPointNewSale .printoverview', app.SalesPointNewSale.printOverview);
        $(document).on('click', '.SalesPointNewSale .printcurrenttab', app.SalesPointNewSale.printCurrentTab);
        $(document).on('click', '.SalesPointNewSale .printkitchen', app.SalesPointNewSale.printCurrentToKitchen);
        $(document).on('click', '.SalesPointNewSale .countadd', app.SalesPointNewSale.countAddClicked);
        $(document).on('click', '.SalesPointNewSale .changeviewmenu', app.SalesPointNewSale.showListOfViews);
        $(document).on('click', '.SalesPointNewSale .product.checkbox', app.SalesPointNewSale.toggleCheckBox);
        $(document).on('click', '.SalesPointNewSale .configurelist', app.SalesPointNewSale.configureList);
        $(document).on('click', '.SalesPointNewSale .openList', app.SalesPointNewSale.openList);
    },
    
    openList: function() {
        var listId = $(this).attr('listid');
        var listToShow = $('.SalesPointNewSale .productlist.isGroup[listid="'+listId+'"]');
        
        if (listToShow.is(':visible')) {
            listToShow.hide();
        } else {
            $('.SalesPointNewSale .productlist.isGroup').hide();
            listToShow.show();
        }
    },
    
    configureList: function() {
        var configView = $(this).closest('.movebuttons').find('.viewconfig');
        if (configView.is(':visible')) {
            configView.slideUp();
        } else {
            configView.slideDown();
        }
        
    },
    
    setTabDiscount: function(newValue, fromTarget) {
        var data = {
            discount : newValue
        };
        
        thundashop.Ajax.simplePost(fromTarget, "setTabDiscount", data);
    },
    
    itemDiscountChange: function(newValue, fromTarget) {
        var data = {
            cartitemid : $(fromTarget).closest('.cartitemline').attr('cartitemid'),
            discountValue : newValue
        }
        
        var event = thundashop.Ajax.createEvent(null, "changeDiscount", fromTarget, data);
        event['synchron'] = true;
        var me = fromTarget;
        
        thundashop.Ajax.post(event, function(res) {
            var result = res.split(';');
            $(me).closest('.cartitemline').find('.price').html(result[0]);
            $(me).closest('.cartitemline').find('.changeprice').val(result[2]);
            $('.SalesPointNewSale .tabtotal span').html(result[1]);
            app.SalesPointNewSale.tabChanged();
        });
    },
    
    toggleCheckBox: function() {
        var type = $(this).closest('.extraoptiongroup').attr('type');
        
        if (type === "group") {
            $(this).closest('.extraoptiongroup').find('.checked').removeClass('checked');
        }
        
        if ($(this).closest('.product').find('.checkboxinner').hasClass('checked')) {
            $(this).closest('.product').find('.checkboxinner').removeClass('checked');
        } else {
            $(this).closest('.product').find('.checkboxinner').addClass('checked');
        }
    },
    
    checkIfDisabled: function(e) {
        if ($(e).hasClass('cash_disabled')) {
            return false;
        }
        
        return null;
    },
    
    showListOfViews: function(e) {
        if ($(e.target).hasClass('listname')) {
            var data = {
                viewId: $(e.target).attr('viewid')
            };
            
            thundashop.Ajax.simplePost(this, "selectView", data);
        } else {
            var innerMenu = $(this).find('.menuviewlist');
            if (innerMenu.is(':visible')) {
                innerMenu.slideUp();
            } else {
                innerMenu.slideDown();
            }
        }
    },
    
    countAddClicked: function() {
        $('.SalesPointNewSale .countadd').removeClass('active');
        $(this).addClass('active');
    },
    
    printCurrentTab: function() {
        thundashop.Ajax.simplePost(this, "printCurrentTab", {});
    },
    
    printOverview: function() {
        var printMenu = $(this).parent().find('.printmenu');
        if (printMenu.is(':visible')) {
           printMenu.slideUp();
        } else {
           printMenu.slideDown(); 
        }
    },
    
    printCurrentToKitchen: function() {
        if ($(this).hasClass('tabnotactive')) {
            return;
        }
        
        thundashop.Ajax.simplePost(this, "printCurrentToKitchen", {});
    },
    
    deleteTab: function() {
        if ($(this).hasClass('tabnotactive')) {
            return;
        }
        
        var res = confirm("Are you sure you want to delete this tab?");
        if (!res)
            return;
        
        thundashop.Ajax.simplePost(this, "deleteCurrentTab", {});
    },
    
    tabChanged: function() {
        var tabActive = $('.SalesPointNewSale .rightmenu .header');
        
        if (tabActive.length) {
            $('.SalesPointNewSale .cash_disabled').removeClass('cash_disabled');
        } else {
            $('.SalesPointNewSale .cashwithdrawal').addClass('cash_disabled');
        }
        
    },
    
    removeItemFromTab: function() {
        var data = {
            cartitemid : $(this).closest('.cartitemline').attr('cartitemid')
        }
        
        var event = thundashop.Ajax.createEvent(null, "removeItemFromTab", this, data);
        event['synchron'] = true;
        var me = this;
        thundashop.Ajax.post(event, function(res) {
            $(me).closest('.cartitemline').remove();
            $('.SalesPointNewSale .tabtotal span').html(res);
        });
    },
    
    countChange: function() {
        var data = {
            cartitemid : $(this).closest('.cartitemline').attr('cartitemid'),
            count : $(this).val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "changeCount", this, data);
        event['synchron'] = true;
        var me = this;
        thundashop.Ajax.post(event, function(res) {
            var result = res.split(';');
            $(me).closest('.cartitemline').find('.count').html(result[0]);
            $('.SalesPointNewSale .tabtotal span').html(result[1]);
            app.SalesPointNewSale.tabChanged();
        });
    },
    
    priceChange: function() {
        var data = {
            cartitemid : $(this).closest('.cartitemline').attr('cartitemid'),
            price : $(this).val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "changePrice", this, data);
        event['synchron'] = true;
        var me = this;
        thundashop.Ajax.post(event, function(res) {
            var result = res.split(';');
            $(me).closest('.cartitemline').find('.price').html(result[0]);
            $('.SalesPointNewSale .tabtotal span').html(result[1]);
            app.SalesPointNewSale.tabChanged();
        });
    },
    
    showDetails: function() {
        $('.cartitemline .details').hide();
        $(this).find('.details').show();
    },
    
    cancelCreateNewTab: function() {
        $('.taboperationoverlay_outer').fadeOut();
    },
    
    leftMenuClicked: function() {
        $('.taboperationoverlay_outer').fadeIn();
        
        var data = {
            action : $(this).attr('tab')
        }
        
        var event = thundashop.Ajax.createEvent(null, "showTabOperationContent", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $('.taboperationoverlay_outer .content').html(res);
        });
    },
    
    addProductToCurrentTab: function() {
        var count = $('.SalesPointNewSale .countadd.active span').text();
        if (!count) {
            count = 1;
        }
        var data = {
            productid : $(this).attr('productid'),
            count : count
        }
        
        if ($(this).attr('taxgroupid')) {
            data.taxgroupid = $(this).attr('taxgroupid');
        }
        
        if ($('.extraoptiongroup').is(':visible')) {
            data.extras = [];
            
            $('.extraoptiongroup').each(function() {
                var extra = {
                    id : $(this).attr('extraid'),
                    selectedVals : []
                };
                
                $(this).find('.checked').each(function() {
                    extra.selectedVals.push($(this).attr('optionid'));
                });
                
                data.extras.push(extra);
            });
        }
        
        var event = thundashop.Ajax.createEvent(null, "addProductToCurrentTab", this, data);
        event['synchron'] = true;
        
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            res = res.trim();
            if (res === "multitaxessupport") {
                app.SalesPointNewSale.showMultiTaxesSelection(me, data, "showMultiTaxes");
            } else if( res === "productconfig") {
                app.SalesPointNewSale.showMultiTaxesSelection(me, data, "showProductConfig");
            } else {
                app.SalesPointNewSale.updateResponseFromAddOfCartItem(me, res);
            }
        });
    },
    
    showMultiTaxesSelection: function(target, data, func) {
        $('.productlistinner').hide();
        $('.multitaxsupport').html("");
        $('.multitaxsupport').show();
        
        target = $('.SalesPointNewSale');
        
        var event = thundashop.Ajax.createEvent(null, func, target, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {

            $('.multitaxsupport').html(res);
        });
    },
    
    updateResponseFromAddOfCartItem: function(me, res) {
        $('.SalesPointNewSale .countadd.defaultone').click();
        $('.SalesPointNewSale .rightmenu').html(res);
        if ($('.tabnotactive').length) {
            app.SalesPointNewSale.activateTab(me);
        }
        
        app.SalesPointNewSale.tabChanged();
        
        $('.productlistinner').show();
        $('.multitaxsupport').html("");
        $('.multitaxsupport').hide();
    },
    
    activateTab: function(from) {
        $('.tabnotactive').removeClass('tabnotactive');
        var event = thundashop.Ajax.createEvent(null, "getCurrentTabId", from, {});
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $("[gs_show_modal='paymentmodal']").attr('tabid', res);
        });
        
        $('.startpaymentbutton').attr('gs_show_modal', 'paymentmodal');
    }
}

app.SalesPointNewSale.init();