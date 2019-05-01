<?php
session_start();
require '../vendor/autoload.php';
include '../common/autoloader.php';
$router = new PageRouter();
$menuHelper = new MenuHelper();

if (isset($_GET['tokenpass'])) {
    $_SESSION['tokenpass'] = $_GET['tokenpass'];
}
?>  
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>
            <?php
            echo $router->getCurrentPage()->title;
            ?>
        </title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="stylesheet" href="css.css">
        <script src="javascript/jquery.js"></script>
        <script src="javascript/ContentManager.js"></script>
        <script src="javascript.js"></script>
        <script src="javascript/ckeditor/ckeditor.js"></script>


        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css" integrity="sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf" crossorigin="anonymous">

        <link rel="stylesheet" href="photoswipe/photoswipe.css"> 
        <link rel="stylesheet" href="photoswipe/default-skin/default-skin.css"> 
        <script src="photoswipe/photoswipe.min.js"></script> 
        <script src="photoswipe/photoswipe-ui-default.min.js"></script> 

        <?php
        $googleincludefile = Config::$domain . '/google.phtml';
        if (file_exists($googleincludefile)) {
            include $googleincludefile;
        }

        $router->renderCurrentPageCss();
        ?>
    </head>

    <body pageid="<?php echo $router->getCurrentPageName(); ?>">

        <?php
        include 'photoswipetemplate.phtml';
        ?>

        <div class="header">
            <div class="header_inner">
                <div class="logo">
                    <a href="/"><img src="<?php echo Config::$domain; ?>/logo.png"/></a>
                </div>

                <div id="menuToggle">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>

                <div class="menu">
                    <?php
                    $pages = $router->getAllPages();
                    $menuHelper->buildMenu($pages);
                    ?>
                </div>
            </div>
        </div>

        <div class="mobilemenu">
            <?php
            $pages = $router->getAllPages();
            $menuHelper->buildMenu($pages);
            ?>
        </div>

        <div class="body">
            <?php
            $router->renderCurrentPage();
            ?>
        </div>

        <?php
        include Config::$domain . '/footer.phtml';
        ?>


        <?php
        include 'translation.php';
        ?>
    </body>
</html>
