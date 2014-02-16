Booking = {
    check: function(test) {
        if (!test || test == "")
            throw "empty";
    },
    bindEvents: function() {
        $(document).on('keyup', '.Booking #birthday.updateOnBlur', this.updateCompanyInformation);
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
            Booking.check(data.eventid);
        }
    } catch (error) {
        thundashop.common.Alert(__w('Stop'), __w('All fields are required'), true);
        return;
    }

    var event = thundashop.Ajax.createEvent('Booking', 'runRegisterEvent', $(this), data);
    var result = thundashop.Ajax.postSynchronWithReprint(event);
    if (result !== false)
        thundashop.common.Alert(__w('Completed'), __w('Your are now signed up for the event'));
});

Booking.bindEvents();