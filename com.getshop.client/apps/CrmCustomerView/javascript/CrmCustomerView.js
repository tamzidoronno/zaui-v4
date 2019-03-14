app.CrmCustomerView = {
    init : function() {
        $(document).on('click','.CrmCustomerView .crmmenuentry', app.CrmCustomerView.loadArea);
        $(document).on('click','.CrmCustomerView .paymentmethodbtn', app.CrmCustomerView.toggleButton);
        $(document).on('click','.CrmCustomerView .docreatenewcode', app.CrmCustomerView.doCreateNewDicountCode);
        $(document).on('change','.CrmCustomerView [gsname="attachedDiscountCode"]', app.CrmCustomerView.changeDiscountSystem);
    },
    saveSuccess : function() {
        thundashop.common.Alert('Saved');
    },
    loadCorrectDiscountSystem : function() {
        $("[gsname='attachedDiscountCode']").each(function() {
            if($(this).is(':visible')) {
                $(this).change();
            }
        });
    },
    changeDiscountSystem : function() {
        if($(this).val() !== "") {
            var event = thundashop.Ajax.createEvent('','loadDiscountCode',$(this), {
                "code" : $(this).val()
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.discountcodesystemconfig').html(res);
            });
            $('.newdiscountsystem').show();
            $('.olddiscountsystem').hide();
        } else {
            $('.olddiscountsystem').show();
            $('.newdiscountsystem').hide();
        }
    },
    doCreateNewDicountCode : function() {
        var suggestion = $(this).attr('suggestion');
        var code = prompt("Enter the new code", suggestion);
        if(!code) {
            return;
        }
        var event = thundashop.Ajax.createEvent('','createDiscountCode',$(this), {
            "code" : code
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            $("[gsname='attachedDiscountCode']").append("<option value='" + code + "'>" + code + "</option>");
             $("[gsname='attachedDiscountCode']").val(code);
             app.CrmCustomerView.loadCorrectDiscountSystem();
        });
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
    },
    toggleButton : function() {
        if($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            $(this).attr('gsvalue','false');
        } else {
            $(this).addClass('selected');
            $(this).attr('gsvalue','true');
        }
    }
}
app.CrmCustomerView.init();