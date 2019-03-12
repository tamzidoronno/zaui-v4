app.ComfortRoomConfig = {
    toggleUnitAdded : function(res) {
        var unitid = res.unitid;
        console.log(res);
        var selected = $('.unitconfig[unitid="'+unitid+'"][roomid="'+res.roomid+'"]');
        if(res.state === "removed") {
            selected.removeClass('selectedunit');
        } else {
            selected.addClass('selectedunit');
        }
    }
};