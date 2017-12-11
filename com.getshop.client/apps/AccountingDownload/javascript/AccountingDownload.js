app.AccountingDownload = {
    init: function() {
        $(document).on('click', '.AccountingDownload .deleteFile', this.deleteFile);
        $(document).on('click', '.AccountingDownload .downloadFile', this.downloadFile);
    },
    
    downloadFile : function() {
        var fileid = $(this).attr('fileid');
        window.open('/scripts/gbat10download.php?fileid=' + fileid);
    },
    
    deleteFile: function() {
        var password = prompt('Files can not be deleted, please enter password');
        var data = {
            password : password,
            fileId: $(this).attr('fileId')
        };
        
        thundashop.Ajax.simplePost(this, "deleteFile", data);
    }
}

app.AccountingDownload.init();