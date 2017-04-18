app.MecaFleetTitlePrinter = {
    init: function(){
        $(document).on('change', '.MecaFleetTitlePrinter #followupslider', app.MecaFleetTitlePrinter.changeCheckboxStyle);
        $(document).on('click', '.MecaFleetTitlePrinter .manualfollowup', app.MecaFleetTitlePrinter.changeToManualfollowUp);
        $(document).on('click', '.MecaFleetTitlePrinter .directfollowup', app.MecaFleetTitlePrinter.changeToDirectFollowUp);
        $(document).on('mousedown', '.MecaFleetTitlePrinter .gsniceslider1', app.MecaFleetTitlePrinter.toggleCheckbox);
        $(document).on('click', '.MecaFleetTitlePrinter .gsniceslider1', app.MecaFleetTitlePrinter.preventDefault);
        $(document).ready(function(){
            if($('#followupslider').is(':checked')){
                $('.manualfollowup').addClass('active');
                $('.directfollowup').removeClass('active');
                $('#followupslider').prop('checked', true);
            }else{
                $('.manualfollowup').removeClass('active');
                $('.directfollowup').addClass('active');
                $('#followupslider').prop('checked', false);
            }
        });
    },
    changeCheckboxStyle: function(){
        $('.manualfollowup').toggleClass('active');
        $('.directfollowup').toggleClass('active');
    },
    changeToManualfollowUp: function(){
        $('.manualfollowup').addClass('active');
        $('.directfollowup').removeClass('active');
        $('#followupslider').prop('checked', true);
    },
    changeToDirectFollowUp: function(){
        $('.manualfollowup').removeClass('active');
        $('.directfollowup').addClass('active');
        $('#followupslider').prop('checked', false);
    },
    toggleCheckbox: function(){
        $('.manualfollowup').toggleClass('active');
        $('.directfollowup').toggleClass('active');
        $('#followupslider').prop('checked', !$('#followupslider').prop('checked'));
    },
    preventDefault: function(e){
        e.preventDefault();
        e.stopPropagation();
    }
};
app.MecaFleetTitlePrinter.init();