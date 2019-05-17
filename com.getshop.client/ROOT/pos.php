<?
include '../loader.php';
$pageFactory = new \PageFactory("salespoint");

// PmsPaymentProcess - Needs to be active on all pages.
$pageFactory->addExtraApplicationsNoneInstance('af54ced1-4e2d-444f-b733-897c1542b5a8');    
// OnlinePaymethod Application, a subapplication for all payments apps
$pageFactory->addExtraApplicationsNoneInstance('d96f955a-0c21-4b1c-97dc-295008ae6e5a');    
$pageFactory->addExtraApplicationsNoneInstance('486009b1-3748-4ab6-aa1b-95a4d5e2d228');    

$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";
$printPageMenuInModulesMenu=true;

$factory = IocContainer::getFactorySingelton();
if(!$factory->getApi()->getUserManager()->isLoggedIn() || !ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
    header('location:/login.php?redirectto=/salespoint.php');
    exit(0);
}

// Add apps if not already added
$_SESSION['firstloadpage'] = true;

if(!$factory->getApi()->getPageManager()->hasAccessToModule("salespoint")) {
    echo "Access denied";
    return;
}

?>
<html pageid="<? echo $page->getId(); ?>" module="salespoint">
    <head>
        <title><? echo $page->getTitle(); ?></title>
        <link rel="stylesheet" href="/icomoon/style.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshop.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshopmodules.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/pos.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css?<? echo calculateCacheName(); ?>">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css?<? echo calculateCacheName(); ?>">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js?<? echo calculateCacheName(); ?>""></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js?<? echo calculateCacheName(); ?>""></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js?<? echo calculateCacheName(); ?>""></script>
        <script type="text/javascript" src="js/moments.js?<? echo calculateCacheName(); ?>""></script>
        <script type="text/javascript" src="js/getshop/getshop.js?<? echo $versionnumber; ?>"></script>
        <script src="js/ckeditor/ckeditor.js?<? echo calculateCacheName(); ?>""></script>
        <? $page->loadAppsJavascripts(); ?>
        
        <meta name="apple-mobile-web-app-capable" content="yes">
        <meta name="viewport" content="width=device-width" />
    </head>
    
    <body>
        
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
            $menu = $page->getMenu("salespoint");
            $menu->renderTop(ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject(), $printPageMenuInModulesMenu);
            ?>
        </div>
        
        <div class="gs_body" hasleftmenu="<? echo $page->getLeftMenu() ? 'yes' : 'no'; ?>">
            <?
            if ($page->getLeftMenu()) { $page->getLeftMenu()->renderLeft("pos", $printPageMenuInModulesMenu); }
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
