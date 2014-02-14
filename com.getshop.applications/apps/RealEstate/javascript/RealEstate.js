RealEstate = {
    deleteImage: function() {
        var dom = $('.RealEstate');
        var event = thundashop.Ajax.createEvent('RealEstate', 'deleteImage', dom);
        thundashop.Ajax.post(event);
    }
};