/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


app.CreateNewInstanceApplication = {
    init: function() {
        $(document).on('change', '.CreateNewInstanceApplication .selectApplication', app.CreateNewInstanceApplication.applicationSelected)
    },
    
    applicationSelected: function() {
        var data = {
            appId : $(this).val()
        }
        
        var event = thundashop.Ajax.createEvent(null, "saveApplicationSelected", this, data);
        thundashop.Ajax.post(event);
    }
}

app.CreateNewInstanceApplication.init();