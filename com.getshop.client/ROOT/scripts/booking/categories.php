<?php

chdir("../../");
include '../loader.php';

header('Access-Control-Allow-Origin: *');
$content = file_get_contents("../apps/GslBooking/template/gslfront_1.phtml");

/* @var $this \ns_81edf29e_38e8_4811_a2c7_bc86ad5ab948\GslBooking */
$factory = IocContainer::getFactorySingelton();

$categories = $factory->getApi()->getPmsBookingProcess()->getAllCategories("default");

echo $_GET['callback'] . '('. json_encode($categories).')';

?>
