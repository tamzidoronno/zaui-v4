 
 app.C3RegisterHours = {
    init: function() {
           $(document).on('click', '.C3RegisterHours .downloadReport', app.C3RegisterHours.downloadReport) 
    },
    
    downloadReport: function() {
        var data = {}
        data['synchron'] = true;
        
        var event = thundashop.Ajax.createEvent(null, "downloadSfiReport", this, data);
        var filename = $(this).attr('filename');
        
        thundashop.Ajax.postWithCallBack(event, function(base64) {
            var url = '/scripts/createExcelFilePlain.php';
            var form = $('<form method="POST" action="' + url + '">');
            form.append($('<input type="hidden" name="data" value="' + base64 + '">'));
            form.append($('<input type="hidden" name="filename" value="' + filename + '">'));
            
            $('body').append(form);
            
            form.submit();
            form.remove();
        });
        
    }
}

app.C3RegisterHours.init();