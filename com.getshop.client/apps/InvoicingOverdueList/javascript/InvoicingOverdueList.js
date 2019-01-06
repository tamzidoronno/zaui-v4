app.InvoicingOverdueList = {
    prevStart: 0,
    prevEnd: 99999,
    lastClicked: null, 
    
    init: function() {
        $(document).on('click', '.InvoicingOverdueList .innerbox', app.InvoicingOverdueList.filterList)
    }, 
    
    filterList: function() {
        app.InvoicingOverdueList.lastClicked = this;
        
        sessionStorage.setItem('InvoicingOverdueListLastClicked', $(this).attr('range'));
        
        var range = $(this).attr('range');
        app.InvoicingOverdueList.filterListByRange(range);
    },
    
    filterListByRange: function(range) {
        var start = 0;
        var end = 999999;
        
        if (range !== "all") {
            var ranges = range.split(',');
            start = ranges[0];
            end = ranges[1];
        }
        
        $('.InvoicingOverdueList .innerbox.active').removeClass('active');
        
        if (app.InvoicingOverdueList.prevEnd === end && app.InvoicingOverdueList.prevStart === start) {
            $('.duerow').show();
            app.InvoicingOverdueList.prevStart = 0;
            app.InvoicingOverdueList.prevEnd = 99999;
            sessionStorage.removeItem('InvoicingOverdueListLastClicked', null)
            return;
        }
        
        $('.InvoicingOverdueList .innerbox[range="'+range+'"]').addClass('active');
        
        $('.duerow').each(function() {
            if ($(this).hasClass('header'))
                return;
            
            var days = parseInt($(this).attr('duedays'),10);
            if (start <= days && days < end) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
        
        app.InvoicingOverdueList.prevStart = start;
        app.InvoicingOverdueList.prevEnd = end;
    },
    
    listLoaded: function() {
        if (sessionStorage.getItem('InvoicingOverdueListLastClicked')) {
            app.InvoicingOverdueList.filterListByRange(sessionStorage.getItem('InvoicingOverdueListLastClicked'));
        }
    }
}

app.InvoicingOverdueList.init();