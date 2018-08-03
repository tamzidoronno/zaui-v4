/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


pga.page = {
    
    init: function() {
        $(document).on('click', '.setcheckoutdate', pga.page.setCheckoutDate);
        $(document).on('click', '.changecheckoutdate', pga.page.unsetCheckoutDate);
        $(document).on('click', '.selectroom', pga.page.selectRoom);
        $(document).on('click', '.addmorerooms', pga.page.addMoreRooms);
        $(document).on('click', '.deleteroom', pga.page.deleteRoom);
        $(document).on('click', '.guestcount', pga.page.selectGuestClick);
        $(document).on('click', '.guestcountselect', pga.page.guestCountSelect);
        $(document).on('click', '.gotooverview', pga.page.goToOverview);
        $(document).on('click', '.nextbutton', pga.page.goToCustomerInfo);
        $(document).on('click', '.startpayment', pga.page.startPayment);
        $(document).on('click', '.paymentcancel', pga.page.paymentCancel);
    },
    
    paymentCancel: function() {
        var data = {
            action: 'paymentCancel',
        }
        pga.post(data);
    },
    
    validateEmail: function (email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    },
    
    startPayment: function() {
        var data = {
            action: "startPayment",
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
    
    goToCustomerInfo: function() {
        var data = {
            action: 'goToCustomerInfo',
        }
        pga.post(data);
    },
    
    guestCountSelect: function() {
        var data = {
            action: 'changeGuestCount',
            roomid: $(this).attr('roomid'),
            count: $(this).attr('count')
        }
        pga.post(data);
    },
    
    selectGuestClick: function() {
        $('.guestcountselector').hide();
        $(this).closest('.roomrow').find('.guestcountselector').show();
    },
    
    deleteRoom: function() {
        var data = {
            action: 'deleteRoom',
            roomid: $(this).attr('roomid')
        }
        pga.post(data);
    },
    
    addMoreRooms: function() {
        var data = {
            action: 'addmorerooms'
        }
        pga.post(data);
    },
    
    goToOverview: function() {
        var data = {
            action: 'gotooverview'
        }
        pga.post(data);
    },
    
    selectRoom: function() {
        var data = {
            typeid: $(this).attr('typeid'),
            action: 'selectRoom'
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
    },
    
    unsetCheckoutDate: function() {
        var data = {
            action: 'unsetCheckoutDate'
        }
        pga.post(data);
    },
    
    showPhotoSwipe: function(id) {
        var pswpElement = document.querySelectorAll('.pswp')[0];
        var options = { index: 0 };
        var gallery = new PhotoSwipe( pswpElement, PhotoSwipeUI_Default, images[id], options);
        gallery.init();
    },
}

pga.page.init();