app.BildoyMarineTheme = {
    getPageWeatherData: function() {
        $.getJSON("/scripts/bildoy/getWeatherData.php",function(data){
            $("#topSymbol").attr("src", data.SymbolUrl.replace("b100", "b30"));
            $("#topSymbol").show();
            $('#topWaitingForWeatherData').hide();
            $("#topTemp").html(data.Temp.toString().replace(".", ","));
        });
    },
    
    fetchData: function() {
        app.BildoyMarineTheme.getPageWeatherData();
        setTimeout(app.BildoyMarineTheme.fetchData, 60000);
        console.log("Fetching weather data");
    },
    
    init: function() {
        PubSub.subscribe('NAVIGATION_COMPLETED', function() {
            var header = $('.gsarea[area="header"]');
            var divOuter = $('<div/>');
            divOuter.addClass('weatherheader');
            
            var divInner = $('<div/>');
            divInner.addClass('gs_page_width');
            
            divInner.html('<a target="_blank" href="http://facebook.com/bildoymarina/"><i class="fa fa-facebook"></i> Facebook </a>&nbsp;&nbsp;&nbsp;<a href="/været.html"><i class="fa fa-video-camera"></i>&nbsp; <span>Webkamera</span></a> <a id="topWeatherLink" href="/været.html"><img id="topSymbol" style="display: none" alt="Været" src="http://symbol.yr.no/grafikk/sym/b30/04.png"> <i class="fa fa-spinner fa-spin" id="topWaitingForWeatherData"></i> <span id="topTemp">-</span><span>° Været</span></a>');
            
            divOuter.prepend(divInner);
            
            header.prepend(divOuter);
            header.css('margin-top', '0px');
            header.css('padding-top', '0px');
            
            app.BildoyMarineTheme.getPageWeatherData();
        });
        
        setTimeout(app.BildoyMarineTheme.fetchData, 60000);
    }
}

app.BildoyMarineTheme.init();