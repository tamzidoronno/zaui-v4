app.SedoxSetProductInformation = {
    init: function() {
        $(document).on('click', '.SedoxSetProductInformation .information_save_button', app.SedoxSetProductInformation.setInformation);
    },
    
    setInformation: function() {
        var information = $(".information_text input").val();
        var radioValue = $.trim($(".radio_button input[type=radio]:checked").parent().text());
        var allInfo = information + (radioValue && information ? " - " : "") + radioValue;
        if (information || radioValue) {
            thundashop.Ajax.simplePost(this, "setInformation", {
                info: allInfo,
                productId : $(this).attr('productid'),
                fileId : $(this).attr('sedox_file_id')
            });
        }
        $(".gsarea gs_modalouter").fadeOut();
    }
}

app.SedoxSetProductInformation.init();