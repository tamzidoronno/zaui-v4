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

?><!DOCTYPE html>
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


?>

<html xmlns:fb="http://ogp.me/ns/fb#">
    <head>
        
        <?
        $themeName = $factory->getApplicationPool()->getSelectedThemeApp()->appName;
        if($themeName == "SedoxDatabankTheme" || $themeName == "ProMeisterTheme") {
            echo '<meta name="viewport" content="width=480, initial-scale=0.666">';
        }
        
        if($factory->includeSeo()) {
            include_once("loadcss.phtml");
        }
        
        if (@$factory->isMobile()) {
            $factory->addCssToBody("skin/default/responsiveignored.css");
            $factory->addCssToBody("skin/default/responsive.css");
            echo '<meta name="viewport" content="width=device-width, minimal-ui, initial-scale=1.0, maximum-scale=1.0, user-scalable=no", target-densitydpi="device-dpi" />';
            echo '<script src="//code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>';
            echo "<script>isMobile=true;</script>";
        } else if (@$factory->isMobileIgnoreDisabled()) {
            $factory->addCssToBody("skin/default/responsiveignored.css");
            echo '<meta name="viewport" content="width=device-width, minimal-ui, initial-scale=1.0, maximum-scale=1.0, user-scalable=no", target-densitydpi="device-dpi" />';
            echo '<script src="//code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>';
            echo "<script>isMobile=true;</script>";
        } else {
            echo "<script>isMobile=false;</script>";
        }

        ?>
        
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

        <?
        $javapage = $factory->getPage()->javapage;
        ?>
        
        <meta http-equiv="Cache-control" content="public">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        
        <meta name="description" content="<? echo $javapage->description; ?>">
        <meta name="keywords" content="<? echo $javapage->metaKeywords; ?>">
        <meta name="title" content="<? echo $javapage->metaTitle; ?>">
        
        <script <? echo $factory->includeSeo(); ?> type="text/javascript" src="https://www.google.com/jsapi"></script>
        <?php
        $html = init($factory);
        $pageDescription = $factory->getPage()->javapage->description;
        if (@$factory->getStore()->favicon) {
            $uuid = $factory->getStore()->favicon;
            if($factory->getApi()->getUUIDSecurityManager()->hasAccess($uuid, true, false)) {
                echo '<link rel="icon" href="/favicon.ico?r='.rand(0,1000).'" />';
                echo '<link href="data:image/x-icon;base64,'.  base64_encode(file_get_contents("../uploadedfiles/".$factory->getStore()->favicon)).'" rel="icon" type="image/x-icon" />';
            }
        }

        $factory->loadJavascriptFiles();
        $factory->showCssFiles();

        $factory->loadJavascriptFilesEditorMode();
        $factory->getPageTitle();
        
        echo "<script>";
        if (isset($settings->fadein) && $settings->fadein == "true") {
            echo "hasFadeInEffect = true";
        } else {
            echo "hasFadeInEffect = false";
        }
        echo "</script>";
        ?>


        <title class='pagetitle'></title>
    <script>
     $(function() {
        if (typeof(CKEDITOR) !== "undefined") {         
            CKEDITOR.dtd.$removeEmpty['span'] = false;
        }
     });
    </script>
    </head>
    <body editormode="<? echo $factory->isEditorMode() ? "true" : "false"?>">
        <?
        if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null && ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->showLoguotCounter) {
            echo "<div class='gs_logout_counter'></div>";
        }
        
        ?>
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
            include_once("js/photoswipe/photoswiperoot.html");
            $factory->printTemplateFunctions();
        ?>
        
        <? if ($factory->isEditorMode() && !$factory->isMobile() && !$factory->isAccessToBackedForEditorDisabled()) {
            echo "<div class='gs_site_main_buttons_view'>";
                echo "<div title='".$factory->__f("Open settings")."' class='gs_site_main_button store_settings_button'><i class='fa fa-gears'></i></div>";
                
                if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->canChangeLayout) {
                    echo "<div title='".$factory->__f("Preview mobile and settings")."' class='gs_site_main_button store_mobile_view_button '><i class='fa fa-mobile'></i></div>";
                    echo "<div title='".$factory->__f("Global CSS editing")."' class='gs_site_main_button store_design_button'><i class='fa fa-image'></i></div>";
                    echo "<div title='".$factory->__f("Toggle advanced mode")."' class='gs_site_main_button gs_toggle_advanced_mode'><i class='fa fa-rocket'></i></div>";
                    echo "<div title='".$factory->__f("Modify your layout? Drag this element to a cell you would like to modify.")."' class='gs_site_main_button gsaddrowcontentdnd'><i class='fa fa-plus-circle'></i></div>";
                    echo "<div title='".$factory->__f("Layout history")."' class='gs_site_main_button gslayouthistorybtn'><i class='fa fa-undo'></i></div>";

                    $selectedThemeApp = $factory->getApplicationPool()->getSelectedThemeAppInstance();
                    if ($selectedThemeApp != null && method_exists($selectedThemeApp, "isAllowingSideBar") && $selectedThemeApp->isAllowingSideBar()) {
                        echo "<div class='gs_site_main_button gs_toggle_sidebar'>";
                        echo "<i class='fa fa-columns'></i>";
                        echo "<div class='left_sidebars'>";
                            $names = $factory->getApi()->getPageManager()->getLeftSideBarNames();
                            foreach ($names as $name) {
                                echo "<div class='togle_named_sidebar' sidebarname='$name'>$name</div>";
                            }
                            echo "<div><input type='text'/></div>";
                        echo "</div>";
                        echo "</div>";
                    }
                }
                
                echo "<a href='/logout.php'><div title='".$factory->__f("Logout")."' class='gs_site_main_button'><i class='fa fa-lock'></i></div></a>";
                echo "<a href='http://download.teamviewer.com/download/version_10x/TeamViewerQS.exe'><div title='".$factory->__f("Teamviewer")."' class='gs_site_main_button'><i class='fa fa-support'></i></div></a>";
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
        
        <div id="dynamicmodal">
            <?
            if (isset($_SESSION['gs_currently_showing_modal'])) {
                $factory->getPage()->renderModal($_SESSION['gs_currently_showing_modal']);
            }
            ?>
        </div>
        

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
        <?
        if ($factory->isEditorMode()) {
            $factory->getPage()->printApplicationAddCellRow();
        }
        $disableMobileview = "gsdisabledmobileview";
        if(!$factory->getStoreConfiguration()->disableMobileMode) {
            $disableMobileview = "gsnotdisabledmobileview";
        }
        
        ?>
        <div id="gsbody" class="<? echo  isset($_SESSION['gs_currently_showing_modal']) ? "gs_modalIsOpen" : ""; echo " " . $disableMobileview; ?> ">
            <?php echo $html; ?>
        </div>
        
        <div class="gs_overlay_row_highlighter gs_overlay_row_highlighter_top"></div>
        <div class="gs_overlay_row_highlighter gs_overlay_row_highlighter_bottom"></div>
        
        <?
        $factory->renderBottom();
        $factory->displayCookieWarning();
        ?>
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
<? if (isset($_GET['logout'])) { 
    $factory->getApi()->getUserManager()->logout();
    session_destroy();
    ?>
    <script>window.location.href = "/"; </script>
<? } ?>    
    
<?

if (isset($_GET['showlogin']) || $factory->isEditorMode()) {
    echo "<script>$('.Login .notloggedon').show();</script>";
}

if(isset($_GET['avoidjavascriptnavigation']) || isset($_SESSION['avoidjavascriptnavigation'])) {
    $_SESSION['avoidjavascriptnavigation'] = true;
    echo "<script>avoidjavascriptnavigation = true;</script>";
}
if(isset($_GET['dojavascriptnavigation'])) {
    unset($_SESSION['dojavascriptnavigation']);
    echo "<script>avoidjavascriptnavigation = false;</script>";
}


if (isset($_SESSION['showadmin']) && $_SESSION['showadmin']) {
    echo "<script>getshop.Settings.showSettings(false);</script>";
}

?>    
<script>
    google.load('visualization', '1.0', {'packages':['corechart']});
    $(function() {
        FastClick.attach(document.body);
    });
</script>