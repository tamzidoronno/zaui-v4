app.PmsSearchBox = {
    init : function() {
        $(document).on('click','.PmsSearchBox .advancesearchtoggle',app.PmsSearchBox.toggleAdvanceSearch);
        $(document).on('click','.PmsSearchBox .opensimplesearch',app.PmsSearchBox.toggleAdvanceSearch);
        $(document).on('click','.PmsSearchBox .advancesearchicon',app.PmsSearchBox.toggleAdvanceSearchButton);
        $(document).on('click','.PmsSearchBox .typeselectiontoggle',app.PmsSearchBox.toggleRoomSelection);
        $(document).on('click','.PmsSearchBox .roomtypeselection .fa-close',app.PmsSearchBox.toggleRoomSelection);
        $(document).on('click','.PmsSearchBox .roomtypeselection .type input[type="checkbox"]',app.PmsSearchBox.updateRoomTypeCounter);
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