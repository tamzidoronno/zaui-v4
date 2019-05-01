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
        <meta name="description" content="<?php echo $router->getCurrentPage()->getDescription(); ?>">
        
        <link rel="stylesheet" href="css.css">
        <script src="javascript/jquery.js"></script>
        <script src="javascript/ContentManager.js"></script>
        <script src="javascript.js"></script>
        <script src="https://cdn.ckeditor.com/ckeditor5/12.0.0/classic/ckeditor.js"></script>
        
        <script async src="https://www.googletagmanager.com/gtag/js?id=UA-31092051-1"></script>
        <script>
          window.dataLayer = window.dataLayer || [];
          function gtag(){dataLayer.push(arguments);}
          gtag('js', new Date());

          gtag('config', 'UA-31092051-1');
        </script>
        
        <?php
            $router->renderCurrentPageCss(); 
        ?>
    </head>
    
    <body pageid="<?php echo $router->getCurrentPageName(); ?>">
        <div class="header">
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
            include  Config::$domain.'/footer.phtml';
        ?>
        
        
        <?php
        include 'translation.php';
        ?>
    </body>
</html>
