/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

thundashop = {};

thundashop.Namespace = {
    Register : function(namespace) {
        namespaceori = namespace;
        namespace = namespace.split('.');

        if(!window[namespace[0]]) window[namespace[0]] = {};

        var strFullNamespace = namespace[0];
        for(var i = 1; i < namespace.length; i++)
        {
            strFullNamespace += "." + namespace[i];
            eval("if(!window." + strFullNamespace + ")window." + strFullNamespace + "={};");
        }
    }
};

if (typeof(com) == "undefined") com = {} 
if (typeof(com.getshop) == "undefined") com.getshop = {}
if (typeof(com.getshop.app) == "undefined") com.getshop.app = {}


if (typeof(thundashop) == "undefined") {
    thundashop = {}
}

thundashop.Namespace.Register('thundashop.common');

thundashop.common = {
    
    init: function() {
        $(document).on('click', '#messagebox .okbutton', thundashop.common.closeAlert)
    },
    
    closeModal: function() {
//        getshop.hideOverlay();
    },
    
    Alert: function(title, message, error, autoHide) {
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

        if (autoHide) 
            $("#messagebox").delay(autoHide).fadeOut(200);
    },
    
    goToPageLink: function(link, callback) {
        var url=location.href;
        var urlFilename = url.substring(url.lastIndexOf('/')+1, url.indexOf('?'));
        
        if (link[0] == "/") {
            link = link.substring(1);
        }
        
        document.location = "/"+urlFilename+link;
    },
    
    closeAlert: function() {
        $('#messagebox').fadeOut(200);
    }

}

thundashop.common.init();

thundashop.base64 = {

    // private property
    _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    // public method for encoding
    encode : function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = thundashop.base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
            this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
            this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output;
    },

    // public method for decoding
    decode : function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        }

        output = thundashop.base64._utf8_decode(output);

        return output;

    },

    // private method for UTF-8 encoding
    _utf8_encode : function (string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

    // private method for UTF-8 decoding
    _utf8_decode : function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while ( i < utftext.length ) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else if((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i+1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                c2 = utftext.charCodeAt(i+1);
                c3 = utftext.charCodeAt(i+2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    },
    
    encodeForAjax: function(data) {
        return data;
    }

}


var doCkeditorBlur = true;
var isNavigating = false;
var hasFadeInEffect = false;

thundashop.Namespace.Register("thundashop.Ajax");

thundashop.handleAjaxError = function(error, textstatus, status, content) {
    
    $('#loaderbox').hide();
    
    if (error.status === 400) {
        var errorObject = JSON.parse(error.responseText);
        
        if (typeof(errorObject) !== "undefined" && typeof(errorObject.version) !== "undefined" && errorObject.version === 2) {
            thundashop.Ajax.handleImprovedErrorHandling(errorObject);
        } else {
            var errorText = errorObject.error_text ? errorObject.error_text : errorObject.error;
            thundashop.common.Alert(__f("Failed"), errorText, true);    
        }
        
    };
    
    if (error.status === 402) {
        var result = thundashop.common.hideInformationBox();
        result.done(function() {
            var event = thundashop.Ajax.createEvent('', 'loadpaymentinfo', $(this), {});
            thundashop.common.showInformationBox(event, 'Payment information');
        });
    };
    
};

thundashop.Ajax = {
    init: function() {
        $(document).on('click','*[gsclick]', thundashop.Ajax.postgeneral);
        $(document).on('change','*[gschange]', thundashop.Ajax.postgeneral);
        $(document).on('click','*[gs_downloadExcelReport]', thundashop.Ajax.createExcelFile);
        $(document).on('click','*[gs_show_modal]', thundashop.Ajax.showModal);
        $(document).on('click','*[gs_close_modal]', thundashop.Ajax.closeModal);
        $(document).on('click','*[gstype="downloadpdf"]', thundashop.Ajax.downloadPdf);
    },
    
    downloadPdf: function() {
        var method = $(this).attr('method');
        var filename = $(this).attr('filename');
        
        var data = {};
        $.each(this.attributes, function(i, attrib) {
            var name = attrib.name;
            var value = attrib.value;
            data[name] = value;
        });
        
        var event = thundashop.Ajax.createEvent(null, method, this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, function(res) {
            var base64 = thundashop.base64.encode(res);
            var url = '/scripts/html2pdf.php';
            var form = $('<form method="POST" action="' + url + '">');
            form.attr('target', '_blank');
            form.append($('<input type="hidden" name="data" value="' + base64 + '">'));
            form.append($('<input type="hidden" name="filename" value="' + filename + '">'));
            
            $('body').append(form);
            
            form.submit();
            form.remove();
        });
    },

    
    closeModal: function() {
        alert("Not yet implemented");
    },
    
    handleImprovedErrorHandling: function(errorObject) {
        var apps = $('.'+errorObject.appName);
        $(apps).each(function() {
            for (var i in errorObject.gsfield) {
                $(this).find('[gsname="'+i+'"]').addClass('gserrorinput');
                $(this).find('#'+i).addClass('gserrorinput');
            }
            
            for (var i in errorObject.fields) {
                $(this).find('#'+i).html(errorObject.fields[i]);
                $(this).find('#'+i).show();
            }
        });
    },
    
    reloadApp: function(appInstanceId, firstLoad) {
        var app = $('.app[appsettingsid="'+appInstanceId+'"]');
        $('.gs_loading_spinner').addClass('active');    
        thundashop.Ajax.simplePost(app, "", {}, firstLoad);
    },
    
    showModal: function() {
        var data = {};
        
        $.each(this.attributes, function(i, attrib) {
            var name = attrib.name;
            var value = attrib.value;
            data[name] = value;
        });
        
        thundashop.Ajax.openModal($(this).attr('gs_show_modal'), data);
    },
    
    openModal: function(modalName, data) {
        getshop.showOverlay("1");
        
        var event = {};
        event.application = "";
        event.event = "";
        event.data = data;
        event.core = {};
        event.core.pageid = modalName;
        event.synchron = true;
//        
        thundashop.Ajax.post(event, function(res) {
            $('.gsoverlay1 .gsoverlayinner').attr('pageid', modalName);
            $('.gsoverlay1 .gsoverlayinner .content').html(res);
            $('.gsoverlay1 .gsoverlayinner .app[appid]').each(function() {
                thundashop.Ajax.reloadApp($(this).attr('appsettingsid'), true);
            });
        });
    },
    
    createExcelFile: function() {
        var method = $(this).attr('gs_downloadExcelReport');
        var filename = $(this).attr('gs_fileName');
        var data = {};
   
        $.each(this.attributes, function(i, attrib) {
            var name = attrib.name;
            var value = attrib.value;
            data[name] = value;
        });
        
        data['synchron'] = true;
        
        var evt = thundashop.Ajax.createEvent(null, method, this, data);
        
        thundashop.Ajax.postWithCallBack(evt, function(res) {
            var base64 = thundashop.base64.encode(res);
            var url = '/scripts/createExcelFile.php';
            var form = $('<form method="POST" action="' + url + '">');
            form.append($('<input type="hidden" name="data" value="' + base64 + '">'));
            form.append($('<input type="hidden" name="filename" value="' + filename + '">'));
            
            $('body').append(form);
            
            form.submit();
            form.remove();
        });
    },
    
    postgeneral: function() {
        var method = $(this).attr('gsclick');
        
        var data = {};
        
        var javascriptCallback = $(this).attr('gs_callback');
        
        if ($(this).attr('gschange')) {
            method = $(this).attr('gschange');
            data.gsvalue = $(this).val();
        }

        if ($(this).attr("gs_confirm")) {
            var conf = confirm($(this).attr("gs_confirm"));
            if (!conf) {
                return;
            }
        }
        
        if ($(this).attr('gs_also')) {
            var extraData = $(this).attr('gs_also').split(",");
            for (var i in extraData) {
                var field = $("."+extraData[i]);
                var addData = $(field).val();
                var name = $(field).attr('gsname');
                data[name] = addData;
            }
        }
        
        $.each(this.attributes, function(i, attrib) {
            var name = attrib.name;
            var value = attrib.value;
            data[name] = value;
        });
        
        var event = thundashop.Ajax.createEvent(null, method, this, data);
        
        if ($(this).attr('synchron')) {
            event['synchron'] = true;
        }
        
        if ($(this).attr('gssilent') == "true") {
            thundashop.Ajax.post(event, null, true, true, true);
            
            if ($(this).attr('gs_scrollToTop')) {
                window.scroll(0,0);
            }
        } else {
            
            if (javascriptCallback) {
                var callbackFunction = function(res, javascriptCallbackFunction) {
                    var funtionBody = javascriptCallbackFunction[0] + "(res);";
                    var toExecute = new Function("res", funtionBody);
                    toExecute(res);
                }
                
                thundashop.Ajax.post(event, callbackFunction, [javascriptCallback]);
            
            } else {
                thundashop.Ajax.simplePost(this, method, data);

                if ($(this).attr('gs_scrollToTop')) {
                    window.scroll(0,0);
                }
            }
        }   
    },
    
    showErrorMessage: function(message) {
        $("#errorbox").show();
        $("#errorbox").html("<div class='errorform'><div class='close'></div>" + message + "</div>");
        $("#errorbox").delay(5000).fadeOut(1000);
    },
    simplePost: function(from, functionName, data, firstLoad) {
        var event = thundashop.Ajax.createEvent(null, functionName, from, data);
        event.firstLoad = firstLoad;
        thundashop.Ajax.post(event);
    },
    postWithCallBack: function(data, callback, dontShowLoaderbox, xtra) {
        var file = "data.php";
        if (xtra !== undefined) {
            if (xtra.file) {
                file = xtra.file;
            }
        }

        if (!(typeof(dontShowLoaderbox) !== "undefined" && dontShowLoaderbox === true))
            $('#loaderbox').show();

        data['synchron'] = true;
        $.ajax({
            type: "POST",
            url: file,
            data: data,
            context: document.body,
            success: function(response) {
                callback(response);
                $('#loaderbox').hide();
            },
            error: thundashop.handleAjaxError
        });
    },
    IsJsonString: function(str) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    },
    post: function(data, callback, extraArg, dontUpdate, dontShowLoaderBox, xtra) {
        $('.gserrorfield').hide();
        $('.gserrorinput').removeClass('gserrorinput');
        
        if($('.gsoverlay2 .gsoverlayinner').is(':visible') && (typeof(callback) === "undefined" || callback === undefined || callback === null)) {
            data['synchron'] = true;
            callback = thundashop.framework.reloadOverLayType2;
        }
        
        if($('.gsoverlay1 .gsoverlayinner').is(':visible') && (typeof(callback) === "undefined" || callback === undefined || callback === null) && !data.firstLoad) {
            data['synchron'] = true;
            callback = thundashop.framework.reloadOverLayType1;
        }
        var file = "data.php";
        var uploadcallback = false;
        if (xtra !== undefined) {
            if (xtra.file)
                file = xtra.file;
            if (xtra.uploadcallback)
                uploadcallback = xtra.uploadcallback;
        }

        if (!(typeof(dontShowLoaderBox) !== "undefined" && dontShowLoaderBox === true)) {
            $('#loaderbox').show();
            
            if (typeof(data.firstLoad) === "undefined" || !data.firstLoad) {
                $('.gs_body_inner .gs_loading_spinner').addClass('active');
            }
        }

        if (data['core']['fromappid']) {
            data['synchron'] = true;
        }
        
        var dataType = (data['synchron']) ? "html" : "json";
       
        $.ajax({
            type: "POST",
            url: file,
            data: data,
            dataType: dataType,
            context: document.body,
            success: function(response) {
                if (typeof(dontUpdate) === "undefined" || dontUpdate === false) {
                    thundashop.Ajax.updateFromResponse(response, data);
                }
                if (typeof(callback) === "function") {
                    
                    if (thundashop.Ajax.IsJsonString(response)) {
                        response = JSON.parse(response);
                        callback(response, extraArg);
                    } else {
                        callback(response, extraArg);
                        $('#loaderbox').hide();
                    }
                }
                
            },
            xhr: function()
            {
                var xhr = new window.XMLHttpRequest();
                if (!xhr.upload) {
                    return xhr;
                }
                
                xhr.upload.addEventListener("progress", function(evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = (evt.loaded / evt.total) * 100;
                        if(uploadcallback) {
                            uploadcallback(percentComplete);
                        }
                    }
                }, false);
                
                return xhr;
            },
            error: thundashop.handleAjaxError
        });
    },
    postSynchron: function(event) {
        event['synchron'] = true;
        var result = "";
        $.ajax({
            type: "POST",
            url: "data.php",
            async: false,
            data: event,
            context: document.body,
            success: function(response) {
                result = response;
            },
            error: thundashop.handleAjaxError
        });

        return result;
    },
    postSynchronWithReprint: function(event) {
        $('#loaderbox').show();
        var result = "";
        $.ajax({
            type: "POST",
            url: "data.php",
            data: event,
            async: false,
            dataType: "json",
            success: function(response) {
                thundashop.Ajax.updateFromResponse(response, event);
                if (response.errors && response.errors !== "")
                    result = false;
            },
            error: thundashop.handleAjaxError
        });
        return result;
    },
    createEvent: function(applicationName, event, fromDomElement, data) {
        var retevent = {};
        retevent.application = applicationName;
        retevent.event = event;
        retevent.data = data;
        retevent.core = {}
        
        retevent.gs_getshopmodule = $('html').attr('module');
        
        if(typeof(fromDomElement) === "string") {
            retevent.core.appid = fromDomElement;
            retevent.core.appid2 = $(fromDomElement).closest('.app').attr('appid2');
            retevent.core.closestappwithinstance = $(fromDomElement).closest('.app[hasinstance="yes"]').attr('appsettingsid');
        } else {
            retevent.core.apparea = $(fromDomElement).closest('.applicationarea').attr('area');
            retevent.core.appname = $(fromDomElement).closest('.app').attr('app');
            retevent.core.appid = $(fromDomElement).closest('.app').attr('appid');
            retevent.core.appid2 = $(fromDomElement).closest('.app').attr('appid2');
            retevent.core.instanceid = $(fromDomElement).closest('.app').attr('appsettingsid');
            retevent.core.fromappid = $(fromDomElement).closest('.app').attr('fromapplication');
            retevent.core.closestappwithinstance = $(fromDomElement).closest('.app[hasinstance="yes"]').attr('appsettingsid');
            
            if ($('.gsoverlay1 .gsoverlayinner').is(':visible') && $('.gsoverlay1 .gsoverlayinner').attr('pageid')) {
                retevent.core.pageid = $('.gsoverlay1 .gsoverlayinner').attr('pageid');
            } else {
                retevent.core.pageid = $('html').attr('pageid');
            }
        }
        
        var parentApps = $(fromDomElement).parents('.app');
        if (parentApps.length > 1) {
            retevent.core.nested = "true";
        }
        
        return retevent;
    },
    
    updateFromResponse: function(response, event) {
        var scrolltop = $(window).scrollTop();
        
        if (response.errors) {
            thundashop.Ajax.showErrorMessage(response.errors)
        } else {
            if (response.content !== "gs_modal_active" && !event.core.nested) {
                $('.app[appsettingsid="'+response.instanceid+'"]').html(response.content);
            }
            
            $('#dynamicmodal').html(response.modal);
            $('.gsrightwidgetbody').html(response.rightWidget);
        }
        $(window).scrollTop(scrolltop);
        $('#loaderbox').hide();
        
        if (response.gs_alsoupdate && !event.firstLoad) {
            for (var i in response.gs_alsoupdate) {
                var updateId = response.gs_alsoupdate[i];
                thundashop.Ajax.reloadApp(updateId);
            }
        }
        
        $('.gs_loading_spinner').removeClass('active');
        
        if (event.core.closestappwithinstance && !event.core.instanceid && $('.gsoverlay1 .gsoverlayinner').is(':visible') ) {
            thundashop.Ajax.reloadApp(event.core.closestappwithinstance, true);
            
        }
    },
    navigateWithJavascript: function(scope) {
        var variables = "";
        data = {};
        
        if ($(scope).is('form')) {
            var formName = $(scope).attr('name');
            if (!formName) {
//                alert('Form name is required');
//                return;
            }

            $.each($(scope).serializeArray(), function(i, field) {
                data[field.name] = field.value;
            });

            var buttonname = $(scope).find('input:submit').attr('name');
            var buttonvalue = $(scope).find('input:submit').attr('value');

            if (buttonname && buttonvalue) {
                data[buttonname] = buttonvalue;
            }
        } else {
            var variables = $(scope).attr('href');
        }
        this.doJavascriptNavigation(variables, data);
    },
    doJavascriptNavigation: function(variables, data, callback) {
        $('#loaderbox').show();
        variables = variables.substring(variables.indexOf('?') + 1);
        url = 'json.php?' + variables + '&scopeid=' + scopeid;
        
        var info = {
            url : url,
            variables : variables
        }
        
        $.ajax({
            type: "POST",
            url: url,
            dataType: "json",
            data: data,
            success: function(response) {
                thundashop.Ajax.updateFromResponse(response, data);
                if (typeof(callback) !== "undefined" && typeof(callback) !== "boolean" && typeof(callback) == "function") {
                    callback();
                }
            },
            error: thundashop.handleAjaxError
        })
    },
    reloadCss: function() {
        document.getElementById('mainlessstyle').href = 'StyleSheet.php';
    },
    changeTheeme: function(template, colors) {
        thundashop.common.unlockMask();
        $('#loaderbox').show();
        url = 'colorloader.php?setTheeme=' + template + '&colors=' + colors;
        $.ajax({
            type: "POST",
            url: url,
            success: function(response) {
                thundashop.Ajax.reloadCss();
                $('#loaderbox').hide();
            },
            error: thundashop.handleAjaxError
        })
    }
}



thundashop.Ajax.init();


app = {};


getshop = {
    init: function() {
        $(document).on('click', '.gsoverlay1', getshop.hideOverlay);
        $(document).on('click', '.gsoverlay2', getshop.hideOverlay);
    },
    
    documentLoaded : function() {
        getshop.loadApps();
    },
    
    loadApps: function() {
        $('.app[appid]').each(function() {
            thundashop.Ajax.reloadApp($(this).attr('appsettingsid'), true);
        });
    },
    
    showOverlay: function(overlaytype) {
        $('.gsoverlay' + overlaytype).addClass('active');
        $('.gsoverlay'+overlaytype+' .gsoverlayinner .content').html('<div style="padding: 30px; text-align: center;"><i style="font-size: 30px;" class="fa fa-spin fa-spinner"></i></div>');
    },
    
    hideOverlay: function(target) {
        if ($(target.target).hasClass('gsoverlay1')) {
            $('.gsoverlay1').removeClass('active');
            var closingPage = $('.gsoverlay1 .gsoverlayinner .gs_page_area').attr('gs_page_content_id');
            
            if (closingPage) {
                var event = thundashop.Ajax.createEvent(null, "gs_close_modal", null, {modalname: closingPage});
                thundashop.Ajax.post(event);
            }
            
            $('.gsoverlay1 .gsoverlayinner .content').html("");
            getshop.loadApps();
        }
        
        if ($(target.target).hasClass('gsoverlay2')) {
            $('.gsoverlay2').removeClass('active');
            var closingPage = $('.gsoverlay .gsoverlayinner .gs_page_area').attr('gs_page_content_id');
            
            if (closingPage) {
                var event = thundashop.Ajax.createEvent(null, "gs_close_modal", null, {modalname: closingPage});
                thundashop.Ajax.post(event);
            }
            
            $('.gsoverlay2 .gsoverlayinner .content').html("");
            getshop.loadApps();
            $('html, body').css({ overflow: 'auto'});
        }
    }
}

getshop.init();


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

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


getshop.Table = {
    init: function () {
        $(document).on('click', '.GetShopModuleTable .datarow .datarow_inner', getshop.Table.rowClicked);
    },
    rowClicked: function (e) {
        if ($(e.target).hasClass('dontExpand')) {
            return;
        }
        if ($(e.target).closest('.dontExpand').length > 0) {
            return;
        }

        if ($(e.target).hasClass('loadContentInOverlay') || $(e.target).closest('.loadContentInOverlay').length > 0) {
            getshop.Table.loadTableContentOverlay(e, $(this));
            return;
        }


        var table = $(this).closest('.GetShopModuleTable');
        var identifier = table.attr('identifier');
        var functioname = table.attr('method');
        if ($(this).closest('.datarow').hasClass('active')) {
            $(this).closest('.datarow').removeClass('active');
            table.find('.datarow_extended_content').slideUp();
            var event = thundashop.Ajax.createEvent(null, 'clearSessionOnIdentifierForTable', this, {
                "identifier": identifier,
                "functioname": functioname
            });
            thundashop.Ajax.postWithCallBack(event, function () {});
            return;
        }
        table.find('.datarow.active').removeClass('active');
        $(this).closest('.datarow').addClass('active');
        var target = $(e.target);
        var base = $(this).closest('.datarow');
        base.find('.datarow_extended_content').html("");
        table.find('.datarow_extended_content').slideUp();
        var rowNumber = $(base).attr('rownumber');

        var data = gs_modules_data_array[identifier][rowNumber];
        data['gscolumn'] = target.attr('index');
        base.find('.datarow_extended_content').slideDown();

        var event = thundashop.Ajax.createEvent(null, table.attr('method'), this, data);
        event['synchron'] = true;

//    thundashop.common.startTableOverLay($(this));

        thundashop.Ajax.post(event, function (res) {
            base.find('.datarow_extended_content').html(res);
        });

        var identifier = table.attr('identifier');

        var data = {
            functionName: table.attr('method'),
            rownumber: rowNumber,
            index: target.attr('index'),
            identified: identifier
        }

        var event = thundashop.Ajax.createEvent(null, "setGetShopTableRowId", this, data);
        thundashop.Ajax.postWithCallBack(event, function () {

        }, true);
    },
    
    loadTableContentOverlay: function (e, btn) {
        $('html, body').css({ overflow: 'hidden', height: '100%'});
        
        var table = btn.closest('.GetShopModuleTable');
        var identifier = table.attr('identifier');
        var functioname = table.attr('method');
        if (btn.closest('.datarow').hasClass('active')) {
            btn.closest('.datarow').removeClass('active');
            table.find('.datarow_extended_content').slideUp();
            var event = thundashop.Ajax.createEvent(null, 'clearSessionOnIdentifierForTable', this, {
                "identifier": identifier,
                "functioname": functioname
            });
            thundashop.Ajax.postWithCallBack(event, function () {});
            return;
        }
        var target = $(e.target);
        var base = btn.closest('.datarow');
        var rowNumber = $(base).attr('rownumber');

        var data = gs_modules_data_array[identifier][rowNumber];
        data['gscolumn'] = target.attr('index');

        var event = thundashop.Ajax.createEvent(null, table.attr('method'), btn, data);
        event['synchron'] = true;
        latestOverLayLoadingEvent = event;
        getshop.showOverlay("2");
        thundashop.Ajax.post(event, function (res) {
            $('.gsoverlay2 .gsoverlayinner .content').html(res);
        });
    }
}

getshop.Table.init();

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


thundashop.Namespace.Register("thundashop.framework");

thundashop.framework = {
    init: function() {
        $(document).on('click', '*[gstype="form"] *[gstype="submit"]',  thundashop.framework.submitFromEvent);
        
        $(document).on('change', '*[gstype="changesubmit"]',  function(e) {
             if ($(this).attr('method')) {
                thundashop.framework.submitElement(e);
            } else {
                thundashop.framework.submitFromEvent(e);
            }
        });
        
        $(document).on('keyup', '*[gstype="form"] *[gstype="submitenter"], *[gstype="clicksubmit"]',  function(e) {
            if (e.keyCode == 13) {
                if ($(e.target).attr('gsType') == "clicksubmit") {
                    thundashop.framework.submitElement(e);
                } else {
                    thundashop.framework.submitFromEvent(e);
                }
            }
        });
        
        $(document).on('click', '*[gstype="clicksubmit"],*[gstype="clicksubmitforce"]',  function(e) {
            var target = $(e.target);
            var gstype = $(this).attr('gstype');
            if (target.prop("tagName") === "INPUT" && gstype === "clicksubmit") {
                return;
            }
            thundashop.framework.submitElement(e);
        });
    },
    
    submitFromEvent: function(event) {
        if($(this).hasClass('disabled')) {
            return;
        }

        var target = $(event.target);
        if(target.attr('gstoarea')) {
            thundashop.Ajax.postToArea = target.attr('gstoarea');
        }
        thundashop.framework.submitFromElement(target);
    },
    
     submitFromElement: function (element) {
        var form = element.closest('*[gstype="form"]');
        var method = form.attr('method');
        var args = thundashop.framework.createGsArgs(form);
        if(element.attr('gsvalue')) {
            args['submit'] = element.attr('gsvalue');
        }
        var target = element;
        
        form.callback = element.callback;
        
        var localUpdateOnly = $(element).attr('gs_local_update_only');
        var javascriptCallback = $(element).attr('gs_callback');
        var event = thundashop.Ajax.createEvent("", method, element, args);
        
        if (localUpdateOnly) {
            event['synchron'] = true;
            thundashop.Ajax.post(event, function(res) {
                $(form[0]).replaceWith(res);
            });
        } 
        
        if (javascriptCallback) {
            var callbackFunction = function(res, javascriptCallbackFunction) {
                var funtionBody = javascriptCallbackFunction[0] + "(res, target);";
                var toExecute = new Function("res", "target", funtionBody);
                toExecute(res, target);
            }
            
            if (!localUpdateOnly) {
                event['synchron'] = true;
                thundashop.Ajax.post(event, callbackFunction, [javascriptCallback, target]);
            }
        } else {
            if (!localUpdateOnly) {
                thundashop.framework.postToChannel(event, form);
            }
        }
    },
    
    submitElement: function (event) {
        var element = $(event.target);
        var name = element.attr('gsname');
        var value = element.attr('gsvalue');
        var method = element.attr('method');
        if (!value) {
            value = element.val();
        }
        if(element.hasClass("clicksubmitcheckbox")) {
            value = element.is(':checked');
        }
        
        var data = {}
        data[name] = value;
        if (element.attr("gs_prompt")) {
            var prompt = window.prompt(element.attr("gs_prompt"));
            if (!prompt) {
                return;
            }
            data['prompt'] = prompt;
        }


        var event = thundashop.Ajax.createEvent("", method, element, data);
        thundashop.framework.postToChannel(event, element);
    },
    
    createGsArgs: function (form) {
        var args = {};

        form.find('*[gsname]').each(function (e) {
            var name = $(this).attr('gsname');
            if (!name || name.trim().length == 0) {
                alert('Name attribute is missing for gstype value, need to be fixed');
                return;
            }
            var value = $(this).attr('gsvalue');
            if (!value || value === undefined) {
                value = $(this).val();
            }
            
            if ($(this).attr('gstype') === "select") {
                value = $(this).find('.gs_selected').attr('gs_value');
            }

            if ($(this).is(':checkbox')) {
                value = $(this).is(':checked');
            }
            if ($(this).attr('gstype') == "ckeditor") {
                value = CKEDITOR.instances[$(this).attr('id')].getData();
            }
            if ($(this).is(':radio')) {
                if ($(this).is(':checked')) {
                    args[name] = $(this).val();
                }
            } else {
                args[name] = value;
            }
        });
        return args;
    },
    
    postToChannel: function (event, element) {
        var callback = this.getCallBackFunction(element);
        if (!element.attr('output')) {
            if(element.attr('gstoarea')) {
                thundashop.Ajax.postWithCallBack(event, function(res) {
                    $(element.attr('gstoarea')).html(res);
                });
            } else {
                thundashop.Ajax.post(event, callback, event);
            }
        } else if (element.attr('output') == "informationbox") {
            var informationTitle = element.attr('informationtitle');
            var box = thundashop.common.showInformationBox(event, informationTitle);
            box.css('min-height', '10px');
            if (typeof (callback) == "function") {
                callback(box.html(), event);
            }
        }
    },
    getCallBackFunction: function (element) {
        if (element && typeof (element.callback) !== "undefined") {
            return element.callback;
        }

        return null;
    },
    
    reloadOverLayType2: function() {
        if (typeof(latestOverLayLoadingEvent) === "undefined") {
            return;
        }
        
        $('.gs_loading_spinner').addClass('active');
        
        thundashop.Ajax.post(latestOverLayLoadingEvent, function(res) {
            $('.gsoverlay2 .gsoverlayinner .content').html(res);
        });
    },
    
    reloadOverLayType1: function() {
        $('.gsoverlay1 .gsoverlayinner .app').each(function() {
            thundashop.Ajax.reloadApp($(this).attr('appsettingsid'), true);
        });
    }
}

thundashop.framework.init();