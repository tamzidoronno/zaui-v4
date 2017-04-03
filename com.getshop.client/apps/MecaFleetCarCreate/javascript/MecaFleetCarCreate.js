app.MecaFleetCarCreate = {
    init:function(){
        $(document).on('click', '.MecaFleetCarCreate .sendKilomterRequest', app.MecaFleetCarCreate.sendKilomterRequest);
    },
    sendKilomterRequest: function(){
        var ownerid = $(this).attr('ownerid')
        if(!ownerid){ownerid = "Eieren";}
        var regnr = $(this).attr('regnr')
        thundashop.common.Alert('Kilometerforespørsel er sendt til:', ownerid + " med reg nr: " + regnr , false);
    }
    
};
app.MecaFleetCarCreate.init();