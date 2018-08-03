/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


pga.page = {
    init: function() {
        $(document).on('click', '.searchbooking', pga.page.startPaymentProcess);
    },
    
    startPaymentProcess: function() {
        var data = {
            reference: $('.reference .inner').html()
        }
        pga.post(data);
    }
}

pga.page.init();