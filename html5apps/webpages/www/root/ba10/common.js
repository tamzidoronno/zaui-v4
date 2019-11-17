getshop_endpoint = "https://20489gc6.getshop.com";
var masonryinitialized = false;
$(document).on('click','.chooseimagebtn',function(res) {
    $('.chosenimagebtn').removeClass('chosenimagebtn');
    $(this).addClass('chosenimagebtn');
    var ids = $(this).attr('ids');
    ids = ids.split(',');
    var grid = $('.grid');
    var end = parseInt(grid.attr('end'));
    var area = grid.attr('area');
    var prepended = "";
    for(i = 1; i <= end; i++) {
        var shouldBeIncluded = false;
        if(ids[0] === "-1" || ids.includes(i+"")) {
            shouldBeIncluded = true;
        }
        
        var isInGrid = grid.find('[imgid="'+i+'"]').length > 0;
        
        if(!shouldBeIncluded && isInGrid) {
            var item = grid.find('[imgid="'+i+'"]').closest('.grid-item');
            grid.masonry( 'remove', item );
        }
        
        if(shouldBeIncluded && !isInGrid) {
            prepended += '<div class="grid-item"><img src="/ba10/images/'+area+'/'+i+'.jpg" imgid="'+i+'"></div>';
        }
    }
     $('.grid').imagesLoaded(function () {
        var elems = $( prepended );
        grid.append( elems ).masonry( 'appended', elems );
        $(function() {
            setTimeout(function() {
                grid.masonry('layout');
            },"200");
        });

    });

});

function loadGrid() {
    var grid = $('.grid');
    var end = parseInt(grid.attr('end'));
    var area = grid.attr('area');
    var items = [];
    for(i = 1; i <= end; i++) {
        var item = $('<div class="grid-item"><img src="/ba10/images/'+area+'/'+i+'.jpg" imgid="'+i+'"></div>');
        grid.prepend(item);
        items.push(grid);
    }
    $('.grid').imagesLoaded(function () {
        $(function() {
        setTimeout(function() {
            $('.grid').masonry({
                // options
                columnWidth: 160,
                fitWidth: true,
                itemSelector: '.grid-item',
              });
              masonryinitialized = true;
          },"200");

        });

    });
}