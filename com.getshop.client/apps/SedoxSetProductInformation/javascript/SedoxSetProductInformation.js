app.SedoxSetProductInformation = {
    init: function() {
        $(document).on('click', '.SedoxSetProductInformation .information_save_button', app.SedoxSetProductInformation.setInformation);
    },
    
    setInformation: function() {
        var information = $(".information_text input").val();
        var dpf = $(".checkbox #dpf").is(":checked");
        var egr = $(".checkbox #egr").is(":checked");
        var decat = $(".checkbox #decat").is(":checked");
        var vmax = $(".checkbox #vmax").is(":checked");
        var adblue = $(".checkbox #adblue").is(":checked");
        var dtc = $(".checkbox #dtc").is(":checked");
        var data = {
            info: information,
            dpf: dpf,
            egr: egr,
            decat: decat,
            vmax: vmax,
            adblue: adblue,
            dtc: dtc,
            productId : $(this).attr('productid'),
            fileId : $(this).attr('sedox_file_id')
        }
            
        var event = thundashop.Ajax.createEvent(null, "setInformation", this, data);
            
        thundashop.Ajax.post(event, function() {
            thundashop.common.closeModal();
        });
    }
}

app.SedoxSetProductInformation.init();