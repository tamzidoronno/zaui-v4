app.CrmListFilter = {
    init : function() {
        $(document).on('click','.CrmListFilter .newcustomertypebutton', app.CrmListFilter.loadNewCustomerType);
        $(document).on('click','.CrmListFilter .newcustomerbutton', app.CrmListFilter.loadNewCustomerField);
        $(document).on('click','.CrmListFilter .searchbrregbutton', app.CrmListFilter.showBrregSearch);        
        $(document).on('click','.CrmListFilter .brregsearchresultrow', app.CrmListFilter.selectBrregResult);
    },
    loadNewCustomerField : function() {
        $('.companytypeselection').slideDown();
        $('.nextstep').slideUp();
    },
    loadNewCustomerType : function() {
        $('.companytypeselection').slideUp();
        var type = $(this).attr('type');
        $('.'+type).slideDown();
    },
    showBrregSearch : function() {
        $('.searchbrregarea').slideDown();
    },
    selectBrregResult : function() {
        var name = $(this).attr('name');
        var vatnumber = $(this).attr('vatnumber');
        $('[gsname="vatnumber"]').val(vatnumber);
        $('[gsname="name"]').val(name);
        $('.searchbrregarea').hide();
    },
    searchResult : function(res) {
        if(res) {
            $('.brregsearchresult').html('');
            for(var k in res) {
                var company = res[k];
                var row = $('<div class="brregsearchresultrow" vatnumber="'+company.vatNumber+'" name="'+company.name+'"><span>' + company.vatNumber + "</span> - <span>" + company.name + "</span></div>");
                $('.brregsearchresult').append(row);
            }
        }
    }
};
app.CrmListFilter.init();