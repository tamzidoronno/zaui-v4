
var getshop_endpoint = "";
var leftInterval;

function getshop_setBookingTranslation() {
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

function getshop_setSameAsGuest() {
    var container = $('.roomrowadded');
    var checkbox = $(this);
    $('.guestRows').each(function() {
        var name = $(this).find("[gsname='name']").val();
        if(name) {
            if(checkbox.is(':checked')) {
                $('[gsname="user_fullName"]').val(name);
                $('[gsname="user_emailAddress"]').val($(this).find("[gsname='email']").val());
                $('[gsname="user_cellPhone"]').val($(this).find("[gsname='phone']").val());
                $('[gsname="user_prefix"]').val($(this).find("[gsname='prefix']").val());
            } else {
                $('[gsname="user_fullName"]').val("");
                $('[gsname="user_emailAddress"]').val("");
                $('[gsname="user_cellPhone"]').val("");
                $('[gsname="user_prefix"]').val("47");
            }
        }
    });    
}

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
    getshop_clearInterval(leftInterval);
});

$(document).on('keyup', '.GslBooking #guest_zipcode', getshop_updateZipCode);

function getshop_updateZipCode() {
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
}


function getshop_loadAddonsAndGuestSumaryView() {
    var toPush = [];
    for(var k in gslbookingcurresult.rooms) {
        var room = gslbookingcurresult.rooms[k];
        var obj = {};
        obj.id = room.id;
        obj.roomsSelectedByGuests = room.roomsSelectedByGuests;
        toPush.push(obj);
    }
    
    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=getAddonsSummary', {
        dataType: 'jsonp',
        data: {
            "body": toPush
        },
        success: function (res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
        }
    });
}

function getshop_loadAddonsAndGuestSummaryByResult(res) {
    $('.addonprinted').remove();
    var template = $('.addonsentry #addon');
    var translation = getshop_getBookingTranslations();
    if(typeof(res) !== "undefined" || typeof(res.items) !== "undefined") {
        for(var k in res.items) {
            var item = res.items[k];
            var entry = template.clone();
            
            var select = entry.find('.addonsSelectionCount');
            if(item.maxAddonCount > 1) {
                select.html('');
                for(var i = 1; i <= item.maxAddonCount; i++) {
                    select.append("<option value='" + i + "'>" + i + "</option>");
                }
            }

            
            var icon = "fa-" + item.icon;
            if(icon === "fa-") {
                icon = "fa-question-circle";
            }
            var fontawesomeicon = $('<i class="fa '+icon+'" title="'+addon.name+'"></i>');
            
            entry.attr('id','');
            entry.attr('productid', item.productId);
            entry.addClass('addonprinted');
            entry.find('.icon').html(fontawesomeicon);
            entry.find('.text').html(item.name);
            entry.find('.price').html(item.price);
            if(item.isAdded) {
                entry.find('.addButton').addClass('added_addon').html(translation['remove']);
            } else if(item.maxAddonCount > 1) {
                select.show();
            }
            entry.show();
            $('.addonsentry .overview_addons').append(entry);
        }
    }
   
    getshop_loadRooms(res);
    getshop_loadTextualSummary(res);
    getshop_loadBookerInformation(res);
    getshop_loadLoggedOn(res);
}

function getshop_loadLoggedOn(res) {
    $('.overview_hide').show();
    $('.overview_contact').hide();
    $('.overview_loggedon').hide();
    if(res.isLoggedOn) {
        $('.overview_loggedon').show();
        $('.loggedonusername').html(res.loggedOnName);
        $('.overview_confirmation').show();
        $('.agreetotermsbox').show();
    } else {
        $('.overview_contact').show();
    }
}

function getshop_loadBookerInformation(res) {
    for(var field in res.fields) {
        $('.overview_article [gsname="'+field+'"]').val(res.fields[field]);
    }
    $('.selectedusertype[id="'+res.profileType+'"]').mousedown();
}

function getshop_loadTextualSummary(res) {
    $('.yourstaysummary').html('');
    for(var k in res.textualSummary) {
        $('.yourstaysummary').append(res.textualSummary[k] + "<br>");
    }
    
    $('[gstranslationfield="readTerms"]').attr('onclick',"window.open('"+getshop_endpoint+"/scripts/loadContractPdf.php?readable=true&engine=default')");
    $('[gstranslationfield="downloadTerms"]').attr('onclick',"window.open('"+getshop_endpoint+"/scripts/loadContractPdf.php?engine=default')");
}

function getshop_loadRooms(res) {
    var roomContainer = $('#roomentrycontainer');
    var translation = getshop_getBookingTranslations();
    $('.roomrowadded').remove();
    for(var k in res.rooms) {
        var newRoom = roomContainer.clone();
        newRoom.addClass('roomrowadded');
        var room = res.rooms[k];
        var guestTemplateRow = newRoom.find('#guestentryrow');
        var addedAddons = false;
        for(var i = 0; i < room.guestCount;i++) {
            var guestRow = guestTemplateRow.clone();
            guestRow.attr('id','');
            guestRow.show();
            var guestObject = room.guestInfo[i];
            if(guestObject) {
                guestRow.find('[gsname="name"]').val(guestObject.name);
                guestRow.find('[gsname="prefix"]').val(guestObject.prefix);
                guestRow.find('[gsname="email"]').val(guestObject.email);
                guestRow.find('[gsname="phone"]').val(guestObject.phone);
            }
            
            if(addedAddons) {
                guestRow.find('.guest_addon').hide();
            }
            guestRow.append('<i class="fa fa-times removeguest" title="'+translation['removeguest']+'"></i>');
            
            addedAddons = true;
            newRoom.find('.guestRows').append(guestRow);
        }
        newRoom.attr('id','');
        newRoom.attr('roomid',room.roomId);
        newRoom.find('.roomname').html(room.roomName);
        newRoom.find('.startdate').html(getshop_js_yyyy_mm_dd_hh_mm_ss(room.start));
        newRoom.find('.enddate').html(getshop_js_yyyy_mm_dd_hh_mm_ss(room.end));
        if(room.maxGuests <= room.guestCount) {
            newRoom.find('.addguest').hide();
        }
        $('.roomentryframe').append(newRoom);
        
        var added = false;
        for(var addonKey in room.addonsAvailable) {
            var addon = room.addonsAvailable[addonKey];
            var icon = "fa-" + addon.icon;
            if(icon === "fa-") {
                icon = "fa-question-circle";
            }
            var fontawesomeicon = $('<i class="fa guestaddonicon '+icon+'" title="'+addon.name+'"></i>');
            fontawesomeicon.attr('productid', addon.productId);
            if(addon.isAdded) {
                fontawesomeicon.addClass('active_addon');
            }
            
            if(addon.maxAddonCount > 1 && !addon.isAdded) {
                var countselection = $('<span class="countselections"></span>');
                for(var j = 1; j <= addon.maxAddonCount; j++) {
                    countselection.append($("<span class='countselection' count='"+j+"'>" + j + "</span>"));
                }
                fontawesomeicon.html(countselection);
            }
            newRoom.find('.guest_addon').append(fontawesomeicon);
            added = true;
        }
        
        if(!added) {
            newRoom.find('.guest_addon').hide();
            newRoom.find('[gstranslationfield="addons"]').hide();
        }
        
        newRoom.show();
    }
}


function getshop_js_yyyy_mm_dd_hh_mm_ss(now) {
  var now = new Date(now);
  var year = "" + now.getFullYear();
  var month = "" + (now.getMonth() + 1); if (month.length == 1) { month = "0" + month; }
  var day = "" + now.getDate(); if (day.length == 1) { day = "0" + day; }
  var hour = "" + now.getHours(); if (hour.length == 1) { hour = "0" + hour; }
  var minute = "" + now.getMinutes(); if (minute.length == 1) { minute = "0" + minute; }
  var second = "" + now.getSeconds(); if (second.length == 1) { second = "0" + second; }
  return day + "." + month + "." + year + " " + hour + "." + minute;
}

function getshop_continueToSummary() {
    $('.productoverview').fadeOut('400', function () {
        $('.addons_overview').fadeIn('400');
        $('.GslBooking .ordersummary').slideUp();
        $('.GslBooking .gslbookingHeader').slideUp();
        localStorage.setItem('gslcurrentpage','summary');
        getshop_loadAddonsAndGuestSumaryView();
        $(window).scrollTop(0);
    });
}

function getshop_createSticky(sticky) {
    if (typeof sticky !== "undefined") {
        var pos = sticky.offset().top;
        var win = $(window);
        var stopperbox = $('#productoverview_footer');
        var paddingBox = $('.productoverview');
        var stopPos = (stopperbox.offset().top - sticky.height());
        var yPos = stopPos - (sticky.height()*2) -14;/*should only be stopPos, but sets yPos wrong*/

        $('.GslBooking .ordersummary').css('width', $('.GslBooking .ordersummary').outerWidth());
        if(!$('.GslBooking .ordersummary').is(':visible')) {
            return;
        }
        win.on("scroll", function () {
            if(win.scrollTop() > pos && win.scrollTop() < stopPos) {
                sticky.addClass('sticky');
                sticky.css({
                    position: 'fixed',
                    top: 0
                }); 
                paddingBox.css('padding-top', sticky.height()+'px');
            } 
            else if (win.scrollTop() >= stopPos) {
                sticky.css({
                    position: 'absolute',
                    top: yPos
                });
            } 
            else {
                sticky.removeClass('sticky');
                sticky.css({
                    position: 'relative',
                    top: '0'
                });
                $('.productoverview').css('padding-top', '0px');
            }
        });
    }
}
function getshop_addRemoveAddons(btn) {
    var saving = getshop_saveGuestInformation();
    saving.done(function() {
        var body = {};
        
        if(btn.hasClass('guestaddonicon')) {
            if(btn.find('.countselections').length > 0) {
               btn.find('.countselections').toggle();
               return;
            }
            
            body['roomId'] = btn.closest('.roomrowadded').attr('roomid');
            body['productId'] = btn.attr('productid');
        } else if(btn.hasClass('countselection')) {
            body['roomId'] = btn.closest('.roomrowadded').attr('roomid');
            body['productId'] = btn.closest('.guestaddonicon').attr('productid');
            body['count'] = btn.attr('count');
        } else {
            body["productId"] = btn.closest('.addon').attr('productid');
            body["count"] = btn.closest('.addon').find('.addonsSelectionCount').val()
        }
        
        if (btn.hasClass('added_addon') || btn.hasClass('active_addon')) {
           $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=removeAddons', {
                dataType: 'jsonp',
                data: { body: body },
                success: function (res) {
                    getshop_loadAddonsAndGuestSummaryByResult(res);
                }
            });
        } else {
            $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=addAddons', {
                dataType: 'jsonp',
                data: { body: body },
                success: function (res) {
                    getshop_loadAddonsAndGuestSummaryByResult(res);
                }
            });
        }
    });
}

function getshop_logon() {
    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=logOn', {
        dataType: 'jsonp',
        data : {
            body : {
                "username" : $("input[gsname='username']").val(),
                "password" : $("input[gsname='password']").val()
            }
        },
        success: function (res) {
            $('.failedlogon').hide();
            getshop_loadAddonsAndGuestSummaryByResult(res);
            if(!res.isLoggedOn) {
                $('.failedlogon').slideDown();
            }
        },
        error: function(res) {
        }
    });
}

function getshop_logout() {
    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=logOut', {
        dataType: 'jsonp',
        success: function (res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
            $('.overview_confirmation').hide();
        },
        error: function(res) {
        }
    });
}
function getshop_changeBookingType() {
    $('.errormessage').hide();
    $('.invalidinput').removeClass('invalidinput');
    var type = $(this).attr('id');
    $('.agreetotermsbox').hide();
    $('.overview_confirmation').hide();
    $('.guesttypeselection').hide();
    $('.guesttypeselection[type="'+type+'"]').show();
    $(this).find('input').click();
    if(type !== "gotAccount") {
        $('.agreetotermsbox').show();
        $('.overview_confirmation').show();
    }
}
function getshop_changedestination() {
    var destination = $(this).text();
    if (destination !== "") {
        $('#destination').val(destination);
        $('.destinationInfoBox').hide();
    }
}

function getshop_showGuestBox() {
    $('.guestInfoBox').show();
}
function getshop_showDesitinationBox() {
    $('.destinationInfoBox').show();
}

function getshop_gotopayment() {
var btn = $(this);
    var saving = getshop_saveBookerInformation();
    $('.errormessage').hide();
    $('.agreetotermserrormessage').hide();
    $('.invalidinput').removeClass('invalidinput');
    saving.done(function(res) {
        for(var field in res.fieldsValidation) {
            if(field === "agreeterms") {
                continue;
            }
            var errorMessage = res.fieldsValidation[field];
            if(errorMessage) {
                $('[gsname="'+field+'"]').closest('div').find('input').addClass('invalidinput');
                $('.errormessage').slideDown();
            }
        }
        if(res.fieldsValidation['agreeterms']) {
            $('.agreetotermserrormessage').slideDown();
        }
        if(res.isValid) {
            $('.successcompleted').hide();
            if(btn.hasClass('fa-spin')) {
                return;
            }
            var completing = getshop_completeBooking();
            btn.html('<i class="fa fa-spin fa-spinner"></i>');
            completing.done(function(res) {
                if(res.continuetopayment == 1) {
                    window.location.href = getshop_endpoint + "/?page=cart&payorder=" + res.orderid;
                } else {
                    $('.gslbookingBody').hide();
                    $('.successcompleted').show();
                }
            });
            completing.fail(function() {
                $('.gslbookingBody').hide();
                $('.errorcompleted').show();
            })
        }
    });    
}


function getshop_completeBooking() {
   var def = $.Deferred();
   $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=completeBooking', {
        dataType: 'jsonp',
        body : "",
        success: function (res) {
            def.resolve(res);
        },
        error: function(res) {
            def.fail(res);
        }
    });
    return def;
}

function getshop_changeGuestSelection() {
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
    
}

function getshop_saveBookerInformation() {
    var fields = {};
    $('.overview_article [gsname]').each(function() {
        var field = $(this).attr('gsname');
        var val = $(this).val();
        fields[field] = val;
    });
    var type = $("input[name='user']:checked").closest('label').attr('id');
    var data = {
        "profileType" : type,
        "fields" : fields,
        "ordertext" : $('[gsname="ordertext"]').val(),
        "agreeToTerms" : $('#agreeTerms').is(':checked')
    };
    var dfr = $.Deferred();
    
    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=setGuestInformation', {
        dataType: 'jsonp',
        data: {
            "body": data
        },
        success: function (res) {
            localStorage.setItem('gslcurrentbooking', JSON.stringify(gslbookingcurresult));
            dfr.resolve(res);
        }
    });
    return dfr;
}

function getshop_showAddonsPage() {
    getshop_saveBookerInformation();
    localStorage.setItem('gslcurrentpage','summary');
    $('.overview').hide();
    $('.productoverview').fadeOut('400', function () {
        $('.addons_overview').fadeIn('400');
        getshop_loadAddonsAndGuestSumaryView();
    });
}

function getshop_saveGuestInformation() {
    var toSave = [];
    $('.roomrowadded').each(function() {
        var guestCount = 0;
        var room = $(this);
        var roomId = room.attr('roomid');
        var roomInfo = {};
        roomInfo.roomId = roomId;
        
        var guests = [];
        $(this).find('.guestRows').find('.guestentry').each(function() {
            guestCount++;
            var info = {
                "prefix" : $(this).find('[gsname="prefix"]').val(),
                "phone" : $(this).find('[gsname="phone"]').val(),
                "name" : $(this).find('[gsname="name"]').val(),
                "email" : $(this).find('[gsname="email"]').val()
            };
            guests.push(info);
            
        });
        roomInfo.guests = guests;
        roomInfo.numberOfGuests = guestCount;
        toSave.push(roomInfo);
    });
    var dfd = jQuery.Deferred();
    if(toSave.length == 0) {
        return dfd;
    }
    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=saveGuestInformation', {
        dataType: 'jsonp',
        data: {
            "body": toSave
        },
        success: function (res) {
            dfd.resolve(res);
        }
    });
    return dfd;
}

function getshop_showProductList() {
    $('.gslbookingBody').fadeIn('400');
    $('.overview').hide();
    $('.addons_overview').fadeOut('400', function () {
        $('.productoverview').fadeIn('400');
        if(gslbookingcurresult) {
            getshop_updateOrderSummary(gslbookingcurresult, false);
            $('.GslBooking .gslbookingHeader').show();
        }
    });
}
function getshop_showOverviewPage() {
    $(window).scrollTop(0);
    localStorage.setItem('gslcurrentpage','overview');
    var saving = getshop_saveGuestInformation();
    $('.invalidinput').removeClass('invalidinput');
    $('.GslBooking .errormessage').hide();
    saving.done(function(res) {
        var success = true;
        for(var k in res.fieldsValidation) {
            if(!k.startsWith("guest_")) {
                continue;
            }
            var text = res.fieldsValidation[k];
            if(!text) {
                text = "&nbsp";
            } else {
                success = false;
                $('.GslBooking .errormessage').slideDown();
            }
            var splitted = k.split("_");
            $('.roomrowadded[roomid="'+splitted[1]+'"] [gsname="'+splitted[2]+'"]').closest("div").addClass('invalidinput');
        }
        if(success) {
            $('.addons_overview').fadeOut('400', function () {
                $('.overview').fadeIn('400');
            });
        }
    });
}
function getshop_previusPage() {
    $('.overview').fadeOut('400', function () {
        $('.productoverview').fadeIn('400');
    });
}
function getshop_confirmGuestInfoBox() {
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
function getshop_updateOrderSummary(res, isSearch) {
    $('.GslBooking .ordersummary .selectedguests').html('');
    var total = 0;
    var totalRooms = 0;
    var totalGuests = 0;
    var chosenRoomText = getshop_getBookingTranslations();
    
    var header = "<tr style='font-weight:bold;'><td style='text-align:left;'>"+chosenRoomText['chosenRoom']+"</td><td>"+chosenRoomText['numberofguests']+"</td><td>"+chosenRoomText['price']+"</td></tr>";
    var row = "";
    for(var k in res.rooms) {
        var room = res.rooms[k];
        for(var guest in room.roomsSelectedByGuests) {
            var count = room.roomsSelectedByGuests[guest];
            if(count > 0) {
                row += "<tr><td style='text-align:left;'>"+ room.name +"</td>";
                row += "<td>" + (guest*count);
                row += " (" + count + " room";
                if(count > 1) {
                    row += "s";
                }
                row += ")</td>";
                row += "<td>" + (room.pricesByGuests[guest] * count) + "</td>";
                row += "</tr>";
                total += (room.pricesByGuests[guest] * count);
                totalRooms += parseInt(count);
                totalGuests += (guest*count);
            }
        }
    }
    var totalAmount = "<tr class='totalAmountline'><td>Total</td><td>"+totalGuests+"("+ totalRooms +" rooms) </td><td>"+total+"</td></tr>";
    $('.GslBooking .ordersummary .selectedguests').html("<table id='priceoffertable' style='text-align:center'>"+ header + row + totalAmount + "</table>");
    $('.GslBooking .ordersummary .totalprice').html(total);
//    $('.GslBooking .ordersummary').css('visibility','visible').css('height','auto');
    if(isSearch) {
        if(!$('.GslBooking .ordersummary').is(":visible")) {
            $('.GslBooking .ordersummary').slideDown('slow', function(){
                $(function(){getshop_createSticky($(".ordersummary"));});
            });
        }
    }
    
}

function getshop_addRoom() {
    var addNewRoom = $('#addnewroom');
    $(this).before(addNewRoom);
}

function getshop_addGuest() {
    var room = $(this).closest('.roomrowadded');
    var guestTemplateRow = $('#guestentryrow');
    var guestRow = guestTemplateRow.clone();
    guestRow.attr('id','');
    guestRow.show();
    room.find('.guestRows').append(guestRow);    
    var saving = getshop_saveGuestInformation();
    saving.done(function(res) {
        getshop_loadAddonsAndGuestSummaryByResult(res);
    });
}

function getshop_addRemoveAddon(e) {
    getshop_addRemoveAddons($(e.target));
}

function getshop_removeGuest() {
var removeGuest = confirm('Are you sure u want to remove this guest?');
    if (removeGuest === true) {
        $(this).closest('.guestentry').remove();
        var saving = getshop_saveGuestInformation();
        saving.done(function(res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
        });
    }    
}

function getshop_setDatePicker() {
    var currentDate = new Date();
    currentDate.setDate(currentDate.getDate());

    var endDate = new Date();
    endDate.setTime(endDate.getTime() + (86400*1000)); 
    $('#date_picker_start').val(currentDate.toISOString().substring(0, 10));
    $('#date_picker_end').val(endDate.toISOString().substring(0, 10));

    var picker = $('#date_picker').daterangepicker({
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
    
    $('#date_picker_start').on('change blur', function() {
        var start = new Date($(this).val());
        $("#date_picker").data('daterangepicker').setStartDate(start);
        
        var end = new Date($('#date_picker_end').val());
        if(start.getTime() > end.getTime()) {
            end.setTime(start.getTime() + (86400*1000)); 
            $('#date_picker_end').val(end.toISOString().substring(0, 10));
            $("#date_picker").data('daterangepicker').setEndDate(end);
        }
    });
    
    $('#date_picker_end').on('change blur', function() {
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

function getshop_changeNumberOfRooms() {
    var index = $(this).closest('.productentrybox').attr('index');
    var guest = $(this).attr('guests');
    var count = $(this).val();
    var time = new Date().toLocaleTimeString('en-us');
    var startDate = $('#date_picker').data('daterangepicker').startDate.format('MMM DD, YYYY ') + time;
    var endDate = $('#date_picker').data('daterangepicker').endDate.format('MMM DD, YYYY ') + time;
    
    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=changeNumberOnType', {
        dataType: 'jsonp',
        data: { 
            "body" :  {
                "id": $(this).closest('.productentry').attr('roomid'),
                "numberOfRooms" : $(this).val(),
                "guests" : $(this).attr('guests'),
                "start" : startDate,
                "end" : endDate
            }
        },
        success: function (res) {
            gslbookingcurresult.rooms[index].roomsSelectedByGuests[guest] = count;            
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

            getshop_updateOrderSummary(gslbookingcurresult, false);
        }
    });
}

var gslbookingcurresult = null;

function getshop_goToOverviewPage() {
    $('.gslbookingBody').show();
    $('.addons_overview').show();
    gslbookingcurresult = JSON.parse(localStorage.getItem('gslcurrentbooking'));
    getshop_showOverviewPage();
    getshop_loadAddonsAndGuestSumaryView();
}

function getshop_goToAddonsPage() {
    $('.gslbookingBody').show();
    $('.addons_overview').show();
    gslbookingcurresult = JSON.parse(localStorage.getItem('gslcurrentbooking'));
    getshop_loadAddonsAndGuestSumaryView();
}


function getshop_searchRooms() {
    if($(this).find('.fa-spin').length > 0) {
        return;
    }
    localStorage.setItem('gslcurrentpage','search');
    $('.GslBooking .ordersummary').hide();
    var btn = $(this);
    var btnText = btn.html();
    btn.html('<i class="fa fa-spin fa-spinner"></i>');
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

    $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=startBooking', {
        dataType: 'jsonp',
        data: {
            "body": data
        },
        success: function (res) {
            btn.html(btnText);
            $('#productentry').html('');
            gslbookingcurresult = res;
            localStorage.setItem('gslcurrentbooking', JSON.stringify(gslbookingcurresult));
            $('.noroomsfound').hide();
            if(!res || (parseInt(res.roomsSelected) === 0)) {
                $('.noroomsfound').show();
                $('.GslBooking .hide').hide();
            } else {
                getshop_updateOrderSummary(res, true);
            }

            for (var k in res.rooms) {
                var room = res.rooms[k];
                var firstFile = "";
                if(room.images.length > 0) {
                    firstFile = room.images[0].fileId;
                }
                var productentry = '';
                var utilities = '';
                var user_icon = 1;
                var roomBox = $('#productentrybox').clone();
                roomBox.attr('id',null);
                roomBox.addClass('productentrybox');
                roomBox.attr('roomid', room.id);

                roomBox.find('.roomname').html(room.name);
                roomBox.find('.product_availablerooms').val(room.availableRooms);
                roomBox.find('.availableroomcontainer').html(room.availableRooms);
                roomBox.find('.lowpricedisplayer').html(room.pricesByGuests[1]);
                roomBox.find('.roomdescription').html(room.description);
                roomBox.find('.featured-image').css('background-image','url('+getshop_endpoint+'/displayImage.php?id='+ firstFile);
                roomBox.attr('index', k);
                for (var guest in room.pricesByGuests) {
                    var numberofrooms = '';
                    var index = 1;
                    var multipleGuests = ' guests';
                    if (guest == 1) {
                        multipleGuests = ' guest'
                    }
//                        user_icon += '<i class="fa fa-user"></i>';
                    for (var i = 1; i <= room.availableRooms; i++) {
                        var price = room.pricesByGuests[guest] * i;
                        numberofrooms += '<option value="' + i + '" data-price="' + price + '">' + i + '&nbsp;&nbsp; (NOK ' + price + ')</option>';
                    }
                    
                    roomBox.find('.guestselection').show();
                    if(numberofrooms) {
                        numberofrooms = "<option value='0' data-price='0'>0</option>" +  numberofrooms;
                        productentry = $('<tr class="productentry_itemlist"><td><i class="fa fa-user"></i> x ' + user_icon + ''+ multipleGuests + '</td><td> NOK ' + room.pricesByGuests[guest] + ',-</td><td style="text-align:right;padding-right:25px;"><select class="numberof_rooms" guests="'+guest+'">' + numberofrooms + '</select></td></tr>');
                        productentry.find('.numberof_rooms').val(room.roomsSelectedByGuests[guest]);
                    } else {
                        roomBox.find('.guestselection').hide();
                    }
                    roomBox.find('.guestselection').append(productentry);
                    user_icon++;
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
                    var imgaddr = getshop_endpoint + '/displayImage.php?id=' + room.images[i].fileId;
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
    });
}

function getshop_removeRoom() {
    var confirmed = confirm("Are you sure you want to remove this room?");
    if(confirmed) {
        var id = $(this).closest('.roomrowadded').attr('roomid');
        var saving = getshop_saveGuestInformation();
        saving.done(function() {
            $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=removeRoom', {
                dataType: 'jsonp',
                data: {
                    "body": id
                },
                success : function(res) {
                    getshop_loadAddonsAndGuestSummaryByResult(res);
                }
            });
        });
    }
}

$(document).on('mousedown touchstart', '.GslBooking #sameasguestselection', getshop_setSameAsGuest);
$(document).on('mousedown touchstart', '.GslBooking .guestInfoBox .fa', getshop_changeGuestSelection);
$(document).on('mousedown touchstart','.GslBooking .gssigninbutton', getshop_logon);
$(document).on('mousedown touchstart','.GslBooking .gssignoutbutton', getshop_logout);
$(document).on('mousedown touchstart','.GslBooking .selectedusertype', getshop_changeBookingType);
$(document).on('mousedown touchstart','.GslBooking .removeroom', getshop_removeRoom);
$(document).on('mousedown touchstart', '.GslBooking .destination_line', getshop_changedestination);
$(document).on('mousedown touchstart', '.GslBooking #guests', getshop_showGuestBox);
$(document).on('mousedown touchstart', '.GslBooking #destination', getshop_showDesitinationBox);
$(document).on('mousedown touchstart', '.GslBooking .go_to_payment_button', getshop_gotopayment);
$(document).on('mousedown touchstart', '.GslBooking #search_rooms', getshop_searchRooms);
$(document).on('mousedown touchstart', '.GslBooking .addguest', getshop_addGuest);
$(document).on('mousedown touchstart', '.GslBooking .addroom', getshop_addRoom);
$(document).on('mousedown touchstart', '.GslBooking .guestentry .guestaddonicon', getshop_addRemoveAddon);
$(document).on('mousedown touchstart', '.GslBooking .guestentry .removeguest', getshop_removeGuest);
$(document).on('change', '.GslBooking .numberof_rooms', getshop_changeNumberOfRooms);
$(document).on('mousedown touchstart', '.GslBooking .ordersummary .continue', getshop_continueToSummary);
$(document).on('mousedown touchstart', '.GslBooking .addButton', getshop_addRemoveAddon);

var lastSelectedPage = localStorage.getItem('gslcurrentpage');
if(lastSelectedPage === "summary") {
    $(function() {
        getshop_goToAddonsPage();
    });
}
if(lastSelectedPage === "overview") {
    $(function() {
        getshop_goToOverviewPage();
    });
}

(function ($) {
$.fn.tclick = function (onclick) {
    this.bind("touchstart", function (e) { onclick.call(this, e); e.stopPropagation(); e.preventDefault(); });
    this.bind("click", function (e) { onclick.call(this, e); });   //substitute mousedown event for exact same result as touchstart         
    return this;
  };
})(jQuery);
