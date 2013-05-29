/* 
 * Skeleton 
 * 
 * Created by KT.
 */

thundashop.Namespace.Register('thundashop.Skeleton');

thundashop.Skeleton = {
    applicationName: null,
    lastName : '',
    currentHelpStep : 1,
    
    hideApplication : function(currentdom) {
        $(currentdom).html(this.lastName);
    },
    
    removeApplication : function(id, area) {
        var data = {};
        data.appid = id;
        
        var confirmed =  thundashop.common.confirm(thundashop.translation.confirm_delete_application);
        if(confirmed) {
            var event = thundashop.Ajax.createEvent('ApplicationManager', 'removeApplicationFromArea', $(document), data);
            thundashop.Ajax.post(event, 'apparea-'+area);
        }
        thundashop.common.unmask();
    }, 
    
    updateSticked : function(element) {
        if (element.hasClass('sticked')) {
            element.removeClass('sticked');
            element.addClass('notsticked');
        } else {
            element.addClass('sticked');
            element.removeClass('notsticked');
        }
    },
    
    addApplicationDirect: function(applicationName, renderStandalone) {
        data = {
            appId : applicationName
        }
        var event = thundashop.Ajax.createEvent('ApplicationManager', 'addApplicationDirect', $(document), data);
        var id = thundashop.Ajax.postSynchron(event);
        window.location.hash = '?page=settings&applicationId=' + id;
    },
    
    stepHelpPrev : function() {
        var helpbox = $('.helpbox');
        if(thundashop.Skeleton.currentHelpStep == 4) {
            $('.helpbox').fadeOut(function(e) {
                helpbox.css('left','10px');
                helpbox.fadeIn();
            });
        }
        thundashop.Skeleton.currentHelpStep = thundashop.Skeleton.currentHelpStep - 2;
        if(thundashop.Skeleton.currentHelpStep < 0) {
            thundashop.Skeleton.currentHelpStep = 0;
        }
        thundashop.Skeleton.stepHelpNext();
    },
    
    stepHelpNext : function() {
        var helpbox = $('.helpbox');
        helpbox.find('.step').hide();
        if(thundashop.Skeleton.currentHelpStep == 0) {
            helpbox.find('.step1').fadeIn();
            helpbox.find('.helpboxarrow').animate( {left: 40});
            thundashop.Skeleton.currentHelpStep = 1;
        } else if(thundashop.Skeleton.currentHelpStep == 1) {
            helpbox.find('.step2').fadeIn();
            helpbox.find('.helpboxarrow').animate( {left: 240});
            thundashop.Skeleton.currentHelpStep = 2;
        } else if(thundashop.Skeleton.currentHelpStep == 2) {
            helpbox.find('.prev').show();
            helpbox.find('.step2').hide();
            helpbox.find('.step3').fadeIn();
            helpbox.find('.helpboxarrow').animate( {left: 360});
            thundashop.Skeleton.currentHelpStep = 3;
        } else if(thundashop.Skeleton.currentHelpStep == 3) {
            $('.helpbox').fadeOut(function(e) {
                helpbox.css('left','');
                helpbox.css('right','10px');
                helpbox.fadeIn();
                helpbox.find('.step4').fadeIn();
                helpbox.find('.helpboxarrow').css('left','350px');
                thundashop.Skeleton.currentHelpStep = 4;
                
                var event = thundashop.Ajax.createEvent('ApplicationManager', 'setHelpRead', $(this), null);
                thundashop.Ajax.post(event);
            });
        } else {
            helpbox.fadeOut();
        }
    },
    
    closeHelpBox : function() {
        var helpbox = $('.helpbox');
        helpbox.fadeOut();
    },
    
    deleteApplication : function(id) {
        var event = thundashop.Ajax.createEvent('ApplicationManager', 'deleteApplication', $(this), { appId : id });
        thundashop.Ajax.post(event, function() {
            window.location.hash = '?page=settings&applicationId=aisdf29-asdf712-asdf23451-asdf-asdfasfd-asdf23-54-das-12';
        }, null, true);
    },

    changeTheme: function(form) {
        var appSettingsId = form.find('[gsname=appSettingsId]').val();
        var data = thundashop.framework.createGsArgs(form);
        data.appId = appSettingsId;
        
        var callback = function() {
            window.location.hash="page=home&app="+appSettingsId;
            document.getElementById('mainlessstyle').href='StyleSheet.php';
            thundashop.common.unlockMask();
            thundashop.common.hideInformationBox(null);
        }
        
        var event = thundashop.Ajax.createEvent('ApplicationManager', 'addApplicationDirect', $(document), data);
        thundashop.Ajax.postWithCallBack(event, callback);
    }
}

$('.application-configuration-top-menu .entry').live('click', function() {
    var action = $(this).attr('action');
    var id = $(this).closest('.app').attr('appid');
    var area = $(this).closest('.app').attr('apparea');
    
    if (action == "removeapplication") {
        thundashop.Skeleton.removeApplication(id, area);
    }
});

$('.sticking').live('click', function() {
    var event = thundashop.Ajax.createEvent('ApplicationManager', 'toggleSticky', $(this), null);
    event.core.appname = "ApplicationManager";
    event.core.applicationid = event.core.appid;
    event.core.appid = undefined;
    thundashop.Ajax.post(event, null);
})

$('#importApplicationToArea').live('click', function() {
    var appSettingsId = $(this).attr('appSettingsId');
    var area = $(this).attr('area');
    var data = {
        appSettingsId : appSettingsId,
        area : area
    };
    var event = thundashop.Ajax.createEvent("ApplicationManager", "importExistingApplication", null, data);
    event['synchron'] = true;
    var callBack = function(response) {
        $('#informationbox-holder').fadeOut(200);
        response = JSON.parse(response);
        GetShop.ImportApplication.setApps(response, area);
        GetShop.ImportApplication.next();
        
        $('.add_application_menu').each(function() {
           $(this).hide(); 
        });
        
    }
    
    thundashop.Ajax.post(event, callBack);
    
});

$('#addApplicationToArea').live('click', function() {
    var form = $(this).closest('.application').find('#applicationform');
    
    var area = form.find('[gsname=applicationArea]').val();
    if (area == "themes") {
        thundashop.Skeleton.changeTheme(form);
        return;
    }
            
            
    //First check if all elements are filled out.
    var valid = true;
    form.find('*[gsname]').each(function(e) {
        if(!$(this).val() && $(this).is('input')) {
            return;
        }
    });
    
    if(valid) {
        var extended = form.find('*[gsname="getshop_extended_application_add"]').val();
        if(extended == "true") {
            var data = {
                "applicationName" : form.find("input[gsname='applicationName']").val(),
                "applicationArea" : form.find("input[gsname='applicationArea']").val(),
                "appSettingsId" : form.find("input[gsname='appSettingsId']").val()
            }
            var event = thundashop.Ajax.createEvent("", "getStartedExtended", null, data);
            thundashop.common.showInformationBox(event, data.applicationName + " configuration");
        } else {
            thundashop.framework.submitFromElement(form);
        }
    } else {
        $('.application .allFieldsNeedToBeFilled').fadeIn();
        setTimeout("$('.application .allFieldsNeedToBeFilled').fadeOut()", "2000");
    }
});

$('#activateSingletonApplication').live('click', function() {
    var id = $(this).attr('appId');
    var standAlone = $(this).attr('renderstandalone') === "true";
    thundashop.Skeleton.addApplicationDirect(id, standAlone);
});
$('.removesingletonapplication').live('click', function() {
    var id = $(this).attr('appid');
    thundashop.Skeleton.deleteApplication(id);
});
$('.helpbox .next').live('click', function(e) {
    thundashop.Skeleton.stepHelpNext();
});
$('.helpbox .prev').live('click', function(e) {
    thundashop.Skeleton.stepHelpPrev();
});
$('.helpbox .close').live('click', function(e) {
    thundashop.Skeleton.closeHelpBox();
});