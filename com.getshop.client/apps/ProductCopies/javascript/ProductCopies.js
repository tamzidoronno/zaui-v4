app.ProductCopies = {
    init: function() {
        $(document).on('click', '.ProductCopies .plus', app.ProductCopies.plus);
        $(document).on('click', '.ProductCopies .minus', app.ProductCopies.minus);
    },
    
    minus: function() {
        var input = $(this).closest('.ProductCopies').find('input');
        var invar = parseInt(input.val());
        invar--;
        if (invar < 1) {
            invar = 1;
        }
        
        input.val(invar);
    },
    
    plus: function() {
        var input = $(this).closest('.ProductCopies').find('input');
        var invar = parseInt(input.val());
        invar++;
        
        input.val(invar);
    }
};

app.ProductCopies.init();