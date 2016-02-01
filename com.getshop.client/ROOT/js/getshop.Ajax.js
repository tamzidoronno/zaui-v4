
var doCkeditorBlur = true;
var isNavigating = false;
var hasFadeInEffect = false;

thundashop.Namespace.Register("thundashop.Ajax");

thundashop.handleAjaxError = function(error, textstatus, status, content) {
    
    $('#loaderbox').hide();
    
    if (error.status === 400) {
        var errorObject = JSON.parse(error.responseText);
        var errorText = errorObject.error_text ? errorObject.error_text : errorObject.error;
        thundashop.common.Alert(__f("Failed"), errorText, true);
    };
    
    if (error.status === 402) {
        var result = thundashop.common.hideInformationBox();
        result.done(function() {
            var event = thundashop.Ajax.createEvent('', 'loadpaymentinfo', $(this), {});
            thundashop.common.showInformationBox(event, 'Payment information');
        });
    };
    
    PubSub.publish("AJAXERROR", error);
};

thundashop.Ajax = {
    
    init: function() {
        $(document).on('click','*[gsclick]', thundashop.Ajax.postgeneral);
    },
    
    postgeneral: function() {
        var method = $(this).attr('gsclick');
        var data = {};
        
        $.each(this.attributes, function(i, attrib) {
            var name = attrib.name;
            var value = attrib.value;
            data[name] = value;
        });
        
        var event = thundashop.Ajax.createEvent(null, method, this, data);
        
        if ($(this).attr('gssilent') == "true") {
            thundashop.Ajax.post(event, null, true, true, true);
        } else {
            thundashop.Ajax.simplePost(this, method, data);
        }   
    },
    
    ajaxFile: 'handler.php',
    
    showErrorMessage: function(message) {
        $("#errorbox").show();
        $("#errorbox").html("<div class='errorform'><div class='close'></div>" + message + "</div>");
        $("#errorbox").delay(5000).fadeOut(1000);
    },
    doPreProcess: function() {
        PubSub.publish('NAVIGATED', {});
    },
    simplePost: function(from, functionName, data) {
        var event = thundashop.Ajax.createEvent(null, functionName, from, data);
        thundashop.Ajax.post(event);
    },
    postWithCallBack: function(data, callback, dontShowLoaderbox, xtra) {
        var file = this.ajaxFile;
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
                if(file !== "Chat.php") {
                    PubSub.publish("POSTED_DATA_WITHOUT_PRINT", "");
                }
                thundashop.common.triggerTimeoutCheck();
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
        PubSub.publish("POSTED_DATA", "");

        var file = this.ajaxFile;
        var uploadcallback = false;
        if (xtra !== undefined) {
            if (xtra.file)
                file = xtra.file;
            if (xtra.uploadcallback)
                uploadcallback = xtra.uploadcallback;
        }

        if (callback === undefined && dontUpdate !== true) {
            this.doPreProcess();
        }
        if (!(typeof(dontShowLoaderBox) !== "undefined" && dontShowLoaderBox === true))
            $('#loaderbox').show();

        var dataType = (data['synchron']) ? "html" : "json";
        $.ajax({
            type: "POST",
            url: file,
            data: thundashop.base64.encodeForAjax(data),
            dataType: dataType,
            context: document.body,
            success: function(response) {
                if (typeof(dontUpdate) === "undefined" || dontUpdate === false) {
                    thundashop.Ajax.updateFromResponse(response);
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
                
                if (callback !== undefined || dontUpdate === true) {
                    PubSub.publish("POSTED_DATA_WITHOUT_PRINT", "");
                }
                
                thundashop.common.triggerTimeoutCheck();
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
        this.doPreProcess();
        event['synchron'] = true;
        var result = "";
        $.ajax({
            type: "POST",
            url: this.ajaxFile,
            async: false,
            data: thundashop.base64.encodeForAjax(event),
            context: document.body,
            success: function(response) {
                result = response;
                PubSub.publish("POSTED_DATA_WITHOUT_PRINT", "");
                thundashop.common.triggerTimeoutCheck();
            },
            error: thundashop.handleAjaxError
        });

        return result;
    },
    postSynchronWithReprint: function(event) {
        $('#loaderbox').show();
        this.doPreProcess();
        var result = "";
        $.ajax({
            type: "POST",
            url: this.ajaxFile,
            data: thundashop.base64.encodeForAjax(event),
            async: false,
            dataType: "json",
            success: function(response) {
                thundashop.Ajax.updateFromResponse(response);
                thundashop.common.triggerTimeoutCheck();
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
        retevent.scopeid = scopeid;
        retevent.data = data;
        retevent.core = {}
        if(typeof(fromDomElement) === "string") {
            retevent.core.appid = fromDomElement;
        } else {
            retevent.core.apparea = $(fromDomElement).closest('.applicationarea').attr('area');
            retevent.core.appname = $(fromDomElement).closest('.app').attr('app');
            retevent.core.appid = $(fromDomElement).closest('.app').attr('appid');
        }
        return retevent;
    },
    updateFromResponse: function(response) {
        var scrolltop = $(window).scrollTop();
        if (response.errors) {
            thundashop.Ajax.showErrorMessage(response.errors)
        } else {
            $('#gsbody').html(response.content);
            PubSub.publish('NAVIGATION_COMPLETED', {response: response});
        }
        $(window).scrollTop(scrolltop);
        $('#loaderbox').hide();
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
        this.doPreProcess();
        $('#loaderbox').show();
        variables = variables.substring(variables.indexOf('?') + 1);
        url = 'json.php?' + variables + '&scopeid=' + scopeid;
        $.ajax({
            type: "POST",
            url: url,
            dataType: "json",
            data: data,
            success: function(response) {
                thundashop.Ajax.updateFromResponse(response);
                thundashop.common.triggerTimeoutCheck();
                PubSub.publish('navigation_complete', variables);
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
