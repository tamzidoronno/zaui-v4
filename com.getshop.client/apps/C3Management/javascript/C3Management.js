app.C3Management = {
    init: function() {
        PubSub.subscribe('GS_TOGGLE_CHANGED', app.C3Management.periodeChanged);
        app.C3Management.initDateFields();
    },
    
    initDateFields: function() {
        $(document).on('focusin', "#c3managementaddfrom", app.C3Management.activateDate);
        $(document).on('focusin', "#c3managementaddto", app.C3Management.activateDate);
    },
    
    activateDate: function() {
        $(this).datepicker({ 
            inline: true,
            dateFormat: "dd.mm.yy"
        });
    },
    
    periodeChanged: function(event, data) {
        if ($(data.field).attr('c3periodeonoff') === "true" && data.toggledByUser) {
            $('.c3periodeclass').each(function() {
                if (this === data.field)
                    return;
                
                $(this).find('i').removeClass('fa-toggle-on');
                $(this).find('i').addClass('fa-toggle-off');
                getshop.Model.activeperiodes[$(this).attr('gs_model_attr')] = false
            })
            
        }
    }
};

app.C3Management.init();