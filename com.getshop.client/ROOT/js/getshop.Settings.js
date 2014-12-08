getshop.Settings = {
    init: function () {
        var me = this;
        $(document).on('click', '.store_settings_button', getshop.Settings.showSettings);
        $(document).on('click', '.gss_backtopage', getshop.Settings.showPage);
        $(document).on('click', '[gss_goto_app]', function () { me.setApplicationId(this); });
        $(document).on('click', '[gss_method]', function () { me.gssMethodInvoke(this); });
        $(document).on('click', '[gss_fragment]:not([gss_method])', function () { me.gssShowFragment(this); });
        $(document).on('click', '.gss_tab', function () { me.gssSelectTab(this); });
        $(document).on('mouseenter', '.gss_topmenu-button', function () { me.showSubMenu(this); });
        $(document).on('mouseleave', '.gss_topmenu-button', function () { me.hideSubMenu(this) });
    },
    showSubMenu: function (element) {
        $(element).find('.gss_menu_submenu').show();
    },
    gssSelectTab: function (element) {
        var tabid = $(element).attr('gss_tab');
        $(element).parent().find('.gss_tab').removeClass('gss_tab_active');
        $(element).parent().find('.gss_tab_view').removeClass('gss_tab_active');
        $(element).parent().find('.gss_tab[gss_tab="' + tabid + '"]').addClass('gss_tab_active');
        $(element).parent().find('.gss_tab_view[gss_tab="' + tabid + '"]').addClass('gss_tab_active');
    },
    hideSubMenu: function (element) {
        $(element).find('.gss_menu_submenu').hide();
    },
    showSuccessMessage: function (message) {
        $('#backsidesettings .successdialoagbacksettings .message').html(message);
        $('#backsidesettings .successdialoagbacksettings').show();
        $('#backsidesettings .successdialoagbacksettings').delay(1000).fadeOut(200);
    },
    gssShowFragment: function (field) {
        this.post({}, "gs_show_fragment", field);
    },
    gssMethodInvoke: function (field) {
        var method = $(field).attr('gss_method');
        var model = {};
        if ($(field).attr('gss_model')) {
            model = getshop.Model[$(field).attr('gss_model')];
        }

        this.post(model, method, field);
    },
    setApplicationId: function (button, callback) {
        var appId = button;
        
        if ($(button).attr('gss_goto_app')) {
            appId = $(button).attr('gss_goto_app');
        }
        
        localStorage.setItem("currentApp", appId);

        $('.gss_topmenu').find('.gss_active').removeClass('gss_active');
        $('.gss_topmenu').find('[gss_goto_app="' + appId + '"]').addClass('gss_active');
        
        
        successMethod = function(response, field, data) {
            getshop.Settings.successfully(response, field, data);
            if (typeof(callback) === "function") 
                callback(response, field, data);
        }

        this.doPost({}, false, successMethod);
    },
    reload: function () {
        this.post(false, null, null, true);
    },
    getCurrentAppId: function () {
        var appId = localStorage.getItem("currentApp");
        if (!appId) {
            // App settings id for the DashBoard application
            return "b81bfb16-8066-4bea-a3c6-c155fa7119f8";
        }

        return appId;
    },
    post: function (data, method, field, loadFragment) {
        if (!data) {
            data = {};
        }

        if (method) {
            data['gss_method'] = method;
        }
        
        if ( $(field).attr('gss_fragment')) {
            data['gss_fragment'] = $(field).attr('gss_fragment');
        }
        
        if ($(field).attr('gss_view')) {
            data['gss_view'] = $(field).attr('gss_view');
        }
    
        if (field && $(field).attr("gss_value")) {
            data.value = $(field).attr("gss_value");
        }
        
        if (field && $(field).attr("gss_value_2")) {
            data.value2 = $(field).attr("gss_value_2");
        }
        
        if (!loadFragment && !$(field).attr('gss_loadsilent')) {
            localStorage.setItem("current_gss_data", JSON.stringify(data));
        }
        
        var successFirstLoad = function(response, field, data) {
            getshop.Settings.successfully(response,field, data);
            
            if (loadFragment) {
                var jsonData = localStorage.getItem("current_gss_data");
                var data = JSON.parse(jsonData);
                getshop.Settings.doPost(data, null, getshop.Settings.successfully);
            }
        }
        
        this.doPost(data, field, successFirstLoad);
    },
    successfully: function(response, field, data) {
        var view = data['gss_view'];
        var successMessage = $(field).attr('gss_success_message');

        var successMethod = $(field).attr('gss_success_method');
        if (successMethod) {
            var appScope = app[$(field).closest('.app').attr('app')];

            if (appScope) {
                var fn = appScope[successMethod];
                if(typeof fn === 'function') {
                    fn(field);
                }
            }
        }
        else if (successMessage) {
            getshop.Settings.showSuccessMessage(successMessage);
        } else if (view) {
            $('.' + view).html(response['data']);
            $('#' + view).html(response['data']);
        } else {
            $('.gss_topmenu').html(response['topMenu']);
            $('.gss_settings_inner.apparea').html(response['data']);
        }

        getshop.Models.addWatchers(response['data']);
    },
    reloadCss: function() {
        $.ajax('/StyleSheet.php', {
            success: function(response) {
                var alreadyLoaded = $('html .appstylesheet');
                var checkThisCss = $("<div>"+response+"</div>").find('.appstylesheet');
                checkThisCss.each(function() {
                    if (!getshop.Settings.isCssLoaded(this, alreadyLoaded)) {
                        $('html').append(this);
                    } 
                })
                
            }
        })
    },
    reloadJavascripts: function() {
        $.ajax('/StyleSheet.php', {
            success: function(response) {
                var alreadyLoaded = $('html .appstylesheet');
                var checkThisCss = $("<div>"+response+"</div>").find('.appstylesheet');
                checkThisCss.each(function() {
                    if (!getshop.Settings.isCssLoaded(this, alreadyLoaded)) {
                        $('html').append(this);
                    } 
                })
                
            }
        })
    },
    isCssLoaded: function(checkCss, checkThisCsss) {
        var index;
        for (index = 0; index < checkThisCsss.length; ++index) {
            if ($(checkThisCsss[index]).attr('href') == $(checkCss).attr('href')) {
                return true;
            }
        }
        
        return false;
    },
    doPost: function(data, field, success) {
        data['appid'] = this.getCurrentAppId();
        getshop.Settings.loadingTimer = setTimeout(function() {
            $('#gss_loading_icon').show();
        }, 300);
        
        
        $.ajax({
            type: "POST",
            url: "/settingsnav.php",
            dataType: "json",
            data: data,
            context: document.body,
            success: function (response) {
                clearTimeout(getshop.Settings.loadingTimer);
                $('#gss_loading_icon').hide();
                success(response, field, data);
            },
            error: function (failure) {
                $('#gss_loading_icon').hide();
                
                
                var json = localStorage.getItem("current_gss_data");
                if (json) {
                    var jsonData = JSON.parse(json);
                    if (jsonData['gss_method'] == data['gss_method']) {
                        localStorage.setItem("current_gss_data", "");
                    }
                }
                
                throw exception(failure);
            }
        });
    },
    
    showSettings: function (fadeIn) {
        var speed = 300;
        if (!fadeIn) {
            $('.gss_settings_inner.apparea').html("");
            speed = 0;
        }
        $('#gsbody').fadeOut(speed, function () {
            $('#backsidesettings').fadeIn(speed);
        });
        var event = thundashop.Ajax.createEvent(null, "setShowingSettings", null, {});
        thundashop.Ajax.post(event);
    },
    showPage: function () {
        $('#backsidesettings').fadeOut(300, function () {
            $('#gsbody').fadeIn(300);
            var event = thundashop.Ajax.createEvent(null, "unsetShowingSettings", null, {});
            thundashop.Ajax.post(event);
        });
        
    },
}

getshop.Settings.init();