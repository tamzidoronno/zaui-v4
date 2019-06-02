app.SamleFaktura = {
    init: function() {
        $(document).on('click', '.SamleFaktura .checkall', app.SamleFaktura.checkAll);
    },
    
    checkAll: function() {
        var dom = $(this).closest('.groupedbyuser');
        
        if(this.checked) {
            // Iterate each checkbox
            dom.find('input').each(function() {
                this.checked = true;                        
            });
        } else {
            dom.find('input').each(function() {
                this.checked = false;                       
            });
        }
        
    }
}

app.SamleFaktura.init();