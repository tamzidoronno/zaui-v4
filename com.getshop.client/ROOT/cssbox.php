<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->loadJavascriptFiles();
?>
<style>
    .menu { padding: 3px; border-radius: 3px; cursor:pointer; border: solid 1px #3a3a3a; marign-left: 10px; background-color:#666666; }
    .menu_selected { background-color:#EFEFEF; color:#000; }
    .textbox { display: none; }
    .gs_stylebox {display: block; }
    #global_css { position: absolute; top: 40px; right: 0; bottom: 0; left: 0; border-top: solid 1px #000; }
    #page_css { position: absolute; top: 40px; right: 0; bottom: 0; left: 0; display:none; width:100%; }
    #page_description { position: absolute; top: 40px; right: 0; bottom: 0; left: 0; display:none; }
    .gs_button { border: solid 1px; font-size: 12px; background-color:#000; padding: 3px; cursor:pointer; padding-left: 5px; padding-right: 5px; position:absolute; right: 10px; top: 7px; }
</style>
<script>
    $(function() {
        $('.menu').on('click', function() {
            $('.menu').removeClass('menu_selected');
            $(this).addClass('menu_selected');
            $('#global_css, #page_css, #page_description').hide();
            $("#" + $(this).attr('target')).show();
        });
        $('.save_css_settings').on('click', function() {
            var data = {
                "page_css" : page_css.getSession().getValue(),
                "global_css" : global_css.getSession().getValue(),
                "page_text" : $('#page_description').val()
            }
            
            var event = thundashop.Ajax.createEvent('','savePageDetails',null,data);
            thundashop.Ajax.postWithCallBack(event, function() {
                alert('Data has been saved');
            });
        });
    });
</script>
<title>Style / page settings</title>
<link rel="stylesheet" href="http://yandex.st/highlightjs/8.0/styles/default.min.css">
<script src="http://yandex.st/highlightjs/8.0/highlight.min.js"></script>

<body style="margin:0px;padding-bottom: 60px; background-color:#3f3f3f;">
    <div style=" padding-top: 10px; color:#FFF; width: 400px; padding-left: 10px; ">
        <span target="global_css" class="menu menu_selected"><? echo $factory->__f("Global css"); ?></span>
        <span target="page_css" class="menu"><? echo $factory->__f("Css for current page"); ?></span>
        <span target="page_description" class="menu"><? echo $factory->__f("Page description"); ?></span>
        <span class="gs_button save_css_settings">Save</span>
    </div>
    <div id="global_css"><? echo $factory->getStore()->configuration->customCss; ?></div>
    <div id="page_css"><? echo $factory->getPage()->backendPage->customCss; ?></div>
    <textarea id="page_description"><? echo $factory->getPage()->description; ?></textarea>    
</body>

    <textarea contenteditable="true" id="styles" class="textbox" style="width:100%; height:100%;"><? echo $factory->getStore()->configuration->customCss; ?></textarea>
    <textarea contenteditable="true" id="styles_page" class="textbox" style="width:100%; height:100%;"><? echo $factory->getPage()->backendPage->customCss; ?></textarea>



<script src="/js/ace/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
<script>
    var global_css = ace.edit("global_css");
    global_css.setTheme("ace/theme/github");
    global_css.getSession().setMode("ace/mode/css");
    global_css.on("change", function(event) {
        $('#styles').val(global_css.getSession().getValue());
    });
    var page_css = ace.edit("page_css");
    page_css.setTheme("ace/theme/github");
    page_css.getSession().setMode("ace/mode/css");
    page_css.on("change", function(event) {
        $('#styles_page').val(page_css.getSession().getValue());
    });
    
    var page_css = ace.edit("page_css");
    page_css.setTheme("ace/theme/github");
    page_css.getSession().setMode("ace/mode/css");
    
</script>