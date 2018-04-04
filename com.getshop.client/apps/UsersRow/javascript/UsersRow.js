app.UsersRow = {
    init : function() {
        $(document).on('click','.UsersRow .menuentry', app.UsersRow.loadArea);
    },
    loadArea : function() {
        var area = $(this).attr('area');
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','changeArea', btn, {
            "area" : area
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.UsersRow .mainarea').html(res);
            $('.selectedmenuentry').removeClass('selectedmenuentry');
            btn.addClass('selectedmenuentry');
        });
    }
}
app.UsersRow.init();