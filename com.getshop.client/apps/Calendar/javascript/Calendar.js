app.Calendar = {
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: __f("Settings"),
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-gears",
                    iconsize: "30",
                    title: __f("Settings"),
                    click: app.Calendar.showSettings
                },
                {
                    icontype: "awesome",
                    icon: "fa-location-arrow",
                    iconsize: "30",
                    title: __f("Locations"),
                    click: app.Calendar.showLocationsConfiguration
                },
                {
                    icontype: "awesome",
                    icon: "fa-trophy",
                    iconsize: "30",
                    title: __f("Change diploma settings"),
                    click: app.Calendar.changeDiplomaSettings
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    changeDiplomaSettings: function(event, app) {
        if (!app) {
            app = this;
        }

        var event = thundashop.Ajax.createEvent(null, "showDiplomaSettings", app, {});
        thundashop.common.showInformationBox(event, __f("Diploma settings"));
    },
    showSettings: function(event, app) {
        var event = thundashop.Ajax.createEvent(null, "showSettings", app, {});
        thundashop.common.showInformationBox(event, __f("Settings"));
    },
    showLocationsConfiguration: function(event, app) {
        if (!app) {
            app = this;
        }

        var event = thundashop.Ajax.createEvent(null, "showLocationsEditor", app, {});
        thundashop.common.showInformationBox(event, __f("Locations"));
    },
    saveLocationConfiguration: function() {
        var data = {
            locationName: $('#locationName').val(),
            locationExtra: $('#locationExtra').val(),
            locationId: $('#locationId').val(),
            commentText: $('#commentText').val(),
            contactPerson: $('#contactPerson').val(),
            locationEmail: $('#locationEmail').val(),
            locationCellPhone: $('#locationCellPhone').val(),
            otherinformation: $('#otherinformation').val(),
            lon: $('#location_lon').val(),
            lat: $('#location_lat').val()
        }

        $('.groupinformation').each(function() {
            $(this).find('input').each(function() {
                var id = $(this).attr('id');
                var val = $(this).val();
                data[id] = val;
            });
        });
        
        var event = thundashop.Ajax.createEvent(null, "saveLocation", this, data);
        thundashop.common.showInformationBox(event, __f("Locations"));
    }
};

Calendar = {
    animations: [],
    init: function() {
        PubSub.subscribe('navigation_complete', this.checkScrolling, this);
        PubSub.subscribe("setting_switch_toggled", this.onOffChanged, this);
        $(document).on('click', '.selectlcoation', Calendar.showEditLocation);
        $(document).on('click', '.calendar_location_save', app.Calendar.saveLocationConfiguration);
        $(document).on('click', '.calendar_location_back', app.Calendar.showLocationsConfiguration);
        $(document).on('click', '.calendar_location_delete', Calendar.deleteLocation);
        $(document).on('click', '.calendar_location_createnew', Calendar.showEditLocation);
        $(document).on('click', '.Calendar .add_comment_to_event', Calendar.addCommentToEvent);
        $(document).on('click', '.Calendar .calendar_location_createarea', Calendar.showEditArea);
        $(document).on('click', '.Calendar .selectarea', Calendar.showEditArea);
        
        $(document).on('click', '.Calendar .calendar_locationarea_save', Calendar.saveLocationArea);
        
        $(document).on('click', '.Calendar .delete_comment', Calendar.deleteComment);
        $(document).on('click', '.Calendar .showCheckList', Calendar.showCheckList);
        $(document).on('click', '.Calendar .saveCheckList', Calendar.saveCheckList);
        $(document).on('click', '.Calendar .createforperiod .gs_button', Calendar.createPeriode);
        $(document).on('click', '.Calendar .diplomasettings .deletediploma', Calendar.deletePeriod);
        $(document).on('click', '.Calendar .diplomasettings .removesignature', Calendar.removesignature);
        $(document).on('change', '.Calendar .diplomasettings input.usersignature', Calendar.addSignature);
        $(document).on('change', '.Calendar .diplomasettings input#diploma_background', Calendar.addBackground);
        $(document).on('change', '.Calendar .diplomasettings input#textColor', Calendar.setTextColorDiploma);
        $(document).on('click', '.Calendar .list_showextrainformation', Calendar.showListExtraInformation);
    },
    saveCheckList: function() {
        var me = this;
        var data = {
            entryId : $(this).attr('entryid'),
            checklist_1 : $('#checklist_1').is(':checked'),
            checklist_2 : $('#checklist_2').is(':checked'),
            checklist_3 : $('#checklist_3').is(':checked'),
            checklist_4 : $('#checklist_4').is(':checked'),
            checklist_5 : $('#checklist_5').is(':checked'),
            checklist_6 : $('#checklist_6').is(':checked'),
            checklist_7 : $('#checklist_7').is(':checked'),
            checklist_8 : $('#checklist_8').is(':checked'),
            comment : $('#checklist_comment').val()
        };
        
        var event = thundashop.Ajax.createEvent("", "saveCheckList", $(this), data);
        thundashop.Ajax.post(event, function() {
            alert('Saved');
            Calendar.showCheckList(data.entryId, me);
        });
    },
    showCheckList: function(entryiId, target) {
        var me = this;
        if (typeof(target) !== "undefined") {
            me = target;
        }
        var entryId = $(this).attr('entryid') ? $(this).attr('entryid') : entryiId;
        var data = {
            entryId : entryId
        };
        var event = thundashop.Ajax.createEvent("", "showCheckList", $(me), data);
        thundashop.common.showInformationBox(event, "Checklist");
    },
    showListExtraInformation: function() {
        var entryId = $(this).attr('entry_id');
        var calendarEntry = $('.extrainformation_calendar_list_entry[entry_id="'+entryId+'"]');

        if (calendarEntry.is(':visible')) {
            calendarEntry.hide();
        } else {
            calendarEntry.show();
        }
        
    },
    saveLocationArea: function() {
        var selectedLocations = $('input:checkbox:checked.locationInArea').map(function () {
            return this.value;
        }).get();
        
        var data = {
            name : $('.Calendar #areaName').val(),
            locationAreaId: $('.Calendar #LocationAreaId').val(),
            northWest : $('.Calendar #northWest').val(),
            northEast : $('.Calendar #northEast').val(),
            southWest : $('.Calendar #southWest').val(),
            southEast : $('.Calendar #southEast').val(),
            locations : selectedLocations
        };
        
        var event = thundashop.Ajax.createEvent(null, "saveLocationArea", this, data);
        thundashop.Ajax.post(event, function() {
            alert('saved');
        });
    }, 
    showEditArea: function() {
        var data = {};

        if ($(this).attr('value')) {
            data.areaId = $(this).attr('value')
        }

        var event = thundashop.Ajax.createEvent(null, "showEditArea", this, data);
        thundashop.common.showInformationBox(event, __f("Edit area"));
    },
    setTextColorDiploma: function() {
        var data = {
            textColor: $(this).val(),
            diplomid : $(this).attr('diplomid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "setDiplomaTextColor", this, data);
        thundashop.Ajax.post(event,  Calendar.reprintDiplomaSettings);
    },
    
    removesignature: function() {
        var data = {
            userid : $(this).attr('userid'),
            diplomid : $(this).attr('diplomid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "removeSignature", this, data);
        thundashop.Ajax.post(event,  Calendar.reprintDiplomaSettings);
    },
    addSignature: function() {
        var input = this;
        if ( input.files && input.files[0] ) {
            var FR= new FileReader();
            FR.onload = function(e) {
                var data = {
                    base64encode : e.target.result,
                    userid : $(input).attr('userid'),
                    diplomId : $(input).attr('diplomId')
                }
                var event = thundashop.Ajax.createEvent(null, "saveSignature", input, data);
                thundashop.Ajax.post(event, Calendar.reprintDiplomaSettings);
            };       
            FR.readAsDataURL( input.files[0] );
        }
    },
    addBackground: function() {
        var input = this;
        if ( input.files && input.files[0] ) {
            var FR= new FileReader();
            FR.onload = function(e) {
                var data = {
                    base64encode : e.target.result,
                    diplomId : $(input).attr('diplomId')
                }
                var event = thundashop.Ajax.createEvent(null, "addBackground", input, data);
                thundashop.Ajax.post(event, Calendar.reprintDiplomaSettings);
            };       
            FR.readAsDataURL( input.files[0] );
        }
    },
    deletePeriod: function() {
        var data = {
            id : $(this).attr('diplomaid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "deletePeriode", this, data);
        thundashop.Ajax.post(event,  Calendar.reprintDiplomaSettings);
    },
    reprintDiplomaSettings: function() {
        app.Calendar.changeDiplomaSettings(null, $('.Calendar .diplomasettings'));
    },
    createPeriode: function() {
        var data = {
            startDate : $('.Calendar .createforperiod #startdate').val(),
            endDate : $('.Calendar .createforperiod #stopdate').val()
        };
        
        var event = thundashop.Ajax.createEvent(null, "createPeriod", this, data);
        thundashop.Ajax.post(event,  Calendar.reprintDiplomaSettings);
    },
    addCommentToEvent : function() {
        var text = $(this).closest('.comment_area').find('.comment_field').val();
        var data= {
            "text" : text,
            "id" : $(this).attr('entryid')
        }
        var event = thundashop.Ajax.createEvent(null, 'addCommentToEntry',$(this), data);
        thundashop.Ajax.post(event);
    },
    
    deleteComment : function() {
        var text = $(this).closest('.comment_area').find('.comment_field').val();
        var data= {
            "entryid" : $(this).attr('entryid'),
            "commentid" : $(this).attr('commentid')
        }
        var event = thundashop.Ajax.createEvent(null, 'deleteCommentToEntry',$(this), data);
        thundashop.Ajax.post(event);
    },
    
    deleteLocation: function() {
        var confirm = thundashop.common.confirm(__f("Are you sure you want to delete the location?"));

        if (!confirm) {
            return;
        }

        var data = {
            locationId: $('#locationId').val(),
        }

        var event = thundashop.Ajax.createEvent(null, "deleteLocation", this, data);
        thundashop.common.showInformationBox(event, __f("Locations"));
    },
    showEditLocation: function() {
        var data = {};

        if ($(this).attr('value')) {
            data.locationId = $(this).attr('value')
        }

        var event = thundashop.Ajax.createEvent(null, "showEditLocation", this, data);
        thundashop.common.showInformationBox(event, __f("Locations"));
    },
    expandDays: function() {
        $('.Calendar .calendar table .containsdata').hover(
                function() {
                    var tdWidth = $(this).closest('td').width();
                    $(this).find('.content .outerdiv div').each(function() {
                        if ($(this).attr('days')) {
                            var days = $(this).attr('days');
                            days = parseInt(days);
                            if (days > 1) {
                                width = tdWidth * days + (2 * days);
                                var animation = $(this).animate({width: width}, 100);
                                Calendar.animations.push(animation);
                            } else {
                                $(this).width($(this).width());
                            }
                        }
                    });
                    $(this).addClass('hovering');
                },
                function() {
                    for (var i in Calendar.animations) {
                        var animation = Calendar.animations[i];
                        animation.stop();
                    }

                    Calendar.animations = [];
                    $(this).find('.content .outerdiv div').each(function() {
                        $(this).removeAttr('style');
                    });
                    $(this).removeClass('hovering');
                }
        );
    },
    onOffChanged: function(msg, data) {
        if (data.id === "allowadd" && data.state === "on") {
            $('#allowaddonsettings').slideDown();
        }

        if (data.id === "allowadd" && data.state === "off") {
            $('#allowaddonsettings').slideUp();
        }
    },
    checkScrolling: function(msg, data) {
        if (data && data.indexOf("&scroll_to_dayview=true") > -1) {
            var dayeventdiv = $('div.dayevents');
            var position = dayeventdiv.position();
            scroll(0, position.top);
        }
    },
    addAnotherDay: function(day, month, year) {
        var name = day + "/" + month + "/" + year;


        var total = $('<div/>');
        total.addClass('daytotal');
        var remove = $('<img/>');
        remove.attr('class', 'removeday inline');
        remove.attr('src', '/skin/default/images/closeinfobox.png');
        remove.css({'height': '15px', 'width': '15px', 'padding-top': '3px', 'margin-right': '5px'});

        var element = $('<div/>');
        element.attr('class', 'inline extradayelement');
        element.attr('day', day);
        element.attr('month', month);
        element.attr('year', year);
        element.html(name);
        element.css({'line-height': '20px'});
        total.prepend(remove);
        total.append(element);

        var timeHolder = $('<span/>');
        timeHolder.css({
            position: 'absolute',
            right: '0px',
            display: 'inline-block',
            'border-left': 'solid 1px #DDD',
            'margin-left': '4px',
            'padding-left': '5px'
        });

        var time = $('<input/>');
        time.css({'width': '55px', 'margin-left': '5px'});
        time.attr('type', 'textfield');
        time.attr('id', 'starttime');

        var starttime = $('#extradaysdiv').closest('.addevent').find('#eventstart').val()
        var stoptime = $('#extradaysdiv').closest('.addevent').find('#eventstop').val();
        time.val(starttime);

        var time2 = time.clone();
        time2.attr('id', 'stoptime');
        time2.val(stoptime);

        timeHolder.append('Clock: ');
        timeHolder.append(time);
        timeHolder.append(' - ');
        timeHolder.append(time2);

        total.append(timeHolder);
        $('#extradaysdiv').append(total);
    },
    getHexColor: function() {
        return "008000";
    },
    updateHistoryTab: function(entryId, dom) {
        var data = {
            entryid: entryId
        };

        var event = thundashop.Ajax.createEvent("", "getReminderHistory", dom, data);
        thundashop.Ajax.postWithCallBack(event, function(response) {
            $('.Calendar .all_reminder_history').html(response);
        }, true);
    }
};

Calendar.init();

$('.Calendar .removeday').live('click', function() {
    $(this).closest('.daytotal').remove();
});

$('.Calendar .addevent #save').live('click', function() {
    var data = {
        day: $(this).attr('day'),
        year: $(this).attr('year'),
        month: $(this).attr('month'),
        entryid: $(this).attr('entryid')
    }

    var extraDays = $(this).closest('.informationbox').find('.daytotal');
    var extraDaysData = [];

    $.each(extraDays, function(extraday, daytotal) {
        var el = $(daytotal).find('.extradayelement')
        var extraDay = {};
        extraDay['day'] = $(el).attr('day');
        extraDay['month'] = $(el).attr('month');
        extraDay['year'] = $(el).attr('year');
        extraDay['starttime'] = $(daytotal).find('#starttime').val();
        extraDay['stoptime'] = $(daytotal).find('#stoptime').val();
        extraDaysData.push(extraDay);
    });

    data.eventname = $('#eventname').val();
    data.eventstart = $('#eventstart').val();
    data.maxattendees = $('#maxattendies').val();
    data.eventstop = $('#eventstop').val();
    data.eventdescription = $('#eventdescription').val();
    data.linkToPage = $('#linkToPage').attr('pageid');
    data.locationId = $('#location').val();
    data.deffered = $('#deffered').is(":checked");
    data.extraText = $('#extraText').val();
    data.extraDays = extraDaysData;
    data.lockedForSignup = $('#lockEvent').is(":checked");
    data.eventHelder = $('.eventhelder_selector').val();

    data.color = Calendar.getHexColor();

    var event = thundashop.Ajax.createEvent('Calendar', 'registerEvent', $(this), data);
    thundashop.Ajax.post(event);
    thundashop.common.hideInformationBox(event);
});

$('.Calendar .save_candidate_setting').live('click', function() {
    var data = {
        receiveDiploma : $('#receiveDiploma_inner').hasClass('on'),
        userId : $('#configuserid').val(),
        configentryId : $('#configentryId').val(),
        participated : $('#participated select').val()
    };
    var event = thundashop.Ajax.createEvent('','updateUserSettings',$(this), data);
    thundashop.Ajax.postWithCallBack(event, function() {
        thundashop.framework.reprintPage();
        thundashop.common.hideInformationBox();
    });
});

$(document).on('click','.Calendar .fa-graduation-cap', function() {
    $(this).closest('tr').find('.candidate_setting').click();
})
$('.Calendar .candidate_setting').live('click', function() {
    var data = {
        userId: $(this).attr('userid'),
        entryId: $(this).attr('entryId')
    }

    var event = thundashop.Ajax.createEvent(null, 'showCandidateSetting', this, data);
    thundashop.common.showInformationBox(event, __f('Add comment'));
});

$('.Calendar .deleteentry').live('click', function() {
    var data = {};
    data.entryId = $(this).attr('entryid');
    var confirmed = confirm("Are you sure you want to delete this event?");
    if (confirmed) {
        var event = thundashop.Ajax.createEvent('Calendar', 'deleteEntry', $(this), data);
        thundashop.Ajax.post(event);
    }
});

$('.Calendar .editentry').live('click', function() {
    var data = {};
    data.entryid = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'showCalenderEvent', $(this), data);
    thundashop.common.showInformationBox(event, __f('Edit event'));
});

$('.Calendar .save_comment').live('click', function() {
    var data = {
        entryId: $(this).attr('entryId'),
        userId: $(this).attr('userId'),
        comment: $(this).parent().find('textarea').val()
    };

    var event = thundashop.Ajax.createEvent(null, "saveComment", this, data);
    thundashop.Ajax.post(event);
    thundashop.common.hideInformationBox();
});

$('.Calendar .deletecomment').live('click', function() {
    var data = {
        commentId: $(this).attr('commentId'),
        userId: $(this).attr('userId')
    }
    var event = thundashop.Ajax.createEvent(null, "deleteComment", this, data);
    thundashop.Ajax.post(event);
    $(this).closest('.commententry').fadeOut(function() {
        $(this).remove();
    });
})

$('.Calendar .addcomment').live('click', function() {
    var data = {
        userId: $(this).attr('userid'),
        entryId: $(this).attr('entryId')
    }

    var event = thundashop.Ajax.createEvent(null, 'showEvent', this, data);
    thundashop.common.showInformationBox(event, __f('Add comment'));
});

$('.Calendar .reminder').live('click', function() {
    var data = {};
    data.entryid = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'showSendReminder', $(this), data);
    thundashop.common.showInformationBox(event, __f("Send reminder"));
});

$('.Calendar .confirmentry').live('click', function() {
    var data = {};
    data.entryid = $(this).attr('entryid');
    var event = thundashop.Ajax.createEvent('Calendar', 'confirmEntry', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.Alert(__w("Success"), __w("The event has been confirmed."));
    });
});

$('.Calendar #sendreminderbysms').live('click', function() {
    var data = {};
    data.users = [];
    $(this).closest('#informationbox').find('[name=attendeestoremind]:checked').each(function() {
        data.users.push($(this).val());
    });

    data.text = $(this).closest('#informationbox').find('textarea').val();
    data.entryid = $(this).attr('entryid');
    data.subject = $('#remindersubject').val();

	if (data.text.length > 470) {
		alert("Your message is to long for SMS, maximum allowed length is 470 chars, you have: " + data.text.length);
		return;
	}
	
	
    var event = thundashop.Ajax.createEvent('Calendar', 'sendReminderBySms', $(this), data);
    var me = this;

    var callback = function() {
        Calendar.updateHistoryTab(data.entryid, me);
    };

    thundashop.Ajax.post(event, function(response) {
        callback();
        thundashop.common.Alert(__f('Reminder'), __f('Reminder sent by sms'));
    });

});

function getBase64EncodedFile(file, callback) {
    if (!file) {
        callback(null, null);
        return;
    }
    
    var reader = new FileReader();
    var filename = file.name;
    
    reader.onload = function(event) {
        callback(event.target.result, filename);
    };
    reader.readAsDataURL(file);
}

$('.Calendar #sendreminderbymail').live('click', function() {
    var fileInput = document.getElementById('file1');
    var outerButton = $(this);
    
    getBase64EncodedFile(fileInput.files[0], function(result, filename) {
        var data = {};
        data.users = [];
        outerButton.closest('#informationbox').find('[name=attendeestoremind]:checked').each(function() {
            data.users.push($(this).val());
        });

        data.text = outerButton.closest('#informationbox').find('textarea').val();
        data.entryid = outerButton.attr('entryid');
        data.subject = $('#remindersubject').val();
        data.attachment1 = result;
        data.filename = filename;

        var event = thundashop.Ajax.createEvent('Calendar', 'sendReminderByEmail', outerButton, data);
        var me = outerButton;

        var callback = function() {
            Calendar.updateHistoryTab(data.entryid, me);
        };

        thundashop.Ajax.post(event, function(response) {
            callback();
            thundashop.common.Alert(__w('Reminder'), __w('Reminder sent by email'));
        });
    });


});

$('.Calendar .addEvent').live('click', function() {
    var data = {
        day: $(this).attr('day'),
        year: $(this).attr('year'),
        month: $(this).attr('month')
    }

    var event = thundashop.Ajax.createEvent('Calendar', 'showCalenderEvent', $(this), data);
    thundashop.common.showInformationBox(event, __f('Create event'));
})

$('.Calendar .add_user_to_this_event').live('click', function() {
    var data = {
        fromEntryId: $(this).attr('currentEntryId'),
        toEntryId: $(this).attr('toEntryId'),
        userid: $(this).attr('userid')
    }

    var event = thundashop.Ajax.createEvent('Calendar', 'transferUser', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.hideInformationBox();
    });
});
$('.Calendar .transferUser').live('click', function() {
    var data = {
        userid: $(this).attr('userid'),
        entryid: $(this).attr('entryid')
    }

    var event = thundashop.Ajax.createEvent('Calendar', 'showTransferUser', $(this), data);
    thundashop.common.showInformationBox(event, __f("Transfer user from one event to another"));
});

$('.Calendar .remove').live('click', function() {
    var confirm = thundashop.common.confirm(__f("Are you sure you want to remove the candidate from this event?"));

    if (!confirm) {
        return;
    }
    
    var data = {
        userid: $(this).attr('userid'),
        entryid: $(this).attr('entryid')
    }

    var event = thundashop.Ajax.createEvent('Calendar', 'removeAttendees', $(this), data);
    thundashop.Ajax.post(event);
});

$('.Calendar .filters .filter').live('click', function() {
    var isSelected = $(this).hasClass('selected');
    var event = null;
    var data = {
        filter: $(this).text()
    };

    if (!isSelected) {
        $(this).addClass('selected');
        event = thundashop.Ajax.createEvent('Calendar', 'applyFilter', $(this), data);
    } else {
        $(this).removeClass('selected');
        event = thundashop.Ajax.createEvent('Calendar', 'removeFilter', $(this), data);
    }

    thundashop.Ajax.post(event);
});
$(document).on('click', '.Calendar .acceptUser', function() {
    var data = {
        entryId: $(this).attr('entryId'),
        userId: $(this).attr('userId')
    };

    var event = thundashop.Ajax.createEvent('Calendar', 'transferUserFromWaitingList', $(this), data);
    thundashop.Ajax.post(event);
});
$(document).on('click', '.Calendar .addusertoevent', function() {
    var data = {
        entryId: $(this).attr('entryId')
    };

    var event = thundashop.Ajax.createEvent('Calendar', 'showAddUserInterface', $(this), data);
    thundashop.common.showInformationBox(event, __f("Add user to event"));
});
$(document).on('click', '.Calendar .gs_button.calendaruserentrysearch', function() {
    var data = {
        entryId: $(this).closest('.searchforuser').attr('entryId'),
        search: $(this).closest('.searchforuser').find('input').val()
    };

    var event = thundashop.Ajax.createEvent('Calendar', 'showAddUserInterface', $(this), data);
    thundashop.common.showInformationBox(event, __f("Add user to event"));
});
$(document).on('click', '.Calendar .gs_button.adduseractionbutton', function() {
    var data = {
        entryId: $(this).closest('.searchforuser').attr('entryId'),
        userId: $(this).attr('userId')
    };

    var event = thundashop.Ajax.createEvent('Calendar', 'addUserToEventSilent', $(this), data);
    thundashop.Ajax.post(event, function() {
        thundashop.common.hideInformationBox();
    });
});