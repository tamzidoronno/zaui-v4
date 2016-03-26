/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


getshop.Modal = {
    init: function() {
        PubSub.subscribe("EVENT_POST_NAV_ACTION", getshop.Modal.removeClassIfNeeded);
    },
    
    removeClassIfNeeded: function(topic, data) {
        if (data != null && data.event != null && (data.event === "closemodal" || data.event === "closeModal")) {
            $('#gsbody').removeClass('gs_modalIsOpen');
        }
    }
};

getshop.Modal.init();