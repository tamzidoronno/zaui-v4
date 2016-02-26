app.EventCertificatesAdmin = {
    init : function() {
        $(document).on('change', '.EventCertificatesAdmin .selectCertificate', app.EventCertificatesAdmin.changed)
    },
    
    changed: function() {
        thundashop.Ajax.simplePost(this, "showCertificate", { certificateId: $(this).val() });
    }
};

app.EventCertificatesAdmin.init();