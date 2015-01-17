<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->loadJavascriptFiles();
?>
<style>
    .menu { padding: 3px; border-radius: 3px; cursor:pointer; border: solid 1px #3a3a3a; background-color:#666666; }
    .menu_selected { background-color:#EFEFEF; color:#000; }
    .textbox { display: none; }
    .gs_stylebox {display: block; }
    #global_css, #global_color_css_editor  { border: solid 1px #bbb; min-height: 350px; }
    #global_css, #global_color_css,.colorstyles { display:none;  }
    #global_css { display:block; }
    #page_description { width:100%; position: absolute; top: 40px; right: 0; bottom: 0; left: 0; display:none; }
    .gs_button { border: solid 1px; font-size: 12px; background-color:#000; padding: 3px; cursor:pointer; padding-left: 5px; padding-right: 5px; position:absolute; right: 10px; top: 7px; }
</style>

<title>Style / page settings</title>
<link rel="stylesheet" href="http://yandex.st/highlightjs/8.0/styles/default.min.css">
<script src="http://yandex.st/highlightjs/8.0/highlight.min.js"></script>

<body style="margin:0px;padding-bottom: 60px; background-color:#efefef;">
    
    <?
    $config = $factory->getStoreConfiguration();
    echo "<span class='colorstyles'>";
    foreach($config->colorTemplates as $index => $value) {
        echo "<textarea name='$index'>$value</textarea>";
    }
    echo "</span>";
    ?>
    
    <div style=" padding-top: 10px; padding-bottom: 10px; color:#FFF; width: 400px; padding-left: 10px; ">
        <span target="global_css" class="menu menu_selected"><? echo $factory->__f("Global css"); ?></span>
        <span target="global_color_css" class="menu"><? echo $factory->__f("Colors css"); ?></span>
        <span target="page_description" class="menu"><? echo $factory->__f("Page description"); ?></span>
        <span class="gs_button save_css_settings">Save</span>
    </div>
    <div id="global_css"><? echo $factory->getStore()->configuration->customCss; ?></div>
    <div id="global_color_css">
        <div style='padding-bottom: 10px;'>
            <select id='gscolortemplateselection'>
                <option value=''>Select a color template</option>
                <?
                $config = $factory->getStoreConfiguration();
                foreach($config->colorTemplates as $index => $value) {
                    echo "<option value='$index'>$index</option>";
                }
                ?>
                <option value='new_colortemplate'>Create a new color template</option>
            </select>
        </div>
        <div id="global_color_css_editor"></div>
    </div>
    <textarea id="page_description"><? echo $factory->getPage()->javapage->description; ?></textarea>    
</body>

<textarea contenteditable="true" id="styles" class="textbox" style="width:100%; height:100%;"><? echo $factory->getStore()->configuration->customCss; ?></textarea>
<textarea contenteditable="true" id="styles_colors" class="textbox" style="width:100%; height:100%;"><? echo $factory->getPage()->backendPage->customCss; ?></textarea>
<input type="hidden" pageid="<? echo $factory->getPage()->backendPage->id; ?>" id="current_pageid">


<script src="/js/ace/src-min-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>

<script>
    var current_page_set = "<? echo $factory->getPage()->javapage->id; ?>";
    var has_css_updated = false;
    
    $('#gscolortemplateselection').on('change', function() {
       var val = $(this).val();
       if(val === "new_colortemplate") {
           var name = prompt("Please enter the name of the template", "");
           if(name) {
               $(this).append("<option value='"+name + "'>" + name + "</option>");
               $('.colorstyles').append('<textarea name="'+name+'"></textarea>');
               $(this).val(name);
           }
       } else if(val) {
            var css = $('.colorstyles textarea[name="'+val+'"]').val();
            global_css_colors.setValue(css);
            $('#styles_colors').val(css);
       }
    });
    
    function saveCss(callback) {
        
        var colorstyles = {};
        $('.colorstyles textarea').each(function() {
            colorstyles[$(this).attr('name')] = $(this).val();
        });
        
        var data = {
            "global_css": global_css.getSession().getValue(),
            "selected_color_template" : $('#gscolortemplateselection').val(),
            "color_templates": colorstyles,
            "page_text": $('#page_description').val()
        }

        var event = thundashop.Ajax.createEvent('', 'savePageDetails', $(this), data);
        thundashop.Ajax.postWithCallBack(event, callback);
    }

    $(function() {
        $('.menu').on('click', function() {
            $('.menu').removeClass('menu_selected');
            $(this).addClass('menu_selected');
            $('#global_css, #page_global_css,#global_color_css, #page_description').hide();
            $("#" + $(this).attr('target')).show();
        });
        $('.save_css_settings').on('click', function() {
            saveCss(function() {
                alert('Data has been saved');
            });
        });
        var hash = window.location.hash;
        if (hash) {
            hash = hash.substr(1, hash.length);
            $('.menu[target="' + hash + '"]').click();
        }
        
        $('#gscolortemplateselection').val('<? echo $config->selectedColorTemplate; ?>');
        $('#gscolortemplateselection').change();
        
    });
    
    var global_css = ace.edit("global_css");
    global_css.setTheme("ace/theme/github");
    global_css.getSession().setMode("ace/mode/css");
    global_css.on("change", function(event) {
        has_css_updated = true;
        $('#styles').val(global_css.getSession().getValue());
    });
    
    var global_css_colors = ace.edit("global_color_css_editor");
    global_css_colors.setTheme("ace/theme/github");
    global_css_colors.getSession().setMode("ace/mode/css");
    global_css_colors.on("change", function(event) {
        has_css_updated = true;
        var selectedColorTemplate = $('#gscolortemplateselection').val();
        var css = global_css_colors.getSession().getValue();
        $('#styles_colors').val(css);
        $('.colorstyles textarea[name="'+selectedColorTemplate+'"]').val(css);
    });
    
    
    
</script>