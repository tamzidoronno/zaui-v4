<?php
chdir("../../");
include '../loader.php';
include 'resturant/Resturant.php';

echo '<script src="jquery.js"></script>';
echo '<script src="https://kit.fontawesome.com/3b2cd389e7.js" crossorigin="anonymous"></script>';
echo '<link rel="stylesheet" href="skin.css">';
echo '<meta name="viewport" content="width=device-width, user-scalable=no">';

$rest = new Resturant();
$rest->render();


?>
<div class="overlay">
    <div class="closebutton" onclick="$('.overlay').fadeOut();"><i class="fa fa-close"></i></div>
    <div class="overlayinner">
        
    </div>
</div>
<div class="shoppingcart">
    <i class="fa fa-shopping-cart"></i> <span> <? echo "Ingen varer i handlekurven"; ?></span>
</div>