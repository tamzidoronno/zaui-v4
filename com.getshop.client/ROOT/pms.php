<?
include '../loader.php';
$pageFactory = new \PageFactory("pms");

// PmsPaymentProcess - Needs to be active on all pages.
$pageFactory->addExtraApplicationsNoneInstance('af54ced1-4e2d-444f-b733-897c1542b5a8');    
// OnlinePaymethod Application, a subapplication for all payments apps
$pageFactory->addExtraApplicationsNoneInstance('d96f955a-0c21-4b1c-97dc-295008ae6e5a');    
$pageFactory->addExtraApplicationsNoneInstance('486009b1-3748-4ab6-aa1b-95a4d5e2d228');    

$factory = IocContainer::getFactorySingelton();
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";


$appbase = new ApplicationBase();
$name = $appbase->getSelectedMultilevelDomainName();
$config = $factory->getApi()->getPmsManager()->getConfiguration($name);

$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
    header('location:/login.php?redirectto=/pms.php');
    exit(0);
}
if(isset($_GET['page']) && $_GET['page'] == "groupbooking" && isset($_GET['bookingId'])) {
    $_SESSION['PmsSearchBooking_bookingId'] = $_GET['bookingId'];
} else {
    unset($_SESSION['PmsSearchBooking_bookingId']);
}
if(isset($_GET['page']) && $_GET['page'] == "a90a9031-b67d-4d98-b034-f8c201a8f496" && isset($_GET['loadBooking'])) {
    $_SESSION['PmsSearchBooking_loadBooking'] = $_GET['loadBooking'];
}
$user = $factory->getApi()->getUserManager()->getLoggedOnUser();

if(!$factory->getApi()->getPageManager()->hasAccessToModule("pms")) {
    echo "Access denied";
    return;
}

$_SESSION['firstloadpage'] = true;

if(sizeof($user->pmsPageAccess) > 0) {
    if(in_array("a90a9031-b67d-4d98-b034-f8c201a8f496", $user->pmsPageAccess)) {
        $user->pmsPageAccess[] = "groupbooking";
    }
    if(in_array("4d89b5cf-5a00-46ea-9dcf-46ea0cde32e8", $user->pmsPageAccess)) {
        $user->pmsPageAccess[] = "048e2e10-1be3-4d77-a235-4b47e3ebfaab";
    }
    
    if(!in_array($page->getId(), $user->pmsPageAccess)) {
        header('location:/pms.php?page='.$user->pmsPageAccess[0]);
    }
}

$title = $page->getTitle();
if($config->bookingTag) {
    $title = $config->bookingTag . " - " . $title;
}

?>
<!DOCTYPE html>
<html pageid="<? echo $page->getId(); ?>" module="pms">
    <head>
        <meta name="viewport" content="width=1024">

        <script>isFirstLoading = true;</script>
        <title><? echo $title; ?></title>
        <link rel="stylesheet" href="/icomoon/style.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshop.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/pms.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshopmodules.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css?<? echo calculateCacheName(); ?>">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css?<? echo calculateCacheName(); ?>">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/moments.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/getshop/getshop.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/getshop.pms.js?<? echo calculateCacheName(); ?>"></script>
        <script src="js/ckeditor/ckeditor.js?<? echo calculateCacheName(); ?>"></script>
        <? $page->loadAppsJavascripts(); ?>
    </head>
    
    <body>
    <?php

    if($config->bookingTag) { echo "<div style='height: 18px;'></div>"; }

//     echo "<div style='text-align:center;padding: 5px; background-color:red; color:#fff; font-size: 20px;       '><i class='fa fa-warning'></i> Instructions on how to change the tax from 12% to 6% for a temporary periode can be found ";
//     echo "<a href='https://www.getshop.com/docs/Korrigere-mva-corona.pdf' target='_criticalinfo' style='color:yellow; font-weight:bold;'>here</a>, for questions - please create a ticket.</div>";

    if($config->bookingTag) {
        echo "<div style='text-align:center; text-transform:uppercase; background-color:yellow; position:fixed; top: 0px; width:100%;z-index:1;'>";
        echo $config->bookingTag;
        
        $loggedOnUser = $factory->getApi()->getUserManager()->getLoggedOnUser();
        echo " logged on as " . $loggedOnUser->fullName;
        
        echo "</div>";
    }
    ?>
        <div id="loaderbox" style="display: none; position: absolute; z-index: 9999999999999999;"><img src="skin/default/images/loader.gif"/></div>
        <script>
            $(document).bind('mousemove', function(e) {
                $('#loaderbox').css({
                    left: e.pageX + 15,
                    top: e.pageY + 15
                });
            });
        </script>
        
        <?
        include 'overlays.php';
        ?>
        
        <div area="header" class="gsarea">
            <?
            $storeObject = $factory->getStore();
            if ($config->conferenceSystemActive) {
                $storeObject->pmsConferenceActivated = true;
            }
            $menu = $page->getMenu("pms");
            $menu->renderTop($user, false, $storeObject);
            ?>
        </div>
        
        <div class="gs_body" hasleftmenu="<? echo $page->getLeftMenu() ? 'yes' : 'no'; ?>">
            <?
            if ($page->getLeftMenu()) { $page->getLeftMenu()->renderLeft(); }
            ?>

            <div class="gs_body_inner">
                <? $page->renderPage(); ?>
            </div>

        </div>
    </body>
    
    <script>
        $(document).ready(function() {
            getshop.documentLoaded();
        });
    </script>
    
    <?
    $page->renderBottom();
    ?>
</html>

