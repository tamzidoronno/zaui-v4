app.SedoxSetProductInformation = {
    init: function() {
        $(document).on('click', '.SedoxSetProductInformation .information_save_button', app.SedoxSetProductInformation.setInformation);
    },
    
    setInformation: function() {
        var information = $(".information_text input").val();
        var radioValue = $.trim($(".radio_button input[type=radio]:checked").parent().text());
        var allInfo = information + (radioValue && information ? " - " : "") + radioValue;
        if (information || radioValue) {
            var data = {
                info: allInfo,
                productId : $(this).attr('productid'),
                fileId : $(this).attr('sedox_file_id')
            }
            
            var event = thundashop.Ajax.createEvent(null, "setInformation", this, data);
            
            thundashop.Ajax.post(event, function() {
                thundashop.common.closeModal();
            });
        }
    }
}

app.SedoxSetProductInformation.init();