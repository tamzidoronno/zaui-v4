app.SedoxPartners = {
    init: function() {
        $(document).on('click', '.SedoxPartners .go_to_slave_credit', app.SedoxPartners.goToSlave);
        $(document).on('click', '.SedoxPartners .creditinformation .sedox_blue_button', app.SedoxPartners.transferCredits);
    },
    
    goToSlave: function() {
        var slaveId = $(this).attr('slaveId');
        thundashop.common.goToPage("slavecredits&slaveid=" + slaveId);
    },
    
    transferCredits: function() {
        var clickedElement = $(this);
        
        var data = {
            slaveid: $(this).attr("slaveId"),
            amount: $(this).parent().find('.credit_transfer_input input').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "transferCredit", this, data);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            clickedElement.parent().find('.credit_transfer_input input').val("");
        });
    }
};

app.SedoxPartners.init();