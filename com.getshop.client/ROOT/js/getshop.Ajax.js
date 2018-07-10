
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
    
    PubSub.publish("AJAXERROR", error);
};

thundashop.Ajax = {
    init: function() {
        $(document).on('click','*[gsclick]', thundashop.Ajax.postgeneral);
        $(document).on('click','*[gslick]', thundashop.Ajax.showFunnyMessage);
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
    
    showFunnyMessage: function() {
        alert("HAHAHAHA, you wrote gslick instead of gsclick!");
    },
    
    closeModal: function() {
        thundashop.common.closeModal();
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
    
    showModal: function() {
        var data = {};
        
        $.each(this.attributes, function(i, attrib) {
            var name = attrib.name;
            var value = attrib.value;
            data[name] = value;
        });
        
        thundashop.common.showModal($(this).attr('gs_show_modal'), data);
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
                thundashop.Ajax.ajaxFile = "handler.php";
                callback(response);
                $('#loaderbox').hide();
                if(file !== "Chat.php") {
                    PubSub.publish("POSTED_DATA_WITHOUT_PRINT", "");
                }
                thundashop.common.sendPubSubMessage(data);
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
        
        PubSub.publish("POSTED_DATA", "");
        if(thundashop.common.isTableOverLayActive() && (typeof(callback) === "undefined" || callback === undefined || callback === null)) {
            data['synchron'] = true;
            callback = thundashop.common.reloadOverLay;
        }
        

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
                thundashop.Ajax.ajaxFile = "handler.php";
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
                
                thundashop.common.sendPubSubMessage(data);
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
                thundashop.Ajax.ajaxFile = "handler.php";
                result = response;
                PubSub.publish("POSTED_DATA_WITHOUT_PRINT", "");
                thundashop.common.sendPubSubMessage(thundashop.base64.encodeForAjax(event));
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
                thundashop.Ajax.ajaxFile = "handler.php";
                thundashop.Ajax.updateFromResponse(response);
                thundashop.common.sendPubSubMessage(thundashop.base64.encodeForAjax(event));
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
            retevent.core.fromappid = $(fromDomElement).closest('.app').attr('fromapplication');
        }
        return retevent;
    },
    updateFromResponse: function(response) {
        var scrolltop = $(window).scrollTop();
        if (response.errors) {
            thundashop.Ajax.showErrorMessage(response.errors)
        } else {
            if (response.content !== "gs_modal_active") {
                $('#gsbody').html(response.content);
            }
            
            $('#dynamicmodal').html(response.modal);
            $('.gsrightwidgetbody').html(response.rightWidget);
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
                thundashop.Ajax.ajaxFile = "handler.php";
                thundashop.Ajax.updateFromResponse(response);
                thundashop.common.sendPubSubMessage(info);
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
                thundashop.Ajax.ajaxFile = "handler.php";
                thundashop.Ajax.reloadCss();
                $('#loaderbox').hide();
            },
            error: thundashop.handleAjaxError
        })
    }
}



thundashop.Ajax.init();
