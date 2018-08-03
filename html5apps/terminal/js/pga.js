/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var pga = {
    init: function() {
        $(document).on('focusin', '.gstextfield', pga.activateKeyBoard)
        $(document).on('focusout', '.gstextfield', pga.deactivateKeyBoard)
        
        $(document).ready(function() {
            $('#keyboard').jkeyboard({layout: "norwegian"});
        });
        
        pga.resetTimeout();
    },
    
    resetTimeout: function() {
        if ($('html').attr('page')) {
            var twoMinutes = 1000*60*3;
            if (pga.timeout) {
                clearTimeout(pga.timeout);
            }
            pga.timeout = setTimeout(pga.navigateToHome, twoMinutes);
        } else {
            if (pga.timeout) {
                clearTimeout(pga.timeout);
            }
        }
    },
    
    navigateToHome: function() {
        document.location = '/';
    },
    
    activateKeyBoard: function(e) {
        var paddingBottom = 0;
        var positionFromBottom = window.innerHeight - $(e.target).offset().top;
        
        if (positionFromBottom < 340) {
            paddingBottom = positionFromBottom;
        }
        
        if (paddingBottom > 0) {
            $('html').css('padding-bottom', paddingBottom + "px");
            window.scrollTo(0,document.body.scrollHeight);
        }
        
        pga.lastSelectedInput = $(this);
        $('.jkeyboard').addClass('active');
        $('.keyboard').addClass('active');
    },
    
    deactivateKeyBoard: function(e, b) {
        if (!$(e.relatedTarget).hasClass('keyboard')) {
            $('.jkeyboard').removeClass('active');
            $('.keyboard').removeClass('active');
            pga.lastSelectedInput = null;
            
            $('html').css('padding-bottom', "0px");

        }
    },
    
    post: function(data) {
        data.page = $('html').attr('page');
        pga.resetTimeout();
        $.ajax({
            type: "POST",
            url: "/data.php",
            data: data,
            dataType: "html",
            context: document.body,
            success: function(response) {
                $('.content').html(response);
            }
        });
    }
}

pga.init();