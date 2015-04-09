app.ProMeisterBosch = {
    init : function() {
        $(document).on('click', '.ProMeisterBosch .groupselection .selectbox', app.ProMeisterBosch.setGroup)
        $(document).on('click', '.ProMeisterBosch .changeit', app.ProMeisterBosch.setGroup)
        $(document).on('keyup', '.ProMeisterBosch .search_company', this.searchBrreg);
        $(document).on('click', '.ProMeisterBosch .select_searched_company', this.selectCompanyFromBrreg);
        $(document).on('click', '.ProMeisterBosch .savebosch', this.savebosch);
        $(document).on('change', '.ProMeisterBosch .courseselector', this.courseChanged);
        $(document).on('change', '.ProMeisterBosch .hours', this.hoursChanged);
    },
    
    courseChanged: function() {
        var data = {
            selected_course : $(this).val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "setSelectedCourse", this, data);
        thundashop.Ajax.post(event);
    },
    
    hoursChanged: function() {
        var data = {
            selected_hours : $(this).val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "setSelectedHours", this, data);
        thundashop.Ajax.post(event);
    },
    
    loadSettings: function (element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    savebosch: function() {
        var data = {
            name : $('.ProMeisterBosch #name').val(),
            email : $('.ProMeisterBosch #email').val(),
            invoiceemail : $('.ProMeisterBosch #invoiceemail').val(),
            cellphone : $('.ProMeisterBosch #cellphone').val(),
            birthday : $('.ProMeisterBosch #birthday').val(),
        }
        
        if (!data.name) {
            alert('Du må oppgi deltakers navn');
            return;
        }
        
        if (!data.email) {
            alert('Du må oppgi deltakers e-post');
            return;
        }
        
        if (!data.email) {
            alert('Du må oppgi teknisk leders e-post');
            return;
        }
        
        if (!data.cellphone) {
            alert('Du må oppgi deltakers mobiltelefon');
            return;
        }
        
        if (!data.birthday) {
            alert('Du må velge et firma');
            return;
        }
        
        var event = thundashop.Ajax.createEvent(null, "signOn", this, data);
        event['synchron'] = true;
        thundashop.Ajax.postWithCallBack(event, function(response) {
            $('.ProMeisterBosch').html("Takk for din påmelding, du er nå påmeldt. En epost med mer infromasjon er sendt til deg.");
        });
        
    },
    
    searchBrreg: function() {
        var me = this;

        $('.ProMeisterBosch .search_result_area').show();
        $('.ProMeisterBosch .search_result_area').html('<i class="fa fa-refresh fa-spin"></i> ' + __f("Loading"));

        var search = function () {
            $('.ProMeisterBosch .search_result_area').show();
            var value = $(me).val();
            var event = thundashop.Ajax.createEvent('', 'findCompanies', $(me), {'name': value});
            thundashop.Ajax.postWithCallBack(event, function (data) {
                $('.ProMeisterBosch .search_result_area').html(data);
            });
        };

        if (app.ProMeisterBosch.delaySearchTimer) {
            clearTimeout(app.ProMeisterBosch.delaySearchTimer);
        }

        app.ProMeisterBosch.delaySearchTimer = setTimeout(search, 500);
    },
    
    setGroup: function() {
        var data = {
            groupId : $(this).attr('groupid')
        };

        var event = thundashop.Ajax.createEvent(null, "setGroup", this, data);
        thundashop.Ajax.post(event);
    },
    
    selectCompanyFromBrreg: function (event) {
        var row = $(this).closest('tr');
        var brregnumber = row.attr('orgnr');
        
        var name = row.find('.selected_name').html();

        $(this).closest('.app').find('#birthday').val(brregnumber);
        $(this).closest('.app').find('.search_company').val(name);
        
        $('.ProMeisterBosch .search_result_area').hide();
        
        var event = thundashop.Ajax.createEvent(null, 'getCompanyInformation', this, {vatnumber: brregnumber});
        event['synchron'] = true;
        thundashop.Ajax.post(event, app.ProMeisterBosch.gotCompanyInformation, {}, true, true);
    },
    
    gotCompanyInformation: function(response) {
        $('.ProMeisterBosch table .companyinformation').html(response);
    }
};

app.ProMeisterBosch.init();