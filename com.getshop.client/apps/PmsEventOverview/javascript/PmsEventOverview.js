app.PmsEventOverview = {
    init : function() {
        $(document).on('click', '.PmsEventOverview .calendarSettings .fa-cog', app.PmsEventOverview.openSettings);
        $(document).on('click', '.PmsEventOverview #calendarConfiguration li', app.PmsEventOverview.toggleVisibility);
    },
    openSettings : function() {
        $(this).toggleClass('active');
        $('#calendarConfiguration').toggle();
    },
    toggleVisibility : function(){
        var btn = $(this);
        if(btn.hasClass('toggleborders')){
            btn.toggleClass('active');
            $('.timeblock').toggleClass('timeblockBorderClass');
        } else if(btn.hasClass('toggleoccupied')){
            btn.toggleClass('active');
            $('.PmsEventOverview .occupied').toggleClass('hidden');
        } else if(btn.hasClass('togglenotconfirmed')){
            btn.toggleClass('active');
            $('.PmsEventOverview .notconfirmed').toggleClass('hidden');
        } else if(btn.hasClass('toggleopenforpublic')){
            btn.toggleClass('active');
            $('.PmsEventOverview .openforpublic').toggleClass('hidden');
        } else if(btn.hasClass('toggleavailable')){
            btn.toggleClass('active');
            $('.PmsEventOverview .available').toggleClass('hidden');
        } else if(btn.hasClass('togglenotavailable')){
            btn.toggleClass('active');
            $('.PmsEventOverview .not_available').toggleClass('hidden');
        }
    },
    loadSettings: function (element, application) {
        var config = {
            draggable: true,
            app: true,
            showSettings : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}
app.PmsEventOverview.init();