<?
include '../loader.php';
$pageFactory = new \PageFactory("intranet");
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";

$factory = IocContainer::getFactorySingelton();

$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
    header('location:/login.php?redirectto=/intranet.php');
    exit(0);
}
if(isset($_GET['page']) && $_GET['page'] == "groupbooking" && isset($_GET['bookingId'])) {
    $_SESSION['PmsSearchBooking_bookingId'] = $_GET['bookingId'];
}
if(isset($_GET['page']) && $_GET['page'] == "a90a9031-b67d-4d98-b034-f8c201a8f496" && isset($_GET['loadBooking'])) {
    $_SESSION['PmsSearchBooking_loadBooking'] = $_GET['loadBooking'];
}
$user = $factory->getApi()->getUserManager()->getLoggedOnUser();

$_SESSION['firstloadpage'] = true;

?>
<html pageid="<? echo $page->getId(); ?>" module="intranet">
    <head>
        <title>Intranet - GetShop</title>
        <link rel="stylesheet" href="/icomoon/style.css">
        <link rel="stylesheet" href="/skin/default/getshop.css">
        <link rel="stylesheet" href="/skin/default/intranet.css">
        <link rel="stylesheet" href="/skin/default/getshopmodules.css">
        <link rel="stylesheet" href="/skin/default/fontawesome/css/font-awesome.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />

        <link rel="stylesheet" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">
        <script src="https://cdn.ckeditor.com/ckeditor5/11.1.1/classic/ckeditor.js"></script>


        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        
        <? $page->loadAppsCss(); ?>
        <script type="text/javascript" src="js/jquery-1.9.0.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js"></script>
        <script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js"></script>
        <script type="text/javascript" src="js/moments.js"></script>
        <script type="text/javascript" src="js/getshop/getshop.js"></script>
        <script type="text/javascript" src="js/getshop.support.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
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
            $menu = $page->getMenu("intranet");
            $menu->renderTop($user);
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

