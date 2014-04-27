app.Booking = {
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: __f("Settings"),
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-gears",
                    iconsize : "30",
                    title: __f("Settings"),
                    click: app.Booking.showSettings
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    showSettings: function(event, app) {
        var event = thundashop.Ajax.createEvent(null, "showSettings", app, {});
        thundashop.common.showInformationBox(event, __f("Settings"));
    }
};

Booking = {
    check: function(test) {
        if (!test || test == "")
            throw "empty";
    },
    bindEvents: function() {
        $(document).on('keyup', '.Booking #birthday.updateOnBlur', this.updateCompanyInformation);
        $(document).on('click', '.Booking .donemarker', this.markAsDone);
        $(document).on('click', '.Booking .find_company_for_selection', this.startSearchCompany);
        $(document).on('change', '.Booking .select_course_location', this.updateCourseLocation);
        $(document).on('click', '.Booking .search_for_company_brreg', this.searchBrreg);
        $(document).on('click', '.Booking .select_searched_company', this.selectCompanyFromBrreg);
        $(document).on('keyup', '.Booking .searchbrregvalue', this.searchBrreg);
    },
    selectCompanyFromBrreg : function(event) {
        
        var row = $(this).closest('tr');
        var brregnumber = $(this).attr('orgnr');
        
        $(this).closest('.app').find('#birthday').val(brregnumber);
        $(this).closest('.app').find('#birthday').keyup();
        $(this).closest('.app').find('#birthday').closest('tr').show();
        $('.Booking .selectcompanyform').fadeOut();
        
    },
    searchBrreg : function(event) {
        if(event.type === "keyup" && event.keyCode !== 13) {
            return;
        }
        
        var value = $(this).closest('.app').find('.searchbrregvalue').val();
        var event = thundashop.Ajax.createEvent('','findCompanies',$(this),{'name':value});
        thundashop.Ajax.postWithCallBack(event, function(data)  {
            $('.Booking .search_result_area').html(data);
        });
    },
    startSearchCompany : function() {
        $('.Booking .selectcompanyform').slideDown();
    },
    updateCourseLocation : function() {
        var selection = $(this).closest('.app').find('#event');
        var location = $(this).val();
        var foundcourse = false;
        selection.find('option').each(function() {
           if($(this).attr('location') === location) {
               if(!foundcourse) {
                   foundcourse = $(this).attr('value');
               }
               $(this).show();
           } else {
               $(this).hide();
           }
        });
        if(foundcourse) {
            selection.find('[value="'+foundcourse+'"]').attr('selected','selected');
            selection.show();
        } else {
            selection.hide();
        }
        
    },
    markAsDone: function() {
        var data = {
            userId : $(this).attr('userId')
        }
        
        var event = thundashop.Ajax.createEvent(null, "markAsDone", this, data);
        thundashop.Ajax.post(event);
    },
    updateCompanyInformation: function() {
        var value = $(this).val();
        if (value.length === 9) {
            var outer = $('<div/>');
            var loader = $('<img src="skin/default/images/ajaxloader.gif"/><br>');
            outer.append(loader);
            outer.append(__w('Loading data... please wait'));
            outer.css('text-align', 'center');
            $(this).closest('table').find('.companyinformation').html(outer);
            var event = thundashop.Ajax.createEvent(null, 'getCompanyInformation', this, {vatnumber: value});
            event['synchron'] = true;
            thundashop.Ajax.post(event, Booking.gotCompanyInformation, {}, true, true);
        } else {
            var invalidText = "Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt: " + value.length;
            $(this).closest('table').find('.companyinformation').html(invalidText);
        }
    },
            
    gotCompanyInformation: function(response) {
        $('.Booking table .companyinformation').html(response);
    },
            
    validate: function(evt) {
        var theEvent = evt || window.event;
        var key = theEvent.keyCode || theEvent.which;
        key = String.fromCharCode(key);
        var regex = /[0-9]|\./;
        if (!regex.test(key)) {
            theEvent.returnValue = false;
            if (theEvent.preventDefault)
                theEvent.preventDefault();
        }
    }
}

$('.Booking .groupselection .selectbox').live('click', function() {
    var groupid = $(this).attr('groupid');
    var data = {
        groupid: groupid
    }
    var event = thundashop.Ajax.createEvent('Booking', 'setGroup', $(this), data);
    thundashop.Ajax.post(event);
});

$('.Booking .selected .changeit').live('click', function() {
    var event = thundashop.Ajax.createEvent('Booking', 'unsetGroup', $(this), {});
    thundashop.Ajax.post(event);
});

$('.Booking .savebooking').live('click', function() {
    var data = {}
    data.name = $('#name').val();
    data.email = $('#email').val();
    data.cellphone = $('#cellphone').val();
    data.birthday = $('#birthday').val();
    
    if ($('#invoiceemail').length > 0) {
        data.invoiceemail = $('#invoiceemail').val();
    }
    
    if ($('#birthday').hasClass("updateOnBlur")) {
        data.company = $('.companyinformation').html();
    } else {
        data.company = $('#company').val();
    }

    var allowAdd = $('#bookingtable').attr('allowadd') === "true";
    if (allowAdd) {
        data.date = $('#date').val();
        data.time = $('#time').val();
    } else {
        data.eventid = $('#event').val();
    }

    try {
        Booking.check(data.name);
        Booking.check(data.email);
        Booking.check(data.cellphone);

        if (allowAdd) {
            Booking.check(data.date);
            Booking.check(data.time);
        } else {
            Booking.check(data.birthday);
            Booking.check(data.company);
            if ($('#event').length > 0)
                Booking.check(data.eventid);
        }
    } catch (error) {
        thundashop.common.Alert(__w('Stop'), __w('All fields are required'), true);
        return;
    }

    var event = thundashop.Ajax.createEvent('Booking', 'runRegisterEvent', $(this), data);
//    if ($('#event').length > 0) {
        var result = thundashop.Ajax.postSynchronWithReprint(event);
        if (result !== false)
            thundashop.common.Alert(__w('Completed'), __w('Your are now signed up for the event'));
//    } else {
//        var result = thundashop.Ajax.post(event);
//        thundashop.common.Alert(__w('Completed'), __w('Your you for your order'));
//    }
});

Booking.bindEvents();