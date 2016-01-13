app.SedoxPartners = {
    init: function() {
        $(document).on('click', '.go_to_slave_credit', app.SedoxPartners.goToSlave);
    },
    
    goToSlave: function() {
        var slaveId = $(this).attr('slaveId');
        thundashop.common.goToPage("slavecredits&slaveid=" + slaveId);
    }
};

app.SedoxPartners.init();