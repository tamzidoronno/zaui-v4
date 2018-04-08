app.CrmListFilter = {
    init : function() {
        $(document).on('click','.CrmListFilter .newcustomertypebutton', app.CrmListFilter.loadNewCustomerType);
        $(document).on('click','.CrmListFilter .newcustomerbutton', app.CrmListFilter.loadNewCustomerField);
    },
    loadNewCustomerField : function() {
        $('.companytypeselection').slideDown();
        $('.nextstep').slideUp();
    },
    loadNewCustomerType : function() {
        $('.companytypeselection').slideUp();
        var type = $(this).attr('type');
        $('.'+type).slideDown();
    }
};
app.CrmListFilter.init();