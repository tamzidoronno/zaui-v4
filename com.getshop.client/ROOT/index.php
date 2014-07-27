<?php
ob_start();

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
$importApplication = new ImportApplication(null, null);
$importApplication->showMenu();


if (isset($_GET['logonwithkey'])) {
    $key = $_GET['logonwithkey'];
    $factory = IocContainer::getFactorySingelton(false);
    $_SESSION['loggedin'] = serialize($factory->getApi()->getUserManager()->logonUsingKey($key));
    $factory->initPage();
    header('location:index.php');
    die();
}

$factory = IocContainer::getFactorySingelton();

if (@$factory->getApplicationPool()->getSelectedThemeApp()->applicationSettings->isResponsive) {
    echo '<meta name="viewport" content="initial-scale=1.0,width=device-width,user-scalable=no;">';
    echo '<link rel="stylesheet" type="text/css" href="skin/default/responsive.css" />';
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
    echo "<span class='language_selection'>";
    foreach($languages as $val) {
        echo "<a href='/?setLanguage=$val'><img src='skin/default/images/languages/$val.png'></a>";
    }
    echo "</span>";
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
        $pageDescription = $factory->getPage()->description;
        if (isset($factory->getSettings()->{'favicon'})) {
            echo '<link rel="shortcut icon" href="favicon.ico" type="image/png">';
            echo '<link rel="shortcut icon" type="image/png" href="favicon.ico" />';
        }
        echo "<meta name=\"description\" content=\"$pageDescription\">";

        $google = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_0cf21aa0_5a46_41c0_b5a6_fd52fb90216f");
        if ($google) {
            /* @var $google \ns_0cf21aa0_5a46_41c0_b5a6_fd52fb90216f\GoogleAnalytics */
            $google[0]->render();
        }

        $google = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_0cf21aa0_5a46_41c0_b5a6_fd52fb90216f");
        if ($google) {
            /* @var $google \ns_0cf21aa0_5a46_41c0_b5a6_fd52fb90216f\GoogleAnalytics */
            $google[0]->render();
        }

        $factory->loadJavascriptFiles();
        $factory->showCssFiles();
        echo $factory->loadInitializationData();

        if ($factory->getPage()->skeletonType == 5)
            echo "<style>body { overflow: auto; } </style>";
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

        <title><?php echo $title; ?></title>
    </head>
    <body>
        <? if (@$factory->getStore()->isDeepFreezed) { ?>
            <div class='deepfreezedActivated'><? echo $factory->__f("Warning! this store will automatically be reset to original state each hour") ?></div>
        <? } ?>
        <input name="storeid" type="hidden"  value="<?php echo $factory->getStore()->id; ?>"/>
        <input name="userid" type="hidden"  value="<?php echo \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id; ?>"/>

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
        <?php echo $html; ?>

        <? if ($factory->isEditorMode()) { ?>
            <div class="designselectionbox">
                <? $factory->includefile("themeselection"); ?>
            </div>

        <? } ?>

    </body>


    <?php
    $analytics = $factory->getApplicationPool()->getApplicationInstance("ba885f72-f571-4a2e-8770-e91cbb16b4ad");
    if ($analytics) {
        echo $analytics->render();
    }
    ?>

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
$factory->getApi()->getInvoiceManager()->createInvoice("4d98cbe0-f614-4ed3-9b46-846acad65cbb");
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
<? if (isset($_GET['logout'])) { ?>
    <script>window.location.reload()</script>
<? } ?>    