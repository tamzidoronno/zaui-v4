app.BookingTemplateTheme = {
    init: function(){
        $(document).on('scroll', app.BookingTemplateTheme.fixedHeader)
    },
    fixedHeader: function(){
        if($(window).scrollTop() > 55){
            $('.gsarea[area="header"').addClass('sticky');
        }
        else{
            $('.gsarea[area="header"').removeClass('sticky');
        }
    }
}
app.BookingTemplateTheme.init();