if (typeof(GetShop) === "undefined")  {
    GetShop = {};
};

GetShop.CalendarEventViewer = function(api, appInstance, page) {
    this.api = api;
    this.appInstance = appInstance;
    this.page = page;
    this.init();
};

GetShop.CalendarEventViewer.prototype = {
    /**
     * Fires when image displayer is ready.
     */
    ready: null,
    
    init: function() {
        
    },
    
    load: function() {
        this.api.CalendarManager.getAllEventsConnectedToPage(this.page.pageId).done($.proxy(this.receivedContentFromApi, this));
    },
    
    receivedContentFromApi: function(entries) {
        var dom = $('<div/>');
        dom.addClass('CalendarEventViewer');
        
        var found = false;
        for (var i in entries) {
            var entry = entries[i];
            var entryHtml = $("<div class='daybutton'>"+entry.day+"/"+entry.month+" - "+entry.year+"</div>");
            
            entryHtml.attr('year', entry.year);
            entryHtml.attr('month', entry.month);
            entryHtml.attr('day', entry.day);
            entryHtml.attr('entryId', entry.entryId);
            entryHtml.tap(function() {    
                var pageId = 'daypage_' + $(this).attr('year') + "_" + $(this).attr('month') + "_" + $(this).attr('day')+"_"+entry.entryId;
                $.mobile.changePage("#" + pageId, {transition: 'slide' });
            });
            
            dom.append(entryHtml);
            found = true;
        }
        
        if (!found) {
            dom.append(App.translateText('Ingen tilgjengelige kurs'));
        } else {
            dom.prepend(App.translateText("Vi holder dette kurset på følgende datoer")+"<br/><br/>");
        }
        
        this.element = dom;        

        if (typeof(this.ready) === "function") {
            this.ready(dom);
        }
    }
};