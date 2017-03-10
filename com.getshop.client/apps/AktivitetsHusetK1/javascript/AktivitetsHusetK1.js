getshop.AktivitetsHusetK1 =  {
    init: function() {
        $(document).on('swiperight', getshop.AktivitetsHusetK1.openLeftMenu);
        $(document).on('swipeleft', getshop.AktivitetsHusetK1.closeLeftMenu);
        $(document).on('tap', '.gs_toggleLeftSideBar', getshop.AktivitetsHusetK1.toggleLeftMenu);
        $(document).on('tap', '.Menu[appid="39d79d5c-0ad8-45d0-b681-2e2eb3c15fe1"] .entries .entry a', getshop.AktivitetsHusetK1.handleMenuClick);
    },
    
    openLeftMenu: function() {
        $(".left_side_bar").addClass("openedmenu");
    },
    
    closeLeftMenu: function() {
        $(".left_side_bar").removeClass("openedmenu");
    },
    
    toggleLeftMenu: function(event) {
        console.log("fasz");
        event.preventDefault();
        $(".left_side_bar").toggleClass("openedmenu");
    },
    
    handleMenuClick: function(event) {
        if($(this).parent().children(".entries").length > 0) {
            console.log($(this).parent().children(".entries:eq(0)").css("display"));
            if($(this).parent().children(".entries:eq(0)").css("display") == "none") {
                event.preventDefault();
                $(this).parent().children(".entries").css("display", "block");
            }
        }
    }
}

getshop.AktivitetsHusetK1.init();