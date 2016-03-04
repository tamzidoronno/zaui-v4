app.EventLister = {
    from: null,
    to: null,
    
    init: function() {
        $(document).on('change', '.EventLister .from', app.EventLister.fromChanged);
        $(document).on('change', '.EventLister .to', app.EventLister.toChanged);
    },
    
    fromChanged: function() {
        app.EventLister.from = $(this).val();
        app.EventLister.post($(this));
    },
    
    toChanged: function() {
        app.EventLister.to = $(this).val();
        app.EventLister.post($(this));
    },
    
    setTimes: function() {
        $('.EventLister .from').val(app.EventLister.from);
        $('.EventLister .to').val(app.EventLister.to);
    },
    
    post: function(me) {
//        if (app.EventLister.from && app.EventLister.to) {
            thundashop.Ajax.simplePost(me, "setTimeFilter", {
                from: app.EventLister.from,
                to: app.EventLister.to
            });
//        }
    }
}

app.EventLister.init();