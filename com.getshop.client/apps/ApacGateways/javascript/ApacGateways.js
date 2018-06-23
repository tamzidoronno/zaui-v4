app.ApacGateWays = {
    lastId : null,
    currentTimeOutRunning : null,
    init : function() {
        $(document).on('click', '.ApacGateways .isfailed', app.ApacGateWays.checkNoOp);
        $(document).on('click', '.ApacGateways .newzwavename', app.ApacGateWays.changeNameOnNode);
    },
    changeNameOnNode : function() {
        var newName = prompt("New name", $(this).attr('nodename'));
        if(!newName) {
            return;
        }
        var node = $(this).attr('nodeid');
        var event = thundashop.Ajax.createEvent('','renameNode', $(this), {
            "nodeid" : node,
            "name" : newName,
            "serverid" : $(this).closest('.locklist').find('#currentserverid').val()
        });
        thundashop.Ajax.postWithCallBack(event, function() {
            $('.zwavedevices').html('<i class="fa fa-spin fa-spinner"></i>');
            app.ApacGateWays.startDevices();
        });
    },
    checkNoOp : function() {
        var row = $(this).closest('.devicerow');
        var nodeId = row.attr('nodeid');
        var event = thundashop.Ajax.createEvent('','doRestCall',$('.ApacGateways'), {
            "path" : "ZWave.zway/Run/devices["+nodeId+"].SendNoOperation()",
            "id" : $('.datarow_extended_content:visible').find('#currentserverid').val()
        });

        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.ApacGateWays.currentTimeOutRunning = null;
            res = JSON.parse(res);
            app.ApacGateWays.printQueue(res);
            app.ApacGateWays.startQueue();
        });
    },
    startQueue : function() {
        var curid = $('.datarow_extended_content:visible').find('#currentserverid').val();
        if(app.ApacGateWays.lastId != null && curid != app.ApacGateWays.lastId) {
            app.ApacGateWays.lastId = null;
            return;
        }
        app.ApacGateWays.lastId = curid;
        var event = thundashop.Ajax.createEvent('','doRestCall',$('.ApacGateways'), {
            "path" : "ZWave.zway/InspectQueue",
            "id" : curid
        });

        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.ApacGateWays.currentTimeOutRunning = null;
            res = JSON.parse(res);
            if($('.queuelog[serverid="'+curid+'"]').is(":visible")) {
                app.ApacGateWays.printQueue(res);
                setTimeout(function() {
                    app.ApacGateWays.startQueue();
                }, "1000");
            }
        });
    },
    startDevices : function() {
        var event = thundashop.Ajax.createEvent('','doRestCall',$('.ApacGateways'), {
            "path" : "ZWave.zway/Data/0",
            "id" : $('.locklist:visible').find('#currentserverid').val()
        });

        thundashop.Ajax.postWithCallBack(event, function(res) {
            res = JSON.parse(res);
            app.ApacGateWays.printDevices(res);
        });
    },
    printDevices : function(res) {
        $('.zwavedevices').html('');
        for(var k in res.devices) {
            var obj = res.devices[k];
            var interviewDone = "<span class='interviewdone'>100% included</span>";
            if(obj.data && obj.data.interviewDone && !obj.data.interviewDone.value) {
                interviewDone = "<span class='interviewdone'>Not included</span>";
            }
            
            var isAwake = "<span class='isfailed'><i class='fa fa-check' title='Device is ok, click to test connectivity'></i></span>";
            if(obj.data.isFailed.value) {
                isAwake = "<span class='isfailed'><i class='fa fa-stop' title='Device seems to be dead, click to test connectivity'></i></span>";
            }
            
            var name = obj.data.givenName.value;
            if(!name) { name = "no name set yet"; }
            
            $('.zwavedevices').append('<div class="devicerow" nodeid="'+k+'"><span class="nodeid">'+k+'</span><span class="devicename"><i nodeid="'+k+'" class="fa fa-edit newzwavename" nodename="'+name+'" style="cursor:pointer;"></i> '+name+'</span>'+isAwake+interviewDone+'</div>');
        }
    },
    printQueue : function(queue) {
        if(!Array.isArray(queue)) {
            return;
        }
        $('.queuelog').html('');
        var queuelog = $('.queuelog:visible');
        queuelog.html('');
        if(queue.length == 0) {
            queuelog.html('Queue is empty');
            return;
        }
        for(var k in queue) {
            if(!queue[k][4]) {
                queue[k][4]= "";
            }
            if(Math.round(queue[k][0]) == 0) {
                queue[k][4] = "waiting";
            }
            queuelog.append("<div class='queuerow'><span class='timer'>"+Math.round(queue[k][0])+"</span><span class='nodeid'>"+queue[k][2]+"</span><span class='operation'>"+queue[k][3]+"</span><span class='queueresponse'>"+queue[k][4]+"</span></div>");
        }
    }
};
app.ApacGateWays.init();