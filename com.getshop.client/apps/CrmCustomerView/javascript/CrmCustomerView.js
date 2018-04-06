app.CrmCustomerView = {
    init : function() {
        $(document).on('click','.CrmCustomerView .crmmenuentry', app.CrmCustomerView.loadArea);
    },
    loadArea : function() {
        var area = $(this).attr('area');
        var btn = $(this);
        var event = thundashop.Ajax.createEvent('','changeArea', btn, {
            "area" : area
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.CrmCustomerView .mainarea').html(res);
            $('.selectedmenuentry').removeClass('selectedmenuentry');
            btn.addClass('selectedmenuentry');
        });
    },
    success : function() {
        thundashop.common.Alert("Success", "Values has been updated");
        app.CrmCustomerView.refresh();
    },
    refresh : function() {
        var event = thundashop.Ajax.createEvent('','refresh', $('.CrmCustomerView .mainarea'), {});
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.CrmCustomerView .mainarea').html(res);
        });
    },
    deleteCard : function() {
        var confirmed = confirm("Are you sure you want to remove this card? This action can not be reverted");
        if(!confirmed) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','deleteCard', $(this), {
            "userid" : $(this).attr('userid'),
            "cardid" : $(this).attr('cardid')
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.cardlist').html(res);
        });
    }
}
app.CrmCustomerView.init();