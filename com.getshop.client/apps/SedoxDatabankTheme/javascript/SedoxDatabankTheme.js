getshop.SedoxDatabankTheme = {
    dontUpdate: false,
    
    resize : function () {
    },
    
    init: function() {
        $(document).on('change', '.fileselector', getshop.SedoxDatabankTheme.fileSelected);
        $(document).on('click', '.action_purchase_product', getshop.SedoxDatabankTheme.purchaseFile);
    },
    
    purchaseFile: function() {
        var data = {
            file : $(this).attr('sedox_file_id'),
            fileId : $(this).attr('sedox_file_id'),
            productId : $(this).attr('productId')
        };

        var me = this;

        var event = thundashop.Ajax.createEvent(null, "purchaseAndDownload", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function(response) {
            window.location = response;
            
            var event = thundashop.Ajax.createEvent(null, "getPriceForProduct", me, data);
            event['synchron'] = true;

            thundashop.Ajax.post(event, function(price) {
                $(me).closest('.innerview').find('.priceview .price').html(price);
            })
        });
    },

    
    fileSelected: function() {
        var parent = $(this).closest('.box_content');
        $(this).find(":selected").each(function() {
            $.each(this.attributes, function(i, attrib) {
                var name = attrib.name;
                var value = attrib.value;
                parent.find('[lookingfor="'+name+'"]').html(value);
            });     
        });
        
        getshop.SedoxDatabankTheme.setSedoxFileIdAttrs($(this).find(":selected").val(), $(this).closest('.sedox_internal_view'));
        if (!getshop.SedoxDatabankTheme.dontUpdate) {
            var event = thundashop.Ajax.createEvent(null, "fileSelected", this, { 
                fileId: $(this).find(":selected").val(),
                productId: $(this).closest('.sedox_internal_view').attr('productid')
            });
            thundashop.Ajax.post(event, null, null, true, true);
        }
        
        if (!getshop.SedoxDatabankTheme.dontUpdate) {
            $(this).closest('.innerview').find('.priceview .price').html('<i class="fa fa-spin fa-spinner"></i>');
            
            var data = {
                productId : $(this).closest('.innerview').attr('productid'),
                fileId : $(this).val()
            };
            
            var event = thundashop.Ajax.createEvent(null, "getPriceForProduct", this, data);
            event['synchron'] = true;
            
            var me = this;
            thundashop.Ajax.post(event, function(price) {
                $(me).closest('.innerview').find('.priceview .price').html(price);
            })
        }
    },
    
    setSedoxFileIdAttrs: function(fileId, productcontainer) {
        $(productcontainer).find('*[sedox_file_id]').each(function() {
            $(this).attr('sedox_file_id', fileId);
        })
    },
    
    setAll: function() {
        getshop.SedoxDatabankTheme.dontUpdate = true;
        $('.fileselector').trigger("change");      
        getshop.SedoxDatabankTheme.dontUpdate = false;
    }
}

getshop.SedoxDatabankTheme.init();
$('.left_bar_1').ready(getshop.SedoxDatabankTheme.resize);