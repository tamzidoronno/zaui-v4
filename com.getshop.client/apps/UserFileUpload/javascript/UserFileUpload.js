app.UserFileUpload = {
    init: function () {
        $(document).on('click', '.gss_user_save_file', app.UserFileUpload.uploadBoxClick);
        $(document).on('input', '.gss_user_save_description', app.UserFileUpload.saveDescription);
        $(document).on('click', '.userfiledeletebutton', app.UserFileUpload.deleteUploadedFile);
        $(document).on('click', '.downloadfilebutton', app.UserFileUpload.downloadfilebutton);
        
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
    saveDescription: function(){
        
        var description = this.value;
        var button = $(this);
        var fileid = button.attr('fileId');
        var userId = button.attr('userId');
        var row = button.closest('.userfileuploadline');
        var event = thundashop.Ajax.createEvent('','saveDescription','ns_ba6f5e74_87c7_4825_9606_f2d3c93d292f\\Users', {
            description : description,
            userId : userId,
            fileId : fileid
        });
        thundashop.Ajax.postWithCallBack(event, function() {

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
        var me = this;
        control.addEventListener("change", function () {
            fileSelector.remove();
            app.UserFileUpload.imageSelected(control.files);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function (files) {
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
            
            var field = $('<div/>');
            field.attr('gss_view', "gs_user_workarea");
            field.attr('gss_fragment', "user");
            field.attr('gss_value', userid);
            getshop.Settings.post(data, "saveFileUploaded", field);
        };

        reader.onerror = function (event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
}
app.UserFileUpload.init();