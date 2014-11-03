/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


app.CalendarFilter =  {
    init: function() {
        $(document).on('keyup', '#vatnumber', app.CalendarFilter.doSearch);
    },
            
    doSearch: function() {
        
        var vatNumber = $('#vatnumber').val();
        
        if (vatNumber.length != 9 && vatNumber.length != 10 && vatNumber.length != 0 ) {
            return;
        }
        var data = {
            vatNumber: vatNumber
        };
        
        var event = thundashop.Ajax.createEvent(null, "showSearchResult", this, data);
        var data = thundashop.Ajax.postSynchron(event);
        $('#calendar_filter_search_result').html(data);
    }
};

app.CalendarFilter.init();