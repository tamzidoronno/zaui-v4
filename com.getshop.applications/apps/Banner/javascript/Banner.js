
app.Banner = {
    crop_api: null,
    isLoading: false,
    banners: {},
    scaled: 1,
    currrentRoute: null,
    img: null,
    init: function() {
        PubSub.subscribe('GS_TAB_NAVIGATED', this.navigateImageTab);
        PubSub.subscribe('IMAGE_UPLOADED', this.imageUploaded, this);
        $('.Banner .addProduct').live('keyup', this.doSearchForProducts);
        $('.Banner .update_banner').live('click', this.updateBannerManager);
        $(document).on('change', '.Banner .uploadimage', this.uploadBannerImage);
        $(document).on('keyup', '.Banner #height', this.changeCropSize);
        $(document).on('change', '.Banner #height', this.changeCropSize);
        $(document).on('click', '.Banner .delete_banner_image', this.deleteBannerImage);
        $(document).on('click', '.Banner .add_text', this.addNewText);
        $(document).on('click', '.Banner .outerdragobject .remove', this.removeText);
        $(document).on('change', '.Banner .outerdragobject select', this.updateTextSize);
        $(document).on('keyup', '.Banner .add_image_preview_frame .image_banner_text', this.updateText);
        $(document).on('keyup', '.Banner .link_input', this.updateLink);
        $(document).on('change', '.Banner .link_input', this.updateLink);
        $(document).on('click', '.Banner .add_image_preview_frame', this.hideToolbar);
        $(document).on('focus', '.Banner .add_image_preview_frame .image_banner_text', this.showToolbar);
    },
            
    loadEdit : function(element, app) {
        var event = thundashop.Ajax.createEvent('','showAddBanner',app, {});
        thundashop.common.showInformationBox(event, __f("Banner(s)"));
    },

    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-gears",
                    iconsize : "30",
                    title: __f("Settings"),
                    click: app.Banner.loadEdit
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },    
    updateLink: function() {
        var tab = $('.Banner .gs_tab_selected');
        tab.attr('link', thundashop.base64.encode($(this).val()));
    },
    hideToolbar: function(event) {
        if(event !== null) {
            if ($(event.target).closest('.outerdragobject').length > 0) {
                return;
            }
        }
        $('.Banner .dragobject').css('z-index','10000');
        $('.Banner .add_image_preview_frame .toolbar').hide();
        $('.colorpicker').fadeOut();
    },
    showToolbar: function() {
        app.Banner.hideToolbar(null);
        $(this).closest('.dragobject').css('z-index','10001');
        $(this).closest('.dragobject').find('.toolbar').show();
    },
    updateText: function() {
        var text = $(this).html();
        var id = $(this).attr('id');
        $('.Banner .textfields #' + id).html(thundashop.base64.encode(text));
    },
    updateTextSize: function() {
        var size = $(this).val();
        var id = $(this).closest('.dragobject').find('.image_banner_text').attr('id');
        $(this).closest('.dragobject').find('.image_banner_text').css('font-size', size + "px");
        var printsize = size * app.Banner.scaled;
        $('.Banner .textfields #' + id).attr('size', printsize);
    },
    removeText: function() {
        var entry = $(this).closest('.dragobject');
        var id = entry.find('.image_banner_text').attr('id');
        entry.fadeOut('slow', function() {
            var selector = '.Banner .textfields #' + id;
            $(selector).remove();
            entry.remove();
        });
    },
    addNewText: function() {
        var tab = $('.Banner .gs_tab_selected');
        var imgid = tab.attr('imageid');
        var textfield = $('.Banner .textfields');
        var size = 16 * app.Banner.scaled;
        var newtext = $("<div class='image_banner_text' contenteditable='true' imageid='" + imgid + "' x='10' y='10' size='"+size+"'>" + thundashop.base64.encode(__f("New text")) + "</div>");
        textfield.append(newtext);
        var last_drag_object = app.Banner.loadBannerText();
        last_drag_object.find('.image_banner_text').click();
        last_drag_object.find('.image_banner_text').focus();
    },
    deleteBannerImage: function() {
        var tab = $('.Banner .gs_tab_selected');
        tab.attr('imageid', null);
        tab.click();
    },
    sortUpdated: function() {
        var count = 1;
        $('.Banner .gs_tab').each(function() {
            if ($(this).attr('route') === "new") {
                return;
            }
            $(this).attr('route', count);
            $(this).html(count);
            count++;
        });
    },
    changeCropSize: function() {
        if (app.Banner.img === null) {
            return;
        }
        $('.Banner .gs_tab').each(function() {
            $(this).attr('cords', null);
        });
        var appid = $(this).closest('.app').attr('appid');
        var width = $('.skelholder .Banner[appid="' + appid + '"]').width();
        var ratio = width / $(this).val();
        app.Banner.crop_api.setOptions({
            aspectRatio: ratio
        });
        app.Banner.crop_api.animateTo([0, 0, 10000, 10000]);
    },
    uploadBannerImage: function() {
        $(this).doImageUpload(event, $('.add_image_preview_frame'), {
            autosave: true,
            saveOriginal: true,
            saveCropped: false,
            autohideinfobox: false,
            progressCallback: function(progress) {
                $('.Banner .progressbar_container').show();
                $('.Banner .progressbar .progress_completed').css('width', progress + "%");
            },
            callback: function(a, b, c) {
                app.Banner.loadBannerImage(c);
            },
            saveOriginalCallback: function(id) {
                $('.Banner .gs_tab.gs_tab_selected').attr('imageid', id);
                $('.Banner .progressbar_container').fadeOut();
                $('.Banner .image_banner_text').each(function() {
                    if(!$(this).attr('imageid')) {
                        $(this).attr('imageid',id);
                    }
                })
            }
        });
    },
    loadBannerImage: function(src) {
        if (app.Banner.isLoading) {
            return;
        }
        if (!src) {
            $('.Banner .add_image_preview_frame').html('<input type="file" value="' + __f("Insert an image") + '" class="uploadimage" accept="image/*">');
            return;
        }

        app.Banner.isLoading = true;
        var img = new Image();
        img.src = src;
        app.Banner.img = img;

        img.addEventListener('load', function() {
            var jimg = $('<img src="' + img.src + '" style="max-width:100%;">');
            $('.add_image_preview_frame').html(jimg);

             $('.Banner #imagewidth').html(img.width);
             $('.Banner #imageheight').html(img.height);
             $('.Banner #imageoptions').slideDown();
            app.Banner.isLoading = false;
            app.Banner.crop_api = $.Jcrop('.add_image_preview_frame img');
            var appid = $('.app.Banner').attr('appid');
            var width = $('.skelholder .Banner[appid="' + appid + '"]').width();
            var ratio = width / $('.Banner #height').val();
            var tab = $('.Banner .gs_tab_selected');
            var previewimgwidth = $('.Banner .add_image_preview_frame').width();
            app.Banner.scaled = img.width / previewimgwidth;
            var range = [0, 0, 10000, 10000];
            if (tab.attr('cords')) {
                var cords = jQuery.parseJSON(tab.attr('cords'));
                range = [cords.x / app.Banner.scaled, cords.y / app.Banner.scaled, cords.x2 / app.Banner.scaled, cords.y2 / app.Banner.scaled];
            }

            $('.Banner .link_input').show();
            if (tab.attr('link')) {
                $('.Banner .link_input').val(thundashop.base64.decode(tab.attr('link')));
            }
            app.Banner.crop_api.setOptions({
                onRelease: function(c) {
                },
                onSelect: function() {
                },
                onChange: function(c) {
                    c.x = c.x * app.Banner.scaled;
                    c.x2 = c.x2 * app.Banner.scaled;
                    c.y = c.y * app.Banner.scaled;
                    c.y2 = c.y2 * app.Banner.scaled;
                    var cords = JSON.stringify(c);
                    $('.Banner .gs_tab_selected').attr('cords', cords);

                    $('.Banner .add_image_preview_frame .image_banner_text').each(function() {
                        var element = $('.Banner .textfields #' + $(this).attr('id'));
                        var position = $(this).closest('.dragobject').position();
                        app.Banner.updatePosition(element, position);
                    });

                },
                setSelect: range,
                aspectRatio: ratio
            });

            app.Banner.loadBannerText();
        });
    },
    getPositionForText: function(element, top) {
        var tab = $('.Banner .gs_tab_selected');
        var cords = jQuery.parseJSON(tab.attr('cords'));

        var result = (parseInt(element.attr('x')) / app.Banner.scaled) + (parseInt(cords.x) / app.Banner.scaled);
        if (top) {
            result = (parseInt(element.attr('y')) / app.Banner.scaled) + (parseInt(cords.y) / app.Banner.scaled);
        }

        return result;
    },
    loadBannerText: function() {

        $('.Banner .add_image_preview_frame .outerdragobject').remove();

        var imgid = $('.Banner .gs_tab_selected').attr('imageid');
        var id_count = 0;
        var last_drag_object = null;
        
        $('.Banner .image_banner_text').each(function() {
            var element = $(this);
            if (imgid !== $(this).attr('imageid')) {
                $(this).attr('id', '');
                return;
            }

            var x = app.Banner.getPositionForText($(this), false);
            var y = app.Banner.getPositionForText($(this), true);
            var outerDragObject = $('<div style="text-align:left;" class="outerdragobject"><span></span></div>');
            var toolbar = $("<span class='toolbar'>\n\
            <span class='drag'></span>\n\
            <span class='remove'></span>\n\
            </span>");

            var sizetoselect = [8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 36, 48, 76];

            var sizeselector = $("<select></select>");
            for (var key in sizetoselect) {
                sizeselector.append("<option value='" + sizetoselect[key] + "'>" + sizetoselect[key] + "px</option>");
            }

            var size = parseInt($(this).attr('size') / app.Banner.scaled);
            while (true) {
                if (sizetoselect.indexOf(size) >= 0) {
                    sizeselector.val(size);
                    break;
                }
                size++;
            }
            toolbar.append(sizeselector);

            var text = $(this).clone();
            text.css('font-size', sizeselector.val() + "px");
            text.css('color', "#" + $(this).attr('color'));
            var input = $('<span class="text_color_selection"></span>');

            toolbar.append(input);
            input.ColorPicker({
                color: "#" + text.css('color'),
                onShow: function(colpkr) {
                    $(colpkr).fadeIn(500);
                    return false;
                },
                onSubmit: function(colpkr) {
                    $(colpkr).fadeOut();
                    return false;
                },
                onHide: function(colpkr) {
                    $(colpkr).fadeOut();
                    return false;
                },
                onChange: function(hsb, hex, rgb) {
                    $('.Banner .textfields #' + text.attr('id')).attr('color', hex);
                    text.css('color', '#' + hex);
                }
            });

            text.html(thundashop.base64.decode(text.html()));
            text.attr('id', 'banner_text_' + id_count);
            $(this).attr('id', 'banner_text_' + id_count);

            var dragObject = $("<span class='dragobject'></span>");
            dragObject.append(text);
            dragObject.append(toolbar);

            dragObject.css('position', "absolute");
            dragObject.css('z-index', 10000);
            dragObject.css('top', y);
            dragObject.css('left', x);

            outerDragObject.append(dragObject);
            outerDragObject.css('position', 'relative');
            last_drag_object = outerDragObject;
            $('.Banner .add_image_preview_frame').prepend(outerDragObject);
            dragObject.draggable({
                handle: ".drag",
                containment: ".jcrop-tracker",
                stop: function(data) {
                    app.Banner.updatePosition(element, $(this).position());
                }
            });
            id_count++;
        });
        return last_drag_object;
    },
    updatePosition: function(element, position) {
        var tab = $('.Banner .gs_tab_selected');
        var cords = jQuery.parseJSON(tab.attr('cords'));
        element.attr('x', ((position.left - parseInt(cords.x / app.Banner.scaled)) * app.Banner.scaled));
        element.attr('y', ((position.top - parseInt(cords.y / app.Banner.scaled)) * app.Banner.scaled));
    },
    navigateImageTab: function(event, data) {
        var route = data.route;
        if ($('.Banner .progressbar_container').is(':visible')) {
            thundashop.common.Alert(__f('Uploading image'), __f('Navigation has been disabled while image is being uploaded.'), true);
            var tab = $('.Banner .gs_tab').removeClass('gs_tab_selected');
            var tab = $('.Banner .gs_tab[route="' + app.Banner.currrentRoute + '"]').addClass('gs_tab_selected');
            return;
        }
        if (route === "new") {
            var count = $('#informationbox.Banner .gs_tabs .gs_tab').length;
            var tab = $('<div class="gs_tab" target="banner_config_area" route="' + count + '" imageid="">' + count + '</div>');
            $('#informationbox.Banner .gs_tabs .gs_tab[route="new"]').before(tab);
            tab.click();
            return;
        } else {
            var tab = $('.Banner .gs_tab[route="' + route + '"]');
            app.Banner.currrentRoute = route;
            tab.removeClass('no_cords');
            var imageid = tab.attr('imageid');
            if (!imageid) {
                app.Banner.loadBannerImage(false);
                $('.Banner #imageoptions').slideUp();
                $('.Banner .link_input').val('');
                $('.Banner .link_input').hide();
            } else {
                app.Banner.loadBannerImage("displayImage.php?id=" + imageid);
            }
        }
    },
    clear: function() {
        var banners = app.Banner.banners;
        $.each(banners, function(id, v) {
            var timeout = app.Banner.banners[id];
            clearInterval(timeout);
            delete app.Banner.banners[id];
        });
    },
    updateBannerManager: function() {
        if ($('.Banner .progressbar_container').is(':visible')) {
            thundashop.common.Alert(__f('Uploading image'), __f('Navigation has been disabled while image is being uploaded.'), true);
            return;
        }

        var cords = [];
        var images = [];
        var failed = false;
        var links = [];
        $('.Banner .gs_tab').each(function() {
            if ($(this).attr('imageid')) {
                var tabcords = $(this).attr('cords');
                if (!tabcords) {
                    $(this).addClass('no_cords');
                    failed = true;
                }
                images.push($(this).attr('imageid'));
                cords.push(tabcords);
                if (!$(this).attr('link')) {
                    $(this).attr('link', 'gs_empty');
                }
                links.push($(this).attr('link'));

            }
        });
        if (failed) {
            thundashop.common.Alert(__f("Not complete"), __f('Some of the tabs need to be inspected before you can continue'), true);
            return;
        }
        var text = [];
        $('.Banner .textfields .image_banner_text').each(function() {
            var texttosave = {
                "x": $(this).attr('x'),
                "y": $(this).attr('y'),
                "imageid": $(this).attr('imageid'),
                "text": $(this).html(),
                "size": $(this).attr('size'),
                "color": $(this).attr('color')
            };
            text.push(texttosave);
        });

        var height = $('.Banner #height').val();
        var interval = $('.Banner #interval').val();
        var showdots = $('.Banner #showdots').is(':checked');
        var data = {
            "links": links,
            "set": images,
            "height": height,
            "interval": interval,
            "cordinates": cords,
            "showdots": showdots,
            "text": text
        };
        var event = thundashop.Ajax.createEvent('', 'SetSize', $(this), data);
        event.synchron = true;
        thundashop.Ajax.post(event, function() {
            thundashop.framework.reprintPage();
            thundashop.common.hideInformationBox();
        });

    },
    formPosted: function(result, event) {
        if (event.event == "addproducttobanner" || event.event == "removeProductFromBannerImage") {
            thundashop.framework.reprintPage();
        }
    },
    doSearchForProducts: function(event) {
        var target = $(event.target);
        var data = {
            "text": $(this).val(),
            "imageId": $(this).closest('.bannerssettings').attr('imageid')
        }
        var event = thundashop.Ajax.createEvent("", "searchForProduct", target, data);
        var result = thundashop.Ajax.postSynchron(event);
        target.closest('.bannerssettings').find('.productSearchArea').html(result);
    },
    is_int: function(value) {
        if ((parseFloat(value) == parseInt(value)) && !isNaN(value)) {
            return true;
        } else {
            return false;
        }
    },
    imageUploaded: function(msg, data) {
        if (data.appname == "Banner") {
            var target = $('#informationbox');
            var event = thundashop.Ajax.createEvent('Banner', 'showAddBanner', target);
            thundashop.common.showInformationBox(event, __w('Banner settings'));
        }
    },
    start: function(id) {
        app.Banner.banners[id] = {};
        app.Banner.banners[id].id = id;
        app.Banner.banners[id].nextImageCounter = 0;
        app.Banner.banners[id].currentImageCounter = 0;
        app.Banner.banners[id].banners = $("#" + id).find('.banner');
        app.Banner.banners[id].interval = $("#" + id).attr("interval");
        if (app.Banner.banners[id].banners.length === 1) {
            app.Banner.banners[id].interval = 1000000000;
        }
        app.Banner.runner(id);
    },
            
    startTimeout: function(id) {
        var timeout = function() {
            app.Banner.runner(id);
        }

        if (typeof(app.Banner.banners[id].timeOutVar) != "undefined") {
            clearInterval(app.Banner.banners[id].timeOutVar);
        }

        app.Banner.banners[id].timeOutVar = setTimeout(timeout, app.Banner.banners[id].interval);
    },
    goToImage: function(id, counter) {
        app.Banner.banners[id].nextImageCounter = counter;
        app.Banner.runner(id);
    },
            
    adjustTextSize : function(diff, container) {
        $(container).find('.banner_text').each(function() {
            $(this).css('left', $(this).attr('left') * diff);
            $(this).css('top', $(this).attr('top') * diff);
            var fontSize = $(this).attr('fontsize');
            fontSize *= diff;
            $(this).css('font-size', fontSize + "px");
            $(this).attr('imageloaded', true);
            $(this).fadeIn(500);
        });
    },
    adjustBannerText: function(container) {
        var img = new Image();
        var allWidth = $(container).closest('.app').width();
        img.src = $(container).find('img').attr('src');
        if (img.addEventListener) {
            img.addEventListener('load', function() {
                var diff = (allWidth / img.width);
                app.Banner.adjustTextSize(diff, container);
            });
        } else {
            function adjustTextSize() {
                if(img.width > 0) {
                    $(container).find('.banner_text').show();
                    var diff = (allWidth / img.width);
                    app.Banner.adjustTextSize(diff, container);
                } else {
                    setTimeout(adjustTextSize, 200);
                }
            }
            adjustTextSize();
        }
    },
    runner: function(id) {
        if (!$('#' + id).length) {
            return;
        }

        if (!app.Banner.banners[id]) {
            return;
        }

        var nextImageCounter = app.Banner.banners[id].nextImageCounter;
        var banners = app.Banner.banners[id].banners;
        var current = banners[app.Banner.banners[id].currentImageCounter];

        var next = banners[nextImageCounter];
        var imageId = $(next).attr('imageid');
        $('.Banner .banner_text').hide();
        app.Banner.adjustBannerText(next, false);

        $(current).attr('visible', "0");
        $(current).hide();
        
        $(next).attr('visible', "1");
        $(next).show();

        $('.dots .dot').each(function() {
            $(this).removeClass('selected');
        });

        $('.dots .dot[imageId=' + imageId + ']').addClass('selected');

        app.Banner.banners[id].currentImageCounter = nextImageCounter;
        nextImageCounter++;

        if (nextImageCounter >= banners.length) {
            app.Banner.banners[id].nextImageCounter = 0;
        } else {
            app.Banner.banners[id].nextImageCounter = nextImageCounter;
        }

        this.startTimeout(id);
    }
}

$(document).ready(function() {
    app.Banner.init();
});

$(window).resize(function() {
    $('.Banner .banner[visible="1"]').each(function() {
        app.Banner.adjustBannerText($(this));
    });
});
