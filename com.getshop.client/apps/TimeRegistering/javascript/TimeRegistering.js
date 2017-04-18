app.TimeRegistering = {
    initEvents : function()  {
        $(document).on('click', '.TimeRegistering .downloaduserbutton', app.TimeRegistering.downloadtoexcel);
    },
    downloadtoexcel : function() {
        var form = $(this).closest('[gstype="form"]');
        var method = form.attr('method');
        var filename = $(this).attr('gs_fileName');
        var data = thundashop.framework.createGsArgs(form);
        data['synchron'] = true;
        data['userid'] = $(this).attr('userid');
        var evt = thundashop.Ajax.createEvent(null, method, this, data);
        
        thundashop.Ajax.postWithCallBack(evt, function(res) {
            var base64 = thundashop.base64.encode(res);
            var url = '/scripts/createExcelFile.php';
            var form = $('<form method="POST" action="' + url + '">');
            form.append($('<input type="hidden" name="data" value="' + base64 + '">'));
            form.append($('<input type="hidden" name="filename" value="' + filename + '">'));
            
            $('body').append(form);
            
            form.submit();
            form.remove();
        });
    }
};
app.TimeRegistering.initEvents();