/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(getshop) === "undefined") {
    getshop = {};
}

getshop.pms = {
   init : function() {
        $(document).on('click', '[gs_model].gss_onoff', getshop.pms.toggleOnOff);
        $(document).on('click', '[gss_method="saveSettings"]', getshop.pms.saveSettings);
        $(document).on('click', '[gss_method="submitToInfoBox"]', getshop.pms.postToInformationBox);
        $(document).on('click', '.gsoverlayinner .closemodal', getshop.pms.closeOverLaysMouse);
        $(document).on('keydown', getshop.pms.closeOverLays);
   },
   hideInformationBox : function() {
       $('.gsoverlay1').removeClass('active');
   },
   postToInformationBox : function(event) {
        if(event.type == "keyup") {
            if(event.keyCode !== 13) {
                return;
            }
        }
        $('#loaderbox').show();

        var form = $(this).closest('[gstype="form"]');
        var data = thundashop.framework.createGsArgs(form);
        
        if($(this).attr('gsvalue')) {
            data["clicksubmit"] = $(this).attr('gsvalue');
        }
        
        var method = form.attr('method');
        if(typeof(method) === "undefined" || $(this).attr('method')) {
            method = $(this).attr('method');
        }
        var event = thundashop.Ajax.createEvent('',method,$(this),data);
        var title = $('#informationboxtitle').text();
        getshop.pms.showInformationBox(event, title, true);
    },
   closeOverLaysMouse : function(e) {
        if($('.gsoverlay1').is(":visible")) {
            $('.gsoverlay1').click();
        }
        if($('.gsoverlay2').is(":visible")) {
            $('.gsoverlay2').click();
        }
   },
   closeOverLays : function(e) {
       if(e.keyCode === 27) {
           if($('.gsoverlay1').is(":visible")) {
               $('.gsoverlay1').click();
           }
           if($('.gsoverlay2').is(":visible")) {
               $('.gsoverlay2').click();
           }
       }
   },
   showInformationBox : function(event, title) {
       getshop.showOverlay("1");
       $('.gsoverlay1 .gsoverlayinner').html("");
       thundashop.Ajax.postWithCallBack(event, function(res) {
            if (typeof(title) === "undefined")
                title = "";
            var appid = null;
            if(event !== undefined) {
                if (event.core.appid !== undefined) {
                    appid = event.core.appid;
                }
            }

            var infoBox = $('<div></div>');
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

            if(event !== undefined) {
                if (event.core.appname === undefined) {
                    event.core.appname = "";
                }
                if (event.core.apparea === undefined) {
                    event.core.apparea = "";
                }
                infoBox.attr('app', event.core.appname);
                infoBox.attr('apparea', event.core.apparea);
                infoBox.attr('appsettingsid', event.core.instanceid);
                infoBox.addClass(event.core.appname);
            }

            infoBox.html(res);
           
           $('.gsoverlay1 .gsoverlayinner').html(infoBox);
       });
   },
   toggleOnOff: function () {
        var fontAwesomeIcon = $(this).find('i');
        if (fontAwesomeIcon.hasClass("fa-toggle-off")) {
            fontAwesomeIcon.removeClass("fa-toggle-off")
            fontAwesomeIcon.addClass("fa-toggle-on")
        } else {
            fontAwesomeIcon.removeClass("fa-toggle-on")
            fontAwesomeIcon.addClass("fa-toggle-off")
        }
    },
    saveSettings : function() {
        var form = $(this).closest('.gss_settings_content');
        var data = {};
        form.find("[gs_model_attr]").each(function() {
            var key = $(this).attr('gs_model_attr');
            var val = "";
            if($(this).is("input") || $(this).is('select')) {
                val = $(this).val();
            }
            if($(this).hasClass('gss_onoff')) {
                if($(this).find('.fa-toggle-on').length > 0) {
                    val = true;
                } else {
                    val = false;
                }
            }
            data[key] = val;
        });
        var appid = $(this).closest('.paymentsetupconfig').attr('appid');
        var event = thundashop.Ajax.createEvent('','saveSettings',appid, data);
        event.core.instanceid = appid;
        event.core.pageid = $(this).closest('[pageid]').attr('pageid');
        event.core.convertDataToRawPost = true;
        thundashop.Ajax.postWithCallBack(event, function(res){
            console.log(res);
        });
    }
};


if(typeof(thundashop) === "undefined") {
    thundashop = {};
}

if(typeof(thundashop.common) === "undefined") {
    thundashop.common = {};
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
    var simpleMode = false;
    var inputConfig = config;

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
        if (config.simpleMode !== undefined)
            simpleMode = config.simpleMode;
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
            save: function(event) {
                var data = event.editor.getData();
                var toPush = {};
                toPush['content'] = data;
                toPush['fromid'] = $(target).attr('id');
                
                var altid = target.attr('altid');

                var gsevent = thundashop.Ajax.createEvent('','saveContent',$(target),toPush);
                thundashop.Ajax.postWithCallBack(gsevent, function() {});
                event.editor.destroy();
                target.attr('contenteditable', false);
                target.blur();
                if (destroyOnSave) {
                    event.editor.destroy();
                }
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
    if (simpleMode) {
        config.toolbar = [
		{ name: 'document', items: [ 'Save','FontSize','Format', 'TextColor', 'BGColor','JustifyLeft', 'JustifyCenter', 'JustifyRight' ] }
        ]
    }
    
    if (inputConfig && typeof(inputConfig.superSimpleMode) !== "undefined" && inputConfig.superSimpleMode) {
        config.toolbar = [
            { name: 'document', items: [ 'Save','JustifyLeft', 'JustifyCenter', 'JustifyRight' ] },
            {name: 'links', items: ['Link', 'Unlink', 'Anchor']}
        ]
    }
    
    if (autogrow === false) {
        config.autoGrow_onStartup = false;
        config.resize_enabled = false;
        config.autoGrow_maxHeight = 10;
        config.autoGrow_minHeight = 10;
    }
    
    var instance = null;
    if (notinline) {
        config.extraPlugins = 'autogrow';
        config.width = '100%';
        instance = CKEDITOR.replace(id, config);
    } else {
        instance = CKEDITOR.inline(id, config);
    }
    
    instance.on('blur',function( e ){
        return false;
     });
}

getshop.pms.init();