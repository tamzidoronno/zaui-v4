app.AutoTekAssistSaving = {
    init: function() {
        $(document).on('change', '.AutoTekAssistSaving .assistrow input', app.AutoTekAssistSaving.calc);
        $(document).on('click', '.AutoTekAssistSaving .fa-minus', app.AutoTekAssistSaving.minus);
        $(document).on('click', '.AutoTekAssistSaving .fa-plus', app.AutoTekAssistSaving.plus);
    },
    
    minus: function() {
        var input = $(this).closest('.assistrow').find('input');
        var val = input.val();
        val--;
        if (val < 1) {
            return;
        }
        
        input.val(val);
        app.AutoTekAssistSaving.calc();
    },
    
    plus: function() {
        var input = $(this).closest('.assistrow').find('input');
        var val = input.val();
        val++;
        
        input.val(val);
        app.AutoTekAssistSaving.calc();
    },
    
    calc : function() {
        var hours = $('#assist_hours').val();
        var days = $('#assist_days').val();
        var totalHours = (hours+days)*48;
        $('.AutoTekAssistSaving .assist_total_hours').html(totalHours);
        var price = totalHours * $('#assist_hourprice').val();
        $('.AutoTekAssistSaving .price_sum').html(price);
    }
};

app.AutoTekAssistSaving.init();