<?
include '../loader.php';
session_start();
$pageFactory = new \PageFactory();
$page = $pageFactory->getPage($_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";
?>
<html pageid="<? echo $page->getId(); ?>" module="<? echo \PageFactory::getGetShopModule(); ?>">
    <head>
        <title><? echo $page->getTitle(); ?></title>
        <link rel="stylesheet" href="/icomoon/style.css">
        <link rel="stylesheet" href="/skin/default/getshop.css">
        <link rel="stylesheet" href="/skin/default/gesthopmodules.css">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js"></script>
        <script type="text/javascript" src="js/getshop/getshop.js"></script>
        <? $page->loadAppsJavascripts(); ?>
    </head>
    
    <body>
        <div class="gsoverlay <? echo $showingModal; ?>">
            <div class="gsoverlayinner">
                <? if ($showingModal) {
                    $modalPage = $pageFactory->getPage($_SESSION['gs_currently_showing_modal']);
                    $modalPage->renderPage(); 
                }
                ?>
            </div>
        </div>
        
        <div area="header" class="gsarea">
            <?
            $menu = $page->getMenu();
            $menu->renderTop();
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
</html>