<?php
ob_start();

if(isset($_GET['PHPSESSID'])) {
    session_id($_GET['PHPSESSID']);
}

function getBrowser() {
    $u_agent = $_SERVER['HTTP_USER_AGENT'];
    $ub = '';
    if (preg_match('/MSIE/i', $u_agent)) {
        $ub = "Internet Explorer";
    } elseif (preg_match('/Firefox/i', $u_agent)) {
        $ub = "Mozilla Firefox";
    } elseif (preg_match('/Safari/i', $u_agent)) {
        $ub = "Apple Safari";
    } elseif (preg_match('/Chrome/i', $u_agent)) {
        $ub = "Google Chrome";
    } elseif (preg_match('/Flock/i', $u_agent)) {
        $ub = "Flock";
    } elseif (preg_match('/Opera/i', $u_agent)) {
        $ub = "Opera";
    } elseif (preg_match('/Netscape/i', $u_agent)) {
        $ub = "Netscape";
    }
    return $ub;
}

function ismobile() {
    $is_mobile = '0';

    if (preg_match('/(android|up.browser|up.link|mmp|symbian|smartphone|midp|wap|phone)/i', strtolower($_SERVER['HTTP_USER_AGENT']))) {
        $is_mobile = 1;
    }

    if ((strpos(strtolower($_SERVER['HTTP_ACCEPT']), 'application/vnd.wap.xhtml+xml') > 0) or ((isset($_SERVER['HTTP_X_WAP_PROFILE']) or isset($_SERVER['HTTP_PROFILE'])))) {
        $is_mobile = 1;
    }

    $mobile_ua = strtolower(substr($_SERVER['HTTP_USER_AGENT'], 0, 4));
    $mobile_agents = array('w3c ', 'acs-', 'alav', 'alca', 'amoi', 'andr', 'audi', 'avan', 'benq', 'bird', 'blac', 'blaz', 'brew', 'cell', 'cldc', 'cmd-', 'dang', 'doco', 'eric', 'hipt', 'inno', 'ipaq', 'java', 'jigs', 'kddi', 'keji', 'leno', 'lg-c', 'lg-d', 'lg-g', 'lge-', 'maui', 'maxo', 'midp', 'mits', 'mmef', 'mobi', 'mot-', 'moto', 'mwbp', 'nec-', 'newt', 'noki', 'oper', 'palm', 'pana', 'pant', 'phil', 'play', 'port', 'prox', 'qwap', 'sage', 'sams', 'sany', 'sch-', 'sec-', 'send', 'seri', 'sgh-', 'shar', 'sie-', 'siem', 'smal', 'smar', 'sony', 'sph-', 'symb', 't-mo', 'teli', 'tim-', 'tosh', 'tsm-', 'upg1', 'upsi', 'vk-v', 'voda', 'wap-', 'wapa', 'wapi', 'wapp', 'wapr', 'webc', 'winw', 'winw', 'xda', 'xda-');

    if (in_array($mobile_ua, $mobile_agents)) {
        $is_mobile = 1;
    }

    if (isset($_SERVER['ALL_HTTP'])) {
        if (strpos(strtolower($_SERVER['ALL_HTTP']), 'OperaMini') > 0) {
            $is_mobile = 1;
        }
    }

    if (strpos(strtolower($_SERVER['HTTP_USER_AGENT']), 'windows') > 0) {
        $is_mobile = 0;
    }

    return $is_mobile;
}

curl_init();

function init($factory) {
    ob_start();
    $factory->run();
    $html = ob_get_contents();
    ob_end_clean();

    return $html;
}

include '../loader.php';

if(isset($_GET['setLanguage'])) {
    $_SESSION['language_selected'] = $_GET['setLanguage'];
}

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<div class="mask" id="fullscreenmask"></div>
<div class="upload_information_panel" ></div>
<div class="informationbox-outer">
    <div id="informationbox-holder">
        <div class='fa fa-times-circle' id="infomrationboxclosebutton"></div>
        <div id="informationboxmiddle">
            <div id="informationboxtitle"></div>
            <div id="informationbox" class="informationbox"></div>
        </div>
    </div>
</div>


<?

if (isset($_GET['logonwithkey'])) {
    $key = $_GET['logonwithkey'];
    $factory = IocContainer::getFactorySingelton(false);
    $_SESSION['loggedin'] = serialize($factory->getApi()->getUserManager()->logonUsingKey($key));
    $factory->initPage();
    header('location:index.php');
    die();
}


$factory = IocContainer::getFactorySingelton();

if (@$factory->isMobile()) {
    echo '<meta name="viewport" content="width=device-width, minimal-ui, initial-scale=1.0, maximum-scale=1.0, user-scalable=no", target-densitydpi="device-dpi" />';
    echo '<link rel="stylesheet" type="text/css" href="skin/default/responsive.css" />';
}

if ($factory->isEditorMode()) {
    echo '<script src="/js/ace/src-min-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>';
    echo '<link rel="stylesheet" type="text/css" href="skin/default/settings.css" />';
}

if (!isset($_SESSION['checkifloggedout']) || !$_SESSION['checkifloggedout']) {
    $result = $factory->getApi()->getUserManager()->getLoggedOnUser();
    if ($result && !ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
        $factory->getApi()->getUserManager()->logout();
    }
    $_SESSION['checkifloggedout'] = true;
}
if(isset($factory->getSettings()->languages)) {
    $languages = json_decode($factory->getSettings()->languages->value);
    if (is_array($languages)) {
        echo "<span class='language_selection'>";
        foreach($languages as $val) {
            echo "<a href='/?setLanguage=$val'><img src='skin/default/images/languages/$val.png'></a>";
        }
        echo "</span>";
    }
}

?>

<html xmlns:fb="http://ogp.me/ns/fb#">
    <head>
        
        <script>

            if (typeof(console) == "undefined") {
                console = {};
            }

            if (typeof(console.log) == "undefined") {
                console.log = function(text) {
                };
            }
            translationMatrix = <? echo $factory->getJsonTranslationMatrix(); ?>
        </script>
        <!--[if gte IE 8]>
            <link rel="stylesheet" type="text/css" href="ie8plus.css" />
        <![endif]-->

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <?php
        $html = init($factory);
        $pageDescription = $factory->getPage()->javapage->description;
        if (isset($factory->getSettings()->{'favicon'})) {
            echo '<link rel="shortcut icon" href="favicon.ico" type="image/png">';
            echo '<link rel="shortcut icon" type="image/png" href="favicon.ico" />';
        }
        echo "<meta name=\"description\" content=\"$pageDescription\">";

        $factory->loadJavascriptFiles();
        $factory->showCssFiles();

        $factory->loadJavascriptFilesEditorMode();
        $settings = $factory->getSettings();
        $title = isset($settings->title) ? $settings->title->value : "";

        echo "<script>";
        if (isset($settings->fadein) && $settings->fadein == "true") {
            echo "hasFadeInEffect = true";
        } else {
            echo "hasFadeInEffect = false";
        }
        echo "</script>";
        ?>

        <script type="text/javascript" src="https://www.google.com/jsapi"></script>

        <title><?php echo $title; ?></title>
    <script>CKEDITOR.dtd.$removeEmpty['span'] = false;</script>
    </head>
    <body editormode="<? echo $factory->isEditorMode() ? "true" : "false"?>">
        <?
            $factory->printTemplateFunctions();
        ?>
        
        <? if ($factory->isEditorMode() && !$factory->isMobile()) {
            echo "<div class='gs_site_main_buttons_view'>";
                echo "<div title='".$factory->__f("Open settings")."' class='gs_site_main_button store_settings_button'><i class='fa fa-gears'></i></div>";
                echo "<div title='".$factory->__f("Preview mobile and settings")."' class='gs_site_main_button store_mobile_view_button '><i class='fa fa-mobile'></i></div>";
                echo "<div title='".$factory->__f("Global CSS editing")."' class='gs_site_main_button store_design_button'><i class='fa fa-image'></i></div>";
                echo "<div title='".$factory->__f("Toggle advanced mode")."' class='gs_site_main_button gs_toggle_advanced_mode'><i class='fa fa-rocket'></i></div>";
                echo "<a href='/logout.php'><div title='".$factory->__f("Logout")."' class='gs_site_main_button'><i class='fa fa-lock'></i></div></a>";
            echo "</div>";
            
            include_once("mobileeditor.phtml");
            echo '<link rel="stylesheet" type="text/css" href="/skin/default/mobileeditor.css" />';
        }
        
        ?>
        <? if (@$factory->getStore()->isDeepFreezed) { ?>
            <div class='deepfreezedActivated'><? echo $factory->__f("Warning! this store will automatically be reset to original state each hour") ?></div>
        <? } ?>
            
        <? if (@$factory->getApi()->getUserManager()->isImpersonating()) { ?>
            <div class='impersonatingActivated'>
                <? 
                echo $factory->__f("You are currently impersonating");
                echo "&nbsp;";
                echo $factory->getApi()->getUserManager()->getLoggedOnUser()->fullName; 
                echo "<a href='/impersonate.php?action=cancel'> ( cancel ) </a>";
                ?>
            </div>
        <? } ?>
            <input name="pageid" type="hidden" id="gspageid" value="<?php echo $factory->getPage()->javapage->id; ?>"/>
        <input name="storeid" type="hidden"  value="<?php echo $factory->getStore()->id; ?>"/>
        <input name="userid" type="hidden"  value="<?php echo  \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null ? \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id : ""; ?>"/>

        <?
        if ($factory->isEditorMode()) { ?>
            <div id='backsidesettings'>
                <? include('../template/default/Common/settings.phtml'); ?>
            </div>
        <? } ?>
        

        <div id="mainmenutoolbox"></div>
        <div id="messagebox" class="ok">
            <div class="inner">
                <div class="title"></div>
                <div class="icon inline"></div>
                <div class="description inline"></div>
                <div class="okbutton"></div>
            </div>
        </div>

        <div id="loaderbox" style="display: none; position: absolute; z-index: 9999999999999999;"><img src="skin/default/images/loader.gif"/></div>
        <script>
            $(document).bind('mousemove', function(e) {
                $('#loaderbox').css({
                    left: e.pageX + 15,
                    top: e.pageY + 15
                });
            });
        </script>
        
        <div id="errorbox"></div>
        <div id="gsbody">
            <?php echo $html; ?>
        </div>
        
        <div class="gs_overlay_row_highlighter gs_overlay_row_highlighter_top"></div>
        <div class="gs_overlay_row_highlighter gs_overlay_row_highlighter_bottom"></div>
    </body>
</html>

<?
if (count($factory->getApi()->transport->errors) > 0) {
    echo "<script>";
    $error = $factory->getErrorsHtml();
    $error = str_replace("'", "\\'", $error);
    $error = str_replace("\n", "\\n", $error);
    echo "thundashop.Ajax.showErrorMessage('$error')";
    echo "</script>";
}
?>

<? if (isset($_GET["onlyShowApp"])) { ?>
    <script>
        $('.applicationarea').each(function() {
            if ($(this).find('.app').size() === 0) {
                $(this).hide();
            }
        });

        $('.app').each(function() {
            if ($(this).html() == "") {
                $(this).hide();
            }
        });
        $('.breadcrumb').hide();
        $('.mainmenu').hide();
        $('.spacingtop').hide();
        var app = $('[appid=<? echo $_GET['onlyShowApp']; ?>]');
        $('skeleton').removeAttr("editormode");
        $('html').width(app.width());
        $('#getshoppower').hide();
    </script>
    <?
}

if (isset($_GET['page'])) {
    if (strstr($_GET['page'], "_standalone")) {
        echo '<script> 
            $(function() { 
                mainmenu.hide(); 
                $("#apparea-bottom").hide(); 
            }); 
        </script>';
    }
}
?>

<?
if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
    echo "<script>isAdministrator = true;</script>";
}
?>

<script>
    $(document).ready(function() {
        PubSub.publish('NAVIGATION_COMPLETED', {});
    });

    if (typeof(CKEDITOR) !== "undefined") {
        CKEDITOR.on('instanceCreated', function(event) {
            var editor = event.editor;
            editor.on('instanceReady', function(e) {
                $(e.editor.element.$).removeAttr("title");
            });
        });
        CKEDITOR.config.allowedContent = true;
    }
</script>
<div id="gs_customcss">
    <style>
        <? echo $factory->getStoreConfiguration()->customCss; ?>
    </style>
</div>
<div id="gs_color_css">
    <style>
        <?
        
        $color = $factory->getStoreConfiguration()->selectedColorTemplate;
        
        if(isset($_GET['colorselection'])) {
            $color = $_GET['colorselection'];
            $_SESSION['gscolorselection'] = $color;
        }
        
        if(isset($_SESSION['gscolorselection'])) {
            $color = $_SESSION['gscolorselection'];
        }
        
        if($color) {
            echo $factory->getStoreConfiguration()->colorTemplates->{$color};
        }
        ?>
    </style>
</div>
<? if (isset($_GET['logout'])) { ?>
    <script>window.location.reload()</script>
<? } ?>    
    
<?

if (isset($_GET['showlogin']) || $factory->isEditorMode()) {
    echo "<script>$('.Login .notloggedon').show();</script>";
}


if (isset($_SESSION['showadmin']) && $_SESSION['showadmin']) {
    echo "<script>getshop.Settings.showSettings(false);</script>";
}
?>    
<script>
    google.load('visualization', '1.0', {'packages':['corechart']});
</script>
