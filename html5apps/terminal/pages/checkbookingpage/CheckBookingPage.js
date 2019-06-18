/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


pga.page = {
    init: function() {
        $(document).on('click', '.reprintcode', pga.page.reprintCode);
    },
    
    reprintCode: function() {
        var data = {
            reference: $('.reference .inner').html(),
            bookingid : $('#bookingid').val(),
            roomid : $('#roomid').val(),
            printcode : true
        }
        pga.post(data);
    }
}

pga.page.init();