app.GetShopPriceModel = {
    init : function() {
        $(document).on('keyup','.GetShopPriceModel .inputvalue',app.GetShopPriceModel.calculate);
        $(document).on('click','.GetShopPriceModel .inputvalue',app.GetShopPriceModel.calculate);
    },
    calculate : function() {
        var rooms = parseInt($('.rooms').val());
        var locks = parseInt($('.locks').val());
        var entrancelocks = $('.entrancelocks').val();
        var selfcheckinindoor = $('.selfcheckinindoor').val();
        var selfcheckinoutdoor = $('.selfcheckinoutdoor').val();
        var pgas = $('.pgas').val();
        var customwebsite = $('.customwebsite').is(':checked');
        var integrationtoaccounting = $('.integrationtoaccounting').is(':checked');
        var getshopdosetup = $('.getshopdosetup').is(':checked');
        var getshopinstalllocks = $('.getshopinstalllocks').is(':checked');
        var getshoptraining = $('.getshoptraining').is(':checked');
        
        app.GetShopPriceModel.logRequest(rooms, locks, entrancelocks, selfcheckinindoor, selfcheckinoutdoor, pgas, customwebsite, integrationtoaccounting, getshopdosetup, getshopinstalllocks, getshoptraining, this);
        
        var roomLicense = 4.49;
        var lockLicense = 3.72;
        var accountinglicense = 128.36;
        var lockPrice = 353.04;
        var websitePrice = 5777.10;
        var bookingPrice = 5777.10;
        var terminalIndoorPrice = 7450.10;
        var terminalOutdoorPrice = 12588.10;
        var trainingProgramPrice = 1156;
        var installLockPrice = 115.6;
        var mainEntrancePrice = 1027;
        var pgaPrice = 257;
        var repeaterPrice = 64.22;
        var serverPrice = 192.81;
        var integrationToAccountingPrice = 3083.04;
        
        var totalMonthly = 0.0;
        totalMonthly += (roomLicense * rooms);
        totalMonthly += (lockLicense * locks);
        if(integrationtoaccounting) {
            totalMonthly += accountinglicense;
        }
        if(totalMonthly < 65) {
            totalMonthly = 65;
        }
        $('.totalmonthlycost').html(Math.round(totalMonthly));
        
        var totalSetupCost = 0;
        totalSetupCost += (lockPrice * locks);
        totalSetupCost += (terminalIndoorPrice * selfcheckinindoor);
        totalSetupCost += (terminalOutdoorPrice * selfcheckinoutdoor);
        totalSetupCost += (pgas * pgaPrice);
        totalSetupCost += (entrancelocks * mainEntrancePrice);
        if(getshopinstalllocks) {
            totalSetupCost += (locks * installLockPrice);
        }
        if(customwebsite) { totalSetupCost += websitePrice; }
        if(getshopdosetup) { totalSetupCost += bookingPrice; }
        if(getshoptraining) { totalSetupCost += trainingProgramPrice; }
        if(integrationtoaccounting) { totalSetupCost += integrationToAccountingPrice; }
        
        var repeaters = 0;
        var servers = 0;
        if(locks > 0) {
            repeaters = locks / 6;
            servers = locks / 30;
            repeaters = Math.round(repeaters);
            if(servers < 1) { servers = 1; }
            totalSetupCost += (repeaters * repeaterPrice);
            totalSetupCost += (servers * serverPrice);
        }
        $('.totalstartupcost').html(Math.round(totalSetupCost));
        $('.numberofservers').html(servers);
        $('.repeaters').html(repeaters);
        
    }, 
    
    logRequest: function(rooms, locks, entrancelocks, selfcheckinindoor, selfcheckinoutdoor, pgas, customwebsite, integrationtoaccounting, getshopdosetup, getshopinstalllocks, getshoptraining, from) {
        var data = {
            'rooms': rooms, 
            'locks': locks, 
            'entrancelocks': entrancelocks, 
            'selfcheckinindoor': selfcheckinindoor, 
            'selfcheckinoutdoor': selfcheckinoutdoor, 
            'pgas': pgas, 
            'customwebsite': customwebsite, 
            'integrationtoaccounting': integrationtoaccounting, 
            'getshopdosetup': getshopdosetup, 
            'getshopinstalllocks': getshopinstalllocks, 
            'getshoptraining': getshoptraining
        }
        
        var event = thundashop.Ajax.createEvent(null, 'log', from, data);
        thundashop.Ajax.post(event, null, null, true, true);
    }
};

app.GetShopPriceModel.init();