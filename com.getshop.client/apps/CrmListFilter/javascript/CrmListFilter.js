app.CrmListFilter = {
    init : function() {
        $(document).on('click','.CrmListFilter .newcustomertypebutton', app.CrmListFilter.loadNewCustomerType);
        $(document).on('click','.CrmListFilter .newcustomerbutton', app.CrmListFilter.loadNewCustomerField);
        $(document).on('click','.CrmListFilter .searchbrregbutton', app.CrmListFilter.showBrregSearch);        
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
    searchResult : function(res) {
        if(res) {
            $('[gsname="vatnumber"]').val(res.vatNumber);
            $('[gsname="name"]').val(res.name);
            $('.searchbrregarea').hide();
        }
    },
};
app.CrmListFilter.init();