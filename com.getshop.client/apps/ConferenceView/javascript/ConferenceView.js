app.ConferenceView = {
    init: function() {
       $(document).on('click', '.ConferenceView .leftmenu .entry', app.ConferenceView.leftMenuChanged);
       $(document).on('click', '.ConferenceView .openevent', app.ConferenceView.openEvent);
       $(document).on('click', '.ConferenceView .closeevent', app.ConferenceView.closeEvent);
       $(document).on('click', '.ConferenceView .saveEvent', app.ConferenceView.saveEvent);
       $(document).on('click', '.ConferenceView .deleteactivity', app.ConferenceView.deleteActivity);
       $(document).on('click', '.ConferenceView .addExtraActivity', app.ConferenceView.addExtraActivity);
       $(document).on('click', '.ConferenceView .addeventbutton', app.ConferenceView.openEvent);
       $(document).on('click', '.ConferenceView .deleteEvent', app.ConferenceView.deleteEvent);
       $(document).on('click', '.ConferenceView .changetitle', app.ConferenceView.changeTitle);
    },
    
    changeTitle: function() {
        var title = $(this).attr('title');
        title = prompt("Please enter new name", title);
        
        if (!title)
            return;
        
        $(this).closest('.headertitle').find('span').text(title);
        thundashop.Ajax.simplePost(this, "setConferenceTitle", {
            confid : $(this).closest('.outarea').attr('confid'),
            meetingTitle: title
        });
    },
    
    deleteEvent: function() {
        var me = this;
        var res = confirm("Are you sure you want to delete this?");
        
        if (res) {
            thundashop.Ajax.simplePost(me, 'deleteEvent', {
                confid : $(this).closest('.outarea').attr('confid'),
                eventid : $(this).attr('eventid')
            });
            
            $(this).closest('.row').remove();
        }
    },
    
    addExtraActivity: function() {
        var theapp = $(this).closest('.app');
        
        var toAdd = $(theapp.find('.template.activity')[0]).clone()[0];
        $(toAdd).removeClass('template');
        
        theapp.find('.activities').append(toAdd);
    },
    
    deleteActivity: function() {
        $(this).closest('.activity.row').remove();
    },
    
    saveEvent: function() {
        var me = this;
        var eventDom = $(this).closest('.inner_data');
        var event_infoDom = eventDom.find('.event_info');
        
        var data = {
            confid : $(this).closest('.outarea').attr('confid'),
            eventid : eventDom.find('.event_info').attr('eventid'),
            title: event_infoDom.find('[gsname="title"]').val(),
            pmsConferenceItemId: event_infoDom.find('[gsname="pmsConferenceItemId"]').val(),
            date: event_infoDom.find('[gsname="date"]').val(),
            starttime: event_infoDom.find('[gsname="starttime"]').val(),
            endtime: event_infoDom.find('[gsname="endtime"]').val(),   
        }
        
        var activities = [];
        
        eventDom.find('.activity').each(function() {
            if ($(this).hasClass('template')) {
                return;
            }
            
            var activity = {
                activityid : $(this).attr('activityid'),
                text: $(this).find('[gsname="text"] input').val(),
                to: $(this).find('[gsname="to"] input').val(),
                from: $(this).find('[gsname="from"] input').val(),
                count: $(this).find('[gsname="count"] input').val(),
                extendedText: $(this).find('[gsname="extendedText"]').val(),
            }
            
            activities.push(activity);
        });
        
        data.activities = activities;
        
        var event = thundashop.Ajax.createEvent(null, "saveEvent", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            $(me).closest('.app').find('.workarea_inner .events').html($("<div>"+ res + "</div>").find('.events').html());
            app.ConferenceView.closeEvent(null, me);
        });
    },
    
    closeEvent: function(event, from) {
        if (!from) {
            me = this;
        } else {
            me = from;
        }
        
        var theapp = $(me).closest('.app');
        
        theapp.find('.eventview').hide();
        theapp.find('.eventview').css('display', 'none');
        theapp.find('.addeventbutton').show();
        
        $( ".row_to_hide" ).animate({
            width: '100%'
        });
        
        window.scrollTo(0, 0);
    },
    
    openEvent: function() {
        var ref = $(this).closest('.events');
        var theapp = $(this).closest('.app');
        
        ref.find('.addeventbutton').hide();
        $(this).show();

        var data = {
            eventid : $(this).attr('eventid'),
            confid : $(this).closest('.outarea').attr('confid')
        }

        var event = thundashop.Ajax.createEvent(null, "renderEvent", this, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event, function(res) {
            theapp.find('.eventview .inner_data').html(res);
        });
        
        $( ".row_to_hide" ).animate({
            width: '0px'
        }, 300, function() {
            theapp.find('.eventview').show();
            theapp.find('.eventview').css('display', 'inline-block');    
        });
        
        window.scrollTo(0, 0);
    },
    
    leftMenuChanged: function() {
        var tab = $(this).attr('tab');
        var confid = $(this).closest('.outarea').attr('confid');
        app.ConferenceView.showTab(tab, $(this), confid);
    },
    
    showTab: function(tab, from, confid) {
        thundashop.Ajax.simplePost(from, 'showTab', {
            'tab': tab,
            'confid' : confid
        });
    }
}

app.ConferenceView.init();