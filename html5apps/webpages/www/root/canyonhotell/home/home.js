Home = {
    init: function() {
        $(document).on('click', '.slider_button', this.changeSlider);
    },
    
    changeSlider: function() {
        $('.slider_button.active').removeClass('active');
        var group = $(this).attr('group');
        $('.slider').hide();
        $('.slider[group="'+group+'"]').show();
        $(this).addClass('active');
    }
}

Home.init();