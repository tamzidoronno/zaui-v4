app.OrderView = {
    init: function() {
        $(document).on('click', '.OrderView .leftmenu .entry', app.OrderView.leftMenuChanged);
        $(document).on('click', '.OrderView .createnewOrderLine', app.OrderView.createNewOrderLine);
        $(document).on('click', '.OrderView .removecartitemtfromorder', app.OrderView.removeCartItemLine);
        $(document).on('click', '.OrderView .gsniceinput1.product', app.OrderView.showSearchProduct);
        $(document).on('change', '.OrderView .gsniceinput1.searchword', app.OrderView.searchForProduct);
        $(document).on('click', '.OrderView .searchForProductBox .searchForProductBoxInner .closebutton', app.OrderView.closeSearchBox);
        $(document).on('click', '.OrderView .searchForProductBox .selectproductid', app.OrderView.selectProduct);
        
        // CartItem Changes
        $(document).on('change', '.OrderView .cartitem input.product_desc', app.OrderView.cartItemChanged);
        $(document).on('change', '.OrderView .cartitem input.count', app.OrderView.cartItemChanged);
        $(document).on('change', '.OrderView .cartitem input.price', app.OrderView.cartItemChanged);
    },
    
    cartItemChanged: function() {
        var data = app.OrderView.getData(this);
        var cartItemDivRow = $(this).closest('.cartitem');
        data.cartItemId = cartItemDivRow.attr('cartitemid');
        data.productDescription = cartItemDivRow.find('input.product_desc').val();
        data.count = cartItemDivRow.find('input.count').val();
        data.price = cartItemDivRow.find('input.price').val();
        
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