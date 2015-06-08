/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.ProMeisterIntrest = {
    delaySearchTimer: false, 
    
    init: function() {
        $(document).on('keyup', '.ProMeisterIntrest .search_company', this.searchBrreg);
        $(document).on('click', '.ProMeisterIntrest .course', this.courseSelected);
        $(document).on('click', '.ProMeisterIntrest .selectbox', this.groupSelected);
        $(document).on('click', '.ProMeisterIntrest .select_searched_company', this.selectCompanyFromBrreg);
        $(document).on('click', '.ProMeisterIntrest .send_email', this.sendForm);
    },
    
    sendForm: function() {
        var data = {
            name : $('#name').val(),
            email : $('#email').val(),
            invoiceemail : $('#invoiceemail').val(),
            cellphone : $('#cellphone').val(),
            extradep : $('#extradep').val(),
            vatNumber: $('#birthday').val()
        };
        
        try {
            Booking.check(data.name);
            Booking.check(data.vatNumber);
        } catch (error) {
            if (error === "emailVal") {
                thundashop.common.Alert(__w('Stop'), __w('Please check your email addresses'), true);
            } else {
                thundashop.common.Alert(__w('Stop'), __w('All fields are required'), true);
            }
            return;
        }
        var sendForm = thundashop.Ajax.createEvent("", "send", this, data);       
        thundashop.Ajax.post(sendForm, function() {
            alert('Tack for din intresseanmälan');
           
        });
    },
    
    
    searchBrreg: function (event) {
        var me = this;

        $('.ProMeisterIntrest .search_result_area').show();
        $('.ProMeisterIntrest .search_result_area').html('<i class="fa fa-refresh fa-spin"></i> ' + __f("Loading"));

        var search = function () {
            $('.ProMeisterIntrest .search_result_area').show();
            var value = $(me).val();
            var event = thundashop.Ajax.createEvent('', 'findCompanies', $(me), {'name': value});
            thundashop.Ajax.postWithCallBack(event, function (data) {
                $('.ProMeisterIntrest .search_result_area').html(data);
            });
        };

        if (app.ProMeisterIntrest.delaySearchTimer) {
            clearTimeout(app.ProMeisterIntrest.delaySearchTimer);
        }

        app.ProMeisterIntrest.delaySearchTimer = setTimeout(search, 500);
    },
    
    courseSelected: function() {
        var entryId = $(this).attr('entryId');
        var data = {
            entryId : entryId
        };
        
        var event = thundashop.Ajax.createEvent("", "courseSelected", this, data);
        thundashop.Ajax.post(event);
    },
    
    groupSelected: function() {
        var entryId = $(this).attr('groupid');
        var data = {
            groupid : entryId
        };
        
        var event = thundashop.Ajax.createEvent("", "groupSelected", this, data);
        thundashop.Ajax.post(event);
    },
    
    selectCompanyFromBrreg: function (event) {
        var row = $(this).closest('tr');
        var brregnumber = row.attr('orgnr');
        var name = row.find('.selected_name').html();

        $(this).closest('.app').find('.search_company').val(name);
        $(this).closest('.app').find('#birthday').val(brregnumber);
//        $(this).closest('.app').find('#birthday').closest('tr').show();
        $('.Booking .search_result_area').hide();
        app.ProMeisterIntrest.updateCompanyInformation();
    },
    
    updateCompanyInformation: function () {
        var field = $($('.ProMeisterIntrest #birthday')[0]);
        var value = $(field).val();
        var orgLength = $(field).attr('orglength');
        if (value.length == orgLength) {
            var outer = $('<div/>');
            var loader = $('<img src="skin/default/images/ajaxloader.gif"/><br>');
            outer.append(loader);
            outer.append(__w('Loading data... please wait'));
            outer.css('text-align', 'center');
            $(field).closest('table').find('.companyinformation').html(outer);
            $('.ProMeisterIntrest .search_result_area').hide();
            var event = thundashop.Ajax.createEvent(null, 'getCompanyInformation', field, {vatnumber: value});
            event['synchron'] = true;
            thundashop.Ajax.post(event, app.ProMeisterIntrest.gotCompanyInformation, {}, true, true);
        } else {
            var invalidText = "Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt: " + value.length;
            $(field).closest('table').find('.companyinformation').html(invalidText);
        }
    },
    
    gotCompanyInformation: function(response) {
        $('.ProMeisterIntrest table .companyinformation').html(response);
    }
};

app.ProMeisterIntrest.init();