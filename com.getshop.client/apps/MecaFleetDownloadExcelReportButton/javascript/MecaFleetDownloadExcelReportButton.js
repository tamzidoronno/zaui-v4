app.MecaFleetDownloadExcelReportButton = {
    init: function() {
        $(document).on('click', '.MecaFleetDownloadExcelReportButton .downloadReport', app.MecaFleetDownloadExcelReportButton.downloadReport);
    },
    
    downloadReport: function() {
        var event = thundashop.Ajax.createEvent(null, "downloadReport", this, {});
        var filename = "fleerreport.xlsx";
        
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

app.MecaFleetDownloadExcelReportButton.init();        