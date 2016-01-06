getshop.SedoxDatabankTheme = {
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
    },
    
    setAll: function() {
        $('.fileselector').trigger("change");      
    }
}

getshop.SedoxDatabankTheme.init();
$('.left_bar_1').ready(getshop.SedoxDatabankTheme.resize);