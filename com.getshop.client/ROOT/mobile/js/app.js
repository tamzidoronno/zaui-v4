/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

App = {            
    start: function() {
        this.getshopApi = new GetShopApiWebSocket(window.location.hostname);
        var me = this;
        
        this.getshopApi.connectedCallback = function() {
            me.loadHeaders();
            me.loadCourses();
            me.loadFilters();
            me.loadCoursePages();
        };
        
        this.getshopApi.connect();
    },
            
    loadCoursePages : function() {
        var me = this;
        
        this.getshopApi.ListManager.getList("71544ed8-406d-4496-baf2-b569975ebb20").done(function(entries) {
            $(entries).each(function() {
                $(this.subentries).each(function() {
                    var page = $('<div data-role="page" id="'+this.pageId+'"/>');
                    
                    page.html("<div class='header'/>");
                    
                    page.find(".header").load("header.html", function() {
                        page.find('.header').trigger('create');
                    }); 
                    
                    
                    me.getshopApi.PageManager.getPage(this.pageId).done(function(dataPage) {
                        
                        var contentHolder = $('<div data-role="content" class="ContentManager">');
                        
                        for (id in dataPage.pageAreas.middle.applications) {
                            
                            var application = dataPage.pageAreas.middle.applications[id];
                            
                            if (application.appName === "ContentManager") {
                                me.getshopApi.ContentManager.getContent(application.id).done(function(content) {
                                    contentHolder.html(content);
                                });
                            }
                        }
                        
                        page.append(contentHolder);
                        $('html .ui-mobile-viewport').append(page);
                    });
                })
            });
        });
    },
            
    loadFilters: function() {
        this.getshopApi.CalendarManager.getFilters().done(function(filters) {
            var filterHolder = $('#filterholdergroup');
            $(filters).each(function() {
                var filter = $("<a href='#' data-rel='back' data-role='button'>"+this+"</a>");
                filterHolder.append(filter);
            });
        });
    },

    loadCourses: function() {
        var topEntry = $('<div data-role="collapsible" data-theme="b" data-content-theme="d"  data-inset="false"/>');
        var subEntryContainer = $('<ul data-role="listview"/>');
        var courselist = $('#courselist');
        
        this.getshopApi.ListManager.getList("71544ed8-406d-4496-baf2-b569975ebb20").done(function(list) {
            
            $(list).each(function() {
                var topEntryCloned = topEntry.clone();
                topEntryCloned.html("<h4>"+this.name+"</h4>");
                courselist.append(topEntryCloned);
                var subentriesClonedTop = subEntryContainer.clone();
                $(this.subentries).each(function() {
                    var subEntry = $('<li><a href="#'+this.pageId+'">'+this.name+'</a></li>');
                    subentriesClonedTop.append(subEntry);
                });
                topEntryCloned.append(subentriesClonedTop);
            });
        });
    },
            
    loadHeaders: function() {
        $(".header").load("header.html", function() {
            $('.header').trigger('create');
        }); 
    }
};