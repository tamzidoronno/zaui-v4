app.MecaFleetTitlePrinter = {
    init:function(){
        $(document).on('click', '.MecaFleetTitlePrinter .fa-plus-square', app.MecaFleetTitlePrinter.showEditFleet);
    },
    showEditFleet: function(){
        $('.editmecafleetform').toggle();
    }
    
};
app.MecaFleetTitlePrinter.init();