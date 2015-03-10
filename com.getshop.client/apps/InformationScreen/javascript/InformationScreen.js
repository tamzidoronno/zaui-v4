app.InformationScreen = {
    init: function() {
        $(document).on('click', '.InformationScreen .add_information_screen', app.InformationScreen.addInformationScreen)
        $(document).on('click', '.InformationScreen .tv_selector', app.InformationScreen.selectTv)
        $(document).on('click', '.InformationScreen .save_slider', app.InformationScreen.saveSlider)
        $(document).on('click', '.InformationScreen .select_slider_class', app.InformationScreen.selectSlider)
        $(document).on('click', '.InformationScreen .delete_slider', app.InformationScreen.deleteSlider)
        $(document).on('click', '.InformationScreen .select_slider_type', app.InformationScreen.selectSliderType)
        $(document).on('click', '.InformationScreen .uploadpicture', app.InformationScreen.uploadBoxClick)
        $(document).on('change', '.InformationScreen .show_news_feed_checkbox', app.InformationScreen.saveCheckbox)
    },
    
    saveCheckbox: function() {
        var data = {
            showNewsFeed : $(this).is(':checked')
        };
        
        var event = thundashop.Ajax.createEvent("", "changeShowNewsFeed", this, data);
        thundashop.Ajax.post(event);
    },
    
    uploadBoxClick: function() {
        var originalButton = $(this);
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();
        
        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function() {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;

        control.addEventListener("change", function() {
            fileSelector.remove();
            app.InformationScreen.imageSelected(control.files, originalButton);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    
    imageSelected: function(files, originalButton) {
        var file = files[0];
        var fileName = file.name;
        var pictureType = originalButton.attr('pictureType');

        var reader = new FileReader();
        var button = originalButton;

        reader.onload = function(event) {
            var dataUri = event.target.result;

            var data = {
                fileBase64: dataUri,
                fileName: fileName,
                pictureType: pictureType
            };

            var event = thundashop.Ajax.createEvent(null, "savePicture", originalButton, data);
            $(button).find('.progressbar').html("0" + "%");
            $(button).find('.progressbar').show();
            
            thundashop.Ajax.post(
                    event,
                    function() {
                        app.InformationScreen.uploadProgressCompleted(button);
                    },
                    null,
                    false,
                    true,
                    {
                        "uploadcallback": function(prog) {
                            app.InformationScreen.uploadProgress(button, prog);
                        }
                    });
        };

        reader.onerror = function(event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
    
    uploadProgress: function(button, progress) {
        progress = Math.round(progress * 100) / 100;
        $(button).find('.progressbar').html(progress+"%");
    },
    
    uploadProgressCompleted: function(button) {
        $(button).find('.progressbar').hide();
    },
    
    selectSliderType: function() {
        var data = {
            type : $(this).attr('type')
        }
        var event = thundashop.Ajax.createEvent("", "setSliderType", this, data);
        thundashop.Ajax.post(event);
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