/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.WilhemsenHouseWashingOrder = {
    init: function() {
        $(document).on('click', '.WilhemsenHouseWashingOrder .orderwash', app.WilhemsenHouseWashingOrder.orderWash);
    },
    
    orderWash: function() {
        var room = $(this).closest('.app').find('.washingroomnumber').val();
        if (!room) {
            alert(__w("Please enter your room number"));
            return;
        }
        
        var data = {
            room : room
        };
        
        var event = thundashop.Ajax.createEvent("", "canOrderWash", this, data);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            if (!result) {
                alert(__w("Please check your room number, you can only order wash while you are staying at the hotel"));
            } else {
                alert("Succcess: " + result);
            }
        });
        
    }
};

app.WilhemsenHouseWashingOrder.init();
