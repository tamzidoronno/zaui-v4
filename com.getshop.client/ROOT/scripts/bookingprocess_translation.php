<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$lang = $factory->getCurrentLanguage();

if(isset($_GET['language']) && $_GET['language']) {
    $lang = $_GET['language'];
}


if($lang == "en") { $lang = "en_en"; }
if($lang == "no") { $lang = "nb_NO"; }


echo $_GET['callback'] . '(';
if(file_exists("scripts/bookingprocess_translations/$lang.txt")) {
    include 'bookingprocess_translations/'.$lang.'.txt';
} else {
    include 'bookingprocess_translations/en_en.txt';
}
echo ")";
?>