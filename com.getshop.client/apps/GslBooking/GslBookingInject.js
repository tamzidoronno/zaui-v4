getshop_endpoint = "";
getshop_domainname = "default";
getshop_manuallycancelledbutton = false;
getshop_hasstickyscroll = false;
if(sessionStorage.getItem('getshop_endpoint')) {
    getshop_endpoint = sessionStorage.getItem('getshop_endpoint');
}
if(sessionStorage.getItem('getshop_domain')) {
    getshop_domainname = sessionStorage.getItem('getshop_domain');
}
var leftInterval;
var getshop_handledevent = false;

//Fix for internet explorer.
if (!String.prototype.startsWith) {
  String.prototype.startsWith = function(searchString, position) {
    position = position || 0;
    return this.indexOf(searchString, position) === position;
  };
}


function load_getBookingTranslations() {
    lang = sessionStorage.getItem("getshop_language");
    var def = $.Deferred();
    $.ajax({
        data: {
            "sessionid" : getshop_getsessionid()
        },
        "dataType": "jsonp",
        "url": getshop_endpoint + "/scripts/bookingprocess_translation.php?language="+lang,
        success: function (res) {
            getshop_translationMatrixLoaded = res;
            if(typeof(getshop_overridetranslation) === "object") {
                for(var key in getshop_overridetranslation) {
                    getshop_translationMatrixLoaded[key] = getshop_overridetranslation[key];
                }
            }
            def.resolve(res);
        }
    });    
    return def;
}

function getshop_setBookingTranslation() {
    var loading = $.Deferred();
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
//        var guestInput = $('#guests');
//        guestInput.val('1 '+translations['room'].toLowerCase()+', 1 ' + translations['guest'].toLowerCase());
        
        var hash = window.location.hash.substr(1);
        if(hash) {
            
        }
        var client = getshop_getWebSocketClient();
        var getConfig = client.PmsBookingProcess.getConfiguration(getshop_domainname, {});
        getConfig.done(function(config) {
            getshop_bookingconfiguration = config;
            var text = getshop_translationMatrixLoaded['agebelow'];
            text = text.replace("{age}", config.childAge);
            $("[gstranslationfield='agebelow']").html(text);              
            if(!config.doNotRecommendBestPrice) {
                $('.GslBooking .fa_box.guests').css('visibility','visible');
            } else {
                $('.GslBooking .fa_box.guests').addClass('donotrecommendprices');
            }
            var text = getshop_translationMatrixLoaded['ischildtext'];
            text = text.replace("{age}", config.childAge);
            $("[gstranslationfield='ischildtext']").html(text);
            if(config.childAge !== 0){
                $('.pfbox').css('width','33.33%').css('padding','0 5%');
                $('.excludeChildSelection').css('display','block');
                $('.switchAdultToGuest').css('display','none');
            } else {
                $('.excludeChildSelection').css('display','none');
            }
            $("[gsname='prefix']").val(config.phonePrefix);
            $("[gsname='user_prefix']").val(config.phonePrefix);
            $("[gsname='company_prefix']").val(config.phonePrefix);
            $("[gstranslationfield='currency']").html(config.currencyText);

            var toReplace = translations["paymentexplanation"];
            toReplace = toReplace.replace("{time}", config.defaultCheckinTime);
            $("[gstranslationfield='paymentexplanation']").html(toReplace);
            
            var types = $('.GslBooking .roomselectiontemplate');
            for(var activeType in config.activeTypes) {
                var systemType = config.activeTypes[activeType];
                var typeTemplate = types.clone();
                typeTemplate.removeClass('roomselectiontemplate');
                typeTemplate.attr('systemcategory',systemType);
                var text = getshop_translationMatrixLoaded['selectionsystemcategory_'+systemType];
                if(!text) {
                    text = "selectiontextforsystemcategory_" + systemType;
                }
                typeTemplate.find('.roomtextname').html(text);
//                $('.roomselectionoptions').append(typeTemplate);
            }
            
            loading.resolve(config);
        });
        getshop_confirmGuestInfoBox();
    });
    return loading;
}

function getshop_getBookingTranslations() {
    
    if(typeof(getshop_translationMatrixLoaded) !== "undefined") {
        return getshop_translationMatrixLoaded;
    }
    
    return {};
}

function getshop_setSameAsGuest(e) {
    try {
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
    }catch(e) { getshop_handleException(e); }
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
        data: {
            "sessionid" : getshop_getsessionid()
        },
        "dataType": "jsonp",
        "url": "https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=no&pnr=" + val,
        "success": function (data) {
            if (data.valid) {
                $('#guest_zipname').val(data.result);
            }
        }
    });    
}

function getshop_showRoomStepTroughConfig() {
    getshop_currentRoomToConfig = -1;
    getshop_storedRoomConfig = [];
    
    var toPush = [];
    for(var k in gslbookingcurresult.rooms) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
        var room = gslbookingcurresult.rooms[k];
        var obj = {};
        obj.id = room.id;
        obj.roomsSelectedByGuests = room.roomsSelectedByGuests;
        toPush.push(obj);
    }
    var client = getshop_getWebSocketClient();
    var getAddons = client.PmsBookingProcess.getAddonsSummary(getshop_domainname, toPush);
    getAddons.done(function(res) {
        getshop_current_booking_data = res;
        getshop_showNextRoomToConfig(res);
    });
    
}

function getshop_getBookingItemType(bookingItemTypeId) {
    for (var i in gslbookingcurresult.rooms) {
        var bookingItemType = gslbookingcurresult.rooms[i];
        if (bookingItemType.id == bookingItemTypeId) 
            return bookingItemType;
    }
    
    return null;
}

function getshop_addTemplateAddons(pmsBookingRoom, template) {
    $(template).find('.addonselectarea').html('');
    
    var k = 1;
    var addonsRow = $('<div/>')
    var colSize = 3;
    var translation = getshop_getBookingTranslations();
    var numberOfAvailableAddons = Object.keys(pmsBookingRoom.addonsAvailable).length;
    
    if (!numberOfAvailableAddons) {
        return;
    }
    
    $('.room_addons_to_select').show();
    
    var j = 0;
    
    for (var addonId in pmsBookingRoom.addonsAvailable) {
        j++;
        if (k == 1) {
            var addonsRow = $('<div/>')
            addonsRow.addClass('select_addons_row');
        }
        var addon = pmsBookingRoom.addonsAvailable[addonId];
        
        var div = $('<div/>');
        div.addClass('addon_select_box');
        
        var namebox = $('<div/>');
        namebox.addClass('addon_select_box_name');
        namebox.html(addon.name);
        
        div.append(namebox);
        
        div.append("<div class='price'>"+translation.price+": "+addon.price+"</div>")
        var counter = 
            "<div class='countselect' addonId='"+addonId+"' maxCount='"+addon.count+"'>\n\
                <div class='minus countcol'>-</div>\n\
                <input class='countcol' type='txt' value='"+addon.addedCount+"'/>\n\
                <div class='plus countcol'>+</div>\n\
            </div>";
        counter = $(counter);
        
        counter.find('.plus').click(function() {
            var parent = $(this).parent();
            var maxCount = parent.attr('maxCount');
            var oldValue = parseInt(parent.find('input').val());
            var newValue = oldValue + 1;
            
            if (newValue > maxCount) {
                newValue = maxCount;
            }
            
            parent.find('input').val(newValue);
            parent.parent().effect("highlight", {}, 1500);
        });
        
        counter.find('.minus').click(function() {
            var parent = $(this).parent();
            var oldValue = parseInt(parent.find('input').val());
            var newValue = oldValue - 1;
            
            if (newValue < 0) {
                newValue = 0;
            }
            
            parent.find('input').val(newValue);
            parent.parent().effect("highlight", {}, 1500);
        });
        
        div.append($(counter));
        
        if (numberOfAvailableAddons === j && k < colSize) {
            div.addClass('last_child');
        }
        
        addonsRow.append(div);
        
        k++;
        if (k > colSize) {
            $(template).find('.addonselectarea').append(addonsRow);
            k = 1;
        }
    }
    
    $('.select_addons_row').each(function() {
        var height = $(this).innerHeight() - 40;
        $(this).find('.addon_select_box .addon_select_box_name').each(function() {
           $(this).innerHeight(height);    
        })
    });
    
    if (k != 1) {
        $(template).find('.addonselectarea').append(addonsRow);
    }
    
}

function getshop_showPrevRoomConfig(e) {
    getshop_currentRoomToConfig--;
    if (getshop_currentRoomToConfig < 0) {
        $('.GslBooking .ordersummary').slideDown();
        $('.GslBooking .gslbookingHeader').slideDown();
        $('.productoverview').show();
        $('.getshop_room_config').hide();
       getshop_searchRooms(e);
        return;
    }
    
    getshop_currentRoomToConfig--;
    getshop_showNextRoomToConfig();
}

function getshop_addGuestProductChoice(template, room) {
    var k = 1;
    var colSize = 3;
    var translation = getshop_getBookingTranslations();
    var numberOfAvailableAddons = Object.keys(room.availableGuestOptionAddons).length;

    if (numberOfAvailableAddons <= 0) {
        return;
    }
    
    for (var i in room.availableGuestOptionAddons) {
        var addonsRow = $('<div/>')
        addonsRow.addClass('group_chose_option_guest');
        
        var groupAddonItem = room.availableGuestOptionAddons[i];
        var main = groupAddonItem.mainItem;
        
        addonsRow.attr('productid', main.productId);
        var name = main.name;
        if(main.descriptionWeb) {
            name += " " + main.descriptionWeb;
        }
        var title = $('<div class="selectgroupheadertitle">'+translation.chose+" "+name+"</div>");
        addonsRow.append(title);
        
        var first = true;
        $(groupAddonItem.items).each(function() {
            var addonDiv = $('<div/>');
            addonDiv.addClass('guest_group_addon_select_box');
            
            var addonSelect = $('<div><i class="fa fa-square"></i></div>');
            var addonTitle = $('<div>'+this.name+'</div>');
            var addonDesc = $('<div>'+this.descriptionWeb+'</div>');
            
            addonSelect.addClass('selecticon');
            addonTitle.addClass('selection_title');
            
            addonDiv.append(addonSelect);
            addonDiv.append(addonTitle);
            addonDiv.append(addonDesc);
            
            first = false;
            
            addonDiv.attr('productid', this.productId);
            addonsRow.append(addonDiv);
            
            addonDiv.click(function() {
                $(this).closest('.group_chose_option_guest').find('.active').removeClass('active');
                $(this).closest('.group_chose_option_guest').find('.fa-check-square').each(function() {
                    $(this).removeClass('fa-check-square');
                    $(this).addClass('fa-square');
                });
                $(this).find('.fa-square').addClass('fa-check-square');
                $(this).find('.fa-square').removeClass('fa-square');
                $(this).addClass('active');
            });
        });
        
        template.append(addonsRow);
    }
}

function getshop_loadSavedData(template) {
    var currentRoom = getshop_current_booking_data.rooms[getshop_currentRoomToConfig];
    
    var i = 0;
    $(template).find('.guestinformation').each(function() {
        if ($(this).hasClass('guestinformationtemplate')) {
            return;
        }
        
        if (currentRoom.guestInfo.length < (i+1)) 
            return;
        
        var savedInfo = currentRoom.guestInfo[i];
        $(this).find('[gsname="prefix"]').val(savedInfo.prefix);
        $(this).find('[gsname="phone"]').val(savedInfo.phone);
        $(this).find('[gsname="name"]').val(savedInfo.name);
        $(this).find('[gsname="email"]').val(savedInfo.email);
        
        if (savedInfo.isChild) {
            $(this).find('[gsname="ischild"]').attr('checked', 'true');
        }

        for (var mainProductId in savedInfo.selectedOptions) {
            var selectedProductId = savedInfo.selectedOptions[mainProductId];
            var groupBox = $(this).find('.group_chose_option_guest[productid="'+mainProductId+'"]');
            groupBox.find('.active').removeClass('active');
            groupBox.find('.guest_group_addon_select_box[productid="'+selectedProductId+'"]').addClass('active');
        }
        i++;
    });
}

function getshop_validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function getshop_saveCurrentStepTroughRoom() {
    if (getshop_current_booking_data.rooms.length < (getshop_currentRoomToConfig+1)) {
        return;
    }
    
    var currentRoom = getshop_current_booking_data.rooms[getshop_currentRoomToConfig];
    currentRoom.guestInfo = [];
    
    var containsErrors = false;
    
    $('.getshop_room_config .contains_error').removeClass('contains_error');
    
    $('.getshop_room_config .guestinformation').each(function(res) {
        if ($(this).hasClass('guestinformationtemplate')) {
            return;
        }
        
        var info = {
            "prefix" : $(this).find('[gsname="prefix"]').val(),
            "phone" : $(this).find('[gsname="phone"]').val(),
            "name" : $(this).find('[gsname="name"]').val(),
            "email" : $(this).find('[gsname="email"]').val(),
            "isChild" : $(this).find('[gsname="ischild"]').is(':checked')
        };
        
        if (!getshop_validateEmail(info.email) && $(this).find('[gsname="email"]').is(':visible')) {
            containsErrors = true;
            $(this).find('[gsname="email"]').addClass('contains_error');
        }
        
        if (!info.prefix && $(this).find('[gsname="prefix"]').is(':visible')) {
            containsErrors = true;
            $(this).find('[gsname="prefix"]').addClass('contains_error');
        }
        
        if (!info.phone && $(this).find('[gsname="phone"]').is(':visible')) {
            containsErrors = true;
            $(this).find('[gsname="phone"]').addClass('contains_error');
        }
        
        if (!info.name) {
            containsErrors = true;
            $(this).find('[gsname="name"]').addClass('contains_error');
        }
        
        info.selectedOptions = {};
        
        $(this).find('.group_chose_option_guest').each(function() {
            var mainProductId = $(this).attr('productid');
            var selectedProductId = $(this).find('.guest_group_addon_select_box.active').attr('productid');
            if (!selectedProductId) {
                containsErrors = true;
                $(this).addClass('contains_error');
            }
            info.selectedOptions[mainProductId] = selectedProductId;
        });
        
        currentRoom.guestInfo.push(info);
    });
    
    return containsErrors;
}

function getshop_pushGuestInformationToServer() {
    var client = getshop_getWebSocketClient();
    
    var roomInformations = [];
    
    
    for (var i in getshop_current_booking_data.rooms) {
        
        var roomInfo = {
            roomId : getshop_current_booking_data.rooms[i].roomId,
            numberOfGuests : getshop_current_booking_data.rooms[i].guestInfo.length,
            guests : getshop_current_booking_data.rooms[i].guestInfo
        };
        
        roomInformations.push(roomInfo);
    }
    
    var saveGuest = client.PmsBookingProcess.saveGuestInformation(getshop_domainname, roomInformations);
    
    saveGuest.done(function(res) { 
        getshop_loadAddonsAndGuestSummaryByResult(res);
        getshop_setPageName('overview');
        $('.gslbookingBody').position().top;
        $('.gslbookingBody').offset().top;
        $('.getshop_room_config').hide();
        $('.invalidinput').removeClass('invalidinput');
        $('.GslBooking .errormessage').hide();
        getshop_overviewPageLoad(res);
    });
}

function getshop_showNextRoomToConfig() {
    
    $('.GslBooking .guestinfowarning').hide();
    
    // Save on next.
    if (getshop_currentRoomToConfig > -1) {
        var warnings = getshop_saveCurrentStepTroughRoom();
        if (warnings) {
            $('.GslBooking .guestinfowarning').show();
            return;
        }
    }
    
    var translation = getshop_getBookingTranslations();
    getshop_currentRoomToConfig++;
    
    if ((getshop_currentRoomToConfig+1) > getshop_current_booking_data.rooms.length) {
        getshop_currentRoomToConfig = getshop_current_booking_data.rooms.length;
        getshop_pushGuestInformationToServer();
        return;
    }
    
    var pmsBookingRoom = getshop_current_booking_data.rooms[getshop_currentRoomToConfig];
    var bookingItemType = getshop_getBookingItemType(pmsBookingRoom.bookingItemTypeId);
    
    
    $('.getshop_room_config').show();
    
    var template = $('.getshop_room_config');
    template.find('.gsl_header .text').html(translation.configureroom);
    template.find('.gsl_header .currentroomnumber').html((getshop_currentRoomToConfig + 1));
    template.find('.gsl_header .tootalrooms').html(getshop_current_booking_data.rooms.length);
    template.find('.gsl_header .roomname').html(bookingItemType.name);
    
    $(template).find('.guestinfoinner').html('');
    
    for (var i=0; i<pmsBookingRoom.guestCount; i++) {
        var guestInfoTemplate = $(template).find('.guestinformationtemplate').clone();
        
        if(i === 0) {
            guestInfoTemplate.closest('.guestinformation').find('.ischildbox').hide();
        }
        
        if(getshop_bookingconfiguration.ignoreGuestInformation && i > 0) {
            guestInfoTemplate.closest('.guestinformation').find('.email.col').hide();
            guestInfoTemplate.closest('.guestinformation').find('.phone.col').hide();
        }
        
        guestInfoTemplate.removeClass('guestinformationtemplate');
        $(guestInfoTemplate).find('.guestnumber').html((i+1));
        getshop_addGuestProductChoice(guestInfoTemplate, pmsBookingRoom);
        $(template).find('.guestinfoinner').append(guestInfoTemplate);
    }
    
    getshop_addTemplateAddons(pmsBookingRoom, template);
    
    // load on back
    getshop_loadSavedData(template);
    
    $('.getshop_room_config').append(template);
}

function getshop_loadAddonsAndGuestSumaryView() {
    var toPush = [];
    for(var k in gslbookingcurresult.rooms) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
        var room = gslbookingcurresult.rooms[k];
        var obj = {};
        obj.id = room.id;
        obj.roomsSelectedByGuests = room.roomsSelectedByGuests;
        toPush.push(obj);
    }
    var client = getshop_getWebSocketClient();
    var getAddons = client.PmsBookingProcess.getAddonsSummary(getshop_domainname, toPush);
    getAddons.done(function(res) {
        getshop_loadAddonsAndGuestSummaryByResult(res);
    });
}

function getshop_loadCampaigns(res) {
    $('.packages_supported').hide();
    if(typeof(res.campaigns) === "undefined" || res.campaigns.length === 0) {
        return;
    }
    var translation = getshop_getBookingTranslations();
    
    $('.packages_supported').show();
    
     $('.packages_supported_inner').find('.campaignrow').remove();
    for(var key in res.campaigns) {
        var campaign = res.campaigns[key];
        var row = $('<div class="campaignrow"></div>');
        var text = translation['add'];
        
        if(campaign.selected) { text = translation['remove']; }
        
        row.append($('<span class="addCampaignButton gsl_button selectcampaign" bookingcode="'+campaign.couponCode+'">'+text+'</span>'));
        row.append($("<span class='campaign_title'>" + campaign.title+"</span>"));
        row.append($("<span class='campaign_description'>" + campaign.description+"</span>"));
        
        $('.packages_supported_inner').append(row);
    }
}

function getshop_loadAddonsAndGuestSummaryByResult(res) {
    $('.addonprinted').remove();
    var template = $('.addonsentry #addon');
    var translation = getshop_getBookingTranslations();
    var foundItems = false;
    if(typeof(res) !== "undefined" || typeof(res.items) !== "undefined") {
        for(var k in res.items) {
            k = parseInt(k); if(!Number.isInteger(k)) { continue; }
            foundItems = true;
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
            var itemname = item.name;
            if(item.descriptionWeb) {
                itemname = item.descriptionWeb;
            }
            entry.find('.text').html(itemname);
            entry.find('.price').html(getshop_printPrice(item.price));
            if(item.isAdded) {
                entry.find('.addButton').addClass('added_addon').html(translation['remove']);
            } else if(item.maxAddonCount > 1) {
                select.show();
            }
            entry.show();
            $('.addonsentry .overview_addons').append(entry);
        }
    }
    if(!foundItems) {
        $('.addonsentry').hide();
    }
   
    getshop_loadRooms(res);
    getshop_loadTextualSummary(res);
    getshop_loadBookerInformation(res);
    getshop_loadLoggedOn(res);
    getshop_loadCampaigns(res);
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
    var translation = getshop_getBookingTranslations();
    for(var k in res.textualSummary) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
        var text = res.textualSummary[k];
        if(typeof(translation['adults']) !== "undefined") {
            text = text.replace("{adults}", translation['adults'].toLowerCase());
        }
        if(typeof(translation['adults']) !== "undefined") {
            text = text.replace("{adult}", translation['adult'].toLowerCase());
        }
        if(typeof(translation['child']) !== "undefined") {
            text = text.replace("{child}", translation['child'].toLowerCase());
        }
        if(typeof(translation['children']) !== "undefined") {
            text = text.replace("{children}", translation['children'].toLowerCase());
        }
        for(var i = 0; i < 20; i++) {
            if(typeof(translation['selection_'+i]) !== "undefined") {
                text = text.replace("{selection_"+i+"}", translation['selection_'+i].toLowerCase());
            }
            if(typeof(translation['selections_'+i]) !== "undefined") {
                text = text.replace("{selections_"+i+"}", translation['selections_'+i].toLowerCase());
            }
        }
        if(typeof(translation['rooms']) !== "undefined") {
            text = text.replace("{rooms}", translation['rooms'].toLowerCase());
        }
        if(typeof(translation['totalprice']) !== "undefined") {
            text = text.replace("{totalprice}", translation['totalprice']);
            text = text.replace("{currency}", getshop_printPrice(""));
        }
        $('.yourstaysummary').append(text + "<br>");
    }
    var lang = sessionStorage.getItem("getshop_language");
    $('[gstranslationfield="readTerms"]').attr('onclick',"window.open('"+getshop_endpoint+"/scripts/loadContractPdf.php?readable=true&engine=default&language="+lang+"')");
    $('[gstranslationfield="downloadTerms"]').attr('onclick',"window.open('"+getshop_endpoint+"/scripts/loadContractPdf.php?engine=default&language="+lang+"')");
}

function getshop_loadRooms(res) {
    var roomContainer = $('#roomentrycontainer');
    var translation = getshop_getBookingTranslations();
    $('.roomrowadded').remove();
    for(var k in res.rooms) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
        var newRoom = roomContainer.clone();
        newRoom.addClass('roomrowadded');
        var room = res.rooms[k];
        newRoom.find('[gsname="newroomstartdate"]').val(getshop_js_yyyy_mm_dd_hh_mm_ss(room.start).substr(0, 10));
        newRoom.find('[gsname="newroomenddate"]').val(getshop_js_yyyy_mm_dd_hh_mm_ss(room.end).substr(0, 10));
        newRoom.find('.totalroomprice').html(getshop_printPrice(room.totalCost));
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
                if(guestObject.isChild) {
                   guestRow.find('[gsname="ischild"]').attr('checked','checked');
                }
            }
            
            if(addedAddons) {
                guestRow.find('.guest_addon').addClass('addonsdisabled');
            }
            guestRow.append('<i class="fa fa-times removeguest" title="'+translation['removeguest']+'"></i>');
            
            addedAddons = true;
            if(getshop_bookingconfiguration.ignoreGuestInformation && i > 0) {
                guestRow.find('[gsname="email"]').css('visibility','hidden');
                guestRow.find('[gsname="prefix"]').css('visibility','hidden');
                guestRow.find('[gsname="phone"]').css('visibility','hidden');
                guestRow.find('.entryfield.phonefield').css('visibility','hidden');
            }
            
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
            newRoom.find('.guest_addon').addClass('addonsdisabled');
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
  return day + "." + month + "." + year;
}

function getGetShopConfigOptions() {
    var optionsString = sessionStorage.getItem('getshop_booking_form_options');
    
    
    var options = {};
    
    if (typeof(optionsString) != "undefined" && optionsString) {
        options = JSON.parse(optionsString);
    }
    
    // Setting default options in case 
    if(!options.hasOwnProperty("roomStepTroughConfig")){
        // If this option is enabled it will do a configuration
        // process for each room.
        options.roomStepTroughConfig = false;
    }
    
    return options;
}

function getshop_setPageName(pagename) {
    $('.GslBooking .gslfront_1').attr('page', pagename);
    sessionStorage.setItem('gslcurrentpage',pagename);
}

function getshop_continueToSummary(e) {
    var options = getGetShopConfigOptions();
 
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $('.productoverview').fadeOut('400', function () {
            $('.GslBooking .ordersummary').slideUp();
            $('.GslBooking .gslbookingHeader').slideUp();
            
            if (options.roomStepTroughConfig) {
                getshop_setPageName("roomconfig");
                getshop_showRoomStepTroughConfig();
            } else {
                $('.addons_overview').fadeIn('400');
                getshop_setPageName("summary");
                getshop_loadAddonsAndGuestSumaryView();
            }

            var padding = $('.gslbookingBody').position().top;
            var body = $('.gslbookingBody').offset().top;
            $(window).scrollTop(body-padding);

        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_createSticky(sticky) {
    if(getshop_hasstickyscroll) {
        return;
    }
    if (typeof sticky !== "undefined") {
        var pos = sticky.offset().top;
        var win = $(window);
        var stopperbox = $('#productoverview_footer');
        var paddingBox = $('.productoverview');
        var stopPos = (stopperbox.offset().top - sticky.height());
        var positionPaddingTop = 0;
        if(sessionStorage.getItem('getshop_topstickyposition')) {
            positionPaddingTop = parseInt(sessionStorage.getItem('getshop_topstickyposition'));
        }

        $('.GslBooking #order-sticky').css('width', $('.GslBooking .gslbookingHeader').outerWidth());
        if(!$('.GslBooking #order-sticky').is(':visible')) {
            return;
        }
        getshop_hasstickyscroll = true;
        win.on("scroll", function () {
            if(win.scrollTop() > pos) {
                sticky.addClass("isSticked");
            } else {
                sticky.removeClass("isSticked");
            }
        });
        $(function() {
            win.scroll();
            $('.GslBooking .ordersummary .continue').show();
        });
    }
}
function getshop_addRemoveAddons(btn) {
    var saving = getshop_saveGuestInformation();
    
    var text = btn.html();

    if(btn.hasClass('fa')) {
        btn.addClass('fa-spin');
    } else {
        btn.html('<i class="fa fa-spin fa-spinner"></i>');
    }
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
            var client = getshop_getWebSocketClient();
            var removeAddon = client.PmsBookingProcess.removeAddons(getshop_domainname, body);
            removeAddon.done(function(res) {
                getshop_loadAddonsAndGuestSummaryByResult(res);
            });
        } else {
            var client = getshop_getWebSocketClient();
            var removeAddon = client.PmsBookingProcess.addAddons(getshop_domainname, body);
            removeAddon.done(function(res) {
                getshop_loadAddonsAndGuestSummaryByResult(res);
            });
        }
    });
}

function getshop_logon(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var client = getshop_getWebSocketClient();
        var logon = client.PmsBookingProcess.logOn(getshop_endpoint, {
            "username" : $("input[gsname='username']").val(),
            "password" : $("input[gsname='password']").val()
        });
        logon.done(function(res) {
            $('.failedlogon').hide();
            getshop_loadAddonsAndGuestSummaryByResult(res);
            if(!res.isLoggedOn) {
                $('.failedlogon').slideDown();
            }
        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_logout(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var client = getshop_getWebSocketClient();
        var logout = client.PmsBookingProcess.logOut(getshop_endpoint, {});
        logout.done(function(res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
            $('.overview_confirmation').hide();
        });
    }catch(e) { getshop_handleException(e); }
}
function getshop_changeBookingType(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $('.errormessage').hide();
        $('.invalidinput').removeClass('invalidinput');
        var type = $(this).attr('fortype');
        $('.agreetotermsbox').hide();
        $('.overview_confirmation').hide();
        $('.guesttypeselection').hide();
        $('.guesttypeselection[type="'+type+'"]').show();
    //    This causes a infinity loop, whats is it suppose to do?
        $(this).find('input').attr('checked',true);
        if(type !== "gotAccount") {
            $('.agreetotermsbox').show();
            $('.overview_confirmation').show();
        }
    }catch(e) { getshop_handleException(e); }
}
function getshop_changedestination(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var destination = $(this).text();
        if (destination !== "") {
            $('#destination').val(destination);
            $('.destinationInfoBox').hide();
        }
    }catch(e) { getshop_handleException(e); }
}

function getshop_showGuestBox(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $('.guestInfoBox').show();
    }catch(e) { getshop_handleException(e); }
}
function getshop_showDesitinationBox(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $('.destinationInfoBox').show();
    }catch(e) { getshop_handleException(e); }
}

function getshop_gotopayment(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var btn = $(this);
        if(btn.hasClass('fa-spin')) {
            return;
        }

        var saving = getshop_saveBookerInformation();
        $('.errormessage').hide();
        $('.agreetotermserrormessage').hide();
        $('.invalidinput').removeClass('invalidinput');
        saving.done(function(res) {
            for(var field in res.fieldsValidation) {
                if(field === "agreeterms" || gslbookingcurresult.prefilledContactUser) {
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

                var paylater = false;
                if(btn.hasClass('paylater_button')) {
                    paylater = true;
                }

                var completing = getshop_completeBooking(paylater);
                btn.html('<i class="fa fa-spin fa-spinner"></i>');
                completing.done(function(res) {
                    if(typeof(getshop_successcallback) !== "undefined") {
                        getshop_successcallback(res);
                    } else {
                        window.location.href = getshop_endpoint + "/scripts/redirectpayment.php?bookingid="+getshop_bookingId+"&engine="+getshop_domainname;
                    }
                });
                completing.fail(function(res) {
                    if(typeof(getshop_failurecallback) !== "undefined") {
                        getshop_failurecallback(res);
                    } else {
                        $('.gslbookingBody').hide();
                        $('.errorcompleted').show();
                    }
                });
            }
        });
    }catch(e) { getshop_handleException(e); }
}


function getshop_completeBooking(paylater) {
   var def = $.Deferred();
   
   var client = getshop_getWebSocketClient();
   var completing = client.PmsBookingProcess.completeBooking(getshop_domainname, {
        "payLater" : paylater,
        "paymentMethod" : $('#paymentmethodselection').val()
    });
    completing.done(function(res) {
        def.resolve(res);
    });

    return def;
}

function getshop_changeGuestSelection(e) {
    if(getshop_avoiddoubletap(e)) { return; }
    try {      
        e.preventDefault();
        var btn = $(this);
        var minusButton = btn.closest('.count_line').find('.fa-minus'); //Closest minusbutton
        var count = btn.closest('.count_line').find('.count').val(); //Closest numbercount for adding guests or room
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
                $('#count_adult').closest('.count_line').find('.fa-minus').removeClass('disabled');
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
                        $('#count_room').closest('.count_line').find('.fa-minus').addClass('disabled');
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
                    $('#count_room').closest('.count_line').find('.fa-minus').addClass('disabled');
                }
            }
        }

        $(this).closest('.count_line').find('.count').val(count);
        getshop_confirmGuestInfoBox();    
    }catch(e) { getshop_handleException(e); }
}

function getshop_saveBookerInformation() {
    var fields = {};
    $('.overview_article [gsname]').each(function() {
        var field = $(this).attr('gsname');
        var val = $(this).val();
        fields[field] = val;
    });
    if($('#organization').is(':checked')) {
        fields["choosetyperadio"] = "registration_company";
    } else {
        fields["choosetyperadio"] = "registration_private";
    }
    var type = $("input[name='user']:checked").closest('label').attr('fortype');
    
    $("[gsname='company_email']").each(function() {
        if($(this).is(':visible') && $(this).val()) {
            fields['company_email'] = $(this).val();
        }
    });
   var ordertext = "";
   $('[gsname="ordertext"]').each(function() {
       if($(this).val()) {
           ordertext = $(this).val();
       }
   });
    var data = {
        "profileType" : type,
        "fields" : fields,
        "ordertext" : ordertext,
        "agreeToTerms" : $('#agreeTerms').is(':checked'),
        "offersAccept" : $('#offersAccept').is(':checked'),
        "travellingBusiness" : $('#travellingBusiness').is(':checked'),
    };
    var dfr = $.Deferred();
    
    var client = getshop_getWebSocketClient();
    var setGuestInfo = client.PmsBookingProcess.setGuestInformation(getshop_domainname, data);
    setGuestInfo.done(function(res) {
        sessionStorage.setItem('gslcurrentbooking', JSON.stringify(gslbookingcurresult));
        dfr.resolve(res);
    });
    return dfr;
}

function getshop_showAddonsPage() {
    var options = getGetShopConfigOptions();
    
    if (options.roomStepTroughConfig) {
        $('.overview').hide();
        $('.productoverview').hide();
        getshop_setPageName("roomconfig");
        getshop_showRoomStepTroughConfig();
        return;
    }
    
    getshop_saveBookerInformation();
    getshop_setPageName("summary");
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
                "email" : $(this).find('[gsname="email"]').val(),
                "isChild" : $(this).find('[gsname="ischild"]').is(':checked')
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
    
    var client = getshop_getWebSocketClient();
    var saveGuest = client.PmsBookingProcess.saveGuestInformation(getshop_domainname, toSave);
    saveGuest.done(function(res) { dfd.resolve(res); });
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

function getshop_overviewPageLoad(res) {
    $('.prefilled_contact').hide();
    if(gslbookingcurresult.prefilledContactUser) {
        $('.overview_contact').hide();
        $('.prefilled_contact').show();
        $('.bookingonbehalfname').html(gslbookingcurresult.prefilledContactUser);
    }

    if(gslbookingcurresult.supportedPaymentMethods.length > 0) {
        $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?paymetmethodnames=true', {
            dataType: 'json',
            success: function (res) {
                $('.paymentmethodselection').show();
                $('#paymentmethodselection').html('');
                for(var k in gslbookingcurresult.supportedPaymentMethods) {
                    k = parseInt(k); if(!Number.isInteger(k)) { continue; }
                    var id = gslbookingcurresult.supportedPaymentMethods[k];
                    var name = res[id];
                    $('#paymentmethodselection').append("<option value='"+id+"'>" + name + "</option>");
                }
            }
        });
    } else {
        $('.paymentmethodselection').hide();
    }

    var success = true;
    for(var k in res.fieldsValidation) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
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
        if(typeof(getshop_viewmode) !== "undefined" && getshop_viewmode == "terminal") {
            getshop_startPaymentTerminalProcess();
        } else {
            $('.addons_overview').fadeOut('400', function () {
                $('.overview').fadeIn('400');
                $(window).scrollTop(0);
            });
        }
    } else {
        $(window).scrollTop(0);
    }
}

function getshop_showOverviewPage() {
    getshop_setPageName('overview');
    var saving = getshop_saveGuestInformation();
    var padding = $('.gslbookingBody').position().top;
    var body = $('.gslbookingBody').offset().top;
    $('.invalidinput').removeClass('invalidinput');
    $('.GslBooking .errormessage').hide();
    saving.done(getshop_overviewPageLoad);
}

function getshop_startPaymentTerminalProcess() {
    $('.terminalpaymentprocess').show();
    var client = getshop_getWebSocketClient();
    var completeForTerminal =  client.PmsBookingProcess.completeBookingForTerminal(getshop_domainname, { "terminalId" : getshop_terminalid });
    completeForTerminal.done(function(res) {
        getshop_currentorderid = res.orderid;
        if(res.goToCompleted === true) {
            var toSend = {};
            res.msg = "completed";
            getshop_displayVerifoneFeedBack(res);
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
function getshop_updateOrderSummary(res, isSearch) {
    $('.GslBooking .ordersummary .selectedguests').html('');
    var total = 0;
    var totalRooms = 0;
    var totalGuests = 0;
    var chosenRoomText = getshop_getBookingTranslations();
    
    var header = "<tr style='font-weight:bold;box-shadow: inset 0px -1px 0px #efeff0;'><td style='text-align:left;'>"+chosenRoomText['chosenRoom']+"</td><td>"+chosenRoomText['numberofguests']+"</td><td>"+chosenRoomText['price']+"</td></tr>";
    var row = "";
    var translationMatrix = getshop_getBookingTranslations();
    var roomsSelected = 0;
    var systemCounter = {};
    for(var k in res.rooms) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
        var room = res.rooms[k];
        for(var guest in room.roomsSelectedByGuests) {
            var count = room.roomsSelectedByGuests[guest];
            if(count > 0) {
                var price = room.pricesByGuests[guest] * res.numberOfDays;
                row += "<tr roomid='"+room.id+"' guests='"+guest+"' index='"+k+"' class='priceofferrow'><td style='text-align:left;' class='removeselectedroom'><i class='fa fa-trash' style='cursor:pointer;'></i> "+ room.name +"</td>";
                row += "<td>" + (guest*count);
                row += " (" + count + " ";
                var text = translationMatrix['selection_'+room.systemCategory];
                if(count > 1) {
                    text = translationMatrix['selections_'+room.systemCategory];
                }
                if(!text) {
                    text = 'selections_'+room.systemCategory;
                }
                row += text;
                row += ")</td>";
                
                row += "<td>" + getshop_printPrice(price*count) + ",-</td>";
                row += "</tr>";
                total += parseInt(price*count);
                totalRooms += parseInt(count);
                if(!systemCounter[room.systemCategory]) {
                    systemCounter[room.systemCategory] = 0;
                }
                systemCounter[room.systemCategory] += parseInt(count);
                totalGuests += (guest*count);
                roomsSelected++;
            }
        }
    }
    $('.GslBooking .ordersummary .selectedguests').html("<table id='priceoffertable' style='text-align:center'>"+ header + row + "</table>");
    
    var txt = "<strong>"+ chosenRoomText['price']+":</strong> " + getshop_printPrice(total) +",- <strong>"+ chosenRoomText['numberofguests']+":</strong> "+totalGuests;
    
    for(var systemType in systemCounter) {
        var totalCount = systemCounter[systemType];
        if(totalCount > 1) {
            txt += " <strong>"+ chosenRoomText['selections_'+systemType]+":</strong> "+ totalCount;
        } else {
            txt += " <strong>"+ chosenRoomText['selection_'+systemType]+":</strong> "+ totalCount;
        }
    }
    
    $('.GslBooking .ordersummary .totalprice').html(txt);
    $('.GslBooking .ordersummary .continue').hide();
    if(isSearch) {
        if(!$('.GslBooking .ordersummary').is(":visible")) {
            $('.GslBooking .ordersummary').show();
            $(function(){
                getshop_createSticky($("#order-sticky"));
                $('.GslBooking .continuetoguestinfooptions').fadeIn();
            });
        }
    } else if(total > 0) {
        $('.GslBooking .ordersummary .continue').show();
    }
    
    if(roomsSelected === 0) {
        $('.roomSelected').hide();
        $('.noRoomSelected').show();
    } else {
        $('.roomSelected').show();
        $('.noRoomSelected').hide();
        $('.GslBooking .ordersummary .continue').show();
        if(!isSearch) {
            $('.continuetoguestinformationbtn').effect( "shake" );
            getshop_createSticky($("#order-sticky"));
        }
    }

    
}

function getshop_addRoom(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var addNewRoom = $('#addnewroom');
        $(this).before(addNewRoom);
    }catch(e) { getshop_handleException(e); }
}

function getshop_addGuest(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
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
    }catch(e) { getshop_handleException(e); }
}

function getshop_avoiddoubletap(e) {
    if(typeof(getshop_lastclicked) !== "undefined") {
        var diff = Date.now() - getshop_lastclicked;
        if(diff < 100) {
            return true;
        }
    }
    
    e.stopPropagation();
    e.stopImmediatePropagation();
    getshop_lastclicked = Date.now();
    
    return false;
}

function getshop_addRemoveAddon(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        getshop_addRemoveAddons($(e.target));
    }catch(e) { getshop_handleException(e); }
}

function getshop_addRemoveCampaign(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        
        var btn = $(this);
          
        var text = btn.html();

        if(btn.hasClass('fa')) {
            btn.addClass('fa-spin');
        } else {
            btn.html('<i class="fa fa-spin fa-spinner"></i>');
        }
        
        var code = btn.attr('bookingcode');
        var client = getshop_getWebSocketClient();
        var campaignSet = client.PmsBookingProcess.setCampaignCode(getshop_domainname, code);
        campaignSet.done(function(res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
        });
        
    }catch(e) { getshop_handleException(e); }
}

function getshop_removeGuest(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var translation = getshop_getBookingTranslations();
        
        if($(this).closest('.guestRows').find('.guestentry').length === 1) {
            return;
        }

        var removeGuest = confirm(translation['sureremoveguest']);
        if (removeGuest === true) {
            $(this).closest('.guestentry').remove();
            var saving = getshop_saveGuestInformation();
            saving.done(function(res) {
                getshop_loadAddonsAndGuestSummaryByResult(res);
            });
        }    
    }catch(e) { getshop_handleException(e); }
}

function getshop_setDatePicker() {
    try {        
        var currentDate = new Date();
        var rooms = $('#count_room');
        var adults = $('#count_adult');
        var children = $('#count_child');
        var discountCode = $('#coupon_input');
        var endDate = new Date();
        endDate.setTime(endDate.getTime() + (86400*1000)); 
        var hash = window.location.hash.substr(1);
        var result = hash.split('&').reduce(function (result, item) {
            var parts = item.split('=');
            result[parts[0]] = parts[1];
            return result;
        }, {});

        if(result.start) {
            var tmpDate = new Date(result.start);
            currentDate.setTime(tmpDate.getTime());
        }
        if(result.end) {
            var tmpDate = new Date(result.end);
            endDate.setTime(tmpDate.getTime());
        }
        if(result.rooms){
            rooms.val(result.rooms);
            if(rooms.val() != 1){
                rooms.closest('.count_line').find('.fa-minus').removeClass('disabled');
            }
        }
        if(result.adults){
            adults.val(result.adults);
            if(adults.val() != 1){
                adults.closest('.count_line').find('.fa-minus').removeClass('disabled');
            }
        }
        if(result.children){
            children.val(result.children);
            if(children.val() != 0){
                children.closest('.count_line').find('.fa-minus').removeClass('disabled');
            }
        }
        if(result.discount){
            discountCode.val(result.discount);
        }

        if(getshop_bookingconfiguration.startYesterday) {
            currentDate.setTime(currentDate.getTime() - 86400000); 
            endDate.setTime(endDate.getTime() - 86400000); 
        }

        var current = moment(currentDate);
        var month = current.get('month')+1;
        var day = current.date();
        var year = current.get('year');
        if(day < 10) { day = "0" + day; }
        if(month < 10) { month = "0" + month; }
        $('#date_picker_start').val(day + "." + month + "." + year);
        
        var current = moment(endDate);
        var month = current.get('month')+1;
        var day = current.date();
        var year = current.get('year');
        if(day < 10) { day = "0" + day; }
        if(month < 10) { month = "0" + month; }
        $('#date_picker_end').val(day + "." + month + "." + year);

        var options = {
            "autoApply": true,
            "showWeekNumbers": true,
            "startDate": currentDate,
            "minDate": currentDate,
            "endDate": endDate,
            "locale": {
                "direction": "ltr",
                "format": "DD MMM",
                "firstDay": 1
            }
        }

        if(typeof(getshop_viewmode) !== "undefined" && getshop_viewmode == "terminal") {
            $('.GslBooking .nights').show();
            options.singleDatePicker = true;
            $('.GslBooking .gslfront_1 .fa_box').css('width','20%');
        }

        if(result.start) {
            $(function() {
                $('.GslBooking #search_rooms').click();
            });
        }
        $('.ui-datepicker').remove();
        
        var minDate = "0d";
        if(getshop_bookingconfiguration.startYesterday) {
            var minDate = "-1d";
        }
        
        $('.date_picker_start_gsl').datepicker({ dateFormat: "dd.mm.yy", minDate: minDate, changeMonth: true, changeYear: true, showButtonPanel: true,firstDay: 1,
            onSelect: function(dateText) {
               var date = moment.utc(dateText, "DD.MM.YYYY").local();
               var currentEnd = $('#date_picker_end').val();
               var endMoment = moment.utc(currentEnd, "DD.MM.YYYY").local();

               var diff = endMoment.diff(date, "minutes");
               if(diff <= 0) {
                   var tomorrow = moment(date);
                   tomorrow.add(1,'days');
                   var newDate = moment(tomorrow);
                   var month = newDate.get('month')+1;
                   var day = newDate.date();
                   var year = newDate.get('year');

                   if(day < 10) { day = "0" + day; }
                   if(month < 10) { month = "0" + month; }

                   $('#date_picker_end').val(day + "." + month + "." + year);
               }
             }
         });
        $('.date_picker_end_gsl').datepicker({ dateFormat: "dd.mm.yy", minDate: "-1d", changeMonth: true, changeYear: true, showButtonPanel: true,firstDay: 1,
            onSelect: function(dateText) {
               var date = moment.utc(dateText, "DD.MM.YYYY").local();
               var currentStart = $('.date_picker_start_gsl').val();
               var endMoment = moment.utc(currentStart, "DD.MM.YYYY").local();

               var diff = date.diff(endMoment);
               if(diff <= 0) {
                   var tomorrow = new Date(date);
                   tomorrow.setDate(tomorrow.getDate()-1);
                   var newDate = moment(tomorrow);
                   var month = newDate.get('month')+1;
                   var day = newDate.date();
                   var year = newDate.get('year');

                   if(day < 10) { day = "0" + day; }
                   if(month < 10) { month = "0" + month; }

                   $('#date_picker_start').val(day + "." + month + "." + year);
               }
             }
         });
        
    }catch(e) { getshop_handleException(e); }
}

function getshop_changeNumberOfRooms() {
     try {
        var index = $(this).closest('.productentrybox').attr('index');
        var guest = $(this).attr('guests');
        var count = $(this).val();
        var start = moment.utc($('.date_picker_start_gsl').val(), "DD.MM.YYYY").local();
        var end = moment.utc($('.date_picker_end_gsl').val(), "DD.MM.YYYY").local();
        
        var client = getshop_getWebSocketClient();
        var data = {
            "id": $(this).closest('.productentry').attr('roomid'),
            "numberOfRooms" : $(this).val(),
            "guests" : $(this).attr('guests'),
            "start" : start,
            "end" : end
        };
        var change = client.PmsBookingProcess.changeNumberOnType(getshop_domainname, data);
        var target = $(this);
        change.done(function(res) {
            gslbookingcurresult.rooms[index].roomsSelectedByGuests[guest] = count;            
            var totalCost = 0;
            var selectedRooms = 0;
            var totalSelectedRooms = 0;
            var allContainers = target.closest('#productentry');
            var mainContainer = target.closest('.productentry_main');
            var availableRooms = parseInt(mainContainer.find('.product_availablerooms').attr('numberavailable'));

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
        });
    }catch(e) { getshop_handleException(e); }
}

var gslbookingcurresult = null;

function getshop_handleException(e) {
    var getbrowser = getshop_get_browserForDebugging();
    var text = e.stack + "<br>";
    text += "Browser:<br>";
    for(var k in getbrowser) {
        k = parseInt(k); if(!Number.isInteger(k)) { continue; }
        text += k + " : " + getbrowser[k] + "<br>";
    }
    text += e;
    var client = getshop_getWebSocketClient();
    client.MessageManager.sendErrorNotify(text);
    alert('An unknown error occured. :(');
    throw e;
}

function getshop_get_browserForDebugging() {
    var ua=navigator.userAgent,tem,M=ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || []; 
    if(/trident/i.test(M[1])){
        tem=/\brv[ :]+(\d+)/g.exec(ua) || []; 
        return {name:'IE',version:(tem[1]||'')};
        }   
    if(M[1]==='Chrome'){
        tem=ua.match(/\bOPR|Edge\/(\d+)/)
        if(tem!=null)   {return {name:'Opera', version:tem[1]};}
        }   
    M=M[2]? [M[1], M[2]]: [navigator.appName, navigator.appVersion, '-?'];
    if((tem=ua.match(/version\/(\d+)/i))!=null) {M.splice(1,1,tem[1]);}
    return {
      name: M[0],
      version: M[1]
    };
 }

function getshop_goToOverviewPage() {
    $('.gslbookingBody').show();
    $('.addons_overview').show();
    gslbookingcurresult = JSON.parse(sessionStorage.getItem('gslcurrentbooking'));
    getshop_showOverviewPage();
    getshop_loadAddonsAndGuestSumaryView();
}

function getshop_goToAddonsPage() {
    $('.gslbookingBody').show();
    $('.addons_overview').show();
    gslbookingcurresult = JSON.parse(sessionStorage.getItem('gslcurrentbooking'));
    getshop_loadAddonsAndGuestSumaryView();
}

function getshop_goToNextPage(page) {
    var startDate = $('#date_picker_start').val();
    var endDate = $('#date_picker_end').val();
    var rooms = $('#count_room').val();
    var adults = $('#count_adult').val();
    var children = $('#count_child').val();
    var discountCode = $('#coupon_input').val();
    
    startDate = startDate.split(".");
    endDate = endDate.split(".");
    
    startDate = startDate[2] + "-" + startDate[1] + "-" + startDate[0];
    endDate = endDate[2] + "-" + endDate[1] + "-" + endDate[0];
    
    var link = page + "#start=" + startDate + "&end=" + endDate + "&rooms=" + rooms + "&adults=" + adults + "&children=" + children + "&discount=" + discountCode;
    window.location.href = link;
}

function getshop_getsessionid() {
    var check = sessionStorage.getItem("getshop_sessionid");
    if(check) {
        return check;
    }
    
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
         .toString(16)
         .substring(1);
    }
    var sessid = s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
     
    sessionStorage.setItem("getshop_sessionid", sessid);
    return sessid;
}

/**
 * 
 * @returns {GetShopApiWebSocketEmbeddedBooking|getshopclient}
 */
function getshop_getWebSocketClient() { 
    if(typeof(getshopclient) === "undefined") {
        var hostToUse = getshop_endpoint;
        if(!hostToUse) {
            hostToUse = window.location.host;
        }
        hostToUse = hostToUse.replace("http://", "");
        hostToUse = hostToUse.replace("https://", "");
        hostToUse = hostToUse.replace("/", "");

        getshopclient = new GetShopApiWebSocketEmbeddedBooking("websocket.getshop.com", "443", hostToUse); //Online
//        getshopclient = new GetShopApiWebSocket("localhost", "31330", getshop_endpoint); //Local
        getshopclient.identifier = hostToUse;
        getshopclient.shouldConnect = true;
        getshopclient.connect();
        getshopclient.addListener("com.thundashop.core.verifonemanager.VerifoneFeedback", getshop_displayVerifoneFeedBack);
    }
    return getshopclient;
}


function getshop_get_browser() {
    var ua=navigator.userAgent,tem,M=ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || []; 
    if(/trident/i.test(M[1])){
        tem=/\brv[ :]+(\d+)/g.exec(ua) || []; 
        return {name:'IE',version:(tem[1]||'')};
        }   
    if(M[1]==='Chrome'){
        tem=ua.match(/\bOPR|Edge\/(\d+)/)
        if(tem!=null)   {return {name:'Opera', version:tem[1]};}
        }   
    M=M[2]? [M[1], M[2]]: [navigator.appName, navigator.appVersion, '-?'];
    if((tem=ua.match(/version\/(\d+)/i))!=null) {M.splice(1,1,tem[1]);}
    return {
      name: M[0],
      version: M[1]
    };
 }

function getshop_printPrice(price) {
    var curtext = getshop_bookingconfiguration.currencyText;
    
    var prefix = "";
    if(curtext == "GBP") {
        prefix = "&#163; ";
    }
    if(curtext == "NOK") {
        prefix = "kr ";
    }
    if(curtext == "CAD" || curtext == "USD") {
        prefix = "&#36; ";
    }
    if(curtext == "EUR") {
        prefix = "&#8364; ";
    }
    if(price) {
        return  prefix + parseInt(price);
    } else {
        return prefix;
    }
}

function getshop_searchRooms(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $('.GslBooking .guestInfoBox').hide();
        var btn = $(this);
        if(btn.find('.fa-spin').length > 0) {
            return;
        }
        if(typeof(getshop_nextPage) !== "undefined") {
            getshop_goToNextPage(getshop_nextPage);
            return;
        }
        var rooms = $('#count_room').val();
        var adults = parseInt($('#count_adult').val());
        var children = parseInt($('#count_child').val());
        if(rooms > (adults+children)) {
            alert('It is not possible to select more rooms than guests');
            return;
        }

        getshop_confirmGuestInfoBox();
        getshop_setPageName('search');
        $('.GslBooking .ordersummary').hide();
        var btnText = btn.text();
        if(btnText) {
            btn.html('<i class="fa fa-spin fa-spinner"></i>');
        }
        $('.productentrybox').remove();
        var time = new Date().toLocaleTimeString('en-us');
        var discountCode = $('#coupon_input').val();
        
        var start = moment.utc($('#date_picker_start').val(), "DD.MM.YYYY").local();
        var startDate = start.format('MMM DD, YYYY ') + time;
        
        var end = moment.utc($('#date_picker_end').val(), "DD.MM.YYYY").local();
        var endDate = end.format('MMM DD, YYYY ') + time;

        startDate = startDate.replace(/[^a-zA-Z0-9,: ]/g, "")
        endDate = endDate.replace(/[^a-zA-Z0-9,: ]/g, "");
        
        var userLang = navigator.language || navigator.userLanguage; 

        var data = {
            "start": startDate,
            "end": endDate,
            "rooms": rooms,
            "adults": adults,
            "children": children,
            "discountCode": discountCode,
            "bookingId": "",
            "language" : sessionStorage.getItem("getshop_language"),
            "browser" : getshop_get_browser(),
            "browserLanguage" : userLang
        };
        var client = getshop_getWebSocketClient();
        var starting = client.PmsBookingProcess.startBooking(getshop_domainname, data);
        starting.done(function(res) {
            if(btnText) {
                btn.html(btnText);
            }
            getshop_bookingId = res.bookingId;
            if(res.supportPayLaterButton) {
                $('.paylater_button').show();
            } else {
                $('.paylater_button').hide();
            }
            
            $('#warningstartyesterday').hide();
            if(res.startYesterday) {
                $('#warningstartyesterday').show();
            }

            $('.gslbookingBody').show();
            $('#productentry').html('');
            gslbookingcurresult = res;
            sessionStorage.setItem('gslcurrentbooking', JSON.stringify(gslbookingcurresult));
            $('.noroomsfound').hide();
            if(!res || !res.hasAvailableRooms) {
                $('.noroomsfound').show();
                $('.GslBooking .hide').hide();
            } else {
                getshop_updateOrderSummary(res, true);
            }

            var translation = getshop_getBookingTranslations();
            
            $('.getshop_specialerrormsg').remove();
            if(res.errorMessage) {
                var errorstring = res.errorMessage.split(":");
                $('.noroomsfound').show();
                $('.noroomsfounderrormessage').show();
                if(res.hasAvailableRooms) {
                    $('.noroomsfounderrormessage').hide();
                }
                var text = translation[errorstring[0]];
                text = text.replace(errorstring[1], errorstring[2]);
                $('.noroomsfound').append('<div class="getshop_specialerrormsg">' + text + "</div>");
            }
            
            for (var k in res.rooms) {
                var room = res.rooms[k];
                var firstFile = "";
                if(room.images.length > 0) {
                    firstFile = room.images[0].fileId;
                }
                var featuredImageUrl = getshop_endpoint+'/displayImage.php?id='+firstFile;
                var productentry = '';
                var utilities = '';
                var user_icon = 1;
                var roomBox = $('#productentrybox').clone();
                roomBox.attr('id',null);
                roomBox.addClass('productentrybox');
                roomBox.attr('roomid', room.id);

                roomBox.find('.roomname').html(room.name);
                roomBox.find('.product_availablerooms').attr('numberavailable',room.availableRooms);
                roomBox.find('.availableroomcontainer').html(room.availableRooms);
                roomBox.find('.lowpricedisplayer').html(room.pricesByGuests[1]);
                roomBox.find('.roomdescription').html(room.description);
                roomBox.find('.featured-image').css('background-image','url('+featuredImageUrl+')');              
                roomBox.attr('index', k);
                
                var text = translation['availablerooms_'+room.systemCategory];
                if(!text) { text ='availablerooms_'+room.systemCategory; };
                roomBox.find('[gstranslationfield="availableRooms"]').html(text);
                
                var text = translation['numberofrooms_'+room.systemCategory];
                if(!text) { text ='numberofrooms_'+room.systemCategory; };
                roomBox.find('[gstranslationfield="numberofrooms"]').html(text);

                var translation = getshop_getBookingTranslations();
                var currency = getshop_bookingconfiguration.currencyText;
                if(!currency) {
                    currency = "NOK";
                }
                for (var guest in room.pricesByGuests) {
                    
                    var numberofrooms = '';
                    var index = 1;
                    var multipleGuests = ' ' + translation['numberofguests'].toLowerCase();
                    if (guest == 1) {
                        multipleGuests = ' ' + translation['guest'].toLowerCase();
                    }
                    for (var i = 1; i <= room.availableRooms; i++) {
                        var price = room.pricesByGuests[guest] * i;
                        price = parseInt(price);
                        numberofrooms += '<option value="' + i + '" data-price="' + price + '">' + i + '&nbsp;&nbsp; ('+currency+' ' + price + ')</option>';
                    }

                    roomBox.find('.guestselection').show();
                    if(numberofrooms) {
                        numberofrooms = "<option value='0' data-price='0'>0</option>" +  numberofrooms;
                        
                        var disabled = "";
                        if(guest < room.minGuests) {
                            disabled = "getshop_disabled_row";
                        }
                        
                        productentry = $('<tr class="productentry_itemlist '+ disabled + '"><td><i class="gsicon-gs-user"></i> x ' + user_icon + ''+ multipleGuests + '</td><td>' + getshop_printPrice(room.pricesByGuests[guest]) + ',-</td><td style="float:right;padding-right:10px;"><div class="select-wrapper"><select class="numberof_rooms" guests="'+guest+'">' + numberofrooms + '</select></div></td></tr>');
                        productentry.find('.numberof_rooms').val(room.roomsSelectedByGuests[guest]);
                    } else {
                        roomBox.find('.guestselection').hide();
                    }
                    roomBox.find('.guestselection').append(productentry);
                    user_icon++;
                }

                for (var utility in room.utilities) {
                    utilities += '<i class="fa fa-' + utility + '" title="' + room.utilities[utility] + '"></i>';
                }

                    $('#productentry').append(roomBox);
                    roomBox.find('.noroomsavailable').hide();
                    if(room.availableRooms === 0) {
                        roomBox.find('.noroomsavailable').show();
                    }
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
                    img.on('load',function () {

                        var idx = $(this).attr('index');
                        var inneridx = $(this).attr('innerindex');
                        var realWidth = this.width;
                        var realHeight = this.height;
                        var img = $(this).attr('src');
                        var roomBox = $('.productentrybox[index="'+idx+'"]');
                        var width = realWidth;
                        var height = realHeight;
                        var image = '<img class="roomimg gsgallery" style="display:none;" src="' + img + '" img="' + img + '" width="' + width + '" height="' + height + '" index="' + inneridx + '">';
                        roomBox.find('.gallery').append('<div class="image-wrapper"><div class="gallery-image image-holder' + active + '" style="background-image:url(\'' + img + '\')"></div></div>');
                        roomBox.find('.photoswipecontainer').append(image);
                        if (inneridx === "0") {
                            roomBox.find('.featured-image').attr("img", img);
                            roomBox.find('.featured-image').attr("height", height);
                            roomBox.find('.featured-image').attr("width", width);
                        }
                    });
                }
            }
        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_removeGroupedRooms(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $(this).find('i').addClass('fa-spin');
        var tr = $(this).closest('tr');
        var index = tr.attr('index');
        var roomId = tr.attr('roomid');
        var guest = tr.attr('guests');

       var client = getshop_getWebSocketClient();
       var remove = client.PmsBookingProcess.removeGroupedRooms(getshop_domainname, {
            "roomId": roomId,
            "guestCount" : guest
        });
        remove.done(function(res) {
            gslbookingcurresult.rooms[index].roomsSelectedByGuests[guest] = 0;    
            getshop_updateOrderSummary(gslbookingcurresult, false);
            $('.productentrybox[index="'+index+'"]').find('.numberof_rooms[guests="'+guest+'"]').val(0);
        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_removeRoom(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var translation = getshop_getBookingTranslations();
        var confirmed = confirm(translation['sureremoveroom']);
        if(confirmed) {
            var id = $(this).closest('.roomrowadded').attr('roomid');
            var saving = getshop_saveGuestInformation();
            saving.done(function() {
               var client = getshop_getWebSocketClient();
               var removeRoom = client.PmsBookingProcess.removeRoom(getshop_domainname, id);
               removeRoom.done(function(res) {
                    getshop_loadAddonsAndGuestSummaryByResult(res);
               });
            });
        }
    }catch(e) { getshop_handleException(e); }
}

function getshop_changeChildSettings(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var saving = getshop_saveGuestInformation();
        saving.done(function(res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_showEditRoomOptions(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $(this).closest('.roomentry').find('.editroomoptions').toggle();
    }catch(e) { getshop_handleException(e); }
}

function getshop_hideGuestSelectionBox(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var target = $(e.target);
        if(target.attr('id') == "guests" || target.closest('#guests').length > 0) {
            return;
        }

        if(target.closest('.guestInfoBox').length > 0 || target.hasClass('guestInfoBox')) {
            return;
        }
        $('.GslBooking .guestInfoBox').hide();
        getshop_confirmGuestInfoBox();
    }catch(e) { getshop_handleException(e); }
}

function getshop_displayVerifoneFeedBack(res) {

    if(res.msg === "payment failed") {
        if(getshop_manuallycancelledbutton) {
            window.location.href="paymentterminal.php";
            return;
        }
//        alert('Payment failed, please try again!');
        var client = getshop_getWebSocketClient();
        client.PmsBookingProcess.chargeOrderWithVerifoneTerminal(getshop_domainname, getshop_currentorderid,getshop_terminalid);
        getAddons.done(function(res) {
            getshop_loadAddonsAndGuestSummaryByResult(res);
        });
    }

    if(res.msg === "completed") {
        var tosend = {
            "orderId" :  getshop_currentorderid,
            "terminalId" : getshop_terminalid
        }

        var client = getshop_getWebSocketClient();
        var printing = client.PmsBookingProcess.printReciept(getshop_domainname, tosend);
        setTimeout(function() {
            alert('Thank you for your payment, the room number and code for the room will be sent to you by sms and email');
            window.location.href="paymentterminal.php";
        }, "2000");
    } else {
        $('.verifonefeedbackdata').show();
        $('.verifonefeedbackdata').html(res.msg);
    }
}

function getshop_tryChangingDate(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        var room = $(this).closest('.roomentry');
        var start = moment(room.find('[gsname="newroomstartdate"]').val(), 'DD.MM.YYYY');
        var end = moment(room.find('[gsname="newroomenddate"]').val(), 'DD.MM.YYYY');

        var time = new Date().toLocaleTimeString('en-us');
        var starttime = start.format('MMM DD, YYYY ') + time;
        var endtime = end.format('MMM DD, YYYY ') + time;
    
        starttime = starttime.replace(/[^a-zA-Z0-9,: ]/g, "")
        endtime = endtime.replace(/[^a-zA-Z0-9,: ]/g, "")

        var roomid = room.attr("roomid");
        var data = {
            "start" : starttime,
            "end" : endtime,
            "roomId" : roomid
        }

        var saving = getshop_saveGuestInformation();
        saving.done(function(res) {
            var client = getshop_getWebSocketClient();
            var changeDateOnRoom = client.PmsBookingProcess.changeDateOnRoom(getshop_domainname, data);
            changeDateOnRoom.done(function(res) { getshop_loadAddonsAndGuestSummaryByResult(res); });
        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_removeAllSelected(e) {
    try {
        if(getshop_avoiddoubletap(e)) { return; }
        $('.removeselectedroom').each(function() {
            $(this).find('i').addClass('fa-spin');
            var tr = $(this).closest('tr');
            var index = tr.attr('index');
            var roomId = tr.attr('roomid');
            var guest = tr.attr('guests');

           var client = getshop_getWebSocketClient();
           var remove = client.PmsBookingProcess.removeGroupedRooms(getshop_domainname, {
                "roomId": roomId,
                "guestCount" : guest
            });
            remove.done(function(res) {
                gslbookingcurresult.rooms[index].roomsSelectedByGuests[guest] = 0;    
                getshop_updateOrderSummary(gslbookingcurresult, false);
                $('.productentrybox[index="'+index+'"]').find('.numberof_rooms[guests="'+guest+'"]').val(0);
                $(window).scrollTop(0);
            });
        });
    }catch(e) { getshop_handleException(e); }
}

function getshop_cancelPayment() {
    try {
        getshop_manuallycancelledbutton = true;
        var client = getshop_getWebSocketClient();
        var cancelling = client.PmsBookingProcess.cancelPaymentProcess(getshop_domainname, {
            "terminalid" : localStorage.getItem("getshopterminalid"),
        });
    }catch(e) { getshop_handleException(e); }
}

$(document).on('touchend click', '.GslBooking', getshop_hideGuestSelectionBox);
$(document).on('change', '.GslBooking .numberof_rooms', getshop_changeNumberOfRooms);
$(document).on('touchend click', '.GslBooking .guestInfoBox .fa', getshop_changeGuestSelection);
$(document).on('click', '.GslBooking #sameasguestselection', getshop_setSameAsGuest);
$(document).on('touchend click','.GslBooking .gssigninbutton', getshop_logon);
$(document).on('touchend click','.GslBooking .gssignoutbutton', getshop_logout);
$(document).on('touchend click','.GslBooking .selectedusertype', getshop_changeBookingType);
$(document).on('touchend click','.GslBooking .removeroom', getshop_removeRoom);
$(document).on('touchend click', '.GslBooking .destination_line', getshop_changedestination);
$(document).on('touchend click', '.GslBooking #guests', getshop_showGuestBox);
$(document).on('touchend click', '.GslBooking #destination', getshop_showDesitinationBox);
$(document).on('touchend click', '.GslBooking .paylater_button', getshop_gotopayment);
$(document).on('touchend click', '.GslBooking .go_to_payment_button', getshop_gotopayment);
$(document).on('touchend click', '.GslBooking #search_rooms', getshop_searchRooms);
$(document).on('touchend click', '.GslBooking .addguest', getshop_addGuest);
$(document).on('touchend click', '.GslBooking .addroom', getshop_addRoom);
$(document).on('touchend click', '.GslBooking .roomheading .guestaddonicon', getshop_addRemoveAddon);
$(document).on('touchend click', '.GslBooking .guestentry .removeguest', getshop_removeGuest);
$(document).on('touchend click', '.GslBooking .continuetoguestinformationbtn', getshop_continueToSummary);
$(document).on('touchend click', '.GslBooking .addButton', getshop_addRemoveAddon);
$(document).on('touchend click', '.GslBooking .addCampaignButton', getshop_addRemoveCampaign);
$(document).on('touchend click', '.GslBooking .removeselectedroom', getshop_removeGroupedRooms);
$(document).on('touchend click', '.GslBooking .gslfront_1 .trychangingdate', getshop_tryChangingDate);
$(document).on('touchend click', '.GslBooking [gsname="ischild"]', getshop_changeChildSettings);
$(document).on('touchend click', '.GslBooking .displayeditroom', getshop_showEditRoomOptions);
$(document).on('touchend click', '.GslBooking .cancelpaymentbutton', getshop_cancelPayment);
$(document).on('touchend click', '.GslBooking .removeallselectedroomsbtn', getshop_removeAllSelected);
$(document).on('touchend click', '.GslBooking .getshop_room_config .prev_button', getshop_showPrevRoomConfig);

function getshop_doEvent(functiontorun) {
    $.proxy(functiontorun, this);
}

getshop_WebSocketClient = {
    client: false, 
    listeners: [],

    connected: function() {
    },

    disconnected: function() {
        getshop_WebSocketClient.client = false;
        setTimeout(getshop_WebSocketClient.getClient, 1000);
    },

    handleMessage: function(msg) {
        var dataObject = JSON.parse(JSON.parse(msg.data));

        for (var i in getshop_WebSocketClient.listeners) {
            var listener = getshop_WebSocketClient.listeners[i];
            if (listener.dataObjectName === dataObject.coninicalName) {
                listener.callback(dataObject.payLoad);
            }
        }
    },

    getClient: function() {
//        var me = getshop_WebSocketClient;
        if (!getshop_WebSocketClient.client) {
            var endpoint = window.location.host;
            if(getshop_endpoint) {
                endpoint = getshop_endpoint;
            }
            this.socket = new WebSocket("wss://"+endpoint+":21330/");
            this.socket.onopen = getshop_WebSocketClient.connected;
            this.socket.onclose = function() {
                getshop_WebSocketClient.disconnected();
            };
            this.socket.onmessage = function(msg) {
                getshop_WebSocketClient.handleMessage(msg);
            };
        }

        return getshop_WebSocketClient.client;
    },

    addListener : function(dataObjectName, callback) {
        getshop_WebSocketClient.getClient(); 
        var listenObject = {
            dataObjectName : dataObjectName,
            callback: callback
        }

        getshop_WebSocketClient.listeners.push(listenObject);
   }
};



/* START GetShop Websocket api */
var GetShopApiWebSocketEmbeddedBooking = function(address, port, identifier, persistMessages) {
    this.sentMessages =  [];
    this.messagesToSendJson =  [];
    this.address = address;

    if (typeof(port) === "undefined" || !port) {
        this.port = "31330";
    } else {
        this.port = port;
    }
    
    if (typeof(identifier) === "undefined" || !identifier) {
        this.identifier = this.address;
    } else {
        this.identifier = identifier; 
    }
};

GetShopApiWebSocketEmbeddedBooking.prototype = {
    websocket: null,
    connectionEstablished: null,
    transferCompleted: null,
    transferStarted: null,
    shouldConnect: true,
    sessionId: false,
    unsentMessageLoaded: false,
    globalErrorHandler: false,
    messageCountChangedEvent: null,
    listeners: [],
    
    connect: function() {
        if (!this.shouldConnect)
            return;
        
        this.shouldConnect = false;
        this.connectedCalled = true;
        var me = this;
        if (this.connectionEstablished === null) {
            this.fireDisconnectedEvent();
        }
        if(typeof(gsisdevmode) !== "undefined" && gsisdevmode) {
            var address = "ws://localhost:31330/";
        } else {
            var address = "wss://websocket.getshop.com/";
        }
        if(typeof(getshop_websockethost) !== "undefined" && getshop_websockethost) { 
            address = "wss://" + getshop_websockethost;
        }

        this.socket = new WebSocket(address);
        this.socket.onopen = $.proxy(this.connected, this);
        this.socket.onclose = function() {
            me.disconnected();
        };
        this.socket.onmessage = function(msg) {
            me.handleMessage(msg);
        };
        
        this.createManagers();
    },

    setGlobalErrorHandler: function(globalErrorHandler) {
        this.globalErrorHandler = globalErrorHandler;
    },
    
    guid: function() {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
    },

    addListener : function(dataObjectName, callback, scope) {
        var listenObject = {
            gs_session_scope: scope,
            dataObjectName : dataObjectName,
            callback: callback
        }
        
        this.listeners.push(listenObject);
    },

    handleMessage: function(msg) {
        var data = msg.data;
        var jsonObject = JSON.parse(data);
        
        var corrolatingMessage = this.getMessage(jsonObject.messageId);
        
        if (typeof(corrolatingMessage) === "undefined") {
            this.handleIncomingMessage(jsonObject);
            return;
        }

        if (this.globalErrorHandler && jsonObject && jsonObject.object && jsonObject.object.errorCode) {
            this.globalErrorHandler(jsonObject.object);
        } else {
            corrolatingMessage.resolveWith({ 'messageId': jsonObject.messageId }, [jsonObject.object]);
        }
        
        if (this.sentMessages.length === 0 && this.transferCompleted) {
            this.transferCompleted();
        }
        
        if (this.sentMessages.length === 0 && this.transferCompletedFirstTimeAfterUnsentMessageSent && this.firstUnsentMessages) {
            this.transferCompletedFirstTimeAfterUnsentMessageSent();
            this.firstUnsentMessages = false;
        }
        
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            messagePersister.markAsSent(corrolatingMessage);
            this.fireMessageCountChanged();
        }
    },

    handleIncomingMessage: function(msg) {
        if ( typeof msg === "object")
            return;
        
        var dataObject = JSON.parse(msg);
        for (var i in this.listeners) {
            var listener = this.listeners[i];
            if (listener.dataObjectName === dataObject.coninicalName) {
                if (listener.gs_session_scope) {
                    listener.callback.apply(listener.gs_session_scope, [dataObject.payLoad]);
                } else {
                    listener.callback(dataObject.payLoad);
                }
            }
        }
    },

    reconnect: function() {
        var me = this;
        this.shouldConnect = true;
        exec = function() {
            me.connect();
        };
        setTimeout(exec, 300);
    },
            
    initializeStore: function() {
        if (this.socket.OPEN)
            this.socket.send('initstore:'+this.identifier);
    },
            
    connected: function() {
        this.setSessionId();
        this.initializeStore();
        this.fireConnectedEvent();
        this.connectionEstablished = true;
    },
    
    getUnsentMessageCount: function() {
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            return messagePersister.getUnsetMessageCount();
        }
        
        return 0;
    },

    setSessionId: function() {
        if (sessionStorage.getItem("getshop.sessionId")) {
            this.sessionId = sessionStorage.getItem("getshop.sessionId");
        }
        
        if (!this.sessionId) {
            this.sessionId = this.guid();
            sessionStorage.setItem("getshop.sessionId", this.sessionId);
        }
        
        if (this.socket.OPEN)
            this.socket.send('sessionid:'+this.sessionId);
    },
          
    disconnected: function() {
        this.sentMessages = [];
        
        this.fireDisconnectedEvent();
        this.connectionEstablished = false;
        this.firstUnsentMessages = false;
        this.reconnect();
    },

    setInitConnectionFailed: function(callback) {
        this.initConnectionFailed = callback;
    },
    fireDisconnectedEvent: function() {
        if (this.connectionEstablished === null || this.connectionEstablished && typeof(this.disconnectedCallback) === "function") {
            if (this.disconnectedCallback) {
                this.disconnectedCallback();
            }
        }
    },
            
    fireConnectedEvent: function() {
        if (this.connectionEstablished === null || !this.connectionEstablished && typeof(this.connectedCallback) === "function") {
            if (this.connectedCallback) {
                this.connectedCallback();
            }
        }
    },
    
    fireMessageCountChanged: function() {
        if (this.messageCountChangedEvent) {
            this.messageCountChangedEvent();
        }
    },
    
    setMessageCountChangedEvent: function(func) {
        this.messageCountChangedEvent = func;
    },
            
    setDisconnectedEvent: function(callback) {
        this.disconnectedCallback = callback;
    },
            
    setConnectedEvent: function(callback) {
        this.connectedCallback = callback;
    },
        
    send: function(message, silent) {
        var deferred = $.Deferred();
        message.messageId = this.makeid();
        
        if (typeof(messagePersister) !== "undefined" && messagePersister) {
            messagePersister.persist(message);
            this.fireMessageCountChanged();
        }
        
        deferred.messageId = message.messageId;
        var messageJson = JSON.stringify(message);
        
        if (this.sentMessages.length === 0 && this.transferStarted && silent !== true) {
            this.transferStarted();
        }
        
        this.sentMessages.push(deferred);
        
        var loginMessage = this.getLoginMessage();
        
        var sendFunc = function(messageJson, me) {
            if (me.socket.readyState !== 1) {
                setTimeout(function() {
                    sendFunc(messageJson, me);
                }, 50);
            } else {
                if (loginMessage) {
                    me.socket.send(loginMessage);
                }
                me.socket.send(messageJson);
                var messageObject = JSON.parse(messageJson);
            }
        }
        
        sendFunc(messageJson, this);
        
        this.sendUnsentMessages();
        
        return deferred;
    },
    
    getLoginMessage: function() {
        if (localStorage.getItem("username") && localStorage.getItem("password")) {
            var data = {
                args : {
                    username : JSON.stringify(localStorage.getItem("username")),
                    password : JSON.stringify(localStorage.getItem("password")),
                },
                method: 'logOn',
                interfaceName: 'core.usermanager.IUserManager',
            };
            
            data.messageId = this.makeid();
            return JSON.stringify(data);
        }
        
        return false;
    },
    
    sendUnsentMessages: function() {
        var sendFunc = function(messageJson, me) { 
            if (me.socket.readyState !== 1) {
                setTimeout(function() {
                    sendFunc(messageJson, me);
                }, 50);
            } else {
                me.socket.send(messageJson);
            }
        }
        
         if (typeof(messagePersister) !== "undefined" && messagePersister) {
            var allUnsetMessages = messagePersister.getAllUnsentMessages();
            
            for (var k in allUnsetMessages) {
                var unsentMessage = allUnsetMessages[k];
                
                if (!this.inUnsentMessages(unsentMessage.messageId)) {
                    var messageJson2 = JSON.stringify(unsentMessage);
                    var deferred2 = $.Deferred();
                    deferred2.messageId = unsentMessage.messageId;
                    this.sentMessages.push(deferred2);
                    sendFunc(messageJson2, this);
                }
            }
        }
    },
    
    inUnsentMessages: function(msgId) {
        for (var i in this.sentMessages) {
            if (this.sentMessages[i].messageId === msgId) {
                return true;
            }
        }
        
        return false;
    },

    getMessage: function(id) {
        for (var i=0;i<this.messagesToSendJson.length; i++) {
            if (this.messagesToSendJson[i].messageId === id) {
                this.messagesToSendJson.splice(i, 1);
            }
        }
        
        if (this.persistMessages) {
            localStorage.setItem("gs_api_messagetopush", JSON.stringify(this.messagesToSendJson));
            this.fireMessageCountChanged();
        }
        
        for (var i=0;i<this.sentMessages.length; i++) {
            if (this.sentMessages[i].messageId === id) {
                var message = this.sentMessages[i];
                this.sentMessages.splice(i, 1);
                return message;
            }
        }
    },
            
    makeid :  function () {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for( var i=0; i < 35; i++ )
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
}


GetShopApiWebSocketEmbeddedBooking.MessageManager = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocketEmbeddedBooking.MessageManager.prototype = {
    'collectEmail' : function(email, gs_silent) {
        var data = {
            args : {
                email : JSON.stringify(email),
            },
            method: 'collectEmail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAllSmsMessages' : function(start,end, gs_silent) {
        var data = {
            args : {
                start : JSON.stringify(start),
                end : JSON.stringify(end),
            },
            method: 'getAllSmsMessages',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getCollectedEmails' : function(gs_silent) {
        var data = {
            args : {
            },
            method: 'getCollectedEmails',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getIncomingMessages' : function(pageNumber, gs_silent) {
        var data = {
            args : {
                pageNumber : JSON.stringify(pageNumber),
            },
            method: 'getIncomingMessages',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMailMessage' : function(mailMessageId, gs_silent) {
        var data = {
            args : {
                mailMessageId : JSON.stringify(mailMessageId),
            },
            method: 'getMailMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getMailSent' : function(from,to,toEmailAddress, gs_silent) {
        var data = {
            args : {
                from : JSON.stringify(from),
                to : JSON.stringify(to),
                toEmailAddress : JSON.stringify(toEmailAddress),
            },
            method: 'getMailSent',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSmsCount' : function(year,month, gs_silent) {
        var data = {
            args : {
                year : JSON.stringify(year),
                month : JSON.stringify(month),
            },
            method: 'getSmsCount',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSmsMessage' : function(smsMessageId, gs_silent) {
        var data = {
            args : {
                smsMessageId : JSON.stringify(smsMessageId),
            },
            method: 'getSmsMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'getSmsMessagesSentTo' : function(prefix,phoneNumber,fromDate,toDate, gs_silent) {
        var data = {
            args : {
                prefix : JSON.stringify(prefix),
                phoneNumber : JSON.stringify(phoneNumber),
                fromDate : JSON.stringify(fromDate),
                toDate : JSON.stringify(toDate),
            },
            method: 'getSmsMessagesSentTo',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveIncomingMessage' : function(message,code, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
                code : JSON.stringify(code),
            },
            method: 'saveIncomingMessage',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendErrorNotify' : function(inText, gs_silent) {
        var data = {
            args : {
                inText : JSON.stringify(inText),
            },
            method: 'sendErrorNotify',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMail' : function(to,toName,subject,content,from,fromName, gs_silent) {
        var data = {
            args : {
                to : JSON.stringify(to),
                toName : JSON.stringify(toName),
                subject : JSON.stringify(subject),
                content : JSON.stringify(content),
                from : JSON.stringify(from),
                fromName : JSON.stringify(fromName),
            },
            method: 'sendMail',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMailWithAttachments' : function(to,toName,subject,content,from,fromName,attachments, gs_silent) {
        var data = {
            args : {
                to : JSON.stringify(to),
                toName : JSON.stringify(toName),
                subject : JSON.stringify(subject),
                content : JSON.stringify(content),
                from : JSON.stringify(from),
                fromName : JSON.stringify(fromName),
                attachments : JSON.stringify(attachments),
            },
            method: 'sendMailWithAttachments',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

    'sendMessageToStoreOwner' : function(message,subject, gs_silent) {
        var data = {
            args : {
                message : JSON.stringify(message),
                subject : JSON.stringify(subject),
            },
            method: 'sendMessageToStoreOwner',
            interfaceName: 'core.messagemanager.IMessageManager',
        };
        return this.communication.send(data, gs_silent);
    },

}
GetShopApiWebSocketEmbeddedBooking.PmsBookingProcess = function(communication) {
    this.communication = communication;
}

GetShopApiWebSocketEmbeddedBooking.PmsBookingProcess.prototype = {
    'addAddons' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'addAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'cancelPaymentProcess' : function(multilevelname, data, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'cancelPaymentProcess',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeDateOnRoom' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'changeDateOnRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'changeNumberOnType' : function(multilevelname, change, gs_silent) {
        var data = {
            args : {
                change : JSON.stringify(change),
            },
            method: 'changeNumberOnType',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'chargeOrderWithVerifoneTerminal' : function(multilevelname, orderId,terminalId, gs_silent) {
        var data = {
            args : {
                orderId : JSON.stringify(orderId),
                terminalId : JSON.stringify(terminalId),
            },
            method: 'chargeOrderWithVerifoneTerminal',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'completeBooking' : function(multilevelname, input, gs_silent) {
        var data = {
            args : {
                input : JSON.stringify(input),
            },
            method: 'completeBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'completeBookingForTerminal' : function(multilevelname, input, gs_silent) {
        var data = {
            args : {
                input : JSON.stringify(input),
            },
            method: 'completeBookingForTerminal',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'getAddonsSummary' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'getAddonsSummary',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'getConfiguration' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'getConfiguration',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'logOn' : function(multilevelname, logindata, gs_silent) {
        var data = {
            args : {
                logindata : JSON.stringify(logindata),
            },
            method: 'logOn',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'logOut' : function(multilevelname, gs_silent) {
        var data = {
            args : {
            },
            method: 'logOut',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'printReciept' : function(multilevelname, data, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'printReciept',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeAddons' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'removeAddons',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeGroupedRooms' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'removeGroupedRooms',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'removeRoom' : function(multilevelname, roomId, gs_silent) {
        var data = {
            args : {
                roomId : JSON.stringify(roomId),
            },
            method: 'removeRoom',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'saveGuestInformation' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'saveGuestInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'setCampaignCode' : function(multilevelname, code, gs_silent, gs_dont_persist) {
        var data = {
            args : {
                code : JSON.stringify(code),
            },
            method: 'setCampaignCode',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent, gs_dont_persist);
    },
    'setGuestInformation' : function(multilevelname, bookerInfo, gs_silent) {
        var data = {
            args : {
                bookerInfo : JSON.stringify(bookerInfo),
            },
            method: 'setGuestInformation',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'startBooking' : function(multilevelname, arg, gs_silent) {
        var data = {
            args : {
                arg : JSON.stringify(arg),
            },
            method: 'startBooking',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

    'startPaymentProcess' : function(multilevelname, data, gs_silent) {
        var data = {
            args : {
                data : JSON.stringify(data),
            },
            method: 'startPaymentProcess',
            multiLevelName: multilevelname,
            interfaceName: 'core.pmsbookingprocess.IPmsBookingProcess',
        };
        return this.communication.send(data, gs_silent);
    },

}

GetShopApiWebSocketEmbeddedBooking.prototype.createManagers = function() {
    this.MessageManager = new GetShopApiWebSocketEmbeddedBooking.MessageManager(this);
    this.PmsBookingProcess = new GetShopApiWebSocketEmbeddedBooking.PmsBookingProcess(this);
}/* END GetShop Websocket api */
