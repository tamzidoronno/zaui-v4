app.LasGruppenOrderSchema = {
    init: function() {
        $(document).on('change', '.LasGruppenOrderSchema [name="security"]', app.LasGruppenOrderSchema.securityChanged);
        $(document).on('change', '.LasGruppenOrderSchema [name="shippingtype"]', app.LasGruppenOrderSchema.shipmentChanged);
        $(document).on('change', '.LasGruppenOrderSchema .keyandcylinders', app.LasGruppenOrderSchema.keyandcylinders);
        $(document).on('click', '.LasGruppenOrderSchema .add_key_row', app.LasGruppenOrderSchema.addKeyRow);
        $(document).on('click', '.LasGruppenOrderSchema .add_cylinder_row', app.LasGruppenOrderSchema.addCylinderRow);
        $(document).on('click', '.LasGruppenOrderSchema .button_area .prev', app.LasGruppenOrderSchema.prev);
        $(document).on('click', '.LasGruppenOrderSchema .button_area .next', app.LasGruppenOrderSchema.next);
    },
    
    keyandcylinders: function() {
        var show = $(this).is(":checked");
        var val = $(this).attr('gs_value');
        if (val === "1") {
            if (show) {
                $('.keys_setup').show();
            } else {
                $('.keys_setup').hide();
            }
        }
        
        if (val === "2") {
            if (show) {
                $('.cylinder_setup').show();
            } else {
                $('.cylinder_setup').hide();
            }
        }
    },
    
    next: function() {
        var currentPage = $(this).closest('.orderpage');
        var pageNumber = currentPage.attr('pageNumer');
        pageNumber++;
        if ($('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').length) {
            $(this).closest('.orderpage').hide();
            $('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').show();
        } else {
            alert('Ferdig?');
        }
    },
    
    prev: function() {
        var currentPage = $(this).closest('.orderpage');
        var pageNumber = currentPage.attr('pageNumer');
        pageNumber--;
        $(this).closest('.orderpage').hide();
        $('.LasGruppenOrderSchema [pageNumer="'+pageNumber+'"').show();
    },
    
    securityChanged: function() {
        $('.pincodesetup').hide();
        $('.signaturesecurity').hide();
        $('.order_page4 .next').show();
        
        var selectedval = $(this).attr('gs_value');
        if (selectedval === "1") {
            $('.order_page4 .next').html('Send');
            $('.pincodesetup').show();
        }
        if (selectedval === "2") {
            $('.order_page4 .next').html('Print');
            $('.signaturesecurity').show();
        }
    },
    
    addKeyRow: function() {
        var row = $('table tr.keys_template_row').clone();
        row.removeClass('keys_template_row');
        $('.keys_setup table').append(row);
    },
    
    shipmentChanged: function() {
        $('.select_stores').hide();
        $('.specialsending').hide();
        
        var selectedval = $(this).attr('gs_value');
        if (selectedval === "4") {
            $('.select_stores').show();
        }
        if (selectedval === "5") {
            $('.specialsending').show();
        }
    },
    
    addCylinderRow: function() {
        var row = $('table tr.cylinder_template_row').clone();
        row.removeClass('cylinder_template_row');
        $('.cylinder_setup table').append(row);
    }
}

app.LasGruppenOrderSchema.init();