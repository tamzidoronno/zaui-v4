app.InformationScreen = {
    init: function() {
        $(document).on('click', '.InformationScreen .add_information_screen', app.InformationScreen.addInformationScreen)
        $(document).on('click', '.InformationScreen .tv_selector', app.InformationScreen.selectTv)
        $(document).on('click', '.InformationScreen .save_slider', app.InformationScreen.saveSlider)
        $(document).on('click', '.InformationScreen .select_slider_class', app.InformationScreen.selectSlider)
        $(document).on('click', '.InformationScreen .delete_slider', app.InformationScreen.deleteSlider)
    },
    
    deleteSlider: function() {
        var data = {
            sliderId : $(this).parent().attr('slider_id')
        }
        var event = thundashop.Ajax.createEvent("", "deleteSlider", this, data);
        thundashop.Ajax.post(event);
    },
    
    selectSlider: function() {
        var data = {
            sliderId : $(this).attr('slider_id')
        }
        var event = thundashop.Ajax.createEvent("", "selectSlider", this, data);
        thundashop.Ajax.post(event);
    },
    
    saveSlider: function() {
        var data = {};
        data.texts = [];
        $('[slider_text_id]').each(function() {
            var attr = $(this).attr('slider_text_id');
            var val = $(this).val();
            data.texts.push({
                key: attr,
                value : val
            });
        });
        
        data.gs_slider_title = $('.slider_title_text').val();
        data.gs_slider_id = $('.current_slider_id').val();
        var event = thundashop.Ajax.createEvent("", "saveSlider", this, data);
        thundashop.Ajax.post(event);
    },
    
    selectTv: function() {
        if (!$(this).attr('tv_id')) {
            return;
        }
        
        var data = {
            id : $(this).attr('tv_id'),
        };
        
        var event = thundashop.Ajax.createEvent("", "selectTv", this, data);
        thundashop.Ajax.post(event);
    },
    
    addInformationScreen: function() {
        var data = {
            customerId : $(this).attr('customerId')
        }
        
        var event = thundashop.Ajax.createEvent("", "addInformationScreen", this, data);
        thundashop.Ajax.post(event);
    }
};

app.InformationScreen.init();