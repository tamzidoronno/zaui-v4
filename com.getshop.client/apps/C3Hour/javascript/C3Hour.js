app.C3Hour = {
    init: function() {
        $(document).on('click', '.C3Hour .tab', app.C3Hour.changeHour);
        $(document).on('change', '.C3Hour .hourrate', app.C3Hour.calculateSum);
        $(document).on('keyup', '.C3Hour .timehours', app.C3Hour.calculateSum);
    },
    
    formatMoney : function(n, d, t){
        return n.toFixed(0).replace(/./g, function(c, i, a) {
            return i && c !== "," && ((a.length - i) % 3 === 0) ? ' ' + c : c;
        });
    },
 
    calculateSum: function() {
        var hours = parseInt($('.C3Hour .timehours').val());
        if (hours) {
            var selected = parseInt($('.C3Hour .hourrate').find(":selected").attr('rate'));
            var sum = hours * selected;
            var formattedSum = app.C3Hour.formatMoney(sum, '.', ',');
            $('.C3Hour .sum').html(formattedSum);
        } else {
            $('.C3Hour .sum').html("*Kun heltall er gyldig for timer");
        }
        
        
    },
    
    changeHour: function() {
        var showTab = $(this).attr('content');
        
        $('.C3Hour .tab.active').removeClass('active');
        $(this).addClass('active');
        
        $('.C3Hour .tabcontent').hide();
        $('.C3Hour .tabcontent[content="'+showTab+'"]').show();
    }
}

app.C3Hour.init();