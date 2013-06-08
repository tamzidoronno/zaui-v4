<?php
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

curl_init();

function init($factory) {
    ob_start();
    $factory->run();
    $html = ob_get_contents();
    ob_end_clean();

    return $html;
}

include '../loader.php';
?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<div class="mask" id="fullscreenmask"></div>
<div class="upload_information_panel" ></div>
<div class="informationbox-outer">
    <div id="informationbox-holder">
        <div id="infomrationboxclosebutton"></div>
        <div id="informationboxtop"></div>
        <div id="informationboxmiddle">
            <div id="informationboxtitle"></div>
            <div id="informationbox" class="informationbox"></div>
        </div>
        <div id="informationboxbottom"></div>
    </div>
</div>
<?
$importApplication = new ImportApplication(null, null);
$importApplication->showMenu();

$factory = IocContainer::getFactorySingelton();

if(!isset($_SESSION['checkifloggedout']) || !$_SESSION['checkifloggedout']) {
    $result = $factory->getApi()->getUserManager()->getLoggedOnUser();
    if ($result && !ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
        $factory->getApi()->getUserManager()->logout();
    }
    $_SESSION['checkifloggedout'] = true;
}

if(isset($_GET['logonwithkey'])) {
    $key = $_GET['logonwithkey'];
    $_SESSION['loggedin'] = serialize($factory->getApi()->getUserManager()->logonUsingKey($key));
    header('location:index.php');
}
?>

<html xmlns:fb="http://ogp.me/ns/fb#">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <?php
        $html = init($factory);

        $factory->loadJavascriptFiles();
        include "js/getshop.translation.php";
        $factory->showCssFiles();
        if ($factory->getPage()->skeletonType == 5)
            echo "<style>body { overflow: auto; } </style>";
        $factory->loadJavascriptFilesEditorMode();
        $settings = $factory->getSettings();
        $title = isset($settings->title) ? $settings->title->value : "GetShop";
       
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
        <input name="storeid" type="hidden"  value="<?php echo $factory->getStore()->id; ?>"/>
        <input name="userid" type="hidden"  value="<?php echo \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id; ?>"/>

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
        $(document).bind('mousemove', function(e){
            $('#loaderbox').css({
            left:  e.pageX+15,
            top:   e.pageY+15
            });
        });
        </script>
        
        <div id="errorbox"></div>
        <?php echo $html; ?>
    </body>

    
    <?php
    $analytics = $factory->getApplicationPool()->getApplicationInstance("ba885f72-f571-4a2e-8770-e91cbb16b4ad");
    if ($analytics) {
        echo $analytics->render();
    }
    ?>

</html>
<div id="getshoppower" style='position:fixed; text-align:right;  bottom:0px; right:0px; display:block;background-color:#FFFFFF; padding: 1px; margin-top: 3px;'>
    &nbsp;&nbsp;&nbsp;<a href='http://www.getshop.com'>Powered by GetShop</a>&nbsp;&nbsp;&nbsp;
</div>

<?
if (count($factory->getApi()->transport->errors) > 0) {
    echo "<script>";
    $error = $factory->getErrorsHtml();
    $error = str_replace("'", "\\'", $error);
    $error = str_replace("\n", "\\n", $error);
    echo "thundashop.Ajax.showErrorMessage('$error')";
    echo "</script>";
}

if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator() && !$factory->hasSelectedDesign()) {
    echo "<script>thundashop.common.lockMask(); $('#mainmenu #displaytheemesbutton').click();</script>";
}
?>

<? if (isset($_GET["onlyShowApp"])) { ?>
<script>
    $('.applicationarea').each(function() { 
       if($(this).find('.app').size() === 0) {
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
    var app = $('[appid=<?echo $_GET['onlyShowApp'];?>]');
    $('skeleton').removeAttr("editormode");
    $('html').width(app.width());
    $('#getshoppower').hide();
//    console.log(app.width());
//    console.log(app.height());
    
</script>
<? } ?>