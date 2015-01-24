app.YouTube = {
    selectMovie: function() {
        var id = $(this).closest('.searchresult').attr('id');
        var height = 0;
        $('[appid=' + $(this).closest('.app').attr('appId') + "]").each(function() {
            if ($(this).hasClass('informationbox')) {
                return;
            }
            height = Math.floor($(this).width() * 0.75)
        });
        var event = thundashop.Ajax.createEvent('', 'setYoutubeId', $(this), {
            "id": id,
            "height": height
        });
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    remove: function(data, app) {
        var appid = app.attr('appid');
        thundashop.Skeleton.removeApplication(appid, 'middle');
    },
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            application: application,
            title: "Settings",
            items: [{
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Change movie"),
                    click: app.YouTube.startSearch
                }]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
    startSearch: function(event) {
        var input = $(this).closest('.app').find('#searchyoutubeinput');
        if ($(this).hasClass('inputfield') && event.keyCode !== 13) {
            return;
        }

        var data = input.val();
        var event = thundashop.Ajax.createEvent('', 'search', $(this), {"query": data});
        thundashop.common.showInformationBox(event, __f("YouTube movie search"));
    },
    setHeight: function(id) {
        var frame = $('#youtube_' + id);
        var width = frame.width();
        var height = frame.height();
        var ratio = width / height;
        if(ratio >= 1.59 && ratio <= 1.61) {
            return;
        }
        frame.height(width / 1.6);
        thundashop.common.resizeOrderMask();
    },
    init: function() {
        $(document).on('keyup', '.YouTube #searchyoutubeinput', app.YouTube.startSearch);
        $(document).on('click', '.YouTube #searchyoutube', app.YouTube.startSearch);
        $(document).on('click', '.YouTube .select_youtube_movie', app.YouTube.selectMovie);
    }
}

app.YouTube.init();