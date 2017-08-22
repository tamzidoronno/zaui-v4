app.BankenHotelTheme = {
    init: function(){
        $(document).on('scroll', app.BankenHotelTheme.fixedHeader)
    },
    fixedHeader: function(){
        if($(window).scrollTop() > 65){
            $('.gsarea[area="header"').addClass('sticky');
        }
        else{
            $('.gsarea[area="header"').removeClass('sticky');
        }
    }
}
app.BankenHotelTheme.init();