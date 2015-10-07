app.QuestBackProgress = {
    init: function() {
        
    },
    
    updateProgressBar: function(percent) {
        percent = 275 * (percent/100);
        
        $('.QuestBackProgress .speedgauge_outer .speedpin').animate({  borderSpacing: percent }, {
            step: function(now,fx) {
              $(this).css('-webkit-transform','rotate('+now+'deg)'); 
              $(this).css('-moz-transform','rotate('+now+'deg)');
              $(this).css('transform','rotate('+now+'deg)');
            },
            duration:'slow'
        },'linear');


//
//        $('.QuestBackProgress .speedgauge_outer .speedpin').css('-webkit-transform','rotate('+percent+'deg)');
//        $('.QuestBackProgress .speedgauge_outer .speedpin').css('-moz-transform','rotate('+percent+'deg)');
//        $('.QuestBackProgress .speedgauge_outer .speedpin').css('-ms-transform','rotate('+percent+'deg)');
//        $('.QuestBackProgress .speedgauge_outer .speedpin').css('-o-transform','rotate('+percent+'deg)');
//        $('.QuestBackProgress .speedgauge_outer .speedpin').css('transform','rotate('+percent+'deg)');
    }
}

app.QuestBackProgress.init();