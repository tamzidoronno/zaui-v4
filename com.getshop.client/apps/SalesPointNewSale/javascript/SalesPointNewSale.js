app.SalesPointNewSale = {
    init: function() {
        $(document).on('click', '.SalesPointNewSale .entry.all', app.SalesPointNewSale.leftMenuClicked);
        $(document).on('click', '.SalesPointNewSale .cancelcreatenew', app.SalesPointNewSale.cancelCreateNewTab);
        $(document).on('click', '.SalesPointNewSale .addProductToCurrentTab', app.SalesPointNewSale.addProductToCurrentTab);
        $(document).on('click', '.SalesPointNewSale .addProductToCurrentTabWithTaxes', app.SalesPointNewSale.addProductToCurrentTabWithTaxes);
        $(document).on('mouseover', '.SalesPointNewSale .cartitemline', app.SalesPointNewSale.showDetails);
        $(document).on('change', '.SalesPointNewSale .cartitemline .changecount', app.SalesPointNewSale.countChange);
        $(document).on('change', '.SalesPointNewSale .cartitemline .changeprice', app.SalesPointNewSale.priceChange);
        $(document).on('click', '.SalesPointNewSale .cartitemline .removefromtab', app.SalesPointNewSale.removeItemFromTab);
        $(document).on('click', '.SalesPointNewSale .deletetab', app.SalesPointNewSale.deleteTab);
        $(document).on('click', '.SalesPointNewSale .printoverview', app.SalesPointNewSale.printOverview);
        $(document).on('click', '.SalesPointNewSale .countadd', app.SalesPointNewSale.countAddClicked);
        $(document).on('click', '.SalesPointNewSale .changeviewmenu', app.SalesPointNewSale.showListOfViews);
        
        this.bindScrollEvent();
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
    
    bindScrollEvent: function() {
        $(window).scroll(function() {
            var top = 76-window.scrollY;
            if (top < 0) {
                top = 0;
            }
            
            $('.SalesPointNewSale .topmenu').css('top', top + 'px');
        });
    },
    
    countAddClicked: function() {
        $('.SalesPointNewSale .countadd').removeClass('active');
        $(this).addClass('active');
    },
    
    printOverview: function() {
        if ($(this).hasClass('tabnotactive')) {
            return;
        }
        
        thundashop.Ajax.simplePost(this, "printCurrentTab", {});
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
        console.log(tabActive);
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
        
        var event = thundashop.Ajax.createEvent(null, "addProductToCurrentTab", this, data);
        event['synchron'] = true;
        
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            if (res === "multitaxessupport") {
                app.SalesPointNewSale.showMultiTaxesSelection(me, data);
            } else {
                app.SalesPointNewSale.updateResponseFromAddOfCartItem(me, res);
            }
        });
    },
    
    addProductToCurrentTabWithTaxes: function() {
        var data = {
            productid : $(this).attr('productid'),
            taxgroupid: $(this).attr('taxgroupid')
        }
        
        var event = thundashop.Ajax.createEvent(null, "addProductToCurrentTabWithTaxCode", this, data);
        event['synchron'] = true;
        
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            app.SalesPointNewSale.updateResponseFromAddOfCartItem(me, res);
            $('.productlistinner').show();
            $('.multitaxsupport').html("");
            $('.multitaxsupport').hide();
        });
    },
    
    showMultiTaxesSelection: function(target, data) {
        $('.productlistinner').hide();
        $('.multitaxsupport').html("");
        $('.multitaxsupport').show();
        
        var event = thundashop.Ajax.createEvent(null, "showMultiTaxes", target, data);
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