/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


pga.page = {
    init: function() {
        $(document).on('click', '.setcheckoutdate', pga.page.setCheckoutDate);
        $(document).on('click', '.changecheckoutdate', pga.page.unsetCheckoutDate);
        $(document).on('click', '.bookingitem.selectable', pga.page.selectBookingItem);
        $(document).on('click', '.startpayment', pga.page.completeBooking);
    },
    
    validateEmail: function (email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    },
    
    completeBooking: function() {
        var data = {
            action: "completeBooking",
            name: $('#name .inner').html(),
            prefix: $('#prefix .inner').html(),
            email: $('#email .inner').html(),
            phone: $('#phone .inner').html()
        }
        
        
        if (!data.name) {
            $('#name').addClass('error');
            return;
        }
        
        if (!pga.page.validateEmail(data.email)) {
            $('#email').addClass('error');
            return;
        }
        
        if (!data.phone) {
            $('#phone').addClass('error');
            return;
        }
        
        pga.post(data);
    },
    
    selectBookingItem: function() {
        var data = {
            action: 'toggleItem',
            itemid: $(this).attr('itemid')
        }
        pga.post(data);
    },
    
    unsetCheckoutDate: function() {
        var data = {
            action: 'unsetCheckoutDate'
        }
        pga.post(data);
    },
    
    setCheckoutDate: function() {
        var data = {
            checkout: $('#checkoutdate').val(),
            action: 'setCheckoutDate',
            discountcode: $('#discountcode .inner').html()
        }
        pga.post(data);
    }
}

pga.page.init();