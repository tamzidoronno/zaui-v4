app.SrsFoodList = {
    updated: function(res) {
        $('.SrsFoodList .summary').html(res);
    },
    
    personNumberChanged: function(res) {
        var prod = $(res)[0];
        var summary = $(res)[1];
        
        $('.SrsFoodList .foodlist').html(prod);
        $('.SrsFoodList .summary').html(summary);
    }
}