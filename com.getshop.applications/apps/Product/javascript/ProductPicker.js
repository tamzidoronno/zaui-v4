ProductPicker = function(application, config) { 
    this.config = config;
    this.element = application;
    this.infobox = $('#informationbox');
    this.init(); 
}

ProductPicker.prototype = {
    pickertype : null,
    searchbox : null,
    init : function() {
    },
    initEvents : function() {
        this.infobox.off('keyup','.right_container input', $.proxy(this.searchProducts, this));
        this.infobox.off('click','.product_line', $.proxy(this.selectProduct, this));
        this.infobox.off('click','.done_selection_products', $.proxy(this.doneSelecting, this));
        this.infobox.off('click','#display_only_selected', $.proxy(this.filterSelected, this));
        
        this.infobox.on('keyup','.right_container input', $.proxy(this.searchProducts, this));
        this.infobox.on('click','.product_line', $.proxy(this.selectProduct, this));
        this.infobox.on('click','.done_selection_products', $.proxy(this.doneSelecting, this));
        this.infobox.on('click','#display_only_selected', $.proxy(this.filterSelected, this));
    },
    filterSelected : function(event) {
        var box = $(event.target);
        var checked = false;
        if(box.is(':checked')) {
            checked = true;
        }
        if(checked) {
            this.infobox.find('.product_line').hide();
            this.infobox.find('.product_line .fa').parent().show();
        } else {
            this.infobox.find('.product_line').show();
            this.infobox.find('.product_line .fa').parent().hide();
        }
    },
    selectProduct : function(event) {
        var row = $(event.target);
        if(!row.hasClass('product_line')) {
            row = row.closest('.product_line');
        }
        if(row.find('.fa-check').length > 0) {
            row.find('.fa-check').remove();
        } else {
            row.prepend('<i class="fa fa-check"></i>');
        }
        if(this.config.type === "single") {
            this.doneSelecting();
        }
    },
    doneSelecting : function() {
        var productids = [];
        this.infobox.find('.fa').each(function() {
            var id = $(this).parent().attr('productid');
            productids.push(id);
        });
        var event = thundashop.Ajax.createEvent("","setProductFromProductPicker", $(this.element), {
            "productids" : productids,
            "config" : this.config
        });
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    searchProducts : function() {
        if(this.searchbox === null) {
            this.searchbox = this.infobox.find('.right_container input');
        }
        var searchword = this.searchbox.val().toLowerCase();
        this.infobox.find('.product_line').each(function() {
            var text = $(this).text().toLowerCase();
            if(text.indexOf(searchword) >= 0) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    },
    load: function(pickertype) {
        var config = this.config;
        if(config.type === undefined) {
            config.type = "single";
        }

        this.pickertype = pickertype;
        var event = thundashop.Ajax.createEvent('','loadProductPicker',$(this),{
            "type" : config.type,
            "id" : config.id
        });
        
        var title = __f("Select your products");
        if(config.type === "single") {
            title = __f("Select your product");
        }
        if(config.type === "delete") {
            title = __f("Delete your product");
        }
        
        thundashop.common.showInformationBox(event, title);
        this.initEvents();
    }
}
