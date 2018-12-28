app.ProductList = {
    init: function() {
        
    },
    
    productCreated: function(res) {
        getshop.showOverlay(2);
        $('.gsoverlay2 .gsoverlayinner .content').html(res);
    }
}

app.ProductList.init();