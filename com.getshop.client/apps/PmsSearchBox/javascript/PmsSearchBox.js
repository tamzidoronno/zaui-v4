app.PmsSearchBox = {
    init : function() {
        $(document).on('click','.PmsSearchBox .advancesearchtoggle',app.PmsSearchBox.toggleAdvanceSearch);
        $(document).on('click','.PmsSearchBox .otherselectiontoggle',app.PmsSearchBox.otherToggle);
        $(document).on('click','.PmsSearchBox .opensimplesearch',app.PmsSearchBox.toggleAdvanceSearch);
        $(document).on('click','.PmsSearchBox .advancesearchicon',app.PmsSearchBox.toggleAdvanceSearchButton);
        $(document).on('click','.PmsSearchBox .typeselectiontoggle',app.PmsSearchBox.toggleRoomSelection);
        $(document).on('click','.PmsSearchBox .roomtypeselection .fa-close',app.PmsSearchBox.toggleRoomSelection);
        $(document).on('click','.PmsSearchBox .roomtypeselection .type input[type="checkbox"]',app.PmsSearchBox.updateRoomTypeCounter);
        $(document).on('click','.PmsSearchBox .searchcustomer',app.PmsSearchBox.searchForCustomers);
        $(document).on('click','.PmsSearchBox .addusertofilter',app.PmsSearchBox.addUserToFilter);
        $(document).on('click','.PmsSearchBox .addaddon',app.PmsSearchBox.addAddon);
        $(document).on('click','.PmsSearchBox .addtofilterrow .fa-trash-o',app.PmsSearchBox.removeRow);
        $(document).on('click','.PmsSearchBox .applyfilterbutton',app.PmsSearchBox.applyFilter);
        $(document).on('click','.PmsSearchBox .addonstofilter',app.PmsSearchBox.updateOtherFilterCounter);
        $(document).on('click','.PmsSearchBox .clearfilter',app.PmsSearchBox.clearFilter);
        $(document).on('click','.PmsSearchBox .displaydailydatepicker',app.PmsSearchBox.displayDailyRangePicker);
        $(document).on('click','.PmsSearchBox .dailydaterangepicker [daytype]',app.PmsSearchBox.changeSelection);
    },
    changeSelection : function() {
        var date = $(this).attr('daytype');
        var type = $(this).closest('.dailydaterangepicker').attr('type');
        thundashop.Ajax.simplePost($(this), "quickfilterselection", {
            "date" : date,
            "type" : type
        });
        
    },
    displayDailyRangePicker : function() {
        var view = $('.PmsSearchBox .dailydaterangepicker');
        view.find('.header').hide();
        var type = $(this).attr('type');
        view.attr('type', type);
        view.find('[type="'+type+'"]').show(); 
        if(view.is(':visible')) {
            view.slideUp();
        } else {
            view.slideDown();
        }
    },
    clearFilter : function() {
        thundashop.Ajax.simplePost($(this),"clearFilter", {});
    },
    applyFilter : function() {
        var form = $(this).closest("[gstype='form']");
        var args = thundashop.framework.createGsArgs(form);
        
        var customers = [];
        $('.customersadded .addtofilterrow').each(function(res) {
           customers.push($(this).attr("userid")); 
        });
        args['customers'] = customers;
        
        var codes = [];
        $('.codesadded .addtofilterrow').each(function(res) {
           codes.push($(this).attr("code")); 
        });
        args['codes'] = codes;

        var addons = [];
        $('.addonstofilter').each(function() {
            if($(this).is(':checked')) {
                addons.push($(this).attr('productid'));
            }
        });
        
        args['addons'] = addons;
        
        var event = thundashop.Ajax.createEvent('',form.attr('method'), form, args);
        thundashop.Ajax.post(event);
    },
    
    otherToggle : function() {
        $('.PmsSearchBox .otherselection').toggle();
    },
    addAddon : function() {
        var input = $('.addaddonvalue');
        $('.codesadded').append('<div class="addtofilterrow" code="'+input.val()+'"><i class="fa fa-trash-o"></i>'+input.val()+"</div>");
        input.val('');
        app.PmsSearchBox.updateOtherFilterCounter();
    },
    addUserToFilter : function(event) {
        var target = $(event.target);
        var box = $(this);
        if(target.hasClass('fa-trash-o')) {
            return;
        }
        $('.customersadded').append(box.clone());
        box.remove();
        app.PmsSearchBox.updateOtherFilterCounter();
    },
    
    
    updateOtherFilterCounter : function() {
        var customerCount = 0;
        var codes = 0;
        var addons = 0;
        $('.customersadded .addtofilterrow').each(function(res) { customerCount++; });
        $('.codesadded .addtofilterrow').each(function(res) { codes++; });
        $('.addonstofilter').each(function() {
            if($(this).is(':checked')) {
                addons++;
            }
        });
        
        var text = "(" + customerCount + "," + codes + "," + addons + ")";
        $('.otherselectiontoggle .selectioncount').html(text);
    },
    removeRow : function() {
        $(this).closest('.addtofilterrow').remove();
        app.PmsSearchBox.updateOtherFilterCounter();
    },
    searchForCustomers : function() {
        var input = $('.searchcustomervalue');
        var toSearch = input.val();
        input.val('');
        
        var event = thundashop.Ajax.createEvent('','searchCustomers', input, {
            "name" : toSearch
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.customersearcresultpanel').html(res);
        });
    },
    updateRoomTypeCounter : function() {
        var counter = 0;
        $('.PmsSearchBox .roomtypeselection .type input[type="checkbox"]').each(function() {
            if($(this).is(':checked')) {
                counter++;
            }
        });
        if(counter === 0) {
            counter = "All";
        }
        $('.PmsSearchBox .selectioncount').html(counter);
    },
    toggleRoomSelection : function() {
        if($('.PmsSearchBox .roomtypeselection').is(':visible')) {
            $('.PmsSearchBox .roomtypeselection').slideUp();
        } else {
            $('.PmsSearchBox .roomtypeselection').slideDown();
        }
    },
    toggleAdvanceSearchButton : function() {
        var forFilter = $(this).attr('forfilter');
        if($(this).hasClass('advanceserachbuttonenabled')) {
            $(this).removeClass('advanceserachbuttonenabled');
            $('input[gsname="'+forFilter+'"]').val('false');
        } else {
            $(this).addClass('advanceserachbuttonenabled');
            $('input[gsname="'+forFilter+'"]').val('true');
        }
    },
    toggleAdvanceSearch : function() {
        if($('.PmsSearchBox .advancesearch').is(':visible')) {
            localStorage.setItem('advancesearchtoggled', "false");
            $('.PmsSearchBox .simplesearch').show();
            $('.PmsSearchBox .advancesearch').hide();
            $('input[gsname="searchtext"]').focus();
        } else {
            localStorage.setItem('advancesearchtoggled', "true");
            $('.PmsSearchBox .simplesearch').hide();
            $('.PmsSearchBox .advancesearch').show();
        }
    }
};
app.PmsSearchBox.init();