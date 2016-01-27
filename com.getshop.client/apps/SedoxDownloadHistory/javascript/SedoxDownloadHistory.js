app.SedoxDownloadHistory = {
    init: function() {
        $(document).on('change', '.SedoxDownloadHistory .filterTextBox', app.SedoxDownloadHistory.applyFilter)
        $(document).on('click', '.SedoxDownloadHistory .col_header', app.SedoxDownloadHistory.applySortering)
        $(document).on('click', '.SedoxDownloadHistory .tablecontent .col_row_content .col_row_content_inner', app.SedoxDownloadHistory.showProduct)
    },
    
    showProduct: function(event) {
        if ($(event.target).hasClass('start_button') || $(event.target).closest('.stop_button').length > 0) {
            event.preventDefault();
            return -1;
        }
        
        $('.row_selected').removeClass('row_selected');
        
        var parent = $(this).closest('.col_row_content');
        if (parent.find('.sedox_internal_view').is(':visible')) {
            parent.find('.sedox_internal_view').slideUp();
        } else {
            $('.sedox_internal_view').slideUp();
            parent.find('.sedox_internal_view').slideDown();
            parent.closest('.col_row_content').addClass('row_selected');
        }
        
        if ($(this).closest('.SedoxAdmin').length > 0 ) {
            var event = thundashop.Ajax.createEvent(null, "markRowAsExpanded", this,  { productid : $(this).closest('.col_row_content').attr('productid') });
            thundashop.Ajax.post(event, null, true, true, true);
        }
    }, 
   
    applySortering: function() {
        if (!$(this).attr('sortby')) {
            return;
        }
        thundashop.Ajax.simplePost(this, "setSortering", {
            sortBy : $(this).attr('sortby')
        });
    },
    
    applyFilter: function() {
        var data = {
            filterText : $(this).val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "setFilter", this, data);
        thundashop.Ajax.post(event);
    }
}

app.SedoxDownloadHistory.init();