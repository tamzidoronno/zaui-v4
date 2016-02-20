app.SimpleFileUpload = {
    init : function() {
        $(document).on('change', '.SimpleFileUpload #file_upload_selector', app.SimpleFileUpload.doUpload);
        $(document).on('click', '.SimpleFileUpload .fa-trash-o', app.SimpleFileUpload.deleteObject);
    },
    deleteObject : function() {
        var confirmed = confirm("Are you sure you want to delete this file?");
        if(confirmed) {
            var row = $(this).closest('tr');
            var fileId = row.attr('fileid');
            var data = {
                "fileid" : fileId
            }
            
            var event = thundashop.Ajax.createEvent('', 'deleteFile', $(this), data);
            thundashop.Ajax.postWithCallBack(event, function() {
                row.remove();
            });
        }
    },
    createProgressBar : function() {
        var bar = $('<div class="progressbar"><span class="meter"></span><span class="percentage"></span><span class="filename"></span></div>');
        bar.hide();
        return bar;
    },
    doUpload: function() {
        var apptoload = $(this);
         var id = $(this).closest('.app').attr('appid');
        for(var key in $(this)[0].files) {
            if(key != parseInt(key)) {
               continue; 
            }
            var formData = new FormData();
            formData.append('file', $(this)[0].files[key]);
            $.ajax({
                   url : 'upload.php?listid='+id,
                   type : 'POST',
                   data : formData,
                   processData: false,  // tell jQuery not to process the data
                   contentType: false,  // tell jQuery not to set contentType
                   success : function(data) {
                       var event = thundashop.Ajax.createEvent('','reloadFileList',apptoload, {});
                       thundashop.Ajax.postWithCallBack(event, function(result) {
                           $('.filelist').html(result);
                       });
                   },
                   xhr: function() {  // custom xhr
                    myXhr = $.ajaxSettings.xhr();
                    var progressBar = app.SimpleFileUpload.createProgressBar();
                    progressBar.find('.filename').html(apptoload[0].files[key].name);
                    $('.progressBars').append(progressBar);
                    progressBar.fadeIn();
                    if(myXhr.upload){ // if upload property exists
                        myXhr.upload.addEventListener('progress', function(event) {
                            var percentage = (event.total / event.totalSize)*100;
                            progressBar.find('.meter').css('width',percentage + "%");
                            progressBar.find('.percentage').html(percentage+"%");
                            if(percentage === 100) {
                                progressBar.fadeOut();
                            }
                        }, false); // progressbar
                    }
                    return myXhr;
                },
            });
        }
        $('.SimpleFileUpload #file_upload_selector').val('');
    }
};

app.SimpleFileUpload.init();