app.PmsSearchBox = {
    disableHover : null,
    init : function() {
        $(document).on('click','.PmsSearchBox .advancesearchtoggle',app.PmsSearchBox.toggleAdvanceSearch);
        $(document).on('click','.PmsSearchBox .otherselectiontoggle',app.PmsSearchBox.otherToggle);
        $(document).on('click','.PmsSearchBox .opensimplesearch',app.PmsSearchBox.toggleAdvanceSearch);
        $(document).on('click','.PmsSearchBox .advancesearchicon',app.PmsSearchBox.toggleAdvanceSearchButton);
        $(document).on('click','.PmsSearchBox .typeselectiontoggle',app.PmsSearchBox.toggleRoomSelection);
        $(document).on('click','.PmsSearchBox .roomtypeselection .fa-close',app.PmsSearchBox.toggleRoomSelection);
        $(document).on('click','.PmsSearchBox .roomtypeselection .type input[type="checkbox"]',app.PmsSearchBox.updateRoomTypeCounter);
        $(document).on('click','.PmsSearchBox .searchcustomer',app.PmsSearchBox.searchForCustomers);
        $(document).on('click','.PmsSearchBox .addusertofilter',app.PmsSearchBox.addUserToFilter);
        $(document).on('click','.PmsSearchBox .addaddon',app.PmsSearchBox.addAddon);
        $(document).on('click','.PmsSearchBox .addtofilterrow .fa-trash-o',app.PmsSearchBox.removeRow);
        $(document).on('click','.PmsSearchBox .applyfilterbutton',app.PmsSearchBox.applyFilter);
        $(document).on('click','.PmsSearchBox .addonstofilter',app.PmsSearchBox.updateOtherFilterCounter);
        $(document).on('click','.PmsSearchBox .clearfilter',app.PmsSearchBox.clearFilter);
        $(document).on('click','.PmsSearchBox .sendmessagesbox',app.PmsSearchBox.showSendMessages);
        $(document).on('click','.PmsSearchBox .togglerow',app.PmsSearchBox.toggleRow);
        $(document).on('click','.PmsSearchBox .sendmessagebtn',app.PmsSearchBox.sendMessage);
        if(getshop.pms && getshop.pms.is_touch_device()) {
            $(document).on('click','.PmsSearchBox .displaydailydatepicker',app.PmsSearchBox.displayDailyRangePicker);
        } else {
            $(document).on('mouseover','.PmsSearchBox .displaydailydatepicker',app.PmsSearchBox.displayDailyRangePicker);
            $(document).on('mouseout','.PmsSearchBox .displaydailydatepicker',app.PmsSearchBox.rangePickerOut);
            $(document).on('click',app.PmsSearchBox.hideRangePicker);
        }
        $(document).on('click','.PmsSearchBox [daytype]',app.PmsSearchBox.changeSelection);
    },
    sendMessage : function() {
        $(this).html('<i class="fa fa-spinner fa-spin"></i>');
        var phoneNumbers = [];
        $('.phonemessagerow').each(function() {
            if($(this).hasClass('disabled')) {
                return;
            }
            var phone = {};
            phone.prefix = $(this).find('.prefix').val();
            phone.phone = $(this).find('.phone').val();
            phone.roomid = $(this).attr('roomid');
            phoneNumbers.push(phone);
        });
        var emails = [];
        $('.emailmessagerow').each(function() {
            if($(this).hasClass('disabled')) {
                return;
            }
            var email = {};
            email.email = $(this).find('.email').val();
            email.roomid = $(this).attr('roomid');
            emails.push(email);
        });
        
        data = {};
        data.phonenumbers = phoneNumbers;
        data.emails = emails;
        data.type = $(this).attr('type');
        data.message = $('#massmessagebox').val();
        data.title = $('#masstitle').val();
        
        var event = thundashop.Ajax.createEvent('','sendMassMessages',$(this),data);
        thundashop.Ajax.postWithCallBack(event, function(res) {
            thundashop.common.Alert('Done','Messages has been sent');
            $('.sendmessageoverviewbox').hide();
        });
    },
    toggleRow : function() {
        if($(this).closest('.messagerow').hasClass('disabled')) {
            $(this).closest('.messagerow').removeClass('disabled');
        } else {
            $(this).closest('.messagerow').addClass('disabled');
        }
    },
    showSendMessages : function() {
        var event = thundashop.Ajax.createEvent('','sendMessagesOverview',$(this), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.sendmessageoverviewbox').html(res);
            $('.sendmessageoverviewbox').show();
        });
    },
    hideRangePicker : function() {
        $('.dailydaterangepicker').hide();
    },
    rangePickerOut : function() {
        app.PmsSearchBox.disableHover = false;
    },
    changeSelection : function() {
        var date = $(this).attr('daytype');
        if(getshop.pms.is_touch_device() && $(this).attr('type')) {
            return;
        } 
        var type = $('.dailydaterangepicker').attr('type');
        var event = thundashop.Ajax.createEvent('', "quickfilterselection",$(this), {
            "date" : date,
            "type" : type
        });
        if(!getshop.pms.is_touch_device() && $('.dailydaterangepicker').is(':visible')) {
            app.PmsSearchBox.disableHover = true;
        }
        thundashop.Ajax.post(event, function(res) {
            $('.tablefilterinput').focus();
        });
    },
    displayDailyRangePicker : function() {
        if(app.PmsSearchBox.disableHover) {
            app.PmsSearchBox.disableHover = false;
            return;
        }
        var view = $('.PmsSearchBox .dailydaterangepicker');
        view.find('.header').hide();
        var type = $(this).attr('type');
        view.attr('type', type);
        view.find('[type="'+type+'"]').show(); 
        view.show();
    },
    clearFilter : function() {
        thundashop.Ajax.simplePost($(this),"clearFilter", {});
    },
    applyFilter : function() {
        var form = $(this).closest("[gstype='form']");
        var args = thundashop.framework.createGsArgs(form);
        
        var customers = [];
        $('.customersadded .addtofilterrow').each(function(res) {
           customers.push($(this).attr("userid")); 
        });
        args['customers'] = customers;
        
        var codes = [];
        $('.codesadded .addtofilterrow').each(function(res) {
           codes.push($(this).attr("code")); 
        });
        args['codes'] = codes;

        var addons = [];
        $('.addonstofilter').each(function() {
            if($(this).is(':checked')) {
                addons.push($(this).attr('productid'));
            }
        });
        
        args['addons'] = addons;
        
        var event = thundashop.Ajax.createEvent('',form.attr('method'), form, args);
        thundashop.Ajax.post(event);
    },
    
    otherToggle : function() {
        $('.PmsSearchBox .otherselection').toggle();
    },
    addAddon : function() {
        var input = $('.addaddonvalue');
        $('.codesadded').append('<div class="addtofilterrow" code="'+input.val()+'"><i class="fa fa-trash-o"></i>'+input.val()+"</div>");
        input.val('');
        app.PmsSearchBox.updateOtherFilterCounter();
    },
    addUserToFilter : function(event) {
        var target = $(event.target);
        var box = $(this);
        if(target.hasClass('fa-trash-o')) {
            return;
        }
        $('.customersadded').append(box.clone());
        box.remove();
        app.PmsSearchBox.updateOtherFilterCounter();
    },
    
    
    updateOtherFilterCounter : function() {
        var customerCount = 0;
        var codes = 0;
        var addons = 0;
        $('.customersadded .addtofilterrow').each(function(res) { customerCount++; });
        $('.codesadded .addtofilterrow').each(function(res) { codes++; });
        $('.addonstofilter').each(function() {
            if($(this).is(':checked')) {
                addons++;
            }
        });
        
        var text = "(" + customerCount + "," + codes + "," + addons + ")";
        $('.otherselectiontoggle .selectioncount').html(text);
    },
    removeRow : function() {
        $(this).closest('.addtofilterrow').remove();
        app.PmsSearchBox.updateOtherFilterCounter();
    },
    searchForCustomers : function() {
        var input = $('.searchcustomervalue');
        var toSearch = input.val();
        input.val('');
        
        var event = thundashop.Ajax.createEvent('','searchCustomers', input, {
            "name" : toSearch
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.customersearcresultpanel').html(res);
        });
    },
    updateRoomTypeCounter : function() {
        var counter = 0;
        $('.PmsSearchBox .roomtypeselection .type input[type="checkbox"]').each(function() {
            if($(this).is(':checked')) {
                counter++;
            }
        });
        if(counter === 0) {
            counter = "All";
        }
        $('.PmsSearchBox .selectioncount').html(counter);
    },
    toggleRoomSelection : function() {
        if($('.PmsSearchBox .roomtypeselection').is(':visible')) {
            $('.PmsSearchBox .roomtypeselection').slideUp();
        } else {
            $('.PmsSearchBox .roomtypeselection').slideDown();
        }
    },
    toggleAdvanceSearchButton : function() {
        var forFilter = $(this).attr('forfilter');
        if($(this).hasClass('advanceserachbuttonenabled')) {
            $(this).removeClass('advanceserachbuttonenabled');
            $('input[gsname="'+forFilter+'"]').val('false');
        } else {
            $(this).addClass('advanceserachbuttonenabled');
            $('input[gsname="'+forFilter+'"]').val('true');
        }
    },
    toggleAdvanceSearch : function() {
        if($('.PmsSearchBox .advancesearch').is(':visible')) {
            localStorage.setItem('advancesearchtoggled', "false");
            $('.PmsSearchBox .simplesearch').show();
            $('.PmsSearchBox .advancesearch').hide();
        } else {
            localStorage.setItem('advancesearchtoggled', "true");
            $('.PmsSearchBox .simplesearch').hide();
            $('.PmsSearchBox .advancesearch').show();
        }
    }
};
app.PmsSearchBox.init();