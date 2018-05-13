app.ApacGateWays = {
    currentTimeOutRunning : null,
    init : function() {
        $(document).on('click', '.ApacGateways .isfailed', app.ApacGateWays.checkNoOp);
    },
    checkNoOp : function() {
        var row = $(this).closest('.devicerow');
        var nodeId = row.attr('nodeid');
        var event = thundashop.Ajax.createEvent('','doRestCall',$('.ApacGateways'), {
            "path" : "ZWave.zway/Run/devices["+nodeId+"].SendNoOperation()",
            "id" : $('#currentserverid').val()
        });

        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.ApacGateWays.currentTimeOutRunning = null;
            res = JSON.parse(res);
            app.ApacGateWays.printQueue(res);
            app.ApacGateWays.startQueue();
        });
    },
    startQueue : function() {
        var event = thundashop.Ajax.createEvent('','doRestCall',$('.ApacGateways'), {
            "path" : "ZWave.zway/InspectQueue",
            "id" : $('#currentserverid').val()
        });

        thundashop.Ajax.postWithCallBack(event, function(res) {
            app.ApacGateWays.currentTimeOutRunning = null;
            res = JSON.parse(res);
            app.ApacGateWays.printQueue(res);
            app.ApacGateWays.startQueue();
        });
    },
    startDevices : function() {
        var event = thundashop.Ajax.createEvent('','doRestCall',$('.ApacGateways'), {
            "path" : "ZWave.zway/Data/0",
            "id" : $('#currentserverid').val()
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
            var interviewDone = "<span class='interviewdone'><i class='fa fa-check'  title='Interview is complete'></i></span>";
            if(!obj.data.interviewDone.value) {
                interviewDone = "<span class='interviewdone'><i class='fa fa-close' title='Interview failed'></i></span>";
            }
            
            var isAwake = "<span class='isfailed'><i class='fa fa-check' title='Device is ok, click to test connectivity'></i></span>";
            if(obj.data.isFailed.value) {
                isAwake = "<span class='isfailed'><i class='fa fa-stop' title='Device seems to be dead, click to test connectivity'></i></span>";
            }
            
            
            
            $('.zwavedevices').append('<div class="devicerow" nodeid="'+k+'"><span class="nodeid">'+k+'</span><span class="devicename">'+obj.data.givenName.value+'</span>'+isAwake+interviewDone+'</div>');
        }
    },
    printQueue : function(queue) {
        $('.queuelog').html('');
        for(var k in queue) {
            if(!queue[k][4]) {
                queue[k][4]= "";
            }
            if(Math.round(queue[k][0]) == 0) {
                queue[k][4] = "waiting";
            }
            $('.queuelog').append("<div class='queuerow'><span class='timer'>"+Math.round(queue[k][0])+"</span><span class='nodeid'>"+queue[k][2]+"</span><span class='operation'>"+queue[k][3]+"</span><span class='queueresponse'>"+queue[k][4]+"</span></div>");
        }
    }
};
app.ApacGateWays.init();