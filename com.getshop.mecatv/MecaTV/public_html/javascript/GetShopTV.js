GetShopTV = {
    tvId : "83897b07-6ef1-41e0-8a06-3061b1a6fbe8",
    tvData : null,
    sliders : [],
    i : 0,
    
    init: function() {
        GetShopTV.api = new GetShopApiWebSocket('tv.2.0.local.getshop.com');
        GetShopTV.api.setConnectedEvent(GetShopTV.connectionEstablished);
        GetShopTV.api.connect();
    },
    
    connectionEstablished: function() {
        GetShopTV.api.InformationScreenManager.getScreen(GetShopTV.tvId).done(function(result) {
            GetShopTV.tvData = result;
            GetShopTV.buildSliders();
        });
    },
    
    showWarning: function(warning) {
        alert(warning);
    },
    
    buildSliders: function() {
        var data = GetShopTV.tvData;
        if (!data.sliders) {
            GetShopTV.showWarning('No sliders added, please login and add one or more sliders');
            return;
        } 
        
        var i = 1;
        for (var sliderId in data.sliders) {
            var slider = data.sliders[sliderId];
            GetShopTV.createSlide(slider, i);
            i++;
        }
        
        GetShopTV.next();
    },
    
    next: function() {
        $('.animationslide').hide();
        if (GetShopTV.i == GetShopTV.sliders.length) {
            GetShopTV.i = 0;
        }
        
        var sliderName = GetShopTV.sliders[GetShopTV.i];
        MecaCard.reset(sliderName);
        GetShopTV.i++;
    },
    
    createSlide: function(slideData, i) {
        var slideName = MecaCard.createSlide(slideData, i);   
        GetShopTV.sliders.push(slideName);
    }
};

$(document).ready(function() {
    GetShopTV.init();
});