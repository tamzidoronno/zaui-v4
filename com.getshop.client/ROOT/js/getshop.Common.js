thundashop.app = {};
thundashop.common = {};
app = {};
isNavigating = false;
mousePositionX = 0;
mousePositionY = 0;
thundashop.common.stopNextNavigation = false;
thundashop.common.currentScroll = 0;
getshopmaintoolboxhidden = false;
isAdministrator = false;
isFirstLoading = true;


$(document).mousemove(function(e) {
    mousePositionX = e.pageX;
    mousePositionY = e.pageY;
});

$(document).on('focus', '.gray', function() {
    $(this).removeClass("gray");
    $(this).val('');
});

(function($) {
    $.fn.liveFileupload = function(opts) {
        $(this).on("mouseenter", function() {
            if (!$(this).data("init")) {
                $(this).data("init", true).fileupload(opts);
            }
            return $();
        });
    };
}(jQuery));


getshop_firstload = true;

getUrl = function(hash) {
    if (!hash)
        return;

    if (hash && hash.indexOf("?") === 0) {
        hash = "index.php" + hash;
    }

    return hash;
};

var navigate = function(url, ajaxLink) {
    var useLink = ajaxLink ? ajaxLink : url;
    if (history.pushState) {
        window.history.pushState({url: url, ajaxLink: ajaxLink}, "Title", url);
        thundashop.Ajax.doJavascriptNavigation(useLink, null, true);
    } else {
        window.location.hash = useLink;
    }
};

if (!history.pushState) {
    jQuery(document).ready(function($) {
        $.history.init(function(hash) {
            hash = window.location.hash;
            if (hash === "") {
                if (!getshop_firstload) {
                    hash = '?page=clear_page';
                }
                getshop_firstload = false;
            }
            thundashop.Ajax.doJavascriptNavigation(hash, null, true);
        },
                {
                    unescape: ",/"
                });
    });
}

if (history.pushState) {
    window.onpopstate = function(event) {
        if(isFirstLoading) {
             isFirstLoading=false;
             return;
        }
        if (event.state) {
            var url = event.state.ajaxLink ? event.state.ajaxLink : event.state.url;
            thundashop.Ajax.doJavascriptNavigation(url, null, true);
        } else {
            thundashop.Ajax.doJavascriptNavigation("?page={HOMEPAGE}", null, true);
        }
    }
}
;

$(function() {
    $(document).on('click', '#skeleton a', function(event) {
        var comp = new RegExp(location.host);
        if (!comp.test($(this).attr('href')) && !($(this).attr('href').indexOf('?') === 0 || $(this).attr('href').indexOf('/') === 0)) {
            return;
        }

        if ($(this).attr('href').indexOf('generatePDF.php') > -1) {
            return;
        }
        
        event.stopPropagation();
        event.preventDefault();

        if (thundashop.common.stopNextNavigation) {
            thundashop.common.stopNextNavigation = false;
            return;
        }

        var target = $(event.target);

        var errorBox = $(document).find('.errors');
        if (errorBox)
            $(errorBox).slideUp(100);

        if (target.parents('.cke_top').length > 0) {
            return;
        }

        var link = $(this).attr('href');

        var url = getUrl(link);

        if (link.indexOf(".html") > -1 || link.indexOf(".htm") > -1) {
            link = "?rewrite=" + encodeURIComponent(link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf(".")));
        }
        var ajaxLink = getUrl(link);
        navigate(url, ajaxLink);

        if ($(this).attr('scrolltop')) {
            if ($(this).attr('scrolltop').length > 0) {
                $(window).scrollTop(0);
            }
        }
    });

});

$(document).on('click', '.errorform .close', function(event) {
    $("#errorbox").hide();
});

thundashop.common.gsToggleButton = function() {
    if ($(this).hasClass('pressed')) {
        $(this).removeClass('pressed');
    } else {
        $(this).addClass('pressed');
    }
};

thundashop.common.addNotificationProgress = function(id, text) {
    var infoPanel = $('.upload_information_panel');
    infoPanel.append('<div class="uploading ' + id + '"><img src="/skin/default/images/ajaxloader.gif"> ' + text + "</div>");
    $('.upload_information_panel').show();
};

thundashop.common.removeNotificationProgress = function(id) {
    var infoPanel = $('.upload_information_panel');
    infoPanel.find('.' + id).fadeOut('slow', function() {
        $(this).remove();
        if (infoPanel.find('.uploading').length === 0) {
            infoPanel.hide();
        }
    });
};

thundashop.common.updateColorsFromPicker = function() {
    var bgcolor = $('.GetShopColorPicker #bgcolor').val();
    var basecolor = $('.GetShopColorPicker #basecolor').val();
    var textcolor = $('.GetShopColorPicker #textcolor').val();
    var buttoncolor = $('.GetShopColorPicker #buttoncolor').val();
    var buttontextcolor = $('.GetShopColorPicker #buttontextcolor').val();

    var template = $('.GetShopColorPicker #theeme').val();
    var colors = basecolor + ":" + textcolor + ":" + buttoncolor + ":" + buttontextcolor + ":" + bgcolor;

    thundashop.Ajax.changeTheeme(template, colors);
};

thundashop.common.notifyCKEditors = function() {
    var retdata = {}
    if (typeof(CKEDITOR) !== undefined) {
        for (var key in CKEDITOR.instances) {
            try {
                CKEDITOR.instances[key].fire('blur');
            } catch (e) {
                //ignore if it fails for this entry.
            }
        }
    }
};

thundashop.common.destroyCKEditors = function() {
    var retdata = {}
    if (typeof(CKEDITOR) !== "undefined") {
        for (var key in CKEDITOR.instances) {
            try {
                if (CKEDITOR.instances[key].checkDirty()) {
                    var target = $('#' + CKEDITOR.instances[key].name);
                    var data = CKEDITOR.instances[key].getData();
                    thundashop.common.saveCKEditor(data, target);
                }
            } catch (e) {
            }
        }
    }


    return retdata;
}
thundashop.common.saveCKEditor = function(data, target, notify) {
    var altid = target.attr('altid');
    var event = thundashop.Ajax.createEvent(null, 'saveContent', target, {
        "content": data,
        "altid": altid
    });
    var text = $("<p>" + data + "</p>").text();
    text = text.replace(/^\s+|\s+$/g, "");

    var notified = false;
    if (text.length === 0) {
        thundashop.Ajax.post(event, notify);
        notified = true;
    } else {
        thundashop.Ajax.postSynchron(event);
    }
    thundashop.common.removeNotificationProgress('contentmanager');
    return notified;
};
thundashop.common.lastPushId = null;
thundashop.common.goToPage = function(id) {
    var link = "?page="+id;
    if(thundashop.common.lastPushId === null || thundashop.common.lastPushId !== id) {
        if(window.history.pushState !== undefined) {
            window.history.pushState({url: link, ajaxLink: link}, "Title", link);
        }
    }
    thundashop.Ajax.doJavascriptNavigation(link, null, true);
}

thundashop.common.selectPredefinedConent = function() {
    var data = {
        config: $(this).attr('config'),
        type: $(this).attr('type'),
        index : $(this).attr('index')
    };
    data = thundashop.common.appendDefaultLayoutData(data);
    data["pagetype"] = $(this).attr('pagetype');
    
    if(data["pagemode"] !== "new") {
        var confirm = thundashop.common.confirm(__f("This will remove all original content for this page, are you sure about this? Use the page layout tab instead if you need to change the layout and keep its page data"));        
        if(!confirm) {
            return;
        }
    }
    

    $('#informationbox').html('<center><i class="fa fa-spinner fa-spin" style="font-size:40px;"></i></center>');
    var event = thundashop.Ajax.createEvent('', 'selectPredefinedData', $(this), data);
    
    thundashop.Ajax.postWithCallBack(event, function(data) {
        thundashop.common.hideInformationBox();
        thundashop.common.goToPage(data);
    });
};

thundashop.common.navigateContentPages = function() {
    $(document).off('click', '.gs_showPageLayoutSelection .option_entry');
    var target = $(this).attr('target');
    var button = $(this);
    var box = $(this).closest('.gs_showPageLayoutSelection');
    box.find('.option_selected').removeClass('option_selected');
    button.addClass('option_selected');

    $('.gs_showPageLayoutSelection .options_content').scrollTop(0);

    box.find('.content_type_selected').hide();
    $(this).removeClass('content_type_selected');
    box.find("." + target).fadeIn(0, function() {
        $(this).addClass('content_type_selected');
        $(document).on('click', '.gs_showPageLayoutSelection .option_entry', thundashop.common.navigateContentPages);
    });
}

thundashop.common.activateCKEditor = function(id, config) {
    var autogrow = false;
    var showMenu = true;
    var autofocus = true;
    var notinline = false;
    var notdestroyonblur = false;
    var saveCallback = false;
    var pushToBackend = true;
    var destroyOnSave = true;

    if (config !== undefined) {
        if (config.autogrow !== undefined)
            autogrow = config.autogrow;
        if (config.showMenu !== undefined)
            showMenu = config.showMenu;
        if (config.autofocus !== undefined)
            autofocus = config.autofocus;
        if (config.notinline !== undefined)
            notinline = config.notinline;
        if (config.notdestroyonblur !== undefined)
            notdestroyonblur = config.notdestroyonblur;
        if (config.saveCallback !== undefined)
            saveCallback = config.saveCallback;
        if (config.pushToBackend !== undefined)
            pushToBackend = config.pushToBackend;
        if (config.destroyOnSave !== undefined)
            destroyOnSave = config.destroyOnSave;
    }

    var target = $('#' + id);
    target.attr('contenteditable', true);
    if (notdestroyonblur === undefined) {
        notdestroyonblur = false;
    }
    var toBeRemoved = 'magiclines';
    if (notinline) {
        toBeRemoved += ",save"
    }

    var notify = function() {
        PubSub.publish("CKEDITOR_SAVED", {});
    }

    var config = {
        filebrowserImageUploadUrl: 'uploadFile.php',
        enterMode: CKEDITOR.ENTER_BR,
        removePlugins: toBeRemoved,
        on: {
            blur: function(event) {
                var data = event.editor.getData();
                if (notdestroyonblur) {
                    return;
                }
                if (saveCallback) {
                    saveCallback(data);
                }
                var data = event.editor.getData();
                if (!notinline) {
                    if (pushToBackend) {
                        thundashop.common.addNotificationProgress('contentmanager', "Saving content");
                        var notified = thundashop.common.saveCKEditor(data, target, notify);
                        if (!notified) {
                            notify();
                        }
                    }
                }
                event.editor.destroy();
                target.attr('contenteditable', false);
                $('.ui-tooltip').remove();
            },
            save: function(event) {
                $('.ui-tooltip').remove();
                var data = event.editor.getData();
                if (saveCallback) {
                    saveCallback(data);
                }
                if (!notinline) {
                    if (pushToBackend) {
                        thundashop.common.addNotificationProgress('contentmanager', "Saving content");
                        var notified = thundashop.common.saveCKEditor(data, target, notify);
                        if (!notified) {
                            notify();
                        }
                    }
                    target.attr('contenteditable', false);
                    target.blur();
                    if (destroyOnSave) {
                        event.editor.destroy();
                    }
                }
                $(document).tooltip("enable");
            },
            focus: function() {
                $('.cke_editable_inline').attr('title', '');
            },
            instanceReady: function(ev) {
                if (autofocus) {
                    ev.editor.focus();
                }
            }
        }
    }

    if (showMenu === false) {
        config.toolbar = [];
    }
    if (autogrow === false) {
        config.autoGrow_onStartup = false;
        config.resize_enabled = false;
        config.autoGrow_maxHeight = 10;
        config.autoGrow_minHeight = 10;
    }

    CKEDITOR.disableAutoInline = false;
    if (notinline) {
        config.extraPlugins = 'autogrow';
        config.width = '100%';
        CKEDITOR.replace(id, config);
    } else {
        CKEDITOR.inline(id, config);
    }
}

thundashop.common.resizeOrderMask = function() {
    $('.order_mask').each(function() {
        $(this).width($(this).closest('.app').width() - 2);
        $(this).height($(this).closest('.app').height());
    });
}

thundashop.common.hideInformationBox = function(event) {
    return thundashop.common.unmask();
}

thundashop.common.showLargeInformationBox = function(event, title) {
    var box = thundashop.common.showInformationBox(event, title);
//    box.removeClass('normalinformationbox');
//    box.addClass('largeinformationbox');
}

thundashop.common.createInformationBox = function(appid, title, open) {
    var infoBoxHolder = $('#informationbox-holder');
    var infoBox = $('#informationbox');
    if (open) {
        thundashop.MainMenu.lockScroll();
        thundashop.common.mask();
        infoBoxHolder.css('display', 'inline-block');
    } else {
        infoBox = infoBox.clone();
    }
    infoBox.attr('class', '');
    infoBox.addClass('informationboxbackground');
    infoBox.addClass('informationbox');
    infoBox.attr('appid', appid);
    infoBox.addClass('app');
    if (open) {
        $('#informationboxtitle').html(title);
    }
    infoBox.addClass('normalinformationbox');
    infoBox.removeClass('largeinformationbox');
    $('.informationbox-outer').css('overflow','hidden');
    $('.informationbox-outer').fadeIn("200", function() {
        $('.informationbox-outer').css('overflow-y','scroll');
        $('body').css('overflow', 'hidden');
    });
    return infoBox;

}

thundashop.common.showInformationBox = function(event, title, avoidScroll) {
    var alreadyvisible = false;
    var timer = 300;
    if($('#informationbox').is(':visible')) {
        alreadyvisible = true;
        timer = 0;
    }
    
    if (typeof(title) === "undefined")
        title = "";
    var appid = null;
    if(event !== undefined) {
        if (event.core.appid !== undefined) {
            appid = event.core.appid;
        }
    }
    
    var infoBox = thundashop.common.createInformationBox(appid, title, true);
    if(event !== undefined) {
        if (event.core.appname === undefined) {
            event.core.appname = "";
        }
        if (event.core.apparea === undefined) {
            event.core.apparea = "";
        }
        infoBox.attr('app', event.core.appname);
        infoBox.attr('apparea', event.core.apparea);
        infoBox.addClass(event.core.appname);
    }
    
    if(!alreadyvisible) {
        infoBox.html('<div style="font-size:35px; text-align:center; color:#3f3f3f;padding-top: 40px; "><i class="fa fa-spinner fa-spin"></i></div>');
    }
    if(event !== undefined) {
        var result = thundashop.Ajax.postSynchron(event);
        setTimeout(function() {
            infoBox.html(result);
            thundashop.common.setMaskHeight();
        }, timer);
    }
    if (!avoidScroll) {
        $('.informationbox-outer').scrollTop(0);
    }

    return infoBox;
}

thundashop.common.setMaskHeight = function() {
    var height = $(document).height();
    $('#fullscreenmask').height(height);
}

$(document).on('click', '#messagebox .okbutton', function() {
    $('#messagebox').fadeOut(200);
});

thundashop.common.Alert = function(title, message, error) {
    $("#messagebox").find('.title').html(title);
    if (typeof(message) === "undefined")
        message = "";

    $("#messagebox").removeClass('error');
    if (error === true)
        $("#messagebox").addClass('error');

    $("#messagebox").find('.description').html(message);
    $("#messagebox").show();

    if (!error)
        $("#messagebox").delay(2000).fadeOut(200);
}

thundashop.common.mask = function() {
    var height = $(document).height();
    $('#fullscreenmask').height(height);
    $("#fullscreenmask").fadeIn();
}


thundashop.common.disableCloseInformationBox = function() {
    $('#infomrationboxclosebutton').hide();
    $(document).off('click', ".informationbox-outer");
    $('body').attr('infoboxdisabled', true)
}


thundashop.common.unmask = function() {
    if($('body').attr('infoboxdisabled')) {
        $('body').attr('infoboxdisabled', null);
        $('#infomrationboxclosebutton').show();
        $(document).on('click', ".informationbox-outer", thundashop.common.closeInfoboxEvent);
    }
    var result = $.Deferred();
    var attr = $('#fullscreenmask').attr('locked');
    $('.informationbox-outer').css('overflow-y','hidden');
    $('body').css('overflow', 'auto');
    $('#informationbox-holder').fadeOut(200, function() {
        $('.informationbox-outer').fadeOut(200, function() {
            if (typeof(attr) === "undefined" || attr === "false") {
                $('#fullscreenmask').hide();
                result.resolve();
            }
        });
    });
    return result;
}

thundashop.common.lockMask = function() {
    $('#infomrationboxclosebutton').hide();
    $('#fullscreenmask').attr('locked', 'true');
}

$(window).resize(function() {
    thundashop.common.setSizeClasses();
});

thundashop.common.setSizeClasses = function() {
    var windowwidth = $(window).width();
    $(document).find('.applicationarea').each(function() {

        $(this).removeClass('small');
        $(this).removeClass('large');
        $(this).removeClass('medium');
        $(this).removeClass('xlarge');

        var width = $(this).innerWidth();
        var css = "";
        if (width > 750 && windowwidth > 750) {
            css = "xlarge";
        } else if (width > 400 && windowwidth > 400) {
            css = "large";
        } else if (width > 200 && windowwidth > 200) {
            css = "medium";
        } else {
            css = "small";
        }
        $(this).addClass(css);
        $(this).attr('size', css);
    });
}

thundashop.common.unlockMask = function() {
    $('#infomrationboxclosebutton').show();
    $('#fullscreenmask').attr('locked', 'false');
}

thundashop.common.confirm = function(content) {
    debugger;
    return confirm(content);
}
thundashop.common.hideEmptyList = function(event) {
    $(this).off('click',thundashop.common.hideEmptyList);
    var target = $(event.target).attr('target');
    if(target === undefined) {
        target = $(event.target).parent().attr('target');
    }
    
    $('.addcontent_menu').each(function() {
        if(target === undefined || !$(this).hasClass(target)) {
            $(this).hide();
        }
    });
}

thundashop.common.closeInfoboxEvent = function(event) {
    if ($(event.target).hasClass('informationbox-outer')) {
        thundashop.common.unmask();
    }
}

$(document).on('click', ".informationbox-outer", thundashop.common.closeInfoboxEvent);


$(document).on('click', '.display_menu_application_button', function() {
    $('.mainmenu').slideDown();

    $(document).on('click.hidemenuentry', function(e) {
        var target = $(e.target);
        if (target.hasClass('display_menu_application_button')) {
            return;
        }
        if (target.parents('.mainmenu').size() > 0) {
            return;
        }
        if ($('.mainmenu').is(':visible')) {
            $('.mainmenu').slideUp();
        }
        $(document).off('click.hidemenuentry');
    });

});


$(document).on('click', '.configuration', function() {
    $(this).find('.entries').slideDown();
});

$(document).on('mouseenter', '.configable', function() {
    $(this).attr('interrupt', "true");
    $(this).find('.configuration').fadeIn(300);
});
$(document).on('mouseleave', '.configable', function() {
    var mongovar = $(this);
    var mongo = function(e) {
        if (mongovar.attr('interrupt') === "true") {
            return;
        }
        mongovar.find('.configuration').hide();
        mongovar.find('.entries').hide();
    }
    $(this).attr('interrupt', "false");
    if ($(document).find('.entries').is(':visible')) {
        setTimeout(mongo, "200");
    } else {
        setTimeout(mongo, "0");
    }
});

getImageElement = function(scope) {
    var searchFrom = $(scope).attr('searchFrom');
    var imageClassTag = $(scope).attr('imageTo');
    return $(scope).closest('.' + searchFrom).find('.' + imageClassTag);
}

$(document).on('mouseenter', '.editormode .deleteable', function() {
    if ($(this).find('#file_selection').size() === 0) {
        $(this).find('.trash').show();
    }
});
$(document).on('mouseleave', '.editormode .deleteable', function() {
    $(this).find('.trash').hide();
});


$(document).on('click', '.editormode .deleteable .trash', function(e) {
    var imgId = $(this).attr('imageId');
    var res = $(this);
    var event = thundashop.Ajax.createEvent('MainMenu', 'deleteImage', res, {
        imageId: imgId
    });
    thundashop.Ajax.post(event);
});

$(document).on('mouseenter', '.imageUploader', function() {
    if (!$(this).data("init")) {
        $(this).data("init", true);
        $(this).fileupload({
            url: 'handler.php',
            sequentialUploads: true,
            add: function(e, data) {
                getImageElement(this).prepend("<div id='progress'></div>");
                data.submit();
            },
            start: function(e, data) {
                $(this).find('#progress').html('0% Complete');
            },
            progress: function(e, data) {
                var imageContainer = getImageElement(this);
                var image = imageContainer.find('img');

                if (image)
                    image.remove();

                var percentage = Math.round((e.loaded / e.total) * 100);
                imageContainer.find('#progress').html(percentage + '% Complete');
            },
            done: function(e, data) {
                var id = data.result.imageId;
                var rand = Math.random();
                var width = $(this).attr('width');
                var height = $(this).attr('height');

                var imageContainer = getImageElement(this);
                imageContainer.find('#progress').remove();
                imageContainer.html('<img gsname="imgId" gsvalue="' + id + '" src="displayImage.php?id=' + id + '&rand=' + rand + '&height=' + height + '&width=' + width + '">');
                imageContainer.prepend('<span imageId="' + id + '" class="trash"></span>');

                var dataMessage = {
                    result: data.result,
                    appname: $(this).closest('.app').attr('app')
                };

                PubSub.publish('IMAGE_UPLOADED', dataMessage);
            }
        });
    }
});

$(document).on('click', '#informationbox .settings .entry, #settingsarea .setting, #informationbox .setting', function(event) {
    if ($(this).find('.onoff')) {
        var data = {
            id: $(this).attr('id')
        }

        data.config = {};
        data.entry = $(this);

        if ($(this).find('.configobject')[0]) {
            $.each($(this).find('.configobject')[0].attributes, function(i, attrib) {
                data.config[attrib.name] = attrib.value;
            });
        }

        if ($(this).find('.onoff').hasClass("on")) {
            $(this).find('.onoff').addClass('off');
            $(this).find('.onoff').removeClass('on');
            data.state = 'off'
        } else {
            data.state = 'on'
            $(this).find('.onoff').addClass('on');
            $(this).find('.onoff').removeClass('off');
        }
        PubSub.publish("setting_switch_toggled", data);
    }
});

$('#cookiewarning_overlay .textbox .continue').on('click', function() {
    $('#cookiewarning_overlay').remove();
    var event = thundashop.Ajax.createEvent('', 'CookieAccepted', $(this), '');
    thundashop.Ajax.postWithCallBack(event, function() {

    });
});

$(document).on('click', '#infomrationboxclosebutton', function(e) {
    thundashop.common.hideInformationBox(null);
})

$(document).on("keyup", function(e) {
    if (e.keyCode == 27) {
        thundashop.common.hideInformationBox();
    }
});

$(document).on('click', '.tabset .tab', function() {
    var tabs = $(this).closest('.tabs');
    tabs.find('.active').each(function() {
        $(this).removeClass('active');
    });
    $(this).addClass('active');
    var activate = $(this).attr('activate');
    tabs.find('#' + activate).addClass('active');
});

$(function() {
    $(document).on('mousedown', '#getshop_logout', function() {
        var event = thundashop.Ajax.createEvent(null, 'logout', $(this), {});
        thundashop.Ajax.postSynchron(event);
        window.location = "/";
    });
});

$(document).on('mouseover', '.add_application_menu .button-large', function() {
    $(this).find('.filler').css('background-color', '#243da9');
});

$(document).on('mouseleave', '.add_application_menu .button-large', function() {
    $(this).find('.filler').css('background-color', '#009b00');
});

$(document).on('click', '.selectablegroup', function(event) {

    $(this).find('.selectable').each(function() {
        $(this).removeAttr("gsname");
        $(this).removeClass('selected');
    });

    var target = event.target;
    $(target).addClass("selected");
    $(target).attr("gsname", "selected");
});
$(document).on('mouseenter', '.getshop_ckeditorcontent', function() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
    }
    ;

    function guid() {
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                s4() + '-' + s4() + s4() + s4();
    }
    if (!$(this).hasClass('cke_editable')) {
        var id = guid();
        $(this).attr('id', id);
        thundashop.common.activateCKEditor(id, {
            "autofocus": false
        });
        $(this).addClass('cke_focus');
    }
});

$(document).on('mouseleave', '.getshop_ckeditorcontent', function() {
    if (!$(this).hasClass('cke_focus')) {
        var id = $(this).attr('id');
        CKEDITOR.instances[id].destroy();
    }
});
$(function() {

    PubSub.subscribe('NAVIGATION_COMPLETED', thundashop.common.setSizeClasses);
    thundashop.common.setSizeClasses();

    $(document).on('click', '.add_content_img', function() {
        var appid = $(this).attr('appid');
        var area = $(this).closest('.applicationarea').attr('area');
        var event = thundashop.Ajax.createEvent('', 'addApplicationToArea', null, {
            "appSettingsId": appid,
            "applicationArea": area
        });
        thundashop.Ajax.post(event);
    });
    $(document).on('click', '.add_content_menu_icon', function() {
        var position = $(this).position();
        $('.ui-tooltip').remove();
        var target = $(this).attr('target');
        var container = $(this).closest('.empty_app_area');
        var target_obj = container.find('.'+target);
        target_obj.css('left',position.left);
        target_obj.css('top',"42px");
        $('.addcontent_menu').hide();
        $(document).on('click', thundashop.common.hideEmptyList);
        target_obj.slideDown();
        
    });
    $(document).on('click','.add_new_product', function() {
        var target = $(this).closest('.applicationarea').attr('area');
        app.Product.create(target, "area");
    });
    $(document).on('click','.add_existing_product', function() {
        var test = new ProductPicker($(this), {
            "type" : "single", 
            "subtype" : "area",
            "area" : $(this).closest('.applicationarea').attr('area')
        });
        test.load();
    });
    $(document).on('click', '.empty_app_area_browse_apps', function() {
        var area = $(this).closest('.applicationarea').attr('area');
        var size = $(this).closest('.applicationarea').attr('size');
        var type = $(this).closest('.applicationarea').attr('type');

        var event = thundashop.Ajax.createEvent('', 'showApplications', null, {
            "area": area,
            "size": size,
            "type": type
        });
        thundashop.common.showInformationBox(event, __f("Please select an application"));
    });
    $(document).on('click', '.empty_app_area_browse_importapps', function() {
        var area = $(this).closest('.applicationarea').attr('area');
        var type = $(this).closest('.applicationarea').attr('type');
        thundashop.MainMenu.importApplicationClicked(area, type);
    });
    $(document).on('click', '.empty_app_area_add_youtube', function() {
        var appid = $(this).attr('appid');
        var area = $(this).closest('.applicationarea').attr('area');
        var event = thundashop.Ajax.createEvent('', 'addApplicationToArea', null, {
            "appSettingsId": appid,
            "applicationArea": area
        });
        thundashop.Ajax.post(event);
    });
    $(document).on('click', '.application_settings', function(event) {
        var app = $(this).closest('.app');
        var appname = app.attr('app');
        $('.GetShopToolbox[attached_to_app="true"]').remove();
        window["app"][appname]["loadSettings"]($(this), app);
    });

    $(document).on('mouseenter', '.app', function() {
        var appname = $(this).attr('app');
        if (app[appname] !== undefined && app[appname]['loadSettings'] !== undefined) {
            if ($(this).find('.order_mask').is(':visible')) {
                return;
            }
            var settingsbox = $(this).find('.application_settings');
            $(this).find('.application_settings').show();
        }
    });
    $(document).on('mouseleave', '.app', function() {
        $(this).find('.application_settings').hide();
    });
});

getText = function(text) {
    if(typeof(translationMatrix) === "undefined") {
        return text;
    }
    if (translationMatrix[text])
        return translationMatrix[text]

    return text;
};

__f = function(text) {
    return getText(text);
};

__w = function(text) {
    return getText(text);
};

__o = function(text) {
    return getText(text);
};

$(document).ready(function() {
    var cookieWarning = $('#cookiewarning_overlay');
    if (cookieWarning.length > 0) {
        cookieWarning.fadeIn(200);
    }
});

$(document).on('click', '.gs_tab', function() {
    var app = $(this).closest('.app');
    app.find('.gs_tab').removeClass('gs_tab_selected');
    $(this).addClass('gs_tab_selected');
    var target = $(this).attr('target');
    console.log(target);
    app.find('.gs_tab_area').removeClass('gs_tab_area_active');
    app.find('.' + target).addClass('gs_tab_area_active');
    var route = $(this).attr('route');
    PubSub.publish("GS_TAB_NAVIGATED", {"target": target, "route": route});
});
$(document).on('change', '#informationbox .skeletondisplayer select', function() {
    var data = {};
    var id = $(this).attr('id');
    data[id] = $(this).val();
    if ($(this).closest('.row_option_panel').length > 0) {
        data["index"] = $(this).closest('.row_option_panel').attr('row');
    }
    data["updatelayout"] = true;
    data = thundashop.common.appendDefaultLayoutData(data);

    var event = thundashop.Ajax.createEvent('', 'showPageLayoutSelection', null, data);
    thundashop.common.showInformationBox(event, __f("Select page layout"), true);
});

thundashop.common.appendDefaultLayoutData = function(data) {
    data["pagetype"] = $('#informationbox').find('#gs_newpage_type').val();
    data["pageSubType"] = $('#informationbox').find('#gs_newpage_subtype').val();
    data["target"] = $('#informationbox').find('#gs_newpage_target').val();
    data["pagemode"] = $('#informationbox').find('#gs_pagelayout_mode').val();
    return data;
}

$(document).on('click', '.gs_showPageLayoutSelection .suggestion_layout', function() {
    var type = $(this).attr('type');
    var data = {
        "layout": type
    };
    data = thundashop.common.appendDefaultLayoutData(data);
    var id = $(this).attr('id');
    data["updatelayout"] = true;
    var event = thundashop.Ajax.createEvent('', 'setPageLayout', null, data);
    thundashop.Ajax.postWithCallBack(event, function(data) {
        thundashop.common.hideInformationBox();
        thundashop.common.goToPage(data);
    });
});
$(document).on('click', '#informationbox .row_option', function() {
    var infobox = $('#informationbox .row_option_panel');
    infobox.attr('row', $(this).attr('index'));
    infobox.find('#numberofcells').val($(this).attr('cells'));
    infobox.css('position', 'absolute');
    infobox.css('display', 'block');
    infobox.css('top', $(this).parent().position().top);
    infobox.left('left', $(this).position().left);
    infobox.show();
});
$(document).on('click', '#informationbox .setnewlayout', function() {
    var type = $(this).attr('type');
    var data = {
        "updateToNewLayout": true
    };
    data = thundashop.common.appendDefaultLayoutData(data);

    var id = $(this).attr('id');
    data["updatelayout"] = true;
    var event = thundashop.Ajax.createEvent('', 'setPageLayout', null, data);
    thundashop.Ajax.postWithCallBack(event, function(data) {
        thundashop.common.hideInformationBox();
        thundashop.common.goToPage(data);
    });
});

$(document).on('click', '.gs_onoff', function() {
    if ($(this).hasClass('gs_on')) {
        $(this).removeClass('gs_on');
        $(this).addClass('gs_off');
    } else {
        $(this).removeClass('gs_off');
        $(this).addClass('gs_on');
    }
});
$(document).on('click', '.gs_showPageLayoutSelection .option_entry', thundashop.common.navigateContentPages);
$(document).on('click', '.layoutpreviewselection .layoutpreview', thundashop.common.selectPredefinedConent);

$(document).on('click', '.gs_toggle_button', thundashop.common.gsToggleButton);