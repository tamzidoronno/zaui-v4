app.PmsBookingSummary = {
    init : function() {
        $(document).on('click','.PmsBookingSummary .addaddonsbutton', app.PmsBookingSummary.addAddon);
        $(document).on('click','.PmsBookingSummary .removeAddon', app.PmsBookingSummary.removeAddon);
        $(document).on('click','.PmsBookingSummary .togglerepeatbox', app.PmsBookingSummary.closeRepeatBox);
        $(document).on('change','.PmsBookingSummary .repeat_type', app.PmsBookingSummary.changeRepeatType);
        $(document).on('click','.PmsBookingSummary .adddatetype', app.PmsBookingSummary.changeAddDateType);
        $(document).on('click','.PmsBookingSummary .showRepeatDates', app.PmsBookingSummary.showRepeatDates);
        $(document).on('click','.PmsBookingSummary .addonselection', app.PmsBookingSummary.addonSelection);
        $(document).on('blur','.PmsBookingSummary .roomaddedrow', app.PmsBookingSummary.updateRoomRow);
        $(document).on('keyup','.PmsBookingSummary .roomaddedrow', app.PmsBookingSummary.updateRoomRow);
    },
    updateRoomRow : function() {
        var args = thundashop.framework.createGsArgs($(this));

        args['itemid'] = $(this).attr('itemid');
        args['typeid'] = $(this).attr('typeid');
        args['roomid'] = $(this).attr('roomid');
        var row = $(this);
        var event = thundashop.Ajax.createEvent('','updateDateOnRow',$(this), args);
        thundashop.Ajax.postWithCallBack(event, function(result) {
            row.find('.resultbox').html(result);
        });
    },
    addonSelection : function() {
        var data = {
            item : $(this).attr('itemid')
        };
        
        thundashop.Ajax.simplePost($(this), "toggleAddon", data);
    },
    
    showRepeatDates : function() {
        if(!$('.repatingroomlist').is(':visible')) {
            $('.repatingroomlist').slideDown();
        } else {
            $('.repatingroomlist').slideUp();
        }
    },
    changeAddDateType : function() {
        $('.PmsBookingSummary .adddatetype.selected').removeClass('selected');
        $(this).addClass('selected');
        $('.datetypepanel').hide();
        var type = $(this).attr('type');
        $('.datetypepanel.'+type).show();
        $('input[gsname="repeattype"]').val(type);
        
    },
    changeRepeatType: function() {
        var type = $(this).val();
        $('.repeatrow').hide();
        if(type !== "0") {
            $('.repeatrow').show();
        } 
        $('.repeateachdaterow').hide();
        if(type === "1") {
            $('.repeateachdaterow').show();
        }
        
        $('.repeatoption').hide();
        $('.repeat_' + type).show();
    },
    closeRepeatBox : function() {
        var box = $('.PmsBookingSummary .addMoredatesPanel');
        if(box.is(":visible")) {
            box.slideUp();
        } else {
            box.slideDown();
        }
    },
    removeAddon : function() {
        var data = {
            itemtypeid : $(this).attr('itemtypeid')
        };
        var event = thundashop.Ajax.createEvent('','removeAddon', $(this),data);
        thundashop.Ajax.post(event);
    },
    addAddon : function() {
        var data = {
            itemtypeid : $(this).closest('.itemrow').attr('itemid')
        };
        var event = thundashop.Ajax.createEvent('','addAddon', $(this),data);
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
                    click: app.PmsBookingSummary.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};

app.PmsBookingSummary.init();