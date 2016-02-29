app.ProMeisterSpiderDiagram = {
    init: function() {
        $(document).on('change', '.ProMeisterSpiderDiagram .changeviewofchart', app.ProMeisterSpiderDiagram.changed);
    },
    
    changed: function() {
        thundashop.Ajax.simplePost(this, "setUserId", {
            userId : $(this).val()
        });
    }
};

app.ProMeisterSpiderDiagram.init();