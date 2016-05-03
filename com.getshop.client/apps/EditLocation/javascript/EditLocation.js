app.EditLocation = {
    init: function() {
        $(document).on('change', '.EditLocation .selectsublocation', app.EditLocation.selectLocationChanged)
        $(document).on('click', '.EditLocation .deleteSubLocation', app.EditLocation.deleteSubLocation)
    },
    
    deleteSubLocation: function() {
        var conf = confirm("Are you sure you want to delete this sublocation?");
        
        if (conf) {
            thundashop.Ajax.simplePost(this, "deleteLocation", {
                subLocationId : $('.selectsublocation').val()
            });
        }
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