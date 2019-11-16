

var getshop_endpoint = "http://20489gc6.3.0.local.getshop.com/";

$.fn.CategoryGallery = function(data) {
    if(!data) {
        data = [];
    }
    var loadCategories = gs_getAllCategories();
    var area = $(this);
    area.addClass('gsroomcategorygallery');
    loadCategories.done(function(res) {
        var buttonRow = $('<div class="gs_categorygallerybutton_row"></div>');
        var descriptionArea = $('<div class="gs_category_descriptions_area"></div>');
        var imageRow = $('<div class="gs_categorygalleryimage_row"></div>');
        area.append(buttonRow);
        area.append(descriptionArea);
        area.append(imageRow);
        var end = 0;
        
        for(var k in res.entries) {
            var entry = res.entries[k];
            var button = $('<span class="gs_categorygallerybutton" categoryid="'+entry.categoryId+'">' + entry.name + '</span>');
            
            if(gs_categorygalleryisloggedon) {
                var checkbox = $('<input type="checkbox" class="configurecategorytoview" categoryid="'+entry.categoryId+'"></input>');
                if(data.includes(entry.categoryId)) {
                    checkbox.attr('checked','checked');
                }
                button.prepend(checkbox);
            } else {
                if(data.length > 0 && !data.includes(entry.categoryId)) {
                    continue;
                }
            }
            buttonRow.append(button);
            
            var description = $('<div class="gs_category_description"  categoryid="'+entry.categoryId+'"></div>');
            description.html(entry.description);
            descriptionArea.append(description);
            
            var ids = [];
            for(var ik in entry.images) {
                var img = entry.images[ik];
                var imgId = img.fileId;
                
                
                imageRow.append('<span class="gs_grid-item" imgid="'+imgId+'"><img src="'+ getshop_endpoint + "/displayImage.php?id="+imgId+'&width=600"></span>');
                ids.push(imgId);
                end++;
            }
            var idsString = ids.join(",");
            button.attr('ids', idsString);
        }
        imageRow.attr('end', end);
        
        imageRow.imagesLoaded(function () {
            $(function() {
            setTimeout(function() {
                var width = $(window).width();
                width = width * 0.3;
                
                imageRow.masonry({
                    // options
                    columnWidth: width,
                    fitWidth: true,
                    itemSelector: '.gs_grid-item',
                  });
              },"200");

            });

        });
        
    });
};

var masonryinitialized = false;
$(document).on('click','.configurecategorytoview',function(res) {
    var checked = [];
    $('.configurecategorytoview').each(function() {
        if($(this).is(':checked')) {
            checked.push($(this).attr('categoryid'));
        }
    });
    
    
    var id = $(this).closest('.gscontatentgalleryapp').attr('id');
    var pageId = getCurrentPageId();

    $.post('plugins/categorygallery.php?id='+id+"&event=save&page="+pageId, { data : checked },
        function (data, textStatus, jqXHR) { 
    });
    
});

$(document).on('click','.gs_categorygallerybutton',function(res) {
    $('.gs_categorygallerybutton_chosen').removeClass('gs_categorygallerybutton_chosen');
    $(this).addClass('gs_categorygallerybutton_chosen');
    var ids = $(this).attr('ids');
    var categoryid = $(this).attr('categoryid');
    $('.gs_category_description').hide();
    $('.gs_category_description[categoryid="'+categoryid+'"]').show();
    ids = ids.split(',');
    var grid = $(this).closest('.gsroomcategorygallery').find('.gs_categorygalleryimage_row');
    var end = grid.find('[imgid]').length;
    var prepended = "";
    grid.find('[imgid]').each(function() {
        var shouldBeIncluded = false;
        var idToCheck = $(this).attr('imgid');
        if(ids[0] === "-1" || ids.includes(idToCheck)) {
            shouldBeIncluded = true;
        }
        
        var isInGrid = grid.find('[imgid="'+idToCheck+'"]').length > 0;
        
        if(!shouldBeIncluded && isInGrid) {
            var item = grid.find('[imgid="'+idToCheck+'"]').closest('.gs_grid-item');
            grid.masonry( 'remove', item );
        }
    });
    
    for(var k in ids) {
        var idToCheck = ids[k];
        var shouldBeIncluded = false;
        if(ids[0] === "-1" || ids.includes(idToCheck)) {
            shouldBeIncluded = true;
        }
        
        var isInGrid = grid.find('[imgid="'+idToCheck+'"]').length > 0;
        
        if(shouldBeIncluded && !isInGrid) {
            prepended += '<div class="gs_grid-item" imgid="'+idToCheck+'"><img src="'+ getshop_endpoint + "/displayImage.php?id="+idToCheck+'&width=600"></div>';
        }
    }
    
     grid.imagesLoaded(function () {
        var elems = $( prepended );
        grid.append( elems ).masonry( 'appended', elems );
        $(function() {
            setTimeout(function() {
                grid.masonry('layout');
            },"200");
        });

    });

});


function gs_getAllCategories() {
    lang = sessionStorage.getItem("getshop_language");
    var def = $.Deferred();
    $.ajax({
        "dataType": "jsonp",
        "url": getshop_endpoint + "/scripts/booking/categories.php?language="+lang,
        success: function (res) {
            getshop_translationMatrixLoaded = res;
            def.resolve(res);
        }
    });
    return def;
}

