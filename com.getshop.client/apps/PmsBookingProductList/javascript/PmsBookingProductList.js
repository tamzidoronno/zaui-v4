app.PmsBookingProductList = {
    setRoomCountTimeout : null,
    init : function() {
        $(document).on('click', '.PmsBookingProductList .select_button', app.PmsBookingProductList.selectRoom);
        $(document).on('click', '.PmsBookingProductList .continue_button', app.PmsBookingProductList.continueToPage);
        $(document).on('click', '.PmsBookingProductList .productentry .roomtypeselectioncount i', app.PmsBookingProductList.updateProductCount);
        $(document).on('keyup', '.PmsBookingProductList .productcount', app.PmsBookingProductList.updateProductCount);
        $(document).on('change', '.PmsBookingProductList .roomcountselection', app.PmsBookingProductList.selectRoomCount);
        $(document).on('click', '.PmsBookingProductList continue_button.nightWarning', app.PmsBookingProductList.nightWarningPrompt);
        $(document).on('click', '.PmsBookingProductList .groupheader', app.PmsBookingProductList.showGroupedList);
    },
    nightWarningPrompt : function(){
        
    },
    showGroupedList : function(){
        var group = $(this).closest('.group');
        var entries = group.find('.productentry');
        entries.toggle();
        $(this).toggleClass('active');
        var groupName = group.attr('groupname');
        if(entries.is(":visible")) {
            localStorage.setItem("groupOpen_"+groupName,"true");
        } else {
            localStorage.setItem("groupOpen_"+groupName,null);
        }
    },
    updateProductCount : function() {
        if(app.PmsBookingProductList.setRoomCountTimeout) {
            clearTimeout(app.PmsBookingProductList.setRoomCountTimeout);
        }
        console.log($(this).val());
        var current = $(this).closest('.productentry');
        var typeid = current.attr('typeid');
        var currentInput = current.find('.productcount');
        var currentCount = parseInt(currentInput.val());
        var max =  parseInt(currentInput.attr('max'));
        if(!$(this).hasClass('productcount')) {
            if($(this).hasClass('fa-plus')) {
                currentCount++;
            } else {
                currentCount--;
            }
        }
        
        if(currentCount < 0) {
            currentCount = 0;
        }
        if(currentCount > max) {
            currentCount = max;
        }
        currentInput.val(currentCount);
        
        app.PmsBookingProductList.setRoomCountTimeout = setTimeout(function() {
            var data = {
                "typeid" : typeid,
                "count" : currentCount
            };

            var event = thundashop.Ajax.createEvent('','selectRoomCount',currentInput, data);
            thundashop.Ajax.postWithCallBack(event, function () {
                var found = false;
                $('.productcount').each(function() {
                    var count = parseInt($(this).val());
                    if(count > 0) {
                        found = true;
                    }
                });
                if(found) {
                    $('.continue_button').removeClass('disabled');
                } else {
                    $('.continue_button').addClass('disabled');
                }

            });
        }, 100);
    },
    addZero : function(i){
        if(i < 10){
            i = "0" + i;
        }
        return i;
    },
    continueToPage : function() {
        if($(this).hasClass('disabled')) {
            return;
        }
        if($(this).hasClass('nightWarning')){
         
            var msg = $(this).attr('title');
            var confirmed = confirm(msg);
            if(confirmed === true){
                var attr = $(this).attr('next_page');
                thundashop.common.goToPageLink(attr);
            }
        }else{
            var attr = $(this).attr('next_page');
            thundashop.common.goToPageLink(attr);
        }
        
    },
    selectRoom : function() {
        var data = {
            "typeid" : $(this).attr('typeid')
        }
        var event = thundashop.Ajax.createEvent('','selectRoom',$(this), data);
        thundashop.Ajax.post(event);
    },
    selectRoomCount : function() {
        var data = {
            "typeid" : $(this).attr('typeid'),
            "count" : $(this).val()
        }
        var event = thundashop.Ajax.createEvent('','selectRoomCount',$(this), data);
        thundashop.Ajax.post(event);
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Show settings"),
                    click: app.PmsBookingProductList.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingProductList.init();