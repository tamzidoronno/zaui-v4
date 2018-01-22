app.PmsBooking = {
    init: function () {
        $(document).on('click', '.PmsBooking .check_available_button', app.PmsBooking.next);
        $(document).on('click', '.PmsBooking .searchbutton', app.PmsBooking.search);
        $(document).on('mousedown touchstart', '.PmsBooking .pfbox .fa', app.PmsBooking.chooseGuestinformation);
    },
    chooseGuestinformation : function() {
        var minusButton = $(this).closest('.inner').find('.fa-minus'); //Closest minusbutton
        var plusButton = $(this).closest('.inner').find('.fa-plus'); //Closest plusbutton
        var count = $(this).closest('.inner').find('.count'); //Closest numbercount for adding guests or room
        if ($(this).is('.fa-plus')) {
            if (count.val() < 10) {
                count.val(function (i, val) {
                    return (+val + 1);
                });
            }
            if ($(this).is('#add_child') && count.val() >= 1) {
                minusButton.removeClass('disabled');
            } else if (count.val() >= 2) {
                minusButton.removeClass('disabled');
            }
            if (count.val() >= 10) {
                plusButton.addClass('disabled');
            }
        }
        if ($(this).is('.fa-minus')) {
            if ($(this).is('#subtract_child')) {
                if (count.val() > 0) {
                    count.val(function (i, val) {
                        return (+val - 1);
                    });
                }
                if (count.val() <= 0) {
                    minusButton.addClass('disabled');
                }
            } else {
                if (count.val() > 1) {
                    count.val(function (i, val) {
                        return (+val - 1);
                    });
                }
                if (count.val() <= 1) {
                    minusButton.addClass('disabled');
                }
            }
            if (count.val() <= 9) {
                plusButton.removeClass('disabled');
            }
        }

    },
    getshop_setBookingTranslation : function() {
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
                    console.log(getshop_bookingconfiguration);
                }
            });    

        });
    },
    getshop_setDatePicker : function() {
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
    },
    search : function(){
        var next = $(this).attr('next_page');
        var startDate = $('#date_picker').data('daterangepicker').startDate.format('YYYY-MM-DD');
        var endDate = $('#date_picker').data('daterangepicker').endDate.format('YYYY-MM-DD');
        var rooms = $('#count_room').val();
        var adults = $('#count_adult').val();
        var children = $('#count_child').val();
        var discountCode = $('#coupon_input').val();
        
        var link = next + "#start=" + startDate + "&end=" + endDate + "&rooms=" + rooms + "&adults=" + adults + "&children=" + children + "&discount=" + discountCode;

        thundashop.common.goToPageLink(link);
    },
    next : function() {
        var next = $(this).attr('next_page');
        var app = $(this).closest('.app');
        var event = thundashop.Ajax.createEvent('','initBooking',$(this), {
            start : app.find('.start_date_input').val(),
            end : app.find('.end_date_input').val(),
            product : app.find('.selected_product').val()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            thundashop.common.goToPageLink(next);
        });
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event,'Pms form settings');
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.PmsBooking.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsBooking.init();