app.CertegoSystems = {
    init: function() {
        $(document).on('keyup', '#gss_search_for_certego_system', app.CertegoSystems.filterResult);
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