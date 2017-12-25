<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$lang = $factory->getCurrentLanguage();
echo $_GET['callback'] . '(';
if(file_exists("scripts/bookingprocess_translations/$lang.txt")) {
    include 'bookingprocess_translations/'.$lang.'.txt';
} else {
    include 'bookingprocess_translations/en_en.txt';
}
echo ")";
?>