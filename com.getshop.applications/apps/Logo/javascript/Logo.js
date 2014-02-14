thundashop.app.logo = {};

thundashop.app.logo.removeLogo = function(target) {
    var conf = confirm(__f("Are you sure you want to delete this logo?"));
    if(conf) {
        var event = thundashop.Ajax.createEvent('Contact', 'removeLogo', target, {
        });
        thundashop.Ajax.post(event, 'Logo');
    }
}

thundashop.app.logo.reloadLog = function(target) {
    var event = thundashop.Ajax.createEvent('Contact', 'reloadLogo', target, {
    });
    thundashop.Ajax.post(event, 'Logo');
}

$('.Logo').live('click', function(e) {
    var target = $(e.target);
    if(target.hasClass('trash')) {
        thundashop.app.logo.removeLogo(target);
    }
});
