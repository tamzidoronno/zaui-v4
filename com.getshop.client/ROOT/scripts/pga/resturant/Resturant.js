/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


Resturant = {
    init: function() {
        $(document).on('click', '.product_config_box', Resturant.toggleCheckBoxForProductConfig);
        $(document).on('click', '.addProductToCurrentTab', Resturant.addProductToCurrentTab);
        $(document).ready(function() {
            Resturant.updateProductCountForTab();
        })
    },
    
    addToCart: function(productId) {
        $.ajax({
            url: '/scripts/pga/resturant/data/productconfig.php',
            data: {
                productId: productId
            }
        }).done(function(res) {
            if (res == "false") {
                Resturant.addDirectlyToCart(productId);
            } else {
                Resturant.showConfig(res);
            }
        });
    },
    
    addDirectlyToCart: function(productId) {
        var extraIds = {};
        
        var data = {
            tabid : Resturant.getTabId(),
            productid : productId,
            extraIds : extraIds
        };
        
        $.ajax({
            type: "POST",
            url: '/scripts/pga/resturant/data/addproducttotab.php',
            data: data
        }).done(function(res) {
            $('.overlay').hide();
            Resturant.updateProductCountForTab();
        });
    },
    
    showConfig: function(res) {
        $('.overlay .overlayinner').html(res);
        $('.overlay').fadeIn();
    },
    
    toggleCheckBoxForProductConfig: function(res) {
        var optionGroup = $(this).closest('.extraoptiongroup');
        var isGroup = optionGroup.attr('type') == "group";
        
        if (isGroup) {
            $(optionGroup).find('.active').removeClass('active');
            $(this).addClass('active');
        } else {
            var isSelceted = $(this).hasClass('active');
            if (isSelceted) {
                $(this).removeClass('active');
            } else {
                $(this).addClass('active');
            }
        }
    },
    
    generateGuid: function() {
        var result, i, j;
        result = '';
        for(j=0; j<32; j++) {
          if( j == 8 || j == 12 || j == 16 || j == 20) 
            result = result + '-';
          i = Math.floor(Math.random()*16).toString(16);
          result = result + i;
        }
        return result;
    },
    
    /**
     * TODO: Handle the logic of how to create a tab
     * properly, also remember to delete it properly 
     * after a tab has been completed.
     */
    getTabId: function() {
        var pgaTabId = localStorage.getItem('pgaTabId');
        if (!pgaTabId) {
            pgaTabId = Resturant.generateGuid();
            localStorage.setItem('pgaTabId', pgaTabId);
        };
        
        return pgaTabId;
    },
    
    addProductToCurrentTab: function() {
        var extraIds = {};
        
        $(this).closest('.productconfigarea').find('.extraoptiongroup').each(function() {
            var optionid = $(this).attr('extraid');
            var extraSelected = [];
            $(this).find('.active').each(function() {
                extraSelected.push($(this).find('.checkboxinner').attr('optionid'));
            });
            extraIds[optionid] = extraSelected;
        });
        
        
        var data = {
            tabid : Resturant.getTabId(),
            productid : $(this).closest('.productconfigarea').attr('productid'),
            extraIds : extraIds
        };
        
        $.ajax({
            type: "POST",
            url: '/scripts/pga/resturant/data/addproducttotab.php',
            data: data
        }).done(function(res) {
            $('.overlay').hide();
            Resturant.updateProductCountForTab();
        });
    },
    
    updateProductCountForTab: function() {
        var data = {
            tabid : Resturant.getTabId(),
        };
        
        $.ajax({
            type: "POST",
            url: '/scripts/pga/resturant/data/productcountfortab.php',
            data: data
        }).done(function(res) {
            var text = (res === "0") ? "Ingen varer i handlekurven" : "Du har lagt til " + res + " varer <br/><div style='font-size: 12px'> Trykk for å fullføre </div>";
            
            if (res === "0") {
                $('.shoppingcart').removeClass('hasproducts');
            } else {
                $('.shoppingcart').addClass('hasproducts');
            }
            
            $('.shoppingcart span').html(text);
        });
    }
};

Resturant.init();