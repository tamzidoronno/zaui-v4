<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<?php
session_start();
include 'classes/autoload.php';
$router = new Router();
$page = $router->getPage();
$page->preRun();
$page->read_csv_translation();
?>

<html page="<?php echo $_GET['page']; ?>">
    <head>
        <meta charset="UTF-8">
        <title>GetShop - PGA</title>
        <link rel="stylesheet" href="/skin/pga.css">
        <link rel="stylesheet" href="/pages/<?php echo strtolower(get_class($page)) . "/" . get_class($page) ?>.css">
        <link rel="stylesheet" href="js/keyboard/jkeyboard.css">
        <link rel="stylesheet" href="icomoon/style.css">
        <link rel="stylesheet" href="js/photoswipe/dist/photoswipe.css"> 
        <link rel="stylesheet" href="js/photoswipe/dist/default-skin/default-skin.css"> 

        <script src="js/jquery-3.3.1.min.js"></script>
        <script src="js/keyboard/jkeyboard.js"></script>
        <script src="js/pga.js"></script>
        <script src="pages/<?php echo strtolower(get_class($page)) . "/" . get_class($page) ?>.js"></script>
        <script src="js/photoswipe/dist/photoswipe.min.js"></script> 
        <script src="js/photoswipe/dist/photoswipe-ui-default.min.js"></script> 


        <link rel="stylesheet" href="js/flatpickr/dist/themes/airbnb.css">
        <script src="js/flatpickr/dist/flatpickr.min.js"></script>

        <meta name="viewport" content="width=1024, initial-scale=1">
    </head>

    <?php include 'photoswipe.phtml'; ?>

    <div class="innerview">
        <div class='content'>
            <?php
            $page->render();
            ?>    
        </div>

        <div id="keyboard" class='keyboard' tabindex="0"></div>
    </div>
    
</body>
</html>

