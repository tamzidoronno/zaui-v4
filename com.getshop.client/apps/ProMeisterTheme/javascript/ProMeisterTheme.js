getshop.ProMeisterTheme =  {
    init: function() {
        $(document).on('swiperight', getshop.ProMeisterTheme.openLeftMenu);
        $(document).on('swipeleft', getshop.ProMeisterTheme.closeLeftMenu);
        $(document).on('tap', '.gs_toggleLeftSideBar', getshop.ProMeisterTheme.toggleLeftMenu);
        $(document).on('tap', '.Menu[appid="e5e6fe9d-1b6b-4b04-9806-4bb4b829b3d3"] .entries .entry a', getshop.ProMeisterTheme.handleMenuClick);
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

getshop.ProMeisterTheme.init();