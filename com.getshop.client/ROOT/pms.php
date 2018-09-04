<?
include '../loader.php';
$pageFactory = new \PageFactory();
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";

$factory = IocContainer::getFactorySingelton();
if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
    header('location:/login.php?redirectto=/pms.php');
    exit(0);
}
if(isset($_GET['page']) && $_GET['page'] == "groupbooking" && isset($_GET['bookingId'])) {
    $_SESSION['PmsSearchBooking_bookingId'] = $_GET['bookingId'];
}

$_SESSION['firstloadpage'] = true;

?>
<html pageid="<? echo $page->getId(); ?>" module="<? echo \PageFactory::getGetShopModule(); ?>">
    <head>
        <title><? echo $page->getTitle(); ?></title>
        <link rel="stylesheet" href="/icomoon/style.css">
        <link rel="stylesheet" href="/skin/default/getshop.css">
        <link rel="stylesheet" href="/skin/default/pms.css">
        <link rel="stylesheet" href="/skin/default/gesthopmodules.css">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css">

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">
        
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js"></script>
        <script type="text/javascript" src="js/moments.js"></script>
        <script type="text/javascript" src="js/getshop/getshop.js"></script>
        <script type="text/javascript" src="js/getshop.pms.js"></script>
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
            $menu = $page->getMenu();
            $menu->renderTop();
            ?>
        </div>
        <div style='text-align:center;background-color:red; color:#fff;padding: 3px;'>
            PMS 2.0 is the same as PMS functionality whise.
            We have rewritten the core of the system to improve speed, thus PMS 2.0. 
            Doing such a large rewrite needs lots of testing and we will therefore run PMS and PMS2.0 in parallell for a while, meaning you can use both.
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
