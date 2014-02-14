thundashop.app.gallery = {};
thundashop.app.gallery.disableEvents = false;
thundashop.app.gallery.activateUploadImage = function(target) {
    var container = target.closest('.app');
    var imgcontainer = $('<div class="imagecontainer" count="0" id="" desc="" title=""><div class="inner_image"><span class="img"></span></div></div>');
    imgcontainer.hide();
    container.find('.listedimages .demo').remove();
    container.find('.applicationinner .listedimages').prepend(imgcontainer);
    imgcontainer.find('.img').imageupload({maxWidth: 200,maxHeight: 180,uploadText: "Uploading logo",keepScalingOnUpload:true,
    onupdate : function(img) {
        imgcontainer.show();
    },
    callback : function(id){
        imgcontainer.attr('id',id);
    }});
    
    thundashop.app.gallery.disableEvents=true;
    imgcontainer.find('.img').mouseup();
};

thundashop.app.gallery.showLargeImage = function(target) {
    var id = target.attr('id');
    var text = target.attr('desc');
    text = decodeURIComponent(text);
    text = text.replace(/\+/g, ' ');
    var offset = target.attr('xcount');
    var event = thundashop.Ajax.createEvent("SomeEvent", "showLargeImage", target, {});
    thundashop.common.showLargeInformationBox(event);
    $('#informationbox').removeClass('informationboxbackground');
    $('#informationbox .image_description').html(text);
    $('#informationbox .image_view').html("<img class='large_image' id='"+id+"' src='displayImage.php?id="+id+"&width=700&height=600'>");
    $('#informationbox .image_view').attr('offset', offset);
    var start = 0;
    if(offset < 3) {
        start = 0;
    } else {
        start = offset - 3;
    }
    var end = start + 5;
    var topRow = $('#informationbox .image_list');
    target.closest('.Gallery').find('.imagecontainer').each(function(e) {
        var image = document.createElement("span");
        image.id = $(this).attr('id');
        topRow.append(image);
    });
    
    thundashop.app.productmanager.loadOtherImages(id);
};

$('.Gallery').live('click', function(e) {
    var target = $(e.target);
    
    if(target.hasClass('edit')) {
        return;
    }
    if(thundashop.app.gallery.disableEvents) {
        thundashop.app.gallery.disableEvents=false;
        return;
    }
    if(target.hasClass('add_new_image')) {
        thundashop.app.gallery.activateUploadImage(target);
    } else if(target.hasClass('imagecontainer')) { 
        thundashop.app.gallery.showLargeImage(target);
    } else if(target.parent().parent().hasClass('imagecontainer')) {
        thundashop.app.gallery.showLargeImage(target.parent().parent());
    } else if(target.parent().hasClass('imagecontainer')) {
        thundashop.app.gallery.showLargeImage(target.parent());
    } else if(target.hasClass('topimgcontainer')) {
        target = target.find('.top_image');
        var toClick = $('.Gallery .imagecontainer[count="'+target.attr('count')+'"]');
        thundashop.app.gallery.showLargeImage(toClick);
    } else if(target.hasClass('top_image')) {
        var toClick = $('.Gallery .imagecontainer[count="'+target.attr('count')+'"]');
        thundashop.app.gallery.showLargeImage(toClick);
    } else if(target.hasClass('image_view')) {
        thundashop.app.productmanager.showNextImage(target);
    } else if(target.parent().hasClass('image_view')) {
        target = target.parent();
        thundashop.app.productmanager.showNextImage(target);
    }
});