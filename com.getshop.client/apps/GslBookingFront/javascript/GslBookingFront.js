var getshop_endpoint = "";

function showGuestBox() {
    $('.guestInfoBox').show();
}
function hideGuestBox(e) {
    var target = $(e.target);

    if(target.attr('id') == "guests" || target.closest('#guests').length > 0) {
        return;
    }

    if(target.closest('.guestInfoBox').length > 0 || target.hasClass('guestInfoBox')) {
        return;
    }
    $('.GslBookingFront .guestInfoBox').hide();

    var room = $('#count_room').val();
    var adult = $('#count_adult').val();
    var child = $('#count_child').val();
    var guest = +adult + +child;
    var translation = getshop_getBookingTranslations();
    if(typeof(translation['room']) === "undefined") {
        return;
    }
    var roomText = ' ' + translation['room'].toLowerCase();
    var guestText = ' ' + translation['guest'].toLowerCase() + ' ';
    if (room > 1) {
        roomText = ' ' + translation['rooms'].toLowerCase();
    }
    if (guest > 1) {
        guestText = ' '+translation['numberofguests'].toLowerCase()+' ';
    }
    $('#guests').val(room + roomText + ', ' + guest + guestText);
}
function chooseGuestinformation(e) {
    e.preventDefault();
    var btn = $(this);
    var minusButton = btn.closest('.inner').find('.fa-minus'); //Closest minusbutton
    var count = btn.closest('.inner').find('.count').val(); //Closest numbercount for adding guests or room
    var room = $('#count_room').val();
    var adult = $('#count_adult').val();
    var child = $('#count_child').val();

    count = parseInt(count);
    room = parseInt(room);
    adult = parseInt(adult);
    child = parseInt(child);

    if(btn.hasClass('disabled')) {
        return;
    }

    if (btn.is('.fa-plus')) {
        count++;
        if ($(this).is('#add_child') && count >= 1) {
            minusButton.removeClass('disabled');
        } else if (count >= 2) {
            minusButton.removeClass('disabled');
        }
        if ($(this).is('#add_room') && count > (adult+child)) {
            $('#count_adult').val(count-child);
            $('#count_adult').closest('.inner').find('.fa-minus').removeClass('disabled');
        }
    }
    if (btn.is('.fa-minus')) {
        count--;
        if ($(this).is('#subtract_child')) {
            if (count <= 0) {
                minusButton.addClass('disabled');
            }
            if((count+adult) < room){
                $('#count_room').val(count+adult);
                if($('#count_room').val() <= 1){
                    $('#count_room').closest('.inner').find('.fa-minus').addClass('disabled');
                }
            }
        } else {
            if (count <= 1) {
                minusButton.addClass('disabled');
            }
        }
        if ($(this).is('#subtract_adult') && (count+child) < room) {
            $('#count_room').val(count+child);
            if($('#count_room').val() <= 1){
                $('#count_room').closest('.inner').find('.fa-minus').addClass('disabled');
            }
        }
    }

    $(this).closest('.inner').find('.count').val(count);
}
function getshop_setBookingTranslationFront() {
    var loadTranslation = load_getBookingTranslations();
    loadTranslation.done(function(translations) {
        for(var key in translations) {
            var field = $('[gstype="bookingtranslation"][gstranslationfield="'+key+'"]');
            if(field.is(':button')) {
                field.attr('value', translations[key]);
            } else {
                field.html(translations[key]);
            }
            var field = $('[gstype="bookingtranslation_placeholder"][gstranslationfield="'+key+'"]');
            field.attr('placeholder', translations[key]);
        }
        var guestInput = $('#guests');
        guestInput.val('1 '+translations['room'].toLowerCase()+', 1 ' + translations['guest'].toLowerCase());

        var hash = window.location.hash.substr(1);
        if(hash) {

        }

        $.ajax({
            "dataType": "jsonp",
            "url": getshop_endpoint + '/scripts/bookingprocess.php?method=getConfiguration',
            success: function (config) {
                getshop_bookingconfiguration = config;
                var text = getshop_translationMatrixLoaded['agebelow'];
                text = text.replace("{age}", config.childAge);
                $("[gstranslationfield='agebelow']").html(text);
            }
        });    

    });
}
function load_getBookingTranslations() {
    var def = $.Deferred();
    $.ajax({
        "dataType": "jsonp",
        "url": getshop_endpoint + "/scripts/bookingprocess_translation.php",
        success: function (res) {
            getshop_translationMatrixLoaded = res;
            def.resolve(res);
        }
    });    
    return def;
}
function getshop_getBookingTranslations() {
    if(typeof(getshop_translationMatrixLoaded) !== "undefined") {
        return getshop_translationMatrixLoaded;
    }
    return {};
}
function getshop_setDatePickerFront() {
    var currentDate = new Date();
    var endDate = new Date();
    endDate.setTime(endDate.getTime() + (86400*1000)); 

    $('#date_picker_start').val(currentDate.toISOString().substring(0, 10));
    $('#date_picker_end').val(endDate.toISOString().substring(0, 10));

    $('#date_picker').daterangepicker({
        "autoApply": true,
        "showWeekNumbers": true,
        "minDate": currentDate,
        "endDate": endDate,
        "locale": {
            "direction": "ltr",
            "format": "DD MMM",
            "firstDay": 1
        }
    });    

    $('#date_picker_start').on('blur', function() {
        var start = new Date($(this).val());
        $("#date_picker").data('daterangepicker').setStartDate(start);

        var end = new Date($('#date_picker_end').val());
        if(start.getTime() > end.getTime()) {
            end.setTime(start.getTime() + (86400*1000)); 
            $('#date_picker_end').val(end.toISOString().substring(0, 10));
            $("#date_picker").data('daterangepicker').setEndDate(end);
        }
    });

    $('#date_picker_end').on('blur', function() {
        var end = new Date($(this).val());
        var start = new Date($('#date_picker_start').val());

        if(start.getTime() > end.getTime()) {
            start.setTime(start.getTime() - (86400*1000)); 
            $('#date_picker_start').val(start.toISOString().substring(0, 10));
            $("#date_picker").data('daterangepicker').setStartDate(start);
        }

        $("#date_picker").data('daterangepicker').setEndDate(end);
    });
}
function search(){
    var startDate = $('#date_picker').data('daterangepicker').startDate.format('YYYY-MM-DD');
    var endDate = $('#date_picker').data('daterangepicker').endDate.format('YYYY-MM-DD');
    var rooms = $('#count_room').val();
    var adults = $('#count_adult').val();
    var children = $('#count_child').val();
    var discountCode = $('#coupon_input').val();

    var link = getshop_urltonextpage + "#start=" + startDate + "&end=" + endDate + "&rooms=" + rooms + "&adults=" + adults + "&children=" + children + "&discount=" + discountCode;

    window.location.href = link;
}


$(document).on('click', '.GslBookingFront .searchbutton', search);
$(document).on('click', '.GslBookingFront .fa', chooseGuestinformation);
$(document).on('mousedown', '.GslBookingFront #guests', showGuestBox);
$(document).on('mousedown', hideGuestBox);