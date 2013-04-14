$('.manager_header').live('click', function(e) {
    $('.manager_header_function').hide();
    var theClass=  $(this).attr('target');
    $('span[parent="'+theClass+'"]').css('display','block');
    $('.manager_header').removeClass('active_manager');
    $(this).addClass('active_manager');
    
    $('.manager').hide();
    $('.'+theClass).show();
    $(window).scrollTop(0);
});

$(function() {
    $('span[target="BannerManager"]').addClass('active_manager');
    $('span[parent="BannerManager"]').css('display','block');
});

$('.manager_header_function').live('click', function(e) {
    var target = $(this).attr('target');
    var toScroll = $('.method_name_header[name="'+target+'"]"').offset();
    $(window).scrollTop(toScroll.top-10);
});

$(function() {
    $('.manager').hide();
    $('.BannerManager').show();
});
