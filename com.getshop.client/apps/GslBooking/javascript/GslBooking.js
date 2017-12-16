
var endpoint = "http://wilhelmsenhouse.3.0.local.getshop.com";

$(document).on('keyup', '.GslBooking [gsname="visitor_name_1"]', function () {
    $('[gsname="user_fullName"]').val($(this).val());
});
$(document).on('keyup', '.GslBooking [gsname="visitor_email_1"]', function () {
    $('[gsname="user_emailAddress"]').val($(this).val());
});
$(document).on('keyup', '.GslBooking [gsname="visitor_prefix_1"]', function () {
    $('[gsname="user_prefix"]').val($(this).val());
});
$(document).on('keyup', '.GslBooking [gsname="visitor_phone_1"]', function () {
    $('[gsname="user_cellPhone"]').val($(this).val());
});
$(document).on('click', '.GslBooking .productentry_gallery .fa-times', function () {
    $(this).closest('.productentry_gallery').removeClass('active');
    $(this).closest('.productentry').css('overflow', 'hidden');
});
$(document).on('mouseover', '.GslBooking .gallery-wrapper', function () {
    var buttons = $(this).closest('.productentry_gallery').find('.move-btn');
    buttons.css('color', 'rgba(0, 0, 0, 0.7);');
});
$(document).on('mouseleave', '.GslBooking .image-wrapper', function () {
    var buttons = $(this).closest('.productentry_gallery').find('.move-btn');
    buttons.css('color', 'transparent');
});
$(document).on('click', '.GslBooking .gallery-image', function (e) {
    var featuredImage = $(this).closest('.productentry_gallery');
    featuredImage.find('.gallery-image').removeClass('active');
    if (!$(this).hasClass('active')) {
        $(this).addClass('active');
        featuredImage.find('.featured-image').css('backgroundImage', e.target.style.backgroundImage);
    }
});

var leftInterval;
$(document).on('mouseenter', '.GslBooking .move-btn', function () {
    var target = $(this);
    var imageWidth = 22;
    var scrollRate = 0.15;
    var gallery = target.closest('.productentry_gallery').find('.gallery');
    var left = gallery.position().left / 3.5 || 0;

    leftInterval = setInterval(function () {
        gallery.css('left', left + '%');
        if (target.is('.move-btn.right')) {
            if (left > -imageWidth) {
                left -= scrollRate;
            } else {
                left = 0;
                var firstImage = gallery.find('.image-wrapper:first-child').detach();
                gallery.css('left', -imageWidth + '%');
                firstImage.appendTo(gallery);
                gallery.css('left', '0%');
            }
        } else if (target.is('.move-btn.left')) {
            if (left < 0) {
                left += scrollRate;
            } else {
                //contains a bug creating lag in the image gallery
                left = -imageWidth;
                var lastImage = gallery.find('.image-wrapper:last-child').detach();
                lastImage.prependTo(gallery);
                gallery.css('left', '22%');
            }
        }
    }, 1);
});
$(document).on('mouseleave', '.GslBooking .move-btn', function () {
    clearInterval(leftInterval);
});
$(document).on('keyup', '.GslBooking #guest_zipcode', function () {
    var val = $(this).val();
    if (val.length !== 4) {
        return;
    }
    $.ajax({
        "dataType": "jsonp",
        "url": "https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=no&pnr=" + val,
        "success": function (data) {
            if (data.valid) {
                $('#guest_zipname').val(data.result);
            }
        }
    });
});
$(document).on('click', '.GslBooking .ordersummary .continue', function () {
    $('.productoverview').fadeOut('400', function () {
        $('.addons_overview').fadeIn('400');
        $('.GslBooking .ordersummary').slideUp();
        $('.GslBooking .gslbookingHeader').slideUp();
    });
});
$(document).on('mousedown', '.GslBooking .addButton', function () {
    if ($(this).hasClass('added_addon')) {
        $(this).removeClass('added_addon').html('Add');
        if ($(this).is('#addon_earlyCheckin')) {
            $('.guestentry .fa-sign-in').removeClass('active_addon');
        } else if ($(this).is('#addon_breakfast')) {
            $('.guestentry .fa-cutlery').removeClass('active_addon');
        } else if ($(this).is('#addon_coat')) {
            $('.guestentry .fa-black-tie').removeClass('active_addon');
        } else if ($(this).is('#addon_lateCheckout')) {
            $('.guestentry .fa-sign-out').removeClass('active_addon');
        }
    } else {
        $(this).addClass('added_addon').html('Remove');
        if ($(this).is('#addon_earlyCheckin')) {
            $('.guestentry .fa-sign-in').addClass('active_addon');
        } else if ($(this).is('#addon_breakfast')) {
            $('.guestentry .fa-cutlery').addClass('active_addon');
        } else if ($(this).is('#addon_coat')) {
            $('.guestentry .fa-black-tie').addClass('active_addon');
        } else if ($(this).is('#addon_lateCheckout')) {
            $('.guestentry .fa-sign-out').addClass('active_addon');
        }
    }
});
$(document).on('mousedown', '.GslBooking .destination_line', function () {
    var destination = $(this).text();
    if (destination !== "") {
        $('#destination').val(destination);
        $('.destinationInfoBox').hide();
    }
});
$(document).on('focus', '.GslBooking #guests', function () {
    $('.guestInfoBox').show();
});
$(document).on('focus', '.GslBooking #destination', function () {
    $('.destinationInfoBox').show();
});
$(document).on('mousedown', '.GslBooking .guestInfoBox .fa', function () {
    var minusButton = $(this).closest('.count_line').find('.fa-minus'); //Closest minusbutton
    var plusButton = $(this).closest('.count_line').find('.fa-plus'); //Closest plusbutton
    var count = $(this).closest('.count_line').find('.count'); //Closest numbercount for adding guests or room
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
});
$(document).on('change', '.GslBooking #coupon_input', function () {//Do not use this live, as it is visible in the browser
    if ($('#coupon_input').val() === 'code' || $('#coupon_input').val() === 'Code') {
        $(this).addClass('validCode');
        $(this).removeClass('non-validCode');
    } else if ($('#coupon_input').val() === "") {
        $(this).removeClass('validCode');
        $(this).removeClass('non-validCode');
    } else {
        $(this).removeClass('validCode');
        $(this).addClass('non-validCode');
    }
});
function showAddonsPage() {
    $('.overview').hide();
    $('.productoverview').fadeOut('400', function () {
        $('.addons_overview').fadeIn('400');
    });
}
function showProductList() {
    $('.gslbookingBody').fadeIn('400');
    $('.overview').hide();
    $('.addons_overview').fadeOut('400', function () {
        $('.productoverview').fadeIn('400');
        if(gslbookingcurresult) {
            updateOrderSummary(gslbookingcurresult);
            $('.GslBooking .gslbookingHeader').show();
        }
    });
}
function showOverviewPage() {
    $('.addons_overview').fadeOut('400', function () {
        $('.overview').fadeIn('400');
    });
}
function previusPage() {
    $('.overview').fadeOut('400', function () {
        $('.productoverview').fadeIn('400');
    });
}
function confirmGuestInfoBox() {
    var room = $('#count_room').val();
    var adult = $('#count_adult').val();
    var child = $('#count_child').val();
    var guest = +adult + +child;
    var roomText = ' room';
    var guestText = ' guest ';
    if (room > 1) {
        roomText = ' rooms';
    }
    if (guest > 1) {
        guestText = ' guests ';
    }
    $('#guests').val(room + roomText + ', ' + guest + guestText);
    $('.guestInfoBox').hide();
}

function updateOrderSummary(res) {
    $('.GslBooking .ordersummary .selectedguests').html('');
    var total = 0;
    var row = "";
    for(var k in res.rooms) {
        var room = res.rooms[k];
        for(var guest in room.roomsSelectedByGuests) {
            var count = room.roomsSelectedByGuests[guest];
            if(count > 0) {
                row += "<tr><td>" + count + " room";
                if(count > 1) {
                    row += "s";
                }
                row += " selected of "+ room.name + " selected</td><td>" + guest + " guest";
                if(guest > 1) {
                    row += "s";
                }
                row += "</td>";
                row += "<td>" + (room.pricesByGuests[guest] * count) + "</td>";
                row += "</tr>";
                total += (room.pricesByGuests[guest] * count);
                console.log("inner: " + total);
            }
        }
    }
    $('.GslBooking .ordersummary .selectedguests').html("<table id='priceoffertable'>" + row + "</table>");
    $('.GslBooking .ordersummary .totalprice').html(total);
    $('.GslBooking .ordersummary').slideDown();
}

$(document).on('mousedown', '.GslBooking .addguest', function () {
    var addNewGuest = $('#addnewguest');
    $(this).before(addNewGuest);
});
$(document).on('mousedown', '.GslBooking .addroom', function () {
    var addNewRoom = $('#addnewroom');
    $(this).before(addNewRoom);
});
$(document).on('mousedown', '.GslBooking .guestentry i', function () {
    if ($(this).is('.removeguest')) {
        var removeGuest = confirm('Are you sure u want to remove this guest?');
        if (removeGuest === true) {
            $(this).closest('.guestentry').remove();
        }
    } else {
        $(this).toggleClass('active_addon');
    }
});
$(document).on('mousedown', '.GslBooking', function (e) {
    var target = $(e.target);
    if (target.is('#guests') || target.hasClass('guestInfoBox') || target.closest('.guestInfoBox').length >= 1) {
        return;
    }
    confirmGuestInfoBox();
});
$(document).on('mousedown', '.GslBooking', function (e) {
    var target = $(e.target);
    if (target.is('#destination') || target.hasClass('destinationInfoBox') || target.closest('.destinationInfoBox').length >= 1) {
        return;
    }
    $('.destinationInfoBox').hide();
});
$(document).ready(function () {
    var currentDate = new Date();
    currentDate.setDate(currentDate.getDate() - 1);

    var endDate = new Date();
    endDate.setDate(endDate.getDate() + 1);

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
});
$(document).on('change', '.GslBooking .numberof_rooms', function () {
    var index = $(this).closest('.productentrybox').attr('index');
    var guest = $(this).attr('guests');
    gslbookingcurresult.rooms[index].roomsSelectedByGuests[guest] = $(this).val();
    
    var target = $(this);
    var totalCost = 0;
    var selectedRooms = 0;
    var totalSelectedRooms = 0;
    var allContainers = target.closest('#productentry');
    var mainContainer = target.closest('.productentry_main');
    var availableRooms = parseInt(mainContainer.find('.product_availablerooms').attr('value'));

    allContainers.find('.numberof_rooms').each(function () {
        var loopdropdown = $(this);
        var selected = parseInt(loopdropdown.val());
        var price = $('option:selected', this).attr('data-price');
        var priced = parseInt(price);
        totalSelectedRooms += selected;
        totalCost += priced;
    });
    mainContainer.find('.numberof_rooms').each(function () {
        var loopdropdown = $(this);
        var selected = parseInt(loopdropdown.val());
        selectedRooms += selected;
    });
    var totalLeft = availableRooms - selectedRooms;
    mainContainer.find('.numberof_rooms').each(function () {
        var loopdropdown = $(this);
        loopdropdown = parseInt(loopdropdown.val());
        var thisIsWhatShouldBeHere = (loopdropdown + totalLeft);
        var roomsToRemove = availableRooms - thisIsWhatShouldBeHere;
        for (var i = 1; i <= roomsToRemove; i++) {
            var optionValue = (availableRooms + 1) - i;
            var thisOption = $(this).find('option[value="' + optionValue + '"]');
            thisOption.addClass('unavailableRooms').prop('disabled', true);
        }
        for (var i = 1; i <= thisIsWhatShouldBeHere; i++) {
            var thisOption = $(this).find('option[value="' + i + '"]');
            thisOption.removeClass('unavailableRooms').prop('disabled', false);
        }
    });
    
    var multipleRooms = ' rooms ';
    if (totalSelectedRooms == 1) {
        multipleRooms = ' room '
    }
    $('#productoverview_footer').slideDown('slow');
    updateOrderSummary(gslbookingcurresult);
});

var gslbookingcurresult = null;

$(document).on('click', '.GslBooking #search_rooms', function () {
    $('.GslBooking .ordersummary').hide();
    $(this).html('<i class="fa fa-spin fa-spinner"></i>');
    var btn = $(this);
    $('.productentrybox').remove();
    var time = new Date().toLocaleTimeString('en-us');
    var startDate = $('#date_picker').data('daterangepicker').startDate.format('MMM DD, YYYY ') + time;
    var endDate = $('#date_picker').data('daterangepicker').endDate.format('MMM DD, YYYY ') + time;
    var rooms = $('#count_room').val();
    var adults = $('#count_adult').val();
    var children = $('#count_child').val();
    var discountCode = $('#coupon_input').val();
    var data = {
        "start": startDate,
        "end": endDate,
        "rooms": rooms,
        "adults": adults,
        "children": children,
        "discountCode": discountCode,
        "bookingId": ""
    };

    $.ajax(endpoint + '/scripts/bookingprocess.php?method=startBooking', {
        dataType: 'jsonp',
        data: {
            "body": data
        },
        success: function (res) {
            btn.html("Search");
            $('#productentry').html('');
            gslbookingcurresult = res;
            updateOrderSummary(res);
            for (var k in res.rooms) {
                var room = res.rooms[k];
                var firstFile = room.images[0].fileId;
                var productentry = '';
                var utilities = '';
                var user_icon = '';
                if (room.availableRooms != 0) {
                    var roomBox = $('#productentrybox').clone();
                    roomBox.attr('id',null);
                    roomBox.addClass('productentrybox');
                    
                    roomBox.find('.roomname').html(room.name);
                    roomBox.find('.product_availablerooms').val(room.availableRooms);
                    roomBox.find('.availableroomcontainer').html(room.availableRooms);
                    roomBox.find('.lowpricedisplayer').html(room.pricesByGuests[1]);
                    roomBox.find('.roomdescription').html(room.description);
                    roomBox.find('.featured-image').css('background-image','url(https://wilhelmsenhouse.getshop.com/displayImage.php?id='+ firstFile);
                    roomBox.attr('index', k);
                    for (var guest in room.pricesByGuests) {
                        var numberofrooms = '';
                        var index = 1;
                        var multipleGuests = ' guests';
                        if (guest == 1) {
                            multipleGuests = ' guest'
                        }
                        user_icon += '<i class="fa fa-user"></i>';
                        for (var i = 1; i <= room.availableRooms; i++) {
                            var price = room.pricesByGuests[guest] * i;
                            numberofrooms += '<option value="' + i + '" data-price="' + price + '">' + i + '&nbsp;&nbsp; (NOK ' + price + ')</option>';
                        }
                        productentry = $('<div class="productentry_itemlist">' + user_icon + '<div>' + room.name + ', ' + guest + multipleGuests + ' NOK ' + room.pricesByGuests[guest] + ',-</div><div style="float:right;padding:0;"><span>Choose rooms</span><select class="numberof_rooms" guests="'+guest+'"><option value="0" data-price="0">0</option>' + numberofrooms + '</select></div></div>');
                        productentry.find('.numberof_rooms').val(room.roomsSelectedByGuests[guest]);
                        roomBox.find('.guestselection').append(productentry);
                    }

                    var controller = '';
                    if (room.images.length > 4) {
                        controller = '<div class="controls"><div class="move-btn left"><i class="fa fa-caret-left"></i></div><div class="move-btn right"><i class="fa fa-caret-right"></i></div></div>'
                    }
                    for (var utility in room.utilities) {
                        utilities += '<i class="fa fa-' + utility + '" title="' + room.utilities[utility] + '"></i>';
                    }

                    $('#productentry').append(roomBox);
                    roomBox.find('.gsgalleryroot').prepend(controller);
                    roomBox.show();
                    
                    for (var i = 0; i <= room.images.length - 1; i++) {
                        var active = "";
                        if (i == 0) {
                            active = ' active';
                        }
                        var imgaddr = endpoint + '/displayImage.php?id=' + room.images[i].fileId;
                        var img = $("<img>");
                        img.attr('index', k);
                        img.attr("src", imgaddr);
                        img.attr('innerindex', i);
                        img.load(function () {
                            
                            var idx = $(this).attr('index');
                            var inneridx = $(this).attr('innerindex');
                            var realWidth = this.width;
                            var realHeight = this.height;
                            var img = $(this).attr('src');
                            var roomBox = $('.productentrybox[index="'+idx+'"]');
                            var width = realWidth;
                            var height = realHeight;
                            var image = '<img class="roomimg gsgallery" style="display:none;" src="' + img + '" img="' + img + '" width="' + width + '" height="' + height + '" index="' + inneridx + '">';
                            roomBox.find('.gallery').append('<div class="image-wrapper"><figure class="gallery-image image-holder' + active + '" style="background-image:url(\'' + img + '\')"></figure></div>');
                            roomBox.find('.photoswipecontainer').append(image);
                            if (inneridx === "0") {
                                roomBox.find('.featured-image').attr("img", img);
                                roomBox.find('.featured-image').attr("height", height);
                                roomBox.find('.featured-image').attr("width", width);
                            }
                        });
                    }
                }
            }
        }
    });
});