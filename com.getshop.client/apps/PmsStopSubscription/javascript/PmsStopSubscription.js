app.PmsStopSubscription = {
    init : function() {
        $(document).on('click', '.PmsStopSubscription .stopdatecalbutton', app.PmsStopSubscription.showStopSubscription);
    },
    showStopSubscription : function() {
        var text = $(this).closest('tr').find('.roomname').text();
        $('.stopsubscriptionbuttontext').text(text);
        $('.pmsroomidtostop').val($(this).attr('pmsRoomId'));
        $('.PmsStopSubscription .completestopform').slideDown();
    }
}

app.PmsStopSubscription.init();