app.EventBookingManuelAddEvent = {
    init: function () {
        $(document).on('click', '.gss_upload_file_to_row', app.EventBookingManuelAddEvent.uploadBoxClick);
        
    },
    downloadfilebutton : function() {
        window.open('/scripts/downloadUserFile.php?id='+$(this).attr('fileId')+"&userId=" + $(this).attr('userId'),'_blank');
    },
    
    deleteUploadedFile: function(){
        var button = $(this);
        var fileid = button.attr('fileId');
        var userId = button.attr('userId');
        var row = button.closest('.userfileuploadline');
        var event = thundashop.Ajax.createEvent('','deleteFileUploaded','ns_ba6f5e74_87c7_4825_9606_f2d3c93d292f\\Users', {
            userId : userId,
            fileId : fileid
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            row.remove();
        });
    },
 
    uploadBoxClick: function () {
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();

        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function () {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        control.parent = this;
        
        control.addEventListener("change", function () {
            fileSelector.remove();
            app.EventBookingManuelAddEvent.imageSelected(control.files, $(this.parent).attr('manualid'));
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function (files, manualid) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        reader.onload = function (event) {
            var dataUri = event.target.result;

            var data = {
                fileBase64: dataUri,
                fileName: fileName
            };

            var userid = $('[gs_model_attr="userid"]').attr('value');
            if($('#selecteduseridoverride').length > 0) {
                userid = $('#selecteduseridoverride').val();
            }
            var field = $('<div/>');
            field.attr('gss_view', "manuallyaddedevents");
            field.attr('gss_fragment', "listmanuallyaddedevents");
            field.attr('gss_value', userid);
            field.attr('gss_value_2', manualid);
            var outer = $('<div class="gss_overrideapp"></div>');
            outer.attr('gss_use_app_id', '723546d2-c113-4c6f-b7bd-23e7f68620e8')
            outer.append(field);
            
            getshop.Settings.post(data, "saveFileUploaded", field);
        };

        reader.onerror = function (event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
}
app.EventBookingManuelAddEvent.init();