app.PmsInvoicing = {
    
    init: function() {
        $(document).on('click', '.PmsInvoicing .startpaymentprocess', app.PmsInvoicing.startCheckoutProcess);
        $(document).on('click', '.PmsInvoicing .headercheckbox', app.PmsInvoicing.toggleCheckBoxes);
        
        document.addEventListener('scroll', app.PmsInvoicing.scrollHeader, true);
    },
    
    toggleCheckBoxes: function() {
        var isChecked = $(this).prop('checked');
        var checkBoxes = $(this).closest('.usercollection').find('.roomcheckbox');
        checkBoxes.prop("checked", isChecked);
    },
    
    scrollHeader: function(event) {
        console.log(window.pageYOffset);
        var top = 100 - window.pageYOffset;
        console.log(top);
        if (top < -135) {
            top = -135;
        }
        
        $('.PmsInvoicing .toprow').css('top', top+"px");
    },
    
    startCheckoutProcess : function() {
        var rooms = [];
        
        $('.PmsInvoicing .roomcheckbox:checked').each(function() {
            rooms.push($(this).val());
        });
        
        if (rooms.length == 0) {
            alert("Please select atleast one booking below");
            return;
        }
        
        thundashop.framework.loadAppInOverLay("af54ced1-4e2d-444f-b733-897c1542b5a8", "3", { pmsBookingRoomId : rooms, state: 'clear', skiproomselection: true}); 
    },
    
    refresh: function() {
        thundashop.Ajax.reloadApp('6f51a352-a5ee-45ca-a8e2-e187ad1c02a5', false);
    }
}

app.PmsInvoicing.init();