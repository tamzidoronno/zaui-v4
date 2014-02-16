thundashop.app.search = {};

$('.Search .searchview').slideUp();
thundashop.app.search.timout = null;

thundashop.app.search.displaySearchView = function() {
    $('.Search .searchview').html('');
    $('.Search .searchview').fadeIn();
}

thundashop.app.search.hideSearchView = function() {
    $('.Search .searchview').html('');
    $('.Search .searchview').fadeOut();
}


thundashop.app.search.result = function(response) {
    $('.Search .searchview').html(response);
}

thundashop.app.search.doSearch = function(text) {
    if(text.length < 2) {
        $('.Search .searchview').html('');
        thundashop.app.search.hideSearchView();
        return;
    }
    if(!$('.Search .searchview').is(':visible')) {
        thundashop.app.search.displaySearchView();
    }
    
    var target = $('.Search');
     var event = thundashop.Ajax.createEvent('Search', 'searchForProducts', target, {
        "value" : text
    });
    var response = thundashop.Ajax.postWithCallBack(event, thundashop.app.search.result);  
}

thundashop.app.search.searchForProduct = function(target) {
    var toSearchFor = target.val();
    try {
        clearTimeout(thundashop.app.search.timout);
    }catch(e) {}
    thundashop.app.search.timout = setTimeout("thundashop.app.search.doSearch('" +toSearchFor +"')", "100");
}

$('.Search').live('focus', function(e) {
    var target = $(e.target);
    if(target.hasClass('searchfield')) {
//        thundashop.app.search.displaySearchView(target);
    }
});

$('.Search .searchfield').live('keyup', function(e) {
    thundashop.app.search.searchForProduct($(e.target));
});

$(document).live('click', function(e) {
    var target = $(e.target);
    if(target.hasClass('searchfield')) {
        return;
    }
    
    $('.Search .searchview').fadeOut();
    
})

