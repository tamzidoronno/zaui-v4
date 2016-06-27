 app.EventEditorBooking = {
     init: function() {
         $(document).on('keyup', '.EventEditorBooking .searchforuserinput', app.EventEditorBooking.searchForUsers);
     },
     
     searchForUsers: function(keycode) {
         if (keycode.keyCode !== 13)
             return;
         
         var event = thundashop.Ajax.createEvent(null, "searchForUsers", this, {
             searchstring: $(this).val()
         });
         
         event['synchron'] = true;
         thundashop.Ajax.post(event, app.EventEditorBooking.searched,null, true);
     },
     
     searched: function(res) {
         $('.EventEditorBooking .usersearchresults').html(res);
     }
 };
 
 app.EventEditorBooking.init();