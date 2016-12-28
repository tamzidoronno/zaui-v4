app.FrigoTheme = {
    init: function() {
        $(document).on('click', '.mobilenavigatemenu', app.FrigoTheme.scrollToTop);
    },
    
    scrollToTop: function() {
        window.scroll(0,0);
    }
}

app.FrigoTheme.init();