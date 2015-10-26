app.SalesPanel = {
    init : function() {
        $(document).on('click', '.SalesPanel .registercustomer', app.SalesPanel.registerCustomer);
        $(document).on('keyup', '.SalesPanel .searchbrregcustomer', app.SalesPanel.searchcustomer);
        $(document).on('click', '.SalesPanel .selectcustomer', app.SalesPanel.selectCustomer);
        $(document).on('click', '.SalesPanel .listcustomer', app.SalesPanel.listcustomerbtn);
        $(document).on('click', '.SalesPanel .registerevent', app.SalesPanel.registerevent);
        $(document).on('keyup', '.SalesPanel .searchcompany', app.SalesPanel.searchCompany);
        $(document).on('click', '.SalesPanel .editevent', app.SalesPanel.editEvent);
    },
    editEvent : function() {
        var event = thundashop.Ajax.createEvent('','editEvent',$(this), {
            eventId : $(this).attr('eventId')
        });
        thundashop.common.showInformationBox(event, 'Edit an event');
    },
    registerevent : function() {
        var event = thundashop.Ajax.createEvent('','registerEvent',$(this), {
            orgId : $("[gsname='orgid']").val()
        });
        thundashop.common.showInformationBox(event, 'Register event');
    },
    searchCompany : function(event) {
        if(event.keyCode !== 13) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','searchCustomer',$(this), {
            key : $(this).val()
        });
        
        thundashop.Ajax.postWithCallBack(event, function(result) {
            $('.companysearchresult').show();
            $('.companysearchresult').html(result);
        });
    },
    listcustomerbtn : function() {
        var id = $(this).attr('data-orgid');
        app.SalesPanel.listCustomer(id);
    },
    listCustomer : function(orgId) {
        var event = thundashop.Ajax.createEvent('','displayCustomer',$('.SalesPanel'), {
            orgId : orgId
        });
        thundashop.Ajax.postWithCallBack(event, function(data) {
            $('.companysearchresult').hide();
            $('.salesmainarea').html(data);
        });
    },
    
    selectCustomer : function() {
        var data = $(this).closest('.searchresultrow').attr('data-json');
        data = JSON.parse(data);
        var form = $('.newcustomerform');
        console.log(data);

        form.find('[gsname="orgid"]').val(data.orgnr);
        form.find('[gsname="name"]').val(data.navn);
        form.find('[gsname="streetAddress"]').val(data.forretningsadr);
        form.find('[gsname="phone"]').val(data.tlf_mobil);
        $('.searchresultrow').remove();
    },
    
    searchcustomer : function(event) {
        if(event.keyCode !== 13) {
            return;
        }
        var searchkey = $(this).val();
        
        $.ajax({
          url: "http://hotell.difi.no/api/jsonp/brreg/enhetsregisteret?query="+ searchkey,
          success: app.SalesPanel.loadResultFromBrreg,
          dataType: "jsonp"
        });
    },
    loadResultFromBrreg : function(result) {
        $('.brregresult').html('');
        for(var key in result.entries) {
            console.log(result.entries[key]);
            var resultentry = result.entries[key];
            $('.brregresult').append("<div class='searchresultrow' data-json='"+JSON.stringify(resultentry)+"'>" + resultentry.navn + " (" + resultentry.orgnr + ", " + resultentry.forretningsadr + ") <span class='selectcustomer'>Select</span></div>");
        }
    },
    
    registerCustomer : function() {
        var event = thundashop.Ajax.createEvent('','registerCustomer',$(this), {});
        thundashop.common.showInformationBox(event, 'Register customer');
    }
};

app.SalesPanel.init();