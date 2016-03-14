app.ProductCategories = {
    init : function() { 
        $(document).on('click', '.ProductCategories .savecategoryconfig', app.ProductCategories.saveCategoryList);
    },
    loadSettings: function(element, application) {
        var config = { showSettings : true, draggable: true, title: "Settings", items: [] }
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    
    saveCategoryList : function() {
        var entries = getshop.jstree.buildList('#categorylist');
        var data = {};
        data.entries = entries;
        
        thundashop.Ajax.simplePost($(this), 'saveJsTree', data);
    }
};

app.ProductCategories.init();