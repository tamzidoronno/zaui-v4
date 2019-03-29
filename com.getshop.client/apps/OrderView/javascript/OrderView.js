app.OrderView = {
    init: function() {
        $(document).on('click', '.OrderView .leftmenu .entry', app.OrderView.leftMenuChanged);
        $(document).on('click', '.OrderView .createnewOrderLine', app.OrderView.createNewOrderLine);
        $(document).on('click', '.OrderView .removecartitemtfromorder', app.OrderView.removeCartItemLine);
        $(document).on('click', '.OrderView .gsniceinput1.product', app.OrderView.showSearchProduct);
        $(document).on('change', '.OrderView .gsniceinput1.searchword', app.OrderView.searchForProduct);
        $(document).on('click', '.OrderView .searchForProductBox .searchForProductBoxInner .closebutton', app.OrderView.closeSearchBox);
        $(document).on('click', '.OrderView .searchForProductBox .selectproductid', app.OrderView.selectProduct);
        $(document).on('click', '.OrderView .changeoverridedatebox .shop_button', app.OrderView.submitNewOverrideDate);
        $(document).on('click', '.OrderView .shop_button.save_history_comment', app.OrderView.saveHistoryComment);
        $(document).on('click', '.OrderView .shop_button.dosendehf', app.OrderView.sendEhf);
        $(document).on('click', '.OrderView .shop_button.sendByEmail', app.OrderView.sendByEmail);
        $(document).on('click', '.OrderView .creditorder', app.OrderView.creditOrder);
        $(document).on('click', '.OrderView .deleteorder', app.OrderView.deleteOrder);
        $(document).on('click', '.OrderView .togglespecialinfo', app.OrderView.toggleSpecialInfo);
        
        // CartItem Changes
        $(document).on('change', '.OrderView .cartitem input.product_desc', app.OrderView.cartItemChanged);
        $(document).on('change', '.OrderView .cartitem input.count', app.OrderView.cartItemChanged);
        $(document).on('change', '.OrderView .cartitem input.price', app.OrderView.cartItemChanged);
        
        
        // Payment History
        $(document).on('click', '.OrderView .registerpayment', app.OrderView.registerPayment);
    },
    
    toggleSpecialInfo: function() {
        var box = $(this).closest('.gs_shop_small_icon').find('.specialiteminfobox');
        var isVisible = box.is(':visible');
        
        $('.specialiteminfobox').hide();
        
        if (!isVisible) {
            box.show();
        }
    },
    
    creditOrder: function() {
        var validated = confirm("Are you sure you want to credit this order?");
        if (validated) {
            var from = $(this).closest('.app').find('.orderview');
            var data = app.OrderView.getData(from);
            
            var event = thundashop.Ajax.createEvent(null, 'creditOrder', from, data);
            event['synchron'] = true;
            
            thundashop.Ajax.post(event, function(res) {
                from.closest('.app').html(res);
            })
        }
    },
    
    deleteOrder: function() {
        var validated = confirm("Are you sure you want to delete this order?");
        if (validated) {
            var from = $(this).closest('.app').find('.orderview');
            var data = app.OrderView.getData(from);
            
            var event = thundashop.Ajax.createEvent(null, 'deleteOrder', from, data);
            event['synchron'] = true;
            
            thundashop.Ajax.post(event, function(res) {
                from.closest('.app').html(res);
            });
        }
    },

    reloadTab: function(from, tab) {
        var data = app.OrderView.getData(from);
        data.tabName = tab;
        
        var event = thundashop.Ajax.createEvent(null, 'rePrintTab', from, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, tab, data);
        })
        
    },
    
    sendByEmail: function() {
        var data = app.OrderView.getData(this);
        data.emailaddress = $(this).closest('.sendByEhf').find('[gsname="emailaddress"]').val();
        
        console.log(data);
        
        var event = thundashop.Ajax.createEvent(null, "sendByEmail", this, data);
        event['synchron'] = true;
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.reloadTab(me, 'history');
            $('.OrderView .sendByEhf .emailSent').show();
            setTimeout(function() {
                $('.OrderView .sendByEhf .emailSent').hide();
            }, 5000);
        });
    },
    
    sendEhf: function() {
        var data = app.OrderView.getData(this);
        data.vatNumber = $(this).closest('.sendehfbox').find('[gsname="vatNumber"]').val();
        
        var event = thundashop.Ajax.createEvent(null, "sendEhf", this, data);
        event['synchron'] = true;
        $('.OrderView .sendehfbox').hide();
        $('.OrderView .sendingehf').show();
        $('.OrderView .sendehfbox .ehfresult').html("");
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.reloadTab(me, 'history');
            $('.OrderView .sendehfbox').show();
            $('.OrderView .sendingehf').hide();
            $('.OrderView .sendehfbox .ehfresult').html(res);
            setTimeout(function() {
                $('.OrderView .sendehfbox .ehfresult').html("");
            }, 5000);
        });
    },
    
    registerPayment: function() {
        var tab = $(this).closest('.app').find('.orderviewtab[tab="paymenthistory"]');
        
        var data = app.OrderView.getData(this);
        data.date = $(tab).find('.manualregisterpaymentdate').val();
        data.amount = $(tab).find('.manualregisterpaymentamount').val();
        data.comment = $(tab).find('.manualregisterpaymentcomment').val();
        
        var event = thundashop.Ajax.createEvent(null, "addTransactionRecord", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, 'paymenthistory', data);
        });
    },
    
    saveHistoryComment: function() {
        var data = app.OrderView.getData(this);
        data.ordercomment = $(this).closest('.app').find('.orderviewtab[tab="history"] [gsname="ordercomment"]').val();
      
        var event = thundashop.Ajax.createEvent(null, "saveInternalCommentOnOrder", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, 'history', data);
        });
    },
    
    submitNewOverrideDate: function() {
        var data = app.OrderView.getData(this);
        data.date = $(this).closest('.changeoverridedatebox').find('input').val();
        
        var event = thundashop.Ajax.createEvent(null, "changeOverrideDate", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, 'accounting', data);
        });
    },
    
    cartItemChanged: function() {
        var data = app.OrderView.getData(this);
        var cartItemDivRow = $(this).closest('.cartitem');
        data.cartItemId = cartItemDivRow.attr('cartitemid');
        data.productDescription = cartItemDivRow.find('input.product_desc').val();
        data.count = cartItemDivRow.find('input.count').val();
        data.price = cartItemDivRow.find('input.price').val();
        data.name = cartItemDivRow.find('input.product').val();
        
        var event = thundashop.Ajax.createEvent(null, "updateCartItem", $(this), data);
        event['synchron'] = true;
        var me = this;
        
        thundashop.Ajax.post(event, function(res) {
            var div = $('<div/>').html(res);
            $(me).closest('.orderview').find('.ordersummary').html(div.find('.ordersummary').html());
        }, [], true, true);
    },
    
    selectProduct: function() {
        var data = app.OrderView.getData(this);
        data.productId = $(this).closest('.selectproductid').attr('productid');
        data.cartItemId = sessionStorage.getItem('searchproductforcartitemid');
        
        var event = thundashop.Ajax.createEvent(null, "changeProductOnCartItem", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, 'orderlines', data);
        });
    },
    
    searchForProduct: function() {
        var data = {
            searchWord : $(this).closest('.searchForProductBoxInner').find('.searchword').val()
        };
  
        var event = thundashop.Ajax.createEvent(null, "searchForProduct", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            $('.OrderView .searchForProductBoxInner .searchresult').html(res);
        });
    },
    
    closeSearchBox: function() {
        $(this).closest('.searchForProductBox').fadeOut();
    },
    
    showSearchProduct: function() {
        sessionStorage.setItem('searchproductforcartitemid', $(this).closest('.cartitem').attr('cartitemid'));
        
        var top = $(this).offset().top - 150;
        var left = $(this).offset().left;
        $('.OrderView .searchForProductBox').css('top', top+"px");
        $('.OrderView .searchForProductBox').css('left', left+"px");
        $('.OrderView .searchForProductBox').fadeIn();
    },
    
    removeCartItemLine: function() {
        var data = app.OrderView.getData(this);
        data.cartItemId = $(this).closest('.cartitem').attr('cartitemid');
        
        var event = thundashop.Ajax.createEvent(null, "removeCartItemLine", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, 'orderlines', data);
        });
    },
    
    getData: function(innerFormElement) {
        var data = {
            orderid : $(innerFormElement).closest('.orderview').attr('orderid')
        }
        return data;
    },
    
    rePrintTab: function(html, tabname, orgData) {
        var view = $('.orderview[orderid="'+orgData.orderid+'"]');
        view.find('.orderviewtab[tab="'+tabname+'"]').html(html);
    },
    
    createNewOrderLine: function() {
        var data = app.OrderView.getData(this);
        var event = thundashop.Ajax.createEvent(null, "addNewCartItemLine", $(this), data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            app.OrderView.rePrintTab(res, 'orderlines', data);
        });
    },
    
    leftMenuChanged: function() {
        var orderid = $(this).closest('.orderview').attr('orderid');
        var tab = $(this).attr('tab');
        sessionStorage.setItem('orderview_'+orderid+'_last_active_tab', tab)
        app.OrderView.showTab(orderid, tab);
        
        if ($(this).attr('reprint')) {
            app.OrderView.reloadTab(this, tab);
        }
    },
    
    orderviewLoaded: function(orderid) {
        var lastActiveTab = sessionStorage.getItem('orderview_'+orderid+'_last_active_tab');
        
        if (!lastActiveTab) {
            lastActiveTab = 'orderlines';
        }
        
        app.OrderView.showTab(orderid, lastActiveTab);
    },
    
    showTab: function(orderid, tabName) {
        var view = $('.orderview[orderid="'+orderid+'"]');
        view.find('.orderviewtab').hide();
        view.find('.orderviewtab[tab="'+tabName+'"]').show();
    }
};

app.OrderView.init();