var lasttimereason = "";
app.GetshhopInvetory = {
    init : function() {
        $(document).on('keyup','.searchinventory', app.GetshhopInvetory.search);
        $(document).on('click','.updateInventoryCount', app.GetshhopInvetory.updateInventoryCount);
    },
    updateInventoryCount : function() {
        var remove = $(this).hasClass('removefromstorage');
        var count = 0;
        if(remove) {
            count = prompt("Number of items removed from storage.");
            var comment = prompt("Why do you remove this from inventory?", lasttimereason);
            lasttimereason = comment;
            count *= -1;
        } else {
            count = prompt("Number of items added to storage.");
        }
        if(!count) {
            return;
        }
         
        var event = thundashop.Ajax.createEvent('','updateInventoryCount',$(this), {
            "count" : count,
            "comment" : comment,
            "productid" : $(this).closest('tr').attr('productid'),
            "remove" : remove
        });
        thundashop.Ajax.post(event);
    },
    search : function() {
        var val = $(this).val().toLowerCase();
        $('.productrow').each(function() {
            var txtinrow = $(this).text();
            txtinrow = txtinrow.toLowerCase();
            if(txtinrow.indexOf(val) >= 0) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    } 
}

app.GetshhopInvetory.init();