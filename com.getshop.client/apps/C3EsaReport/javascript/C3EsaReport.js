app.C3EsaReport = {
    init: function() {
        $(document).on('click', '.C3EsaReport .downloadReport', app.C3EsaReport.downloadReport);
    },
    
    downloadReport: function() {
        var data = {
            from: $('#from').val(),
            to: $('#to').val()
        }
        
        if (!data.from || !data.to) {
            alert('velg en periode først');
            return;
        }
        
        data['synchron'] = true;
        
        var event = thundashop.Ajax.createEvent(null, "downloadEsaReport", this, data);
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

app.C3EsaReport.init();