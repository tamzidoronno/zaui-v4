<?php
chdir("../");
include '../loader.php';

if(isset($_GET['code']) && $_GET['code']) {
    $code = $_GET['code'];
    $type = $_GET['type'];
    $name = $_GET['name'];
    $factory = IocContainer::getFactorySingelton();
    echo $factory->getApi()->getDoorManager()->pmsDoorAction($name, $code, $type);
    return;
}

?>
<html>
    <head>
        <title>TODO supply a title</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="../js/jquery-1.9.0.js"></script>
        <script type='application/javascript' src='../js/fastclick.js'></script>
    </head>
    <input type='hidden' id="multiname" value="<?php echo $_GET['name']; ?>">
    <body>
        <div class="codefield">Enter Code</div>
        
        <container>
            <block id="key1">1</block>
            <block id="key2">2</block>
            <block id="key3">3</block>
            
            <block id="key4">4</block>
            <block id="key5">5</block>
            <block id="key6">6</block>
            
            <block id="key7">7</block>
            <block id="key8">8</block>
            <block id="key9">9</block>
            
            <block id="keyX">LOCK</block>
            <block id="key0">0</block>
            <block id="keyOk">UNLOCK</block>
        </container>
        
    </body>
</html>

<script>
$(function() {
    FastClick.attach(document.body);
})


/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var completeApp = {
    code: "",
    inprogess: {},
    
    init: function () {
        $(document).on('click', 'block', completeApp.numpadClicked);
        $(document).on('keydown', completeApp.keyUp);
//        $(document).on('tap', 'block', completeApp.numpadClicked)
    },
    keyUp: function(e) {
        var key = e.keyCode;
        if (key === 35 || key === 97) completeApp.animateKeyPress(1);
        if (key === 40 || key === 98) completeApp.animateKeyPress(2);
        if (key === 34 || key === 99) completeApp.animateKeyPress(3);
        if (key === 37 || key === 100) completeApp.animateKeyPress(4);
        if (key === 12 || key === 101) completeApp.animateKeyPress(5);
        if (key === 39 || key === 102) completeApp.animateKeyPress(6);
        if (key === 36 || key === 103) completeApp.animateKeyPress(7);
        if (key === 38 || key === 104) completeApp.animateKeyPress(8);
        if (key === 33 || key === 105) completeApp.animateKeyPress(9);
        if (key === 45 || key === 96 || key === 0) completeApp.animateKeyPress(0);
        if (key === 8 || key === 110) completeApp.animateKeyPress('X');
        if (key === 13) completeApp.animateKeyPress('Ok');
    },
    animateKeyPress: function(keyCode) {
        $('#key'+keyCode).click();  
        $('#key'+keyCode).addClass('active');
        setTimeout(function() {
            $('#key'+keyCode).removeClass('active');
        }, 100);
        
        
    },
    numpadClicked: function () {
        var html = $(this).text();
        console.log(html);
        if (html === "LOCK") {
            completeApp.checkCode("close");
            return;
        }
        if (html === "UNLOCK") {
            completeApp.checkCode("open");
            return;
        }
      completeApp.code = completeApp.code + html;
        completeApp.showCode();
    },
    
    checkCode: function (type) {
        $.get( "?code=" + completeApp.code + "&type=" + type + "&name=" + $('#multiname').val(), function(data) {
            if(data)
                $('.codefield').html(data);
        });
        completeApp.code = "";
        completeApp.showCode();
    },
    
    
    showCode: function () {
        var mask = "";
        for (var i = 0; i < completeApp.code.length; i++) {
            mask += "*";
        }
        $('.codefield').html(mask);
    },
    removeLastChar: function () {
        if (completeApp.code && completeApp.code.length > 0) {
            completeApp.code = completeApp.code.substring(0, (completeApp.code.length - 1));
            completeApp.showCode();
        }
    }
}

jsonCallback = function(res) {
    completeApp.inprogess["pin"+res.pin] = false;
    
    var allDone = true;
    for (var i in completeApp.inprogess) {
        if (completeApp.inprogess[i]) {
            allDone = false;
        }
    }
    
    if (res.txt === "SUCCESS") {
        $('.codefield').html(res.txt);
    }
    
    if (allDone) {
        if ($('.codefield').html() !== "SUCCESS") {
            $('.codefield').html("ACCESS DENIED");
        }
        
        setTimeout(function() { $('.codefield').html("Enter Code"); }, 5000);
    }
}

completeApp.init();


</script>

<style>
    body {
     -moz-user-select: none;
   -khtml-user-select: none;
   -webkit-user-select: none;

   /*
     Introduced in IE 10.
     See http://ie.microsoft.com/testdrive/HTML5/msUserSelect/
   */
   -ms-user-select: none;
   user-select: none;
}
container {
    width: 100%;
    display: inline-block;
text-align: center;
}

block {
    display: inline-block;
    box-sizing: border-box;
    -moz-box-sizing: border-box;
    width: 32%;
    height: 100px;
    background: red;
    text-align: center;
    font-size: 20px;
    line-height: 100px;
    border: solid 1px #DDD;
    cursor: pointer;

    /* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#ffffff+0,e5e5e5+100;White+3D */
    background: rgb(255,255,255); /* Old browsers */
    background: -moz-linear-gradient(top,  rgba(255,255,255,1) 0%, rgba(229,229,229,1) 100%); /* FF3.6-15 */
    background: -webkit-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(229,229,229,1) 100%); /* Chrome10-25,Safari5.1-6 */
    background: linear-gradient(to bottom,  rgba(255,255,255,1) 0%,rgba(229,229,229,1) 100%); /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */
    filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#ffffff', endColorstr='#e5e5e5',GradientType=0 ); /* IE6-9 */

}


block.active,
block:active {
        /* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#f6e6b4+0,ed9017+100;Yellow+3D+%231 */
    background: rgb(246,230,180); /* Old browsers */
    background: -moz-linear-gradient(top,  rgba(246,230,180,1) 0%, rgba(237,144,23,1) 100%); /* FF3.6-15 */
    background: -webkit-linear-gradient(top,  rgba(246,230,180,1) 0%,rgba(237,144,23,1) 100%); /* Chrome10-25,Safari5.1-6 */
    background: linear-gradient(to bottom,  rgba(246,230,180,1) 0%,rgba(237,144,23,1) 100%); /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */
    filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#f6e6b4', endColorstr='#ed9017',GradientType=0 ); /* IE6-9 */
}

.codefield {
    text-align: center;
    line-height: 50px;
    height: 50px;
    font-size: 30px;
}
</style>