app.ProMeisterScoreSettings = {
    init: function() {
        $(document).on('change', '.ProMeisterScoreSettings .selectTest', app.ProMeisterScoreSettings.selectedTest);
    },
    
    selectedTest: function() {
        var testId = $(this).val();
        var dom = $('.ProMeisterScoreSettings .catrow[testid="'+testId +'"]');
        if ($(this).is(':checked')) {
            dom.show();
        } else {
            dom.hide();
        }
    }
};

app.ProMeisterScoreSettings.init();