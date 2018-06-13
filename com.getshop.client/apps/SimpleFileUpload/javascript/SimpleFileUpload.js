app.SimpleFileUpload = {
    init : function() {
        $(document).on('change', '.SimpleFileUpload .file_upload_selector', app.SimpleFileUpload.doUpload);
        $(document).on('click', '.SimpleFileUpload .fa-trash-o', app.SimpleFileUpload.deleteObject);
        $(document).on('click', '.SimpleFileUpload .fa-edit', app.SimpleFileUpload.renameObject);
    },
    
    renameObject: function() {
        var newFileName = prompt("What is the new filename?" , $(this).attr('fileName'));
        
        if (!newFileName) 
            return;
        
        var row = $(this).closest('tr');
        var fileId = row.attr('fileid');
            
        thundashop.Ajax.simplePost(this, "updateFileName",  {
            fileId: fileId,
            newFileName: newFileName
        });
    },
    
    deleteObject : function() {
        var confirmed = confirm("Are you sure you want to delete this file?");
        if(confirmed) {
            var row = $(this).closest('tr');
            var fileId = row.attr('fileid');
            var data = {
                "fileid" : fileId
            }
            
            rows = $(this).closest('table').find('tr[fileId="'+fileId+'"]');
            
            var event = thundashop.Ajax.createEvent('', 'deleteFile', $(this), data);
            thundashop.Ajax.postWithCallBack(event, function() {
                rows.remove();
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
        var appId = $(this).closest('.app').attr('appid');
        var id = $(this).closest('.uploadform').attr('listid');
        
        for(var key in $(this)[0].files) {
            if(key != parseInt(key)) {
               continue; 
            }
            var formData = new FormData();
            formData.append('file', $(this)[0].files[key]);
            $.ajax({
                   url : 'upload.php?listid='+id+'&appid='+appId,
                   type : 'POST',
                   data : formData,
                   processData: false,  // tell jQuery not to process the data
                   contentType: false,  // tell jQuery not to set contentType
                   success : function(data) {
                       PubSub.publish('SIMPELFILEUPLOAD_COMPLETED', data);
                       var event = thundashop.Ajax.createEvent('','reloadFileList',apptoload, {});
                       thundashop.Ajax.postWithCallBack(event, function(result) {
                           apptoload.closest('.app').find('.filelist').html(result);
                       });
                   },
                   xhr: function() {  // custom xhr
                    myXhr = $.ajaxSettings.xhr();
                    var progressBar = app.SimpleFileUpload.createProgressBar();
                    progressBar.find('.filename').html(apptoload[0].files[key].name);
                    $('.progressBars').append(progressBar);
                    progressBar.fadeIn();
                    if(myXhr.upload){ // if upload property exists
                        myXhr.upload.addEventListener('progress', function(e) {
                            if(event.lengthComputable){
                                var loaded = e.position || e.loaded, total = e.totalSize || e.total;
                                var percentage = Math.floor((loaded / total)*100)+'%'
                                progressBar.find('.percentage').html(percentage);
                                progressBar.find('.meter').css('width',percentage);
                                if(percentage === '100%') {
                                    progressBar.fadeOut();
                                }
                            }
                        }, false); // progressbar
                    }

                    return myXhr;
                },
            });
        }
        $('.SimpleFileUpload #file_upload_selector').val('');
    },
    
    setSortingAlphaDescending: function(extra, application) {
        thundashop.Ajax.simplePost(application, "setSortingAlphaDesc", {});
    },
    
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-sort",
                    iconsize : "30",
                    title: __f("Set sorting alpha descending"),
                    click: app.SimpleFileUpload.setSortingAlphaDescending
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.SimpleFileUpload.init();