app.PmsAddonSelection = {
    init : function() {
        $(document).on('change', '.PmsAddonSelection .toggleAddonTypeCheckbox', app.PmsAddonSelection.toggleAddon);
    },
    toggleAddon : function() {
        thundashop.Ajax.simplePost($(this), 'toggleAddon', {
            "type" : $(this).attr('addonType')
        });
    }
};
app.PmsAddonSelection.init();