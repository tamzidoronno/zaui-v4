app.MecaFleetGroupCreate = {
    init: function(){
        $(document).on('change', '.MecaFleetGroupCreate #followupslider', app.MecaFleetGroupCreate.changeCheckboxStyle);
        $(document).on('click', '.MecaFleetGroupCreate .manualfollowup', app.MecaFleetGroupCreate.changeToManualfollowUp);
        $(document).on('click', '.MecaFleetGroupCreate .directfollowup', app.MecaFleetGroupCreate.changeToDirectFollowUp);
        $(document).on('mousedown', '.MecaFleetGroupCreate .gsniceslider1', app.MecaFleetGroupCreate.toggleCheckbox);
        $(document).on('click', '.MecaFleetGroupCreate .gsniceslider1', app.MecaFleetGroupCreate.preventDefault);
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
app.MecaFleetGroupCreate.init();