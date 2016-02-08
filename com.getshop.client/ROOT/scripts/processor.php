<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getPmsManager()->processor($_GET['bookingengine']);
?>