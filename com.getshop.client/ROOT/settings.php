<?
include '../loader.php';
$pageFactory = new \PageFactory("settings");
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";

$factory = IocContainer::getFactorySingelton();
if(!$factory->getApi()->getUserManager()->isLoggedIn() || !ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
    setcookie('PHPSESSID','');
    header('location:/login.php?redirectto=/settings.php');
    exit(0);
}

// Add apps if not already added
$_SESSION['firstloadpage'] = true;

?>
<html pageid="<? echo $page->getId(); ?>" module="settings">
    <head>
        <title><? echo $page->getTitle(); ?></title>
        <? include 'modulescommon.php'; ?>
        
        <link rel="stylesheet" href="/skin/default/settings.css">
        
        <? $page->loadAppsCss(); ?>
        <script src="js/ckeditor/ckeditor.js"></script>
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
            $menu = $page->getMenu("settings");
            $menu->renderTop(ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject());
            ?>
        </div>
        
        <div class="gs_body" hasleftmenu="<? echo $page->getLeftMenu() ? 'yes' : 'no'; ?>">
            <?
            if ($page->getLeftMenu()) { $page->getLeftMenu()->renderLeft("settings"); }
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

