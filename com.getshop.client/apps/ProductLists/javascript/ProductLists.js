app.ProductLists = {
    init: function() {
        $(document).on('click', '.ProductLists .setproductlist', app.ProductLists.setList);
    },
    
    setList: function() {
        var event = thundashop.Ajax.createEvent(null, "setList", this, $(this).attr('listid'));
        thundashop.Ajax.post(event);
    }
}

app.ProductLists.init();