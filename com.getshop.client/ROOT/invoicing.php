<?
include '../loader.php';
$pageFactory = new \PageFactory("invoicing");

// PmsPaymentProcess - Needs to be active on all pages.
$pageFactory->addExtraApplicationsNoneInstance('af54ced1-4e2d-444f-b733-897c1542b5a8');    

// OnlinePaymethod Application, a subapplication for all payments apps
$pageFactory->addExtraApplicationsNoneInstance('d96f955a-0c21-4b1c-97dc-295008ae6e5a');    
$pageFactory->addExtraApplicationsNoneInstance('486009b1-3748-4ab6-aa1b-95a4d5e2d228');    

// PMS VIEW
$pageFactory->addExtraApplicationsNoneInstance('f8cc5247-85bf-4504-b4f3-b39937bd9955');    
$pageFactory->addExtraApplicationsNoneInstance('b5e9370e-121f-414d-bda2-74df44010c3b');
$pageFactory->addExtraApplicationsNoneInstance('28886d7d-91d6-409a-a455-9351a426bed5');
$pageFactory->addExtraApplicationsNoneInstance('b72ec093-caa2-4bd8-9f32-e826e335894e');
$pageFactory->addExtraApplicationsNoneInstance('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
$pageFactory->addExtraApplicationsNoneInstance('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
$pageFactory->addExtraApplicationsNoneInstance('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
$pageFactory->addExtraApplicationsNoneInstance('bce90759-5488-442b-b46c-a6585f353cfe');

$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";

$factory = IocContainer::getFactorySingelton();
if(!$factory->getApi()->getUserManager()->isLoggedIn() || !ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
    header('location:/login.php?redirectto=/invoicing.php');
    exit(0);
}

// Add apps if not already added
$_SESSION['firstloadpage'] = true;

?>
<html pageid="<? echo $page->getId(); ?>" module="invoicing">
    <head>
        <title><? echo $page->getTitle(); ?></title>
        <link rel="stylesheet" href="/icomoon/style.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshop.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshopmodules.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/invoicing.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css?<? echo calculateCacheName(); ?>">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css?<? echo calculateCacheName(); ?>">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/moments.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/getshop/getshop.js?<? echo calculateCacheName(); ?>"></script>
        <script src="js/ckeditor/ckeditor.js?<? echo calculateCacheName(); ?>"></script>
        <? $page->loadAppsJavascripts(); ?>
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
            $menu = $page->getMenu("invoicing");
            $menu->renderTop(ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject());
            ?>
        </div>
        
        <div class="gs_body" hasleftmenu="<? echo $page->getLeftMenu() ? 'yes' : 'no'; ?>">
            <?
            if ($page->getLeftMenu()) { $page->getLeftMenu()->renderLeft("invoicing"); }
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

