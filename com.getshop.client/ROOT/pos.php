<?
include '../loader.php';
$pageFactory = new \PageFactory("salespoint");
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";
$printPageMenuInModulesMenu=true;
$versionnumber = "1.0";

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
        <link rel="stylesheet" href="/icomoon/style.css?version=<? echo $versionnumber; ?>">
        <link rel="stylesheet" href="/skin/default/getshop.css?version=<? echo $versionnumber; ?>">
        <link rel="stylesheet" href="/skin/default/getshopmodules.css?version=<? echo $versionnumber; ?>">
        <link rel="stylesheet" href="/skin/default/pos.css?version=1.0">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js"></script>
        <script type="text/javascript" src="js/moments.js"></script>
        <script type="text/javascript" src="js/getshop/getshop.js?version=<? echo $versionnumber; ?>"></script>
        <script src="js/ckeditor/ckeditor.js"></script>
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
        
        <div id="messagebox" class="ok">
            <div class="inner">
                <div class="title"></div>
                <div class="icon inline"></div>
                <div class="description inline"></div>
                <div class="okbutton"></div>
            </div>
        </div>
        
        <div class="gsoverlay1 <? echo $showingModal; ?>">
            
            <div class="gsoverlayinner">
                <div class='gs_loading_spinner'><i class='fa fa-spin'></i></div>
                <div class='content'>
                    <? if ($showingModal) {
                        $modalPage = $pageFactory->getPage($_SESSION['gs_currently_showing_modal']);
                        $modalPage->renderPage(); 
                    }
                    ?>
                </div>
            </div>
        </div>
        
        <div class="gsoverlay2">
            <div class="gsoverlayinner">
                <div class='gs_loading_spinner'><i class='fa fa-spin'></i></div>
                <div class='content'></div>
            </div>
        </div>
        
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
