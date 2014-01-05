if (typeof(GetShop) === "undefined")  {
    GetShop = {}
}

GetShop.Page = function(api, pageId) {
    this.api = api;
    this.pageId = pageId;
};


GetShop.Page.prototype = {
    /**
     * This event is fired when the
     * page is ready to be displayed.
     * Note, data is loaded async.
     */
    ready : null,
            
    load: function() {
        this.api.PageManager.getPage(this.pageId).done($.proxy(this.receivedPageFromApi, this));
    },
            
    receivedPageFromApi: function(page) {
        this.backendPage = page;
        this.createSkeleton();
    },
            
    createSkeleton: function() {
        this.header = $('<div data-role="header" ><div data-role="navbar"><ul><li><a href="#" class="ui-btn-active" data-rel="back">Tilbake</a></li></ul></div></div>');
        this.page = $('<div data-role="page" id="getshoppage_'+this.backendPage.id+'"></div>');
        this.contentArea = $('<div data-role="content"></div>');
        
        this.page.append(this.header);
        this.page.append(this.contentArea);
        
        this.createCells();
        $('body').append(this.page);
        this.firePageReadyEvent();
    },
            
    createCells: function() {
        var cellNumber=0;
        var rowNumber=0;
        for (var i in this.backendPage.layout.rows) {
            var row = this.backendPage.layout.rows[i];
            if (row.numberOfCells === 1) {
                rowNumber++;
                this.createAppArea("main_"+rowNumber);
            } else {
                for (var j=0;j<row.numberOfCells; j++) {
                    cellNumber++;
                    this.createAppArea("col_"+cellNumber);
                }
            }
        }
    },
            
    createAppArea: function(name) {
        var pageArea = this.backendPage.pageAreas[name];
        if (pageArea) {
            var cell = $('<div class="gs_cell"></div>');
            cell.attr('id', name);
            this.startLoadingApplications(pageArea, cell);
            this.contentArea.append(cell);
        }
    },

    startLoadingApplications: function(pageArea, dom) {
        for (var i in pageArea.applicationsSequenceList) {
            var appId = pageArea.applicationsSequenceList[i];
            var appInstance = pageArea.applications[appId];
            this.loadContentManager(appInstance, dom);
            this.loadImageDisplayer(appInstance, dom);
            this.loadYouTube(appInstance, dom);
        }
    },
            
    loadContentManager: function(appInstance, dom) {
        if (appInstance.appSettingsId === "320ada5b-a53a-46d2-99b2-9b0b26a7105a") {
            var contentManager = new GetShop.ContentManager(this.api, appInstance);
            contentManager.ready = function(content) {
                dom.html(content);
            };
            contentManager.load();
        }
    },
            
    loadImageDisplayer: function(appInstance, dom) {
        if (appInstance.appSettingsId === "831647b5-6a63-4c46-a3a3-1b4a7c36710a") {
            var imageDisplayer = new GetShop.ImageDisplayer(this.api, appInstance);
            imageDisplayer.ready = function(content) {
                dom.html(content);
            };
            imageDisplayer.load();
        }
    },
            
    loadYouTube: function(appInstance, dom) {
        if (appInstance.appSettingsId === "8e239f3d-2244-471e-a64d-3241b167b7d2") {
            var youTube = new GetShop.YouTube(this.api, appInstance, this);
            youTube.ready = function(content) {
                dom.html(content);
            };
            youTube.load();
        }
    },
            
    firePageReadyEvent: function() {
        if (typeof(this.ready) === "function") {
            this.ready();
        }
    },
            
    refresh: function() {
        
    }
};