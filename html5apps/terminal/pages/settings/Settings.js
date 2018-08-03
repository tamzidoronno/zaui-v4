/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


pga.page = {
    init: function() {
        $(document).on('click', '.button.save', pga.page.saveData);
        $(document).on('click', '.button.enterpassword', pga.page.postPassword);
    },
    
    postPassword: function() {
        var data = {
            action: 'updatePassword',
            password: $('#password .inner').html()
        };
        
        pga.post(data);
    },
    
    saveData: function() {
        var data = {
            action: 'saveData',
            multilevelname: $('#multilevelname .inner').html(),
            terminalid: $('#terminalid .inner').html()
        };
        
        pga.post(data);
    }
};

pga.page.init();