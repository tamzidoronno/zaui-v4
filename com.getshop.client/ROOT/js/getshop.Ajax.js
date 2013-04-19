/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var doCkeditorBlur = true;
var isNavigating = false;
var hasFadeInEffect = false;

thundashop.Namespace.Register("thundashop.Ajax");

thundashop.Ajax = {
    ajaxFile: 'handler.php',
    showErrorMessage: function(message) {
        $("#errorbox").show();
        $("#errorbox").html("<div class='errorform'><div class='close'></div>" + message + "</div>");
        $("#errorbox").delay(5000).fadeOut(1000);
    },
    doPreProcess: function() {
        PubSub.publish('NAVIGATED', {});
    },
    postWithCallBack: function(data, callback, dontShowLoaderbox, useFile) {
        if (!(typeof(dontShowLoaderbox) !== "undefined" && dontShowLoaderbox === true))
            $('#loaderbox').show();

        data['synchron'] = true;
        $.ajax({
            type: "POST",
            url: typeof(useFile) !== "undefined" ? useFile : this.ajaxFile,
            data: data,
            context: document.body,
            success: function(response) {
                callback(response);
                $('#loaderbox').hide();
            }
        });
    },
    post: function(data, callback, extraArg, dontUpdate, dontShowLoaderBox, useFile) {
        if (callback === undefined && dontUpdate !== true) {
            this.doPreProcess();
        }
        if (!(typeof(dontShowLoaderBox) !== "undefined" && dontShowLoaderBox === true))
            $('#loaderbox').show();

        $.ajax({
            type: "POST",
            url: typeof(useFile) !== "undefined" ? useFile : this.ajaxFile,
            data: thundashop.base64.encodeForAjax(data),
            dataType: "json",
            context: document.body,
            success: function(response) {
                if (typeof(dontUpdate) === "undefined" || dontUpdate === false) {
                    thundashop.Ajax.updateFromResponse(response);
                }
                if (typeof(callback) === "function") {
                    callback(response, extraArg);
                    $('#loaderbox').hide();
                }
            }
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
            }
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
                if (response.errors && response.errors !== "")
                    result = false;
            }
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
        retevent.core.apparea = $(fromDomElement).closest('.applicationarea').attr('area');
        retevent.core.appname = $(fromDomElement).closest('.app').attr('app');
        retevent.core.appid = $(fromDomElement).closest('.app').attr('appid');
        return retevent;
    },
    updateFromResponse: function(response) {
        var scrolltop = $(window).scrollTop();
        if (response.errors) {
            console.log(response.errorCodes);
            if(response.errorCodes[0] === 93) {
                var event = thundashop.Ajax.createEvent('','loadpaymentinfo',$(this), {});
                thundashop.common.showInformationBox(event,'Payment information');
            } else {
                thundashop.Ajax.showErrorMessage(response.errors)
            }
        } else {
            for (var divid in response) {
                if (response[divid] !== null) {
                    if (hasFadeInEffect) {
                        if (divid === "apparea-middle") {
                            $('#' + divid).hide();
                            $('#' + divid).html(response[divid]);
                            $('#' + divid).fadeIn(600);
                        } else {
                            $('#' + divid).html(response[divid]);
                        }
                    } else {
                        $('#' + divid).html(response[divid]);
                    }

                    if (thundashop.MainMenu.hidden) {
                        $('.mainmenu .content').hide();
                        $('.mainmenu .hide div').html("show mainmenu");
                    }
                }
            }
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
                PubSub.publish('navigation_complete', variables);
                if (typeof(callback) !== "undefined" && typeof(callback) !== "boolean") {
                    callback();
                }
            }
        })
    },
    changeTheeme: function(template, colors) {
        $('#loaderbox').show();
        url = 'colorloader.php?setTheeme=' + template + '&colors=' + colors;
        $.ajax({
            type: "POST",
            url: url,
            success: function(response) {
                document.getElementById('mainlessstyle').href = 'StyleSheet.php';
                $('#loaderbox').hide();
            }
        })
    }
}
