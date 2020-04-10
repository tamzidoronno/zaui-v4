<?
include '../loader.php';
$pageFactory = new \PageFactory("pga");
$pageFactory->addExtraApplicationsNoneInstance("752aee89-0abc-43cf-9067-5aeadfe07cc1");
$pageFactory->addExtraApplicationsNoneInstance("b01782d0-5181-4b12-bec8-ee2e844bcae5"); // PGA Conference View
$pageFactory->addExtraApplicationsNoneInstance("d8ac717e-8e03-4b59-a2c3-e61b064a21c2"); // PGA Update Guest Information
//$pageFactory->addExtraApplicationsNoneInstance("b01782d0-5181-4b12-bec8-ee2e844bcae5"); // PGA Conference View

$factory = IocContainer::getFactorySingelton();
$page = $pageFactory->getPage(@$_GET['page']);



$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

$title = $page->getTitle();

?>
<!DOCTYPE html>
<html pageid="<? echo $page->getId(); ?>" module="pga">
    <head>
        <meta name="viewport" content="width=1024">

        <script>isFirstLoading = true;</script>
        <title><? echo $title; ?></title>
        <link rel="stylesheet" href="/icomoon/style.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/getshop.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/pga.css?<? echo calculateCacheName(); ?>">
        <link rel="stylesheet" href="/skin/default/circle.css?<? echo calculateCacheName(); ?>">
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
        <script type="text/javascript" src="js/getshop.pga.js?<? echo calculateCacheName(); ?>"></script>
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
        
        <div class="gs_body" hasleftmenu="<? echo $page->getLeftMenu() ? 'yes' : 'no'; ?>">
            <div class="gs_area_menu hidden">
                <?
                $menu = new ns_752aee89_0abc_43cf_9067_5aeadfe07cc1\PgaMenu();
//                $menu->setPage($page);
                $menu->renderApplication();
                ?>
            </div>
            
            <div class="gs_body_inner">
                <div class='mobilemenu' onclick='toggleMenu();'>
                    <i class='fa fa-list'></i>
                </div>
                <? $page->renderPage(); ?>
            </div>

        </div>
    </body>
    
    <script>
        function toggleMenu() {
            var menu = $('.gs_area_menu');
            if (menu.is(':visible')) {
                menu.addClass('hidden');
            } else {
                menu.removeClass('hidden');
            }
        }
        
        $(document).ready(function() {
            getshop.documentLoaded();
        });
    </script>
    
    <?
    $page->renderBottom();
    ?>
</html>

