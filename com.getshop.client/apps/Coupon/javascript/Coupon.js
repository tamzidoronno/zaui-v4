/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Coupon = {
    init: function() {
        $(document).on('click', '.Coupon .remove',  this.removeCoupon);
        $(document).on('click', '.Coupon .savecoupons',  this.saveCoupons);
        $(document).on('click', '.leftcart #applycoupon',  this.applyCoupon);
    },
            
    saveCoupons: function() {
        var tabset = $(this).closest('.Coupon').find('.tabs');
        var data = {};
        
        tabset.find('.content_holder.active').each(function() {
            var id = $(this).attr('id');
            var content = {};
            $(this).find('input').each(function() {
                var name = $(this).attr('name');
                var value = $(this).val();
                content[name] = value;
            });
            data[id] = content;
        });
        
        Coupon.save(data, this);
    },
    
    save : function(data, from) {
        var event = thundashop.Ajax.createEvent(null, "save", from, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, Coupon.update, {}, true);
    },
            
    applyCoupon: function() {
        var code = $(this).closest('.leftcart').find('.coupon input').val();
        var event = thundashop.Ajax.createEvent(null, "applyCode", this,  { code : code });
        thundashop.Ajax.post(event);
    },
            
    update: function(response) {
        if (response === "BlankCodeField") {
            thundashop.common.Alert(__f("Error"), __f("The Coupon name can not be blank."), true);
        } else if (response === "NotValidPercentage") {
            thundashop.common.Alert(__f("Error"), __f("The percentage must be between 0 and 100"), true);
        } else if (response === "NotInteger") {
            thundashop.common.Alert(__f("Error"), __f("The discount can not be 0 or the amount of times coupons can be used can not be 0"), true);
        } else {
            $('#settingsarea').html(response);
        }
    },
            
    removeCoupon: function() {
        var code = $(this).closest('tr').find('.code').text();
        var event = thundashop.Ajax.createEvent(null, "deleteCode", this,  { code : code });
        event['synchron'] = true;
        thundashop.Ajax.post(event, Coupon.update, {}, true);
    }
};

Coupon.init();