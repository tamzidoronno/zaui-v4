//Put your javascript here.

app.Netaxept = {
    init: function () {
        $(document).on('click', '.gss_nets_upload_csv_file', this.uploadCsv);
        $(document).on('change', '#csvfile', this.fileChanged);
    },
    fileChanged: function (evt) {
        var files = evt.target.files; // FileList object
        var output = [];

        for (var i = 0, f; f = files[i]; i++) {
            var reader = new FileReader();

            // Closure to capture the file information.
            reader.onload = (function (theFile) {
                return function (e) {
                    var content = e.target.result;
                    
                    getshop.Settings.post({ content: content }, "parseCsv");
                };
            })(f);

            // Read in the image file as a data URL.
            reader.readAsText(f);
        }
    },
    uploadCsv: function () {
        alert('test');
    },
    
    
};

app.Netaxept.init();