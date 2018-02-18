<div class="keyboard">
    <div class="keyboardrow">
        <span class='button'>1</span>
        <span class='button'>2</span>
        <span class='button'>3</span>
        <span class='button'>4</span>
        <span class='button'>5</span>
        <span class='button'>6</span>
        <span class='button'>7</span>
        <span class='button'>8</span>
        <span class='button'>9</span>
        <span class='button'>0</span>
        <span class='button backspace'>BACK</span>
    </div>
    <div class="keyboardrow">
        <span class='button'>Q</span>
        <span class='button'>W</span>
        <span class='button'>E</span>
        <span class='button'>R</span>
        <span class='button'>T</span>
        <span class='button'>Y</span>
        <span class='button'>U</span>
        <span class='button'>I</span>
        <span class='button'>O</span>
        <span class='button'>P</span>
        <span class='button'>Å</span>
    </div>
    <div class="keyboardrow">
        <span class='button'>A</span>
        <span class='button'>S</span>
        <span class='button'>D</span>
        <span class='button'>F</span>
        <span class='button'>G</span>
        <span class='button'>H</span>
        <span class='button'>J</span>
        <span class='button'>K</span>
        <span class='button'>L</span>
        <span class='button'>Ø</span>
        <span class='button'>Æ</span>
    </div>
    <div class="keyboardrow">
        <span class='button shift'><i class="fa fa-arrow-up"></i>&nbsp;</span>
        <span class='button'>Z</span>
        <span class='button'>X</span>
        <span class='button'>C</span>
        <span class='button'>V</span>
        <span class='button space'> </span>
        <span class='button'>B</span>
        <span class='button'>N</span>
        <span class='button'>M</span>
        <span class='button'>@</span>
        <span class='button'>.</span>
    </div>
</div>

<style>
    .keyboard { position:fixed; bottom: 0px; width: 100%; background-color:#fff;text-align: center; height: 270px;  }
    .keyboard .button { border: solid 1px; font-size: 30px; line-height: 50px; width: 50px; height: 50px; display:inline-block; margin:5px;border-radius: 3px; cursor:pointer;}
    .keyboard .button.space { width: 200px; } 
    .keyboard .button.backspace { width: 100px; }
    .keyboard .button.shift { width: 70px; }
    .keyboard .activeshift { background-color:green; color:#fff; }
    body { margin-bottom: 270px; }

</style>
<script>
    getshop_lastfocused = null;
    $('.keyboardrow').on('mouseup', function() {
        getshop_lastfocused.focus();
    });
    $('.keyboardrow').on('mousedown', function() {
        getshop_lastfocused = $(':focus');
    });
    $('.button').on('mousedown', function() {
        var text = $(this).html();
        if($(this).hasClass('shift')) {
            if($(this).hasClass('activeshift')) {
                $(this).removeClass('activeshift');
            } else {
                $(this).addClass('activeshift');
            }
            getshop_toggleKeyBoardUpperCase();
        } else if($(this).hasClass('backspace')) {
            var focused = $(':focus');
            focused.val(focused.val().substring(0, focused.val().length-1));
        } else {
            var focused = $(':focus');
            focused.val(focused.val()+text);
            $('.button.shift').removeClass('activeshift')
            getshop_toggleKeyBoardUpperCase();
        }
    });
    
    getshop_toggleKeyBoardUpperCase();
    function getshop_toggleKeyBoardUpperCase() {
        $('.button').each(function() {
            if($(this).hasClass('shift')) {
                return;
            }
            if($('.button.shift').hasClass('activeshift')) {
                $(this).text($(this).text().toUpperCase());
            } else {
                $(this).text($(this).text().toLowerCase());
            }
        });
    }
</script>