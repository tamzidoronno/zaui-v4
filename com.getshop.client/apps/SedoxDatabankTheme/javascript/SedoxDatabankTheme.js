getshop.SedoxDatabankTheme = {
    dontUpdate: false,
    
    resize : function () {
//        debugger;
//        $('.left_bar_1').css("min-height", "1000px");
    },
    
    init: function() {
        $(document).on('change', '.fileselector', getshop.SedoxDatabankTheme.fileSelected);
    },
    
    fileSelected: function() {
        var parent = $(this).closest('.box_content');
        $(this).find(":selected").each(function() {
            $.each(this.attributes, function(i, attrib) {
                var name = attrib.name;
                var value = attrib.value;
                parent.find('[lookingfor="'+name+'"]').html(value);
            });     
        });
        
        getshop.SedoxDatabankTheme.setSedoxFileIdAttrs($(this).find(":selected").val(), $(this).closest('.sedox_internal_view'));
        if (!getshop.SedoxDatabankTheme.dontUpdate) {
            var event = thundashop.Ajax.createEvent(null, "fileSelected", this, { 
                fileId: $(this).find(":selected").val(),
                productId: $(this).closest('.sedox_internal_view').attr('productid')
            });
            thundashop.Ajax.post(event, null, null, true, true);
        }
    },
    
    setSedoxFileIdAttrs: function(fileId, productcontainer) {
        $(productcontainer).find('*[sedox_file_id]').each(function() {
            $(this).attr('sedox_file_id', fileId);
        })
    },
    
    setAll: function() {
        getshop.SedoxDatabankTheme.dontUpdate = true;
        $('.fileselector').trigger("change");      
        getshop.SedoxDatabankTheme.dontUpdate = false;
    }
}

getshop.SedoxDatabankTheme.init();
$('.left_bar_1').ready(getshop.SedoxDatabankTheme.resize);