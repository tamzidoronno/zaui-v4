GetShop = {};

thundashop.framework = {
    bindEvents : function() {
        $('*[gstype="form"] *[gstype="submit"]').live('click', function(e) {
            thundashop.framework.submitFromEvent(e);
        });
        $('*[gstype="changesubmit"]').live('change', function(e) {
            if($(this).attr('method')) {
                thundashop.framework.submitElement(e);
            } else {
                thundashop.framework.submitFromEvent(e);
            }
        });
        $('*[gstype="form"] *[gstype="submitenter"], *[gstype="clicksubmit"]').live('keyup', function(e) {
            if(e.keyCode === 13) {
                if($(e.target).attr('gsType') === "clicksubmit") {
                    thundashop.framework.submitElement(e);
                } else {
                    thundashop.framework.submitFromEvent(e);
                }   
            }
        });
        $('*[gstype="clicksubmit"]').live('click', function(e) {
            var target = $(e.target);
            if(target.prop("tagName") === "INPUT") {
                return;
            }
            thundashop.framework.submitElement(e);
        });
        
        $('.informationbox .payforapplications').live('click', function(e) {
             $(this).closest('.informationbox').find('#paypalform').submit();
        });
    },
    
    submitElement : function(event) {
        var element = $(event.target);
        var name = element.attr('gsname');
        var value = element.attr('gsvalue');
        var method = element.attr('method');
        if(!value) {
            value = element.val();
        }
        
        var data = {}
        data[name] = value;
        
        var event = thundashop.Ajax.createEvent("", method, element, data);
        thundashop.framework.postToChannel(event, element);
    },
    
    getCallBackFunction: function(element) {
        var appName = element.closest('.app').attr('app');
        var appContext = GetShop[appName]
        if (appContext && appContext.formPosted) 
            return appContext.formPosted
        
        if (element && typeof(element.callback) !== "undefined" ) {
            return element.callback;
        }
        
        return null;
    },
    
    postToChannel : function(event, element) {
        thundashop.common.destroyCKEditors();
        var callback = this.getCallBackFunction(element);
        if(!element.attr('output')) {
            thundashop.Ajax.post(event, callback, event);
            thundashop.common.hideInformationBox(null);
        } else if(element.attr('output') == "informationbox") {
            var informationTitle = element.attr('informationtitle');
            var box = thundashop.common.showInformationBox(event, informationTitle);
            box.css('min-height','10px');
            if (typeof(callback) == "function") {
                callback(box.html(), event);
            }
        }
    },
    
    submitFromEvent : function(event) {
        var target = $(event.target);
        thundashop.framework.submitFromElement(target);
    },
    
    createGsArgs : function(form) {
        var args = {};
        var ckeditors = thundashop.common.destroyCKEditors();
        
        form.find('*[gsname]').each(function(e) {
            var name = $(this).attr('gsname');
            if(!name || name.trim().length == 0) {
                alert('Name attribute is missing for gstype value, need to be fixed');
                return;
            }
            var value = $(this).attr('gsvalue');
            if(!value || value === undefined) {
                value = $(this).val();
            }
        
            if($(this).is(':checkbox')) {
                value = $(this).is(':checked');
            }
        
            if($(this).attr('gstype') == "ckeditor") {
                value = ckeditors[$(this).attr('id')];
            }
            args[name] = value;
        });
        return args;
    },
            
    submitFromElement : function(element) {
        var form = element.closest('*[gstype="form"]');
        var method = form.attr('method');
        var args = thundashop.framework.createGsArgs(form);
        form.callback = element.callback;
        var event = thundashop.Ajax.createEvent("", method, element, args);
        thundashop.framework.postToChannel(event, form);        
    },
    
    reprintPage : function() {
        var event = thundashop.Ajax.createEvent("", "systemReloadPage", null, null);
        thundashop.Ajax.post(event);
    }
    
};

thundashop.framework.bindEvents();