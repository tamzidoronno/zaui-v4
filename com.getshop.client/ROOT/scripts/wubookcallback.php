<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$rcode = $_POST['rcode'];
$factory->getApi()->getWubookManager()->fetchBookingFromCallback("default", $rcode);
?>