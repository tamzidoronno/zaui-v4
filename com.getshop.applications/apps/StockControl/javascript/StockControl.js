/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

StockControl = {
    init: function() {
        PubSub.subscribe("setting_switch_toggled", this.onOffChanged, this);
    },
    
    deactivateReturnStockStatus: function() {
        $("#returnStockIfOrderCanceled").find('.onoff').addClass('off');
        $("#returnStockIfOrderCanceled").find('.onoff').removeClass('on');
    },
    
    onOffChanged: function(msg, data) {
        if (data.id == "trackControl" && data.state == "off") {
            this.deactivateReturnStockStatus();
        }
        
        if (data.id == "returnStockIfOrderCanceled" && data.state == "on") {
            if ($("#trackControl").find('.onoff').hasClass("off")) {
                this.deactivateReturnStockStatus();
                thundashop.common.Alert(__f("Not possible"), __f("It is not possible to activate this option with the current settings."), true);
            }
        }
    }
}

StockControl.init();