app.C3SFIRapport = {
    init: function() {
        $(document).on('click', '.C3SFIRapport .downloadReport', app.C3SFIRapport.downloadReport) 
        $(document).on('click', '.C3SFIRapport .downloadReportTotal', app.C3SFIRapport.downloadReportTotal) 
    },
    
    downloadReport: function() {
        var data = {
            from: $('#from').val(),
            to: $('#to').val(),
            companyId: $(this).attr('companyId')
        }
        
        if (!data.from || !data.to) {
            alert('velg en periode først');
            return;
        }
        
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
        
    },
    
    downloadReportTotal: function() {
        var data = {
            from: $('#from').val(),
            to: $('#to').val(),
            companyId: $(this).attr('companyId')
        }
        
        if (!data.from || !data.to) {
            alert('velg en periode først');
            return;
        }
        
        data['synchron'] = true;
        
        var event = thundashop.Ajax.createEvent(null, "downloadSfiReportTotal", this, data);
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

app.C3SFIRapport.init();