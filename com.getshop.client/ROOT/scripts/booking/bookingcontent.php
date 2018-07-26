<?php
chdir("../../");
include '../loader.php';

header('Access-Control-Allow-Origin: *');
$content = file_get_contents("../apps/GslBooking/template/gslfront_1.phtml");

/* @var $this \ns_81edf29e_38e8_4811_a2c7_bc86ad5ab948\GslBooking */
$factory = IocContainer::getFactorySingelton();
$prod = $factory->getApi()->getStoreManager()->isProductMode();
$endpoint = "http://" . $_SERVER['SERVER_NAME'];
if($prod || $_SERVER['SERVER_NAME'] == "www.getshop.com") {
    $endpoint = "https://www.getshop.com/";
}
$content = str_replace("{getshop_endpoint}", $endpoint, $content);

echo $content;
?>