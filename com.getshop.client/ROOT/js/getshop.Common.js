

thundashop.app = {};
thundashop.common = {};
app = {};
isNavigating = false;
mousePositionX = 0;
mousePositionY = 0;
thundashop.common.stopNextNavigation = false;
thundashop.common.currentScroll = 0;


$(document).mousemove(function(e) {
    mousePositionX = e.pageX;
    mousePositionY = e.pageY;
});

$('.gray').live('focus', function() {
    $(this).removeClass("gray");
    $(this).val('');
});

(function($) {
    $.fn.liveFileupload = function(opts) {
        this.live("mouseover", function() {
            if (!$(this).data("init")) {
                $(this).data("init", true).fileupload(opts);
            }
            return $();
        });
    };
}(jQuery));


$(function() {
    $('#skeleton a').live('click', function(event) {
        var comp = new RegExp(location.host);
        if (!comp.test($(this).attr('href')) && !($(this).attr('href').indexOf('?') === 0)) {
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
        window.location.hash = $(this).attr('href');
        if ($(this).attr('scrolltop')) {
            if ($(this).attr('scrolltop').length > 0) {
                $(window).scrollTop(0);
            }
        }
    });

});

$('.errorform .close').live('click', function(event) {
    $("#errorbox").hide();
});


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
thundashop.common.saveCKEditor = function(data, target) {
    var altid = target.attr('altid');
    var event = thundashop.Ajax.createEvent(null, 'saveContent', target, {
        "content": data,
        "altid": altid
    });
    var text = $("<p>" + data + "</p>").text();
    text = text.replace(/^\s+|\s+$/g, "");
    var notify = function() {
        PubSub.publish("CKEDITOR_SAVED", {});
    }
    if (text.length === 0) {
        thundashop.Ajax.post(event, notify);
    } else {
        thundashop.Ajax.postSynchron(event);
        notify();
    }
    thundashop.common.removeNotificationProgress('contentmanager');
};

thundashop.common.activateCKEditor = function(id, autogrow, showMenu, autofocus, notinline) {
    var target = $('#' + id);
    target.attr('contenteditable', true);
    var toBeRemoved = 'magiclines';
    if(notinline) {
        toBeRemoved += ",save"
    }

    var config = {
        filebrowserImageUploadUrl: 'uploadFile.php',
        enterMode: CKEDITOR.ENTER_BR,
        removePlugins: toBeRemoved,
        on: {
            blur: function(event) {
                var data = event.editor.getData();
                if(!notinline) {
                    thundashop.common.addNotificationProgress('contentmanager', "Saving content");
                    thundashop.common.saveCKEditor(data, target);
                    event.editor.destroy();
                }
                $(document).tooltip("enable");
            },
            focus: function() {
                $(document).tooltip("disable");
            },
            instanceReady: function(ev) {
                if(autofocus) {
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
    if(notinline) {
        config.extraPlugins = 'autogrow';
        config.width = '100%';
        CKEDITOR.replace(id, config);
    } else {
        CKEDITOR.inline(id, config);
    }
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
    }
    infoBox.attr('class', '');
    infoBox.addClass('informationboxbackground');
    infoBox.addClass('informationbox');
    infoBox.attr('appid', appid);
    infoBox.addClass('app');
    $('#informationboxtitle').html(title);
    infoBox.addClass('normalinformationbox');
    infoBox.removeClass('largeinformationbox');
    $('body').css('overflow','hidden');
    $('.informationbox-outer').show();

    return infoBox;

}

thundashop.common.showInformationBox = function(event, title) {
    if (typeof(title) === "undefined")
        title = "";
    var appid = null;
    if (event.core.appid !== undefined) {
        appid = event.core.appid;
    }
    var infoBox = thundashop.common.createInformationBox(appid, title, true);
    infoBox.attr('app', event.core.appname);
    infoBox.attr('apparea', event.core.apparea);
    infoBox.addClass(event.core.appname);

    var result = thundashop.Ajax.postSynchron(event);
    infoBox.html(result);
    setTimeout(thundashop.common.setMaskHeight, "200");
    return infoBox;
}

thundashop.common.setMaskHeight = function() {
    var height = $(document).height();
    $('#fullscreenmask').height(height);
}

$('#messagebox .okbutton').live('click', function() {
    $('#messagebox').fadeOut(200);
});

thundashop.common.Alert = function(title, message, error) {
    $("#messagebox").find('.title').html(title);
    if (typeof(message) == "undefined")
        message = "";

    $("#messagebox").removeClass('error');
    if (error === true)
        $("#messagebox").addClass('error');

    $("#messagebox").find('.description').html(message);
    $("#messagebox").show();

    if (!error)
        $("#messagebox").delay(800).fadeOut(200);
}

thundashop.common.mask = function() {
    var height = $(document).height();
    $('#fullscreenmask').height(height);
    $("#fullscreenmask").show();
}

thundashop.common.unmask = function() {
    var result = $.Deferred();
    var attr = $('#fullscreenmask').attr('locked');
    $('.informationbox-outer').hide();
    $('body').css('overflow','scroll');
    if (typeof(attr) === "undefined" || attr === "false") {
        $('#informationbox-holder').fadeOut(200);
        $('#fullscreenmask').fadeOut(200, function() {
            result.resolve();
        });
        thundashop.MainMenu.unlockScroll();
    }
    return result;
}

thundashop.common.lockMask = function() {
    $('#infomrationboxclosebutton').hide();
    $('#fullscreenmask').attr('locked', 'true');
}

thundashop.common.setSizeClasses = function() {
    $(document).find('.applicationarea').each(function() {
        var width = $(this).innerWidth();
        var css = "";
        if (width > 750) {
            css = "xlarge";
        } else if (width > 400) {
            css = "large";
        } else if (width > 200) {
            css = "medium";
        } else {
            css = "small";
        }
        $(this).addClass(css);
    });
}

thundashop.common.unlockMask = function() {
    $('#infomrationboxclosebutton').show();
    $('#fullscreenmask').attr('locked', 'false');
}

thundashop.common.confirm = function(content) {
    return confirm(content);
}

$("#fullscreenmask").live('click', function() {
    thundashop.common.unmask();
});
$(".informationbox-outer").live('click', function(event) {
    if($(event.target).hasClass('informationbox-outer')) {
        thundashop.common.unmask();
    }
});

$('.display_menu_application_button').live('click', function() {
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

jQuery(document).ready(function($) {

    $.history.init(function(hash) {
        if (hash != "") {
            thundashop.Ajax.doJavascriptNavigation(hash, null, true);
        }
    },
            {
                unescape: ",/"
            });
});

$('.configuration').live('click', function() {
    $(this).find('.entries').slideDown();
});

$('.configable').live({
    mouseenter: function() {
        $(this).attr('interrupt', "true");
        $(this).find('.configuration').fadeIn(300);
    },
    mouseleave: function() {
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
    }
});

getImageElement = function(scope) {
    var searchFrom = $(scope).attr('searchFrom');
    var imageClassTag = $(scope).attr('imageTo');
    return $(scope).closest('.' + searchFrom).find('.' + imageClassTag);
}

$('.imageUploader .upload_image_text').live('click', function() {
    //    $(this).closest('form').find("#file_selection").click();
});

$('.editormode .deleteable').live({
    mouseenter: function() {
        if ($(this).find('#file_selection').size() == 0) {
            $(this).find('.trash').show();
        }
    },
    mouseleave: function() {
        $(this).find('.trash').hide();
    }
})


$('.editormode .deleteable .trash').live('click', function(e) {
    var imgId = $(this).attr('imageId');
    var res = $(this);
    var event = thundashop.Ajax.createEvent('MainMenu', 'deleteImage', res, {
        imageId: imgId
    });
    thundashop.Ajax.post(event);
});

$('.imageUploader').liveFileupload({
    url: 'handler.php',
    sequentialUploads: true,
    getImageElement: function() {
    },
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

$('#informationbox .settings .entry, #settingsarea .setting').live('click', function(event) {
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

$('#infomrationboxclosebutton').live('click', function(e) {
    thundashop.common.hideInformationBox(null);
})

$(document).live("keyup", function(e) {
    if (e.keyCode == 27) {
        thundashop.common.hideInformationBox();
    }
});

$('.tabset .tab').live('click', function() {
    var tabs = $(this).closest('.tabs');
    tabs.find('.active').each(function() {
        $(this).removeClass('active');
    });
    $(this).addClass('active');
    var activate = $(this).attr('activate');
    tabs.find('#' + activate).addClass('active');
});

$(function() {
    $(document).on('click', '#getshop_logout', function() {
        var event = thundashop.Ajax.createEvent(null, 'logout', $(this), {});
        thundashop.Ajax.postSynchron(event);
        thundashop.framework.reprintPage();
    });
});

$('.add_application_menu .button-large').live(
        {
            mouseover: function() {
                $(this).find('.filler').css('background-color', '#243da9');
            },
            mouseout: function() {
                $(this).find('.filler').css('background-color', '#009b00');
            }
        });

$('.selectablegroup').live('click', function(event) {

    $(this).find('.selectable').each(function() {
        $(this).removeAttr("gsname");
        $(this).removeClass('selected');
    });

    var target = event.target;
    $(target).addClass("selected");
    $(target).attr("gsname", "selected");
});
$('.getshop_ckeditorcontent').live({
    mouseenter: function() {
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
            $(this).attr('title', 'Click to edit, the text is being saved automatically.');
            thundashop.common.activateCKEditor(id, true, true, false);
            $(this).addClass('cke_focus');
        }
    },
    mouseleave: function() {
        if (!$(this).hasClass('cke_focus')) {
            var id = $(this).attr('id');
            CKEDITOR.instances[id].destroy();
        }
    }
});
$('.getshop_ckeditorcontent').live('mouseover', function(e) {


});
$(function() {
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
    $(document).on('click', '.empty_app_area_browse_apps', function() {
        var area = $(this).closest('.applicationarea').attr('area');
        var event = thundashop.Ajax.createEvent('', 'showApplications', null, {
            "type": area
        });
        thundashop.common.showInformationBox(event);
    });
    $(document).on('click', '.empty_app_area_browse_importapps', function() {
        var area = $(this).closest('.applicationarea').attr('area');
        thundashop.MainMenu.importApplicationClicked(area);
    });
    $(document).on('click', '.application_settings', function(event) {
        var app = $(this).closest('.app');
        var appname = app.attr('app');
        $('.GetShopToolbox[attached_to_app="true"]').remove();
        window["app"][appname]["loadSettings"]($(this), app);
    });
    
    $(document).on('mouseenter','.app', function() {
        var appname = $(this).attr('app');
        if(app[appname]['loadSettings'] !== undefined) {
            var settingsbox = $(this).find('.application_settings');
            $(this).find('.application_settings').show();
        }
    });
    $(document).on('mouseleave','.app', function() {
        $(this).find('.application_settings').hide();
    });
});

