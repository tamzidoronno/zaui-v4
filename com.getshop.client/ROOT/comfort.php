<?
include '../loader.php';
$pageFactory = new \PageFactory("comfort");
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";

$factory = IocContainer::getFactorySingelton();

$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
    setcookie('PHPSESSID','');
    header('location:/login.php?redirectto=/comfort.php');
    exit(0);
}
$user = $factory->getApi()->getUserManager()->getLoggedOnUser();

if(!$factory->getApi()->getPageManager()->hasAccessToModule("comfort")) {
    echo "Access denied";
    return;
}

$_SESSION['firstloadpage'] = true;

?>
<html pageid="<? echo $page->getId(); ?>" module="comfort">
    <head>
        <title><? echo $page->getTitle(); ?></title>
        <link rel="stylesheet" href="/icomoon/style.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshop.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/comfort.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshopmodules.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css?<? echo calculateCacheName(); ?>">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/moments.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/getshop/getshop.js?<? echo calculateCacheName(); ?>"></script>
        <script type="text/javascript" src="js/getshop.comfort.js?<? echo calculateCacheName(); ?>"></script>
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
            $menu = $page->getMenu("comfort");
            $menu->renderTop($user);
            ?>
        </div>
        
        <div class="gs_body" hasleftmenu="<? echo $page->getLeftMenu() ? 'yes' : 'no'; ?>">
            <?
            if ($page->getLeftMenu()) { $page->getLeftMenu()->renderLeft("comfort"); }
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

