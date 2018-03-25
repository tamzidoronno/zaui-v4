app.GetShopProductPresenter = {
    init : function() {
        $(document).on('click', '.GetShopProductPresenter .activate', app.GetShopProductPresenter.activate);
    },
    
    activate: function() {
        if ($(this).find('.deactivate_innner').is(':visible')) {
            $(this).closest('.roundbox').removeClass('active');
            var clslist = $(this).closest('.roundbox').attr('class').split(/\s+/);
            for (var i in clslist) {

                var cls = clslist[i];
                if (cls == "roundbox")
                    continue;
                
                $('.activate[require*="'+cls+'"').each(function() {
                    if (!$(this).closest('.roundbox').hasClass('active')) {
                        return;
                    }
                    
                    var required = $("<div class='removedtorequirement'>Dependent</div>");
                    $(this).closest('.roundbox').removeClass('active');
                    $(this).closest('.roundbox').prepend(required);
                    setTimeout(function() {
                        $('.removedtorequirement').fadeOut(function() {
                            $(this).remove();
                        });
                    }, 1000);
                })
            }
        } else {
            var requirements = $(this).attr('require');

            var requiredModules = requirements.split(',');
            for (var i in requiredModules) {
                var module = requiredModules[i];
                if (module) {
                    
                    if ($(".roundbox."+module).hasClass('active')) {
                        continue;
                    }
                    
                    var required = $("<div class='addedutorequirement'>Required</div>");
                    $(".roundbox."+module).addClass('active');
                    $(".roundbox."+module).prepend(required);
                    setTimeout(function() {
                        $('.addedutorequirement').fadeOut(function() {
                            $(this).remove();
                        });
                    }, 1000);
                }
            }

            $(this).closest('.roundbox').addClass('active');
        }
        
        app.GetShopProductPresenter.selectionChanged(this);
    },
    
    selectionChanged: function(me) {
        var activeModules = [];
        
        $(".GetShopProductPresenter .roundbox.active").each(function() {
            activeModules.push($(this).attr('module'));
        });
        
        var string = activeModules.join(',');
        
        if (!string) {
            $('.GetShopProductPresenter .selectionform').hide();
            return;
        } else {
            $('.GetShopProductPresenter .selectionform').show();
        }
        
        $('.GetShopProductPresenter input[gsname="modules"]').val(string);
        var data = {
            modules : string
        }
        
        var event = thundashop.Ajax.createEvent(null, "showDescription", me, data);
        
        $('.GetShopProductPresenter .descriptions').html("<i class='fa fa-spin fa-spinner'></i>");
        $('.GetShopProductPresenter .selectionform .description[selectiontype="'+string+'"]').show();
    }
}

app.GetShopProductPresenter.init();