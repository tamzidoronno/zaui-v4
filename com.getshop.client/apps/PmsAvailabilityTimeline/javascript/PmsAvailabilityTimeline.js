app.PmsAvailabilityTimeline = {
    ignoreMouseOver : false,
    init : function() {
        $(document).on('click', '.PmsAvailabilityTimeline .loadbookingfromquickmenu', app.PmsAvailabilityTimeline.loadBooking);
        $(document).on('click', '.PmsAvailabilityTimeline .loadBookingList', app.PmsAvailabilityTimeline.loadBookingList);
        $(document).on('click', '.PmsAvailabilityTimeline .checkallclosingroom', app.PmsAvailabilityTimeline.checkallclosingroom);
        $(document).on('click', '.PmsAvailabilityTimeline .checkallclosingtypes', app.PmsAvailabilityTimeline.checkallclosingtypes);
        $(document).on('mouseover', '.PmsAvailabilityTimeline .valueentry', app.PmsAvailabilityTimeline.mouseOver);
        $(document).on('mouseout', '.PmsAvailabilityTimeline .valueentry', app.PmsAvailabilityTimeline.mouseOut);
        $(document).on('click', '.PmsAvailabilityTimeline .closeRoomOptionsButton', app.PmsAvailabilityTimeline.closeRoomOptionsButton);
        $(document).on('change', '.PmsAvailabilityTimeline .changetypeonbookingselector', app.PmsAvailabilityTimeline.ifNeedMoveTypes);
        $(document).on('dragover dragenter dragleave drop', '.PmsAvailabilityTimeline .full', app.PmsAvailabilityTimeline.dragBoxPrevent);
        $(document).on('dragover dragenter', '.PmsAvailabilityTimeline .full', app.PmsAvailabilityTimeline.dragBoxEnter);
        $(document).on('dragleave dragend drop', '.PmsAvailabilityTimeline .full', app.PmsAvailabilityTimeline.dragBoxLeave);
        $(document).on('drop', '.PmsAvailabilityTimeline .full', app.PmsAvailabilityTimeline.dragBoxDrop);
        $(document).on('click', '.PmsAvailabilityTimeline .valueentry', app.PmsAvailabilityTimeline.showShortOptionsForEntry);
        $(document).on('click', '.PmsAvailabilityTimeline .shortinformationboxrow', app.PmsAvailabilityTimeline.hideShortOptionsForEntry);
        $(document).on('click', '.PmsAvailabilityTimeline .shortinformationboxFooter', app.PmsAvailabilityTimeline.hideShortOptionsForEntry);
        $(document).on('click', '.PmsAvailabilityTimeline #toggleRoomoverview', app.PmsAvailabilityTimeline.toggleRoomoverview);
        $(document).on('click', '.PmsAvailabilityTimeline .completeaction', app.PmsAvailabilityTimeline.completeQuickAction);
        $(document).keyup(app.PmsAvailabilityTimeline.hideShortOptionsForEntry);
    },
    toggleRoomoverview : function(){
        $('.roomoverview').slideToggle();
        $("#toggleRoomoverview").toggleClass('flip');
        if($('#toggleRoomoverview').hasClass('flip')){
            localStorage.setItem('PmsRoomoverviewState','hidden');
        }else{
            localStorage.setItem('PmsRoomoverviewState','visible');
        }
    },
    completeQuickAction : function(e) {
        if(e.stopPropagation) e.stopPropagation();
        if(e.preventDefault) e.preventDefault();
        e.cancelBubble=true;
        e.returnValue=false;
        
        $(this).each(function() {
            $.each(this.attributes, function() {
                app.PmsAvailabilityTimeline.mouseDownData[this.name] = this.value;
            });
        });
        $('[gsarg]').each(function() {
            app.PmsAvailabilityTimeline.mouseDownData[$(this).attr("gsarg")]= $(this).val();
        });
        
        var event = thundashop.Ajax.createEvent('', 'completeAction',$(this), app.PmsAvailabilityTimeline.mouseDownData);
        thundashop.Ajax.post(event);
        
        app.PmsAvailabilityTimeline.hideShortOptionsForEntry(e);
        
        return false;
    },
    showShortOptionsForEntry : function(e) {
        if(!$(e.target).hasClass('valueentry')) {
            return;
        }
        var bid = $(this).closest('.valueentry').attr('bid');
        app.PmsAvailabilityTimeline.ignoreMouseOver = true;
        if(e.stopPropagation) e.stopPropagation();
        if(e.preventDefault) e.preventDefault();
        e.cancelBubble=true;
        e.returnValue=false;
        var classes = $(this).attr('class');
        var data = {
            "type" : $(this).attr('type'),
            "bookingid" : $(this).attr('bid'),
            "time" : $(this).attr('time'),
            "itemid" : $(this).attr('itemid'),
            "classes" : classes,
            "bid" : bid
        };
        
        app.PmsAvailabilityTimeline.mouseDownData = data;
        var event = thundashop.Ajax.createEvent('','showShortInformation', $(this), data);
        var field = $(this);
        app.PmsAvailabilityTimeline.ignoreMouseOver = true;
        $('.shortinformationbox').remove();
        $('.ui-tooltip').remove();

        thundashop.Ajax.postWithCallBack(event, function(res) {
            field.prepend(res);
        });
        
        return false;
    },
    hideShortOptionsForEntry : function(e) {
        if(e.type === "keyup" && e.keyCode !== 27) {
            return;
        }
        var outbox = $(e.target);
        var action = outbox.attr('action');
        if(action) {
            app.PmsAvailabilityTimeline.mouseDownData['action'] = action;
            var event = thundashop.Ajax.createEvent('','prepareAction', outbox, app.PmsAvailabilityTimeline.mouseDownData);
            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.shortinformationbox_inner').html(res);
            });
        } else {
            app.PmsAvailabilityTimeline.ignoreMouseOver = false;
            setTimeout(function() {
                $('.shortinformationbox').remove();
                $('.ui-tooltip').remove();
            }, "0");
        }
    },
    ifNeedMoveTypes : function() {
        var count = $(this).find('option:selected').attr('count');
        var form = $(this).closest('[gstype="form"]');
        var roomId = form.find('[gsname="roomid"]').val();
        var warnMessage = $(this).closest('tr').find('.warnmovepeople');
        warnMessage.hide();
        if(count === "0") {
            var data = {
                "movetotype" : $(this).val(),
                "moveRoom" : roomId
            }
            var event = thundashop.Ajax.createEvent('','findTypesToMove', $(this), data);
            
            thundashop.Ajax.postWithCallBack(event, function(res) {
                warnMessage.html(res);
                warnMessage.fadeIn();
            });
        }
    },
    dragBoxDrop : function(jevt){
        var box = $(this);
        box.removeClass('is-dragover');
        
        var files = jevt.originalEvent.dataTransfer.files;
        app.PmsAvailabilityTimeline.showUploadProgress(files[0], box);
    },
    dragBoxEnter : function(){
        $(this).addClass('is-dragover');
    },
    dragBoxLeave : function(){
        $(this).removeClass('is-dragover');
    },
    dragBoxPrevent : function(e){
        e.preventDefault();
        e.stopPropagation();
    },
    showUploadProgress: function(file, box) {
        box.addClass('is-uploading');
        box.append('<i class="fa fa-spin fa-spinner" aria-hidden="true"></i>');
        
        var bookingid = box.attr('bid');
        var reader = new FileReader();
        reader.onload = function(event) {
            var fileName = file.name;
            var dataUri = event.target.result;
            
            var data = {
                fileBase64: dataUri,
                fileName: fileName,
                "bookingid" : bookingid
            };
            
            var event = thundashop.Ajax.createEvent(null, "uploadFile", box, data);
            
            thundashop.Ajax.postWithCallBack(event, function() {
                box.find('i').remove();
                box.addClass('is-success').removeClass('is-uploading');
                box.append('<i class="fa fa-file" aria-hidden="true"></i>');
            });
        };
        reader.onerror = function(event) {
            console.error("File could not be read! Code " + event.target.error.code);
            box.addClass('is-success').removeClass('is-uploading');
            box.append('<i class="fa fa-times" aria-hidden="true"></i>');
        };
        reader.readAsDataURL(file);
    },
    checkallclosingroom : function() {
        var checked = $(this).is(':checked');
        $('.closeforroom').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    checkallclosingtypes : function() {
        var checked = $(this).is(':checked');
        $('.closeforota').each(function() {
            if(checked) {
                $(this).attr('checked','checked');
            } else {
                $(this).attr('checked',null);
            }
        });
    },
    
    closeRoomOptionsButton : function() {
        var event = thundashop.Ajax.createEvent('','loadCloseRoomBox', $(this), {});
        thundashop.common.showInformationBoxNew(event, 'Close a room');
    },
    mouseOut : function() {
        $('.ui-tooltip').remove();
        if(typeof(pmsAvailabilityTimelineTimeout) !== "undefined") {
            clearTimeout(pmsAvailabilityTimelineTimeout);
        }
    },
    loadBookingList : function() {
        var event = thundashop.Ajax.createEvent('','loadBookingList', $(this), {
            "day" : $(this).attr('day'),
            "type" : $(this).attr('type')
        });
        thundashop.common.showInformationBoxNew(event, "Active rooms");
    },
    
    mouseOver : function(e) {
        e.preventDefault();
        $('.ui-tooltip').remove();
        if(app.PmsAvailabilityTimeline.ignoreMouseOver) {
            return;
        }
        if($(this).hasClass('available')) {
            return;
        }
        
        var bookingid = $(this).attr('bid');
        var from = $(this);
        if(!bookingid) {
            return;
        }
        
        pmsAvailabilityTimelineTimeout = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('', 'loadHover', from, {
                "bookingid" : bookingid
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                from.tooltip({ content: res });
                from.attr('title', res);
                from.tooltip("open");
            });
        }, "200");
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event, 'Settings');
    },
    
    loadBooking : function(e) {
        $('.ui-tooltip').remove();
        var data = {
            "bid" : $(this).closest('.valueentry').attr('bid')
        }
        $('.shortinformationbox').remove();
        app.PmsAvailabilityTimeline.ignoreMouseOver = false;
        
        var instanceId = $('#bookinginstanceid').val();
        var event = thundashop.Ajax.createEvent('','showBookingOnBookingEngineId',instanceId,data);
        event.core.appname = "PmsManagement";
        app.PmsAvailabilityTimeline.hideShortOptionsForEntry(e);
        thundashop.common.showInformationBoxNew(event,'Configuration');
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
                    click: app.PmsAvailabilityTimeline.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
    
app.PmsAvailabilityTimeline.init();