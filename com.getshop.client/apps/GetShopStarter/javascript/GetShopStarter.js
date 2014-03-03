app.GetShopStarter = {
    clicked : false,
    register : function() {
        if(app.GetShopStarter.clicked) {
            return;
        }
        app.GetShopStarter.clicked = true;
        var name = $(this).closest('.app').find('.name').val();
        var email = $(this).closest('.app').find('.email').val();
        var password = $(this).closest('.app').find('.password').val();

        $(this).closest('.app').find(".allfields").hide();
        if(!password || !name || !email || email.indexOf("@") === -1) {
            $(this).closest('.app').find(".allfields").slideDown();
            app.GetShopStarter.clicked=false;
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','createStore',$(this), {
           "name" : name,
           "email" : email,
           "password" : password
        });
        $(this).find('.fa').removeClass('fa-arrow-right');
        $(this).find('.fa').addClass('fa-spinner');
        $(this).find('.fa').addClass('fa-spin');
        $(this).find('.text').html(__w("Building your store"));
        thundashop.Ajax.postWithCallBack(event, function(data) {
            debugger;
            document.location.href=data;
        });
    },
    init : function() {
        $(document).on('click','.GetShopStarter .start_button', app.GetShopStarter.register);
    }
}

app.GetShopStarter.init();