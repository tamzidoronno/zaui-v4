app.CrmCustomerView = {
    init : function() {
        $(document).on('click','.CrmCustomerView .crmmenuentry', app.CrmCustomerView.loadArea);
        $(document).on('click','.CrmCustomerView .paymentmethodbtn', app.CrmCustomerView.toggleButton);
        $(document).on('click','.CrmCustomerView .docreatenewcode', app.CrmCustomerView.doCreateNewDicountCode);
        $(document).on('click','.CrmCustomerView .attacheddiscountcode', app.CrmCustomerView.changePrintedDiscountCode);
        $(document).on('click','.CrmCustomerView .savediscountcustomer', app.CrmCustomerView.saveDiscountForm);
        $(document).on('change','.CrmCustomerView [gsname="attachedDiscountCode"]', app.CrmCustomerView.changeDiscountSystem);
    },
    saveDiscountForm : function() {
        var form = $(this).closest('[gstype="form"]');
        var args = thundashop.framework.createGsArgs(form);
        var event = thundashop.Ajax.createEvent('',form.attr('method'), form, args);
        
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.CrmCustomerView.saveSuccess();
        });
    },
    saveSuccess : function() {
        thundashop.common.Alert('Saved');
    },
    removeDiscountCode : function(res) {
        $('.attacheddiscountcode[code="'+res.code+'"]').remove();
        $('.chosendiscountcode').removeClass('chosendiscountcode');
        $('.attacheddiscountcode[code="'+res.primary+'"]').addClass('chosendiscountcode');
        if(!res.primary) {
            $('.newdiscountsystem').hide();
            $('.olddiscountsystem').show();
        } else {
            $('.primarydiscountcode').removeClass('primarydiscountcode');
            $('.attacheddiscountcode[code="'+res.primary+'"]').addClass('primarydiscountcode');
            app.CrmCustomerView.changeDiscountSystem();
        }
    },
    primarySet : function(code) {
        $('.primarydiscountcode').removeClass('primarydiscountcode');
        $('.attacheddiscountcode[code="'+code+'"]').addClass('primarydiscountcode');
    },
    loadCorrectDiscountSystem : function() {
        if($('.chosendiscountcode:visible').length > 0) {
            app.CrmCustomerView.changeDiscountSystem();
        }
    },
    changePrintedDiscountCode : function(res) {
        $('.chosendiscountcode').removeClass('chosendiscountcode');
        $(this).addClass('chosendiscountcode');
        app.CrmCustomerView.changeDiscountSystem();
    },
    codeAdded : function(res) {
        $('.chosencodearea').find('.chosendiscountcode').removeClass('chosendiscountcode');
        $('.chosencodearea').append(res);
        app.CrmCustomerView.changeDiscountSystem();
        $('.connectdiscountcodedropdown').val('');
    },
    
    changeDiscountSystem : function() {
        var code = $('.chosendiscountcode').attr('code');
        var event = thundashop.Ajax.createEvent('','loadDiscountCode',$('.chosendiscountcode'), {
            "code" : code
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.discountcodesystemconfig').html(res);
        });
        $('.newdiscountsystem').show();
        $('.olddiscountsystem').hide();
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
        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.CrmCustomerView.codeAdded(res);
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