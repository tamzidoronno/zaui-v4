app.EditLocation = {
    init: function() {
        $(document).on('change', '.EditLocation .selectsublocation', app.EditLocation.selectLocationChanged)
    },
    
    startup: function() {
        var lastSelected = localStorage.getItem("last_selected_sublocation");
        if (lastSelected) {
            $('.EditLocation .selectsublocation option[value="'+lastSelected+'"]').attr('selected', 'true');
        }
        $('.EditLocation .selectsublocation').trigger('change');
        setTimeout(function() {
            $('.EditLocation .saveinformation').fadeOut(300);
        }, 4000);
    },
    
    selectLocationChanged: function() {
        $('.sublocation').hide();
        var locationId = $(this).val();
        localStorage.setItem("last_selected_sublocation" , locationId);
        $('.sublocation[sublocationid="'+locationId+'"]').show();
    }
};

app.EditLocation.init();