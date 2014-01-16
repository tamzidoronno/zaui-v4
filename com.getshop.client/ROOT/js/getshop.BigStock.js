if (typeof (getshop) === "undefined") {
    getshop = {};
};

getshop.BigStock = function(attachToDom, imageEditor) {
    this.attachToDom = attachToDom;
    this.imageEditor = imageEditor;
    this.init();
};

getshop.BigStock.prototype = {
    currentPage: 1,
    
    init: function() {
        this.createContainer();
        this.addCancelButton();
        this.addPagingArea();
        this.addLogo();
        this.addSearchField();
        this.createResultContainer();
        this.createPreviewForm();
        this.addBuyButton();
    },    
    addBuyButton: function() {
        var button = $('<div/>');
        button.addClass('gs_button');
        button.addClass('buybutton');
        button.hide();
        button.html('<i class="fa fa-money"></i> '+__f('Buy Picture'));
        this.buyButton = button;
        this.outerDom.append(button);
    },
    addPagingArea: function() {
        var pageingArea = $('<div/>');
        pageingArea.addClass('paging');
        pageingArea.hide();
        
        var pageingInner = $('<div/>');
        pageingInner.addClass('paginginner');
        
        var next = $('<i/>')
        next.addClass('fa');
        next.addClass('nextpaging');
        next.addClass('fa-step-forward');
        next.click($.proxy(this.goToNextPage, this));
        
        var prev = $('<i/>')
        prev.addClass('fa');
        prev.addClass('backpaging');
        prev.addClass('fa-step-backward');
        prev.click($.proxy(this.goToPrevPage, this));
        
        this.pagingTextArea = $('<div/>');
        pageingArea.append(pageingInner);
        
        pageingInner.append(prev);
        pageingInner.append(next);
        pageingInner.append(this.pagingTextArea);
        
        this.nextButton = next;
        this.prevButton = prev;
        
        this.pageingArea = pageingArea;
        this.outerDom.append(pageingArea);
    },
    createPreviewForm: function() {
        this.previewForm = $('<div/>');
        this.previewForm.addClass('preview');
        this.previewForm.hide();
        
        var image = new Image();
        this.previewForm.append(image);
        
        this.outerDom.append(this.previewForm);
    },
    createResultContainer: function() {
        this.searchResult = $('<div class="searchresult"><div class="resultarea"/></div>');
        this.searchResult.hide();
        this.outerDom.append(this.searchResult);
    },
    addLogo: function() {
        var logo = $('<div/>');
        logo.addClass('logo');
        this.outerDom.append(logo);
    },
    addSearchField: function() {
        var searchContainer = $('<div/>');
        searchContainer.addClass('searchContainer');
        this.outerDom.append(searchContainer);
        
        var header = $('<div/>');
        header.addClass('header');
        header.html(__f("Images for everyone."));
        searchContainer.append(header);
        
        var subtext = $('<div/>');
        subtext.addClass('subtext');
        subtext.html(__f("Over 16 million photos, illustrations, and vectors."));
        searchContainer.append(subtext);
        
        var emptyField = $('<div/>');
        emptyField.addClass('searchbackground');
        searchContainer.append(emptyField);
        
        var button = $('<div class="gs_button"><i class="fa fa-search"></i></div>');
        var me = this;
        button.click(function() {
            me.currentPage = 1;
            me.search();
        });
        emptyField.append(button)
        
        var input = $('<input id="bigstocksearch" type="text" placeholder="'+__f("Find the perfect image...")+'"/>');
        emptyField.append(input);
        
        this.searchContainer = searchContainer;
    },
    goToNextPage: function() {
        this.currentPage++;
        this.search();
    },
    goToPrevPage: function() {
        this.currentPage--;
        if (this.currentPage < 1) {
            this.currentPage = 1;
        }
        this.search();
    },
    addCancelButton: function() {
        var button = $('<div/>');
        button.addClass('gs_button');
        button.addClass('cancelbutton');
        button.click($.proxy(this.backClicked, this));
        button.html('<i class="fa fa-arrow-circle-left"></i>'+__f("Back"));
        this.outerDom.append(button);
    },
    
    backClicked: function() {
        if (this.previewForm.is(":visible")) {
            this.showSearchResult();
        } else if (this.searchResult.is(":visible")) {
            this.showSearchForm();
        } else {
            this.imageEditor.bigstockCanceled();
        }
    },
    
    showSearchResult: function() {
        this.buyButton.hide();
        this.pageingArea.show();
        this.previewForm.hide();
        this.searchResult.show();
    },
    
    showSearchForm: function() {
        this.buyButton.hide();
        this.pageingArea.hide();
        this.searchResult.hide();
        this.pagingTextArea
        $('#bigstocksearch').val("");
        this.searchContainer.show();
    },
    
    createContainer: function() {
        this.outerDom = $('<div/>');
        this.outerDom.addClass("gs_bigstock");
        this.attachToDom.html(this.outerDom);
    },
    
    setSize: function() {
        this.outerDom.width(this.imageEditor.attachToDom.width());
        this.outerDom.height(this.imageEditor.attachToDom.height());
    },
    
    search: function() {
        var text = $('#bigstocksearch').val();
        getshop.BigStockApi.search($.proxy(this.receivedResult, this), text, this.currentPage);
    },
    
    hideSearchField: function() {
        this.outerDom.hide();
    },
    
    showPreview: function(data) {
        this.searchResult.hide();
        this.pageingArea.hide();
        this.buyButton.show();
        this.previewForm.find("img").attr('src',data.preview.url);
        this.previewForm.find(".bigstock_preview_title").html(data.title);
        this.previewForm.show();
    },
    
    receivedResult: function(result) {
        this.searchContainer.hide();
        this.pageingArea.show();
        this.searchResult.show();
        
        var searchResultArea = this.searchResult.find('.resultarea');
        searchResultArea.html("");
        var data = result.data;
        var me = this;
        
        var pageText = __f("Page")+": "+data.paging.page + "/" + data.paging.total_pages;
        this.pagingTextArea.html(pageText);
        this.lastResult = result;
        
        if (data.images.length === 0) {
            this.pageingArea.hide();
            searchResultArea.html('<i class="fa fa-exclamation-triangle"></i> ' + __f("Sorry, no result found."));
            return;
        }
        
        this.nextButton.show();
        this.prevButton.show();
        
        if (data.paging.total_pages === this.currentPage) {
            this.nextButton.hide();
        }
        
        if (this.currentPage === 1) {
            this.prevButton.hide();
        }
        
        for (var i in data.images) {
            var div = $('<div/>');
            div.addClass('imageholder');
            
            var inner = $('<div/>');
            inner.addClass('innerholder');
            div.append(inner);
            
            var image = data.images[i];
            console.log(image);
            var domImage = new Image();

            domImage.src = image.small_thumb.url;
            domImage.data = image;
            $(domImage).click(function() {
                me.showPreview(this.data);
            })
            inner.append(domImage)
            searchResultArea.append(div);
        }
    }
};


getshop.BigStockApi = {
    APIADDRESS : "http://testapi.bigstockphoto.com/2/442193/",
    
    search: function(success, keywords, page) {
        $.ajax({
            type : "GET",
            dataType : "jsonp",
            url : getshop.BigStockApi.APIADDRESS + 'search/?q='+keywords+'&response_detail=all&page='+page,
            success: success
        });
    }
};
