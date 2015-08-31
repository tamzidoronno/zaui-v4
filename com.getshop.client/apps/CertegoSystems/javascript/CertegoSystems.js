app.CertegoSystems = {
    init: function() {
        $(document).on('change', '#gss_search_for_system_to_add_to_group', app.CertegoSystems.search);
        $(document).on('click', '.gss_addSystemToGroup', app.CertegoSystems.showSearchForm);
    },
    
    search: function() {
        $('#gss_search_for_systems_in_list').click();
    },
    
    showSearchForm: function() {
        if ($('.gss_addSystemForm').is(':visible')) {
            $('.gss_addSystemForm').slideUp();
        } else {
            $('.gss_addSystemForm').slideDown();
        }
    },
    
    filterResult: function() {
        var textToSearchFor = $(this).val();
        
        if (textToSearchFor.length === 0) {
            $('#gss_certego_systems_overview .gss_edit_system').show();
            return;
        }
        
        $('#gss_certego_systems_overview .gss_edit_system').each(function() {
            var text = $(this).html();
            if (text.indexOf(textToSearchFor) > -1) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    }
}

app.CertegoSystems.init();